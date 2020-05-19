package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.entity.minion.MinionEntity;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Tokens are invalidated if minion dies or if minion is recalled
 */
public class PlayerMinionController implements INBTSerializable<CompoundNBT> {

    private final static Logger LOGGER = LogManager.getLogger();
    private final Random rng = new Random();
    private final MinecraftServer server;
    private final UUID lordID;
    @Nonnull
    private final Int2IntMap minionToken = new Int2IntArrayMap();
    private int maxMinions;
    @Nonnull
    private MinionInfo[] minions = new MinionInfo[0];

    public PlayerMinionController(@Nonnull MinecraftServer server, @Nonnull UUID lordID) {
        this.server = server;
        this.lordID = lordID;
    }

    public boolean canControlMoreMinions() {
        return minions.length < maxMinions;
    }

    /**
     * Mark a minion as inactive
     * Don't use associated MinionData afterwards
     *
     * @param token Previously received token
     */
    public void checkInMinion(int token) {
        MinionInfo i = getMinionInfo(token);
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
    public MinionData checkoutMinionData(int token, MinionEntity entity) {
        MinionInfo i = getMinionInfo(token);
        if (i != null) {
            int entityId = entity.getEntityId();
            DimensionType dimension = entity.dimension;
            if (i.checkout(entityId, dimension)) {
                return i.data;
            }
        }
        return null;
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

    /**
     * @return unique token or 0 if something failed
     */
    public int createNewMinion(MinionData data) {
        int i = minions.length;
        if (i < maxMinions) {
            MinionInfo[] n = Arrays.copyOf(minions, i + 1);
            n[i] = new MinionInfo(data);
            minions = n;
            int token = rng.nextInt();
            minionToken.put(token, i);
            return token;
        }
        return 0;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

    /**
     * Mark a minion as dead and as inactive.
     * Don't use associated MinionData afterwards
     * Token is invalidated
     *
     * @param token Previously received token
     */
    public void markDeadAndRelease(int token) {
        MinionInfo i = getMinionInfo(token);
        if (i != null) {
            i.checkin();
            i.deathCooldown = 20 * 60 * 5;
            minionToken.remove(token);
        }
    }

    /**
     * Recalls all minions.
     * Corresponding entities are removed if present.
     *
     * @return A fresh set of tokens for all alive minions that should be used to create new entities.
     */
    public Collection<Integer> recallMinions() {
        contactMinions(MinionEntity::recallMinion);
        minionToken.clear();
        Collection<Integer> newTokens = new IntArrayList(minions.length);
        for (int i = 0; i < minions.length; i++) {
            if (minions[i].isDead()) continue;
            int t = rng.nextInt();
            minionToken.put(t, i);
            newTokens.add(t);
        }
        return newTokens;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return null;
    }

    public void tick() {
        for (MinionInfo i : minions) {
            if (i.deathCooldown > 0) {
                i.deathCooldown--;
                if (i.deathCooldown == 0) {
                    //TODO notify revive
                }
            }
        }
    }

    @Nullable
    private MinionInfo getMinionInfo(int token) {
        int id = minionToken.get(token);
        if (id < minions.length) {
            return minions[id];
        }
        return null;
    }

    private class MinionInfo {
        @Nonnull
        final MinionData data;
        int entityId = -1;
        int deathCooldown = 0;
        @Nullable
        DimensionType dimension;

        private MinionInfo(@Nonnull MinionData data) {
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
