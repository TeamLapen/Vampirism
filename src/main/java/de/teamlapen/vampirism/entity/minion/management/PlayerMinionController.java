package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Tokens are invalidated if minion dies or if minion is recalled
 */
public class PlayerMinionController implements INBTSerializable<CompoundNBT> {

    private final static Logger LOGGER = LogManager.getLogger();
    private final Random rng = new Random();
    @Nonnull
    private final MinecraftServer server;
    @Nonnull
    private final UUID lordID;
    private int maxMinions;

    @Nonnull
    private MinionInfo[] minions = new MinionInfo[0];
    @SuppressWarnings("unchecked")
    @Nonnull
    private Optional<Integer>[] minionTokens = new Optional[0];

    public PlayerMinionController(@Nonnull MinecraftServer server, @Nonnull UUID lordID) {
        this.server = server;
        this.lordID = lordID;
    }


    /**
     * Mark a minion as inactive
     * Don't use associated MinionData afterwards
     *
     * @param token Previously received token
     */
    public void checkInMinion(int id, int token) {
        MinionInfo i = getMinionInfo(id, token);
        if (i != null) {
            i.checkin();
        }
    }

    /**
     * Request minion data for a previously created token. Marks the respective minion slot as active
     * Returns null if
     * a) Minion already active
     * b) Minion dead
     * c) Token invalid
     *
     * @param token  Previously received token
     * @param entity wrapper entity
     */
    @Nullable
    public MinionData checkoutMinion(int id, int token, MinionEntity entity) {
        MinionInfo i = getMinionInfo(id, token);
        if (i != null) {
            int entityId = entity.getEntityId();
            DimensionType dimension = entity.dimension;
            if (i.checkout(entityId, dimension)) {
                return i.data;
            }
        }
        return null;
    }

    public Optional<Integer> claimMinionSlot(int id) {
        if (id < minionTokens.length) {
            if (!minionTokens[id].isPresent()) {
                int t = rng.nextInt();
                minionTokens[id] = Optional.of(t);
                return minionTokens[id];
            }
        }
        return Optional.empty();
    }

    /**
     * Check {@link PlayerMinionController#hasFreeMinionSlot()}
     *
     * @return minion id or -1 if no free minion slot
     */
    public int createNewMinion(MinionData data) {
        int i = minions.length;
        if (i < maxMinions) {
            MinionInfo[] n = Arrays.copyOf(minions, i + 1);
            Optional<Integer>[] t = Arrays.copyOf(minionTokens, i + 1);
            n[i] = new MinionInfo(i, data);
            t[i] = Optional.empty();
            minions = n;
            minionTokens = t;
            return i;
        }
        return -1;
    }

