package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Minions are represented by a {@link MinionData}. All important information except for position and similar should be stored in there.
 * {@link MinionEntity} are merely shells for this.
 * <p>
 * When the player has a free minion slot {@link PlayerMinionController#createNewMinionSlot(MinionData, EntityType)} can be used to reserve one.
 * The minion slots are represented by their id (0-x). The minion slot holds the minion data and is not directly related to an entity.
 * <p>
 * An unclaimed minion slot (either a freshly reserved one or of a dead minion) can be claimed by a {@link MinionEntity} via {@link PlayerMinionController#claimMinionSlot(int)}.
 * If successful, it returns a token that grants the entity access to the minion data. It should be saved with the slot id in the minion entity.
 * Once the entity joins the world checks out the minion data via {@link PlayerMinionController#checkoutMinion(int, int, MinionEntity)}.
 * While the minion is checked out, the minion controller knows about the entity id and dimension and can access it when necessary. Furthermore, no other minion can access the data (no one else should have the token anyway).
 * When the minion is unloaded it must check in the data and save the token, so it can check out the data on load again.
 * <p>
 * If the player calls the minions to them, the checkout (loaded) minions  are forced to check in their data and are removed from the world. Then the tokens are invalidated and a fresh set of tokens is created for the new minion entities that access the same minion slots.
 * If a minion entity is stored in an unloaded chunk, it will try to check out the minion data/slot again once loaded. However, if the player has recalled it in the meantime (which means a new shell entity has been created) the minion entity will fail to check out the data and remove itself from the world.
 * <p>
 * <p>
 * - Recruit a new minion{@link PlayerMinionController#createNewMinionSlot(MinionData, EntityType)}
 * - Associate a entity representation (real entity, nbt saved entity, ...) with the minion slot {@link PlayerMinionController#claimMinionSlot(int)}
 * - Checkout minion slot if entity is added to world. Can "fail" if minion has been reclaimed in the meantime. {@link PlayerMinionController#checkoutMinion(int, int, MinionEntity)}
 * - Checkin minion slot if entity is removed from world {@link PlayerMinionController#checkInMinion(int, int)}
 * - Release minion slot if minion dies {@link PlayerMinionController#markDeadAndReleaseMinionSlot(int, int)}
 */
public class PlayerMinionController implements INBTSerializable<CompoundTag> {

    private final static Logger LOGGER = LogManager.getLogger();

    public static List<IMinionTask<?, ?>> getAvailableTasks(ILordPlayer player) {
        return ModRegistries.MINION_TASKS.getValues().stream().filter(t -> t.isAvailable(player.getLordFaction(), player)).collect(Collectors.toList());
    }

    private final Random rng = new Random();
    @Nonnull
    private final MinecraftServer server;
    @Nonnull
    private final UUID lordID;
    private int maxMinions;
    @Nullable
    private IPlayableFaction<?> faction;
    @Nonnull
    private MinionInfo[] minions = new MinionInfo[0];
    @SuppressWarnings("unchecked")
    @Nonnull
    private Optional<Integer>[] minionTokens = new Optional[0];

    public PlayerMinionController(@Nonnull MinecraftServer server, @Nonnull UUID lordID) {
        this.server = server;
        this.lordID = lordID;
    }

    public void activateTask(int minionID, IMinionTask<?, MinionData> task) {
        if (minionID >= minions.length) {
            LOGGER.warn("Trying to activate a task for a non-existent minion {}", minionID);
        } else {
            if (minionID < 0) {
                for (MinionInfo i : minions) {
                    if (!i.data.isTaskLocked()) {
                        activateTask(i, task);
                    }
                }
            } else {
                activateTask(minions[minionID], task);
            }
        }
    }

    /**
     * Mark a minion as inactive
     * Don't use associated MinionData afterwards
     *
     * @param id    minion slot
     * @param token Previously received token
     */
    public void checkInMinion(int id, int token) {
        MinionInfo i = getMinionInfo(id, token);
        if (i != null) {
            i.checkin();
        }
    }

    /**
     * Request minion data for a previously claimed slot. Marks the respective minion slot as active
     * Returns null if
     * a) Minion already active
     * b) Minion dead
     * c) Token invalid
     *
     * @param id     minion slot
     * @param token  Previously received token
     * @param entity wrapper entity
     */
    @Nullable
    public <T extends MinionData> T checkoutMinion(int id, int token, MinionEntity<T> entity) {
        MinionInfo i = getMinionInfo(id, token);
        if (i != null) {
            int entityId = entity.getId();
            ResourceKey<Level> dimension = entity.level.dimension();
            if (i.checkout(entityId, dimension)) {
                //noinspection unchecked
                return (T) i.data;
            }
        }
        return null;
    }

    /**
     * Claim a minion slot.
     *
     * @param id slot id
     * @return the granted token or empty if slot either in use or not present
     */
    public Optional<Integer> claimMinionSlot(int id) {
        if (id < minionTokens.length) {
            if (minionTokens[id].isEmpty() && !minions[id].isStillRecovering()) {
                int t = rng.nextInt();
                minionTokens[id] = Optional.of(t);
                return minionTokens[id];
            }
        }
        return Optional.empty();
    }

    /**
     * Contact the minion entity for the given slot if loaded
     */
    public void contactMinion(int slot, Consumer<MinionEntity<?>> entityConsumer) {
        if (slot < minions.length) {
            getMinionEntity(minions[slot]).ifPresent(entityConsumer);
        }
    }

    /**
     * Allow interacting with the minion data directly regardless of checkout.
     * DO NOT use to get references of the data or anything inside.
     */
    public <T> Optional<T> contactMinionData(int id, Function<MinionData, T> function) {
        if (id >= 0 && id < minions.length) {
            return Optional.of(function.apply(minions[id].data));
        }
        return Optional.empty();
    }

    /**
     * Contact all currently loaded (checked out) minions
     */
    public void contactMinions(Consumer<MinionEntity<?>> entityConsumer) {
        for (MinionInfo m : minions) {
            getMinionEntity(m).ifPresent(entityConsumer);
        }
    }

    @Nullable
    public MinionEntity<?> createMinionEntityAtPlayer(int id, Player p) {
        assert id >= 0;
        EntityType<? extends MinionEntity<?>> type = minions[id].minionType;
        if (type == null) {
            LOGGER.warn("Cannot create minion because type does not exist");
        } else {
            return Helper.createEntity(type, p.getCommandSenderWorld()).map(m -> {
                if (faction == null || faction.isEntityOfFaction(m)) {
                    LOGGER.warn("Specified minion entity is of wrong faction. This: {} Minion: {}", faction, m.getFaction());
                    m.discard();
                    return null;
                } else {
                    m.claimMinionSlot(id, this);
                    m.copyPosition(p);
                    p.level.addFreshEntity(m);
                    activateTask(id, MinionTasks.STAY.get());
                    return m;
                }
            }).orElse(null);


        }
        return null;
    }

    /**
     * Check {@link PlayerMinionController#hasFreeMinionSlot()}
     *
     * @return minion slot id or -1 if no free minion slot
     */
    public int createNewMinionSlot(MinionData data, EntityType<? extends MinionEntity<?>> minionType) {
        int i = minions.length;
        if (i < maxMinions) {
            MinionInfo[] n = Arrays.copyOf(minions, i + 1);
            Optional<Integer>[] t = Arrays.copyOf(minionTokens, i + 1);
            n[i] = new MinionInfo(i, data, minionType);
            t[i] = Optional.empty();
            minions = n;
            minionTokens = t;
            return i;
        }
        return -1;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        IFaction<?> f = VampirismAPI.factionRegistry().getFactionByID(new ResourceLocation(nbt.getString("faction")));
        if (!(f instanceof IPlayableFaction)) {
            this.maxMinions = 0;
            return;
        }
        this.faction = (IPlayableFaction<?>) f;
        this.maxMinions = nbt.getInt("max_minions");
        ListTag data = nbt.getList("data", 10);
        MinionInfo[] infos = new MinionInfo[data.size()];
        //noinspection unchecked
        Optional<Integer>[] tokens = new Optional[data.size()];
        for (Tag n : data) {
            CompoundTag tag = (CompoundTag) n;
            int id = tag.getInt("id");
            MinionData d = MinionData.fromNBT(tag);
            ResourceLocation entityTypeID = new ResourceLocation(tag.getString("entity_type"));
            if (!ForgeRegistries.ENTITIES.containsKey(entityTypeID)) {
                LOGGER.warn("Cannot find saved minion type {}. Aborting controller load", entityTypeID);
                this.minions = new MinionInfo[0];
                //noinspection unchecked
                this.minionTokens = new Optional[0];
                return;
            }

            //noinspection unchecked
            EntityType<? extends MinionEntity<?>> type = (EntityType<? extends MinionEntity<?>>) ForgeRegistries.ENTITIES.getValue(entityTypeID);

            MinionInfo i = new MinionInfo(id, d, type);
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

    public Collection<Integer> getCallableMinions() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < minions.length; i++) {
            if (!minions[i].isStillRecovering()) {
                ids.add(i);
            }
        }
        return ids;
    }

    public List<MutableComponent> getRecoveringMinionNames() {
        return Arrays.stream(this.minions).filter(MinionInfo::isStillRecovering).map(i -> i.data).map(MinionData::getFormattedName).collect(Collectors.toList());
    }

    public UUID getUUID() {
        return this.lordID;
    }

    /**
     * @return A collection of currently unclaimed and non-dead minion slots
     */
    public Collection<Integer> getUnclaimedMinions() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < minionTokens.length; i++) {
            if (minionTokens[i].isEmpty()) {
                if (!minions[i].isStillRecovering()) {
                    ids.add(i);
                }
            }
        }
        return ids;

    }

    /**
     * @return Whether a new minion can be created via {@link PlayerMinionController#createNewMinionSlot(MinionData, EntityType)}
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
     * The minion slot is released and the token is invalidated
     * Don't use associated MinionData afterwards
     *
     * @param id    Minion slot
     * @param token Previously received token
     */
    public void markDeadAndReleaseMinionSlot(int id, int token) {
        MinionInfo i = getMinionInfo(id, token);
        if (i != null) {
            i.checkin();
            i.deathCooldown = 20 * VampirismConfig.BALANCE.miDeathRecoveryTime.get();
            if (id < minionTokens.length) {
                minionTokens[id] = Optional.empty();
            }
        }
    }

    /**
     * Recall the given minion. Corresponding entity is removed if present, token is invalidated and slot is released.
     *
     * @return If minion can be reclaimed (isAlive)
     */
    public boolean recallMinion(int id) {
        if (id >= 0 && id < minions.length) {
            return recallMinion(minions[id]);
        }
        return false;
    }

    /**
     * Recalls all minions.
     * Corresponding entities are removed if present, tokens are invalidated  and slots are released
     *
     * @param force Include minions with locked task
     * @return A list of minions ids that can be reclaimed
     */
    public Collection<Integer> recallMinions(boolean force) {
        List<Integer> ids = new ArrayList<>(minions.length);
        for (MinionInfo minion : minions) {
            if ((force || !minion.data.isTaskLocked()) && recallMinion(minion)) {
                ids.add(minion.minionID);
            }
        }
        return ids;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("max_minions", maxMinions);
        if (faction != null) {
            nbt.putString("faction", faction.getID().toString());
        }
        ListTag data = new ListTag();
        for (MinionInfo i : minions) {
            CompoundTag d = i.data.serializeNBT();
            d.putInt("death_timer", i.deathCooldown);
            d.putInt("id", i.minionID);
            if (i.minionType != null) d.putString("entity_type", i.minionType.getRegistryName().toString());
            minionTokens[i.minionID].ifPresent(t -> d.putInt("token", t));
            data.add(d);
        }
        nbt.put("data", data);
        return nbt;
    }

    public void setMaxMinions(@Nullable IPlayableFaction<?> faction, int newCount) {
        assert newCount >= 0;
        if (this.faction != null && faction != this.faction) {
            LOGGER.warn("Changing player minion controller faction");
            contactMinions(MinionEntity::recallMinion);
            minions = new MinionInfo[0];
            //noinspection unchecked
            minionTokens = new Optional[0];
            this.faction = faction;
            this.maxMinions = this.faction == null ? 0 : newCount;
        } else {
            this.faction = faction;
            if (newCount >= maxMinions) {
                this.maxMinions = newCount;
            } else {
                LOGGER.debug("Reducing minion count from {} to {}", this.maxMinions, newCount);
                //Iteratively remove minion entities and minion slots, starting with the last claimed one
                while (this.minions.length > newCount) {
                    int nL = this.minions.length - 1;
                    contactMinion(nL, MinionEntity::recallMinion);
                    MinionInfo[] n = Arrays.copyOf(minions, nL);
                    Optional<Integer>[] t = Arrays.copyOf(minionTokens, nL);
                    minions = n;
                    minionTokens = t;
                }
            }
        }
    }

    /**
     * Tick serverside
     */
    public void tick() {
        for (MinionInfo i : minions) {
            if (i.deathCooldown > 0) {
                i.deathCooldown--;
                if (i.deathCooldown == 0) {
                    i.data.setHealth(i.data.getMaxHealth());
                    getLordPlayer().ifPresent(player -> player.displayClientMessage(new TranslatableComponent("text.vampirism.minion.can_respawn", i.data.getFormattedName()), true));
                }
            } else {
                IMinionTask.IMinionTaskDesc<MinionData> taskDesc = i.data.getCurrentTaskDesc();
                tickTask(taskDesc.getTask(), taskDesc, i);
            }
        }
    }

    private void activateTask(MinionInfo info, IMinionTask<?, MinionData> task) {
        @Nullable
        IMinionTask.IMinionTaskDesc<MinionData> desc = task.activateTask(getLordPlayer().orElse(null), getMinionEntity(info).orElse(null), info.data);
        if (desc == null) {
            getLordPlayer().ifPresent(player -> player.displayClientMessage(new TranslatableComponent("text.vampirism.minion.could_not_activate"), false));
        } else {
            MinionData d = info.data;
            d.switchTask(d.getCurrentTaskDesc().getTask(), d.getCurrentTaskDesc(), desc);
            this.contactMinion(info.minionID, MinionEntity::onTaskChanged);
        }
    }

    private LazyOptional<? extends ILordPlayer> getLord() {
        return getLordPlayer().map(FactionPlayerHandler::getOpt).orElse(LazyOptional.empty());
    }

    private Optional<Player> getLordPlayer() {
        return Optional.ofNullable(server.getPlayerList().getPlayer(lordID));
    }

    private Optional<MinionEntity<?>> getMinionEntity(MinionInfo info) {
        if (info.isActive()) {
            assert info.dimension != null;
            Level w = server.getLevel(info.dimension);
            if (w != null) {
                Entity e = w.getEntity(info.entityId);
                if (e instanceof MinionEntity) {
                    return Optional.of((MinionEntity<?>) e);
                } else {
                    LOGGER.warn("Retrieved entity is not a minion entity {}", e);
                }
            }
        }
        return Optional.empty();
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

    /**
     * Recall the given minion. Corresponding entity is removed if present, token is invalidated and slot is released.
     *
     * @return If minion can be reclaimed (isAlive)
     */
    private boolean recallMinion(MinionInfo i) {
        contactMinion(i.minionID, MinionEntity::recallMinion);
        if (i.isActive()) {
            LOGGER.debug("Minion still active after recall");
        }
        minionTokens[i.minionID] = Optional.empty();
        return !i.isStillRecovering();

    }

    @SuppressWarnings("unchecked")
    private <Q extends IMinionTask.IMinionTaskDesc<MinionData>, T extends IMinionTask<Q, MinionData>> void tickTask(T task, IMinionTask.IMinionTaskDesc<MinionData> desc, MinionInfo info) {
        if (info.isActive()) {
            task.tickActive((Q) desc, () -> getMinionEntity(info).map(m -> m), info.data);
        } else {
            task.tickBackground((Q) desc, info.data);

        }
    }

    private static class MinionInfo {
        final int minionID;
        @Nonnull
        final MinionData data;
        @Nullable
        final EntityType<? extends MinionEntity<?>> minionType;
        int entityId = -1;
        int deathCooldown = 0;
        @Nullable
        ResourceKey<Level> dimension;

        private MinionInfo(int id, @Nonnull MinionData data, @Nullable EntityType<? extends MinionEntity<?>> minionType) {
            this.minionID = id;
            this.data = data;
            this.minionType = minionType;
        }

        void checkin() {
            if (this.entityId == -1) {
                LOGGER.debug("Closing minion data for inactive minion");
            }
            this.entityId = -1;
            this.dimension = null;
        }

        boolean checkout(int entityId, ResourceKey<Level> dim) {
            if (this.entityId != -1 || isStillRecovering()) {
                return false;
            }
            this.entityId = entityId;
            this.dimension = dim;
            return true;
        }

        boolean isActive() {
            return entityId != -1;
        }

        boolean isStillRecovering() {
            return deathCooldown > 0;
        }
    }
}