    public void contactMinions(Consumer<MinionEntity> entityConsumer) {
        for (MinionInfo m : minions) {
            if (m.isActive()) {
                assert m.dimension != null;
                World w = DimensionManager.getWorld(server, m.dimension, false, false);
                if (w != null) {
                    Entity e = w.getEntityByID(m.entityId);
                    if (e instanceof MinionEntity) {
                        entityConsumer.accept((MinionEntity) e);
                    } else {
                        LOGGER.warn("Retrieved entity is not a minion entity {}", e); //TODO check and remove or adjust
                    }
                }
            }
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        LOGGER.info("Deserializing");
        this.maxMinions = nbt.getInt("max_minions");
        ListNBT data = nbt.getList("data", 10);
        MinionInfo[] infos = new MinionInfo[data.size()];
        //noinspection unchecked
        Optional<Integer>[] tokens = new Optional[data.size()];
        for (INBT n : data) {
            CompoundNBT tag = (CompoundNBT) n;
            int id = tag.getInt("id");
            MinionData d = new MinionData();
            d.deserializeNBT(tag);
            MinionInfo i = new MinionInfo(id, d);
            i.deathCooldown = tag.getInt("death_timer");
            infos[id] = i;
            if (tag.contains("token", 99)) {
                tokens[id] = Optional.of(tag.getInt("token"));
            } else {
                tokens[id] = Optional.empty();
            }

        }
        this.minions = infos;
        this.minionTokens = tokens;
    }

    public UUID getUUID() {
        return this.lordID;
    }

    public Collection<Integer> getUnclaimedMinions() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < minionTokens.length; i++) {
            if (!minionTokens[i].isPresent()) {
                if (!minions[i].isDead()) {
                    ids.add(i);
                }
            }
        }
        return ids;

    }

    /**
     * @return Whether a new minion can be created via {@link PlayerMinionController#createNewMinion(MinionData)}
     */
    public boolean hasFreeMinionSlot() {
        return minions.length < maxMinions;
    }

    /**
     * The controller is only saved if it has minions
     *
     * @return Whether the minion controller has minions.
     */
    public boolean hasMinions() {
        return this.minions.length > 0;
    }

    /**
     * Mark a minion as dead and as inactive.
     * Don't use associated MinionData afterwards
     * Token is invalidated
     *
     * @param token Previously received token
     */
    public void markDeadAndReleaseMinionSlot(int id, int token) {
        MinionInfo i = getMinionInfo(id, token);
        if (i != null) {
            i.checkin();
            i.deathCooldown = 20;//* 60 * 5; TODO
            if (id < minionTokens.length) {
                minionTokens[id] = Optional.empty();
            }
        }
    }

    /**
     * Recalls all minions.
     * Corresponding entities are removed if present.
     *
     * @return A list of minions ids that can be reclaimed
     */
    public Collection<Integer> recallMinions() {
        contactMinions(MinionEntity::recallMinion);
        for (MinionInfo i : minions) { //TODO remove
            if (i.isActive()) {
                LOGGER.warn("Minion still active after recall");
            }
        }
        //noinspection unchecked
        minionTokens = new Optional[minions.length];
        Arrays.fill(minionTokens, Optional.empty());
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < minions.length; i++) {
            if (!minions[i].isDead()) {
                ids.add(i);
            }
        }
        return ids;
    }

    @Override
    public CompoundNBT serializeNBT() {
        LOGGER.info("Serializing");
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("max_minions", maxMinions);
        ListNBT data = new ListNBT();
        for (MinionInfo i : minions) {
            CompoundNBT d = i.data.serializeNBT();
            d.putInt("death_timer", i.deathCooldown);
            d.putInt("id", i.minionID);
            minionTokens[i.minionID].ifPresent(t -> d.putInt("token", t));
            data.add(d);
        }
        nbt.put("data", data);
        return nbt;
    }

    public void setMaxMinions(int newCount) {
        if (newCount > maxMinions) {
            this.maxMinions = newCount;

        }
        //TODO
    }

    public void tick() {
        for (MinionInfo i : minions) {
            if (i.deathCooldown > 0) {
                i.deathCooldown--;
                if (i.deathCooldown == 0) {
                    LOGGER.info("Minion can respawn");
                }
            }
        }
    }

    @Nullable
    private MinionInfo getMinionInfo(int id, int token) {
        assert minions.length == minionTokens.length;
        if (id < minions.length) {
            if (minionTokens[id].map(t -> t == token).orElse(false))
                return minions[id];
        }
        return null;
    }


    private class MinionInfo {
        final int minionID;
        @Nonnull
        final MinionData data;
        int entityId = -1;
        int deathCooldown = 0;
        @Nullable
        DimensionType dimension;

        private MinionInfo(int id, @Nonnull MinionData data) {
            this.minionID = id;
            this.data = data;
        }

        void checkin() {
            if (this.entityId == -1) {
                LOGGER.warn("Closing minion data for inactive minion"); //TODO check and remove
            }
            this.entityId = -1;
            this.dimension = null;
        }

        boolean checkout(int entityId, DimensionType dim) {
            if (this.entityId != -1 || isDead()) {
                return false;
            }
            this.entityId = entityId;
            this.dimension = dim;
            return true;
        }

        boolean isActive() {
            return entityId != -1;
        }

        boolean isDead() {
            return deathCooldown > 0;
        }
    }
}
