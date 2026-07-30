package de.teamlapen.faction.common.factions.minions;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionHelper;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.core.FactionSkills;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.server.commands.arguments.MinionArgument;
import de.teamlapen.faction.common.util.ModCodecs;
import de.teamlapen.faction.common.util.SpawnUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
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
 * If the player calls the minions to them, the checkout (loaded) minions are forced to check in their data and are removed from the world. Then the tokens are invalidated and a fresh set of tokens is created for the new minion entities that access the same minion slots.
 * If a minion entity is stored in an unloaded chunk, it will try to check out the minion data/slot again once loaded. However, if the player has recalled it in the meantime (which means a new shell entity has been created), the minion entity will fail to check out the data and remove itself from the world.
 * <p>
 * <p>
 * - Recruit a new minion{@link PlayerMinionController#createNewMinionSlot(MinionData, EntityType)}
 * - Associate an entity representation (real entity, nbt saved entity, ...) with the minion slot {@link PlayerMinionController#claimMinionSlot(int)}
 * - Check out minion slot if entity is added to world. Can "fail" if minion has been reclaimed in the meantime. {@link PlayerMinionController#checkoutMinion(int, int, MinionEntity)}
 * - Check in minion slot if entity is removed from world {@link PlayerMinionController#checkInMinion(int, int)}
 * - Release minion slot if minion dies {@link PlayerMinionController#markDeadAndReleaseMinionSlot(int, int)}
 */
public class PlayerMinionController implements ValueIOSerializable {

    private final static Logger LOGGER = LogManager.getLogger();

    private final Random rng = new Random();
    @NotNull
    private final MinecraftServer server;
    @NotNull
    private final UUID lordID;
    private int maxMinions;
    @NotNull
    private Holder<? extends IPlayableFaction<?>> faction;
    @NotNull
    private MinionInfo @NotNull [] minions = new MinionInfo[0];
    @SuppressWarnings("unchecked")
    @NotNull
    private Optional<Integer> @NotNull [] minionTokens = new Optional[0];

    public PlayerMinionController(@NotNull MinecraftServer server, @NotNull UUID lordID) {
        this.server = server;
        this.lordID = lordID;
    }

    public void activateTask(int minionID, @NotNull IMinionTask<?, MinionData> task) {
        if (!this.getLordPlayer().map(LivingEntity::isSpectator).orElse(false)) {
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
    }

    /**
     * Mark a minion as inactive
     * Don't use associated MinionData afterward
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
     * a) Minion is already active
     * b) Minion dead
     * c) Token invalid
     *
     * @param id     minion slot
     * @param token  Previously received token
     * @param entity wrapper entity
     */
    @Nullable
    public <T extends MinionData> T checkoutMinion(int id, int token, @NotNull MinionEntity<T> entity) {
        MinionInfo i = getMinionInfo(id, token);
        if (i != null) {
            int entityId = entity.getId();
            ResourceKey<Level> dimension = entity.level().dimension();
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
    public <T> @NotNull Optional<T> contactMinionData(int id, @NotNull Function<MinionData, T> function) {
        if (id >= 0 && id < minions.length) {
            return Optional.of(function.apply(minions[id].data));
        }
        return Optional.empty();
    }

    public void forEach(@NotNull BiConsumer<MinionData, Optional<MinionEntity<?>>> consumer) {
        for (MinionInfo minion : minions) {
            consumer.accept(minion.data, getMinionEntity(minion));
        }
    }

    /**
     * Contact all currently loaded (checked-out) minions
     */
    public void contactMinions(Consumer<MinionEntity<?>> entityConsumer) {
        for (MinionInfo m : minions) {
            getMinionEntity(m).ifPresent(entityConsumer);
        }
    }

    @Nullable
    public MinionEntity<?> createMinionEntityAtPlayer(int id, @NotNull Player p) {
        assert id >= 0;
        EntityType<? extends MinionEntity<?>> type = minions[id].minionType;
        if (type == null) {
            LOGGER.warn("Cannot create minion because type does not exist");
        } else {
            return SpawnUtil.createEntity(type, p.level(), EntitySpawnReason.LOAD).map(m -> {
                if (IFaction.isNeutral(faction) || !IFactionHelper.get().isEntityOfFaction(m, faction)) {
                    LOGGER.warn("Specified minion entity is of wrong faction. This: {} Minion: {}", faction, m.getFaction());
                    m.discard();
                    return null;
                } else {
                    m.claimMinionSlot(id, this);
                    m.copyPosition(p);
                    checkDeathStatus(id, m);
                    p.level().addFreshEntity(m);
                    activateTask(id, FactionMinionTasks.STAY.get());
                    m.sync();
                    return m;
                }
            }).orElse(null);


        }
        return null;
    }

    /**
     * tmp fix to heal minions that are not dead but have negative health
     */
    @Deprecated
    private void checkDeathStatus(int id, MinionEntity<?> minion) {
        MinionData data = minions[id].data;
        if (data.getHealth() <= 0) {
            data.setHealth(data.getMaxHealth());
        }
    }

    /**
     * Check {@link PlayerMinionController#hasFreeMinionSlot()}
     *
     * @return minion slot id or -1 if no free minion slot
     */
    public int createNewMinionSlot(@NotNull MinionData data, EntityType<? extends MinionEntity<?>> minionType) {
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

    public @NotNull Collection<Integer> getCallableMinions() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < minions.length; i++) {
            if (!minions[i].isStillRecovering()) {
                ids.add(i);
            }
        }
        return ids;
    }

    public @NotNull List<MutableComponent> getRecoveringMinionNames() {
        return Arrays.stream(this.minions).filter(MinionInfo::isStillRecovering).map(i -> i.data).map(MinionData::getFormattedName).collect(Collectors.toList());
    }

    public @NotNull UUID getUUID() {
        return this.lordID;
    }

    /**
     * @return A collection of currently unclaimed and non-dead minion slots
     */
    public @NotNull Collection<Integer> getUnclaimedMinions() {
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
     * The minion slot is released, and the token is invalidated
     * Don't use associated MinionData afterward
     *
     * @param id    Minion slot
     * @param token Previously received token
     */
    public void markDeadAndReleaseMinionSlot(int id, int token) {
        MinionInfo i = getMinionInfo(id, token);
        if (i != null) {
            i.checkin();
            i.deathCooldown = 20 * FactionConfig.server().minionDeathRecoveryTime.get();
            getLord().filter(x -> x instanceof ISkillPlayer<?>).map(ISkillPlayer.class::cast).map(ISkillPlayer::getSkillHandler).ifPresent(s -> {
                if (s.isSkillEnabled(FactionSkills.MINION_RECOVERY)) {
                    i.deathCooldown = (int) (i.deathCooldown * 0.8);
                }
            });
            if (id < minionTokens.length) {
                minionTokens[id] = Optional.empty();
            }
        }
    }

    /**
     * Recall the given minion. Corresponding entity is removed if present, token is invalidated, and slot is released.
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
     * Corresponding entities are removed if present, tokens are invalidated, and slots are released
     *
     * @param force Include minions with locked task
     * @return A list of minions ids that can be reclaimed
     */
    public @NotNull Collection<Integer> recallMinions(boolean force) {
        List<Integer> ids = new ArrayList<>(minions.length);
        for (MinionInfo minion : minions) {
            if ((force || !minion.data.isTaskLocked()) && recallMinion(minion)) {
                ids.add(minion.minionID);
            }
        }
        return ids;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("max_minions", maxMinions);
        output.store("faction", ModCodecs.faction(), faction);
        ValueOutput.ValueOutputList data = output.childrenList("data");
        for (MinionInfo i : minions) {
            ValueOutput child = data.addChild();

            i.data.serialize(child.child("content"));
            child.putInt("death_timer", i.deathCooldown);
            child.putInt("id", i.minionID);

            if (i.minionType != null) {
                child.store("entity_type", BuiltInRegistries.ENTITY_TYPE.byNameCodec(), i.minionType);
            }
            minionTokens[i.minionID].ifPresent(t -> child.putInt("token", t));
        }
    }

    @Override
    public void deserialize(@NotNull ValueInput input) {
        var faction = input.read("faction", ModCodecs.playableFaction());
        if (faction.isEmpty()) {
            this.maxMinions = 0;
            return;
        }

        this.faction = faction.get();
        this.maxMinions = input.getIntOr("max_minions", 0);

        Map<Integer, MinionInfo> infos = new HashMap<>();
        Map<Integer, Optional<Integer>> tokens = new HashMap<>();

        for (ValueInput data : input.childrenListOrEmpty("data")) {
            Optional<Integer> idOpt = data.getInt("id");
            if (idOpt.isEmpty()) {
                continue;
            }

            int id = idOpt.get();

            MinionData minionData = MinionData.fromNBT(data.childOrEmpty("content"));
            if (minionData == null) {
                continue;
            }

            EntityType<? extends MinionEntity<?>> type = data.read("entity_type", ModCodecs.<MinionEntity<?>>entityCodec()).orElse(null);

            MinionInfo minionInfo = new MinionInfo(id, minionData, type);
            minionInfo.deathCooldown = data.getIntOr("death_timer", 0);

            infos.put(id, minionInfo);
            tokens.put(id, data.getInt("token").or(Optional::empty));
        }

        this.minions = infos.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toArray(MinionInfo[]::new);
        this.minionTokens = (Optional<Integer>[]) tokens.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toArray(Optional[]::new);

    }

    public void removeMinions() {
        setMaxMinions(this.faction, 0);
    }

    public void setMaxMinions(@NotNull Holder<? extends IPlayableFaction<?>> faction, int newCount) {
        assert newCount >= 0;
        if (!IFaction.is(faction, this.faction)) {
            LOGGER.warn("Changing player minion controller faction");
            contactMinions(MinionEntity::recallMinion);
            minions = new MinionInfo[0];
            //noinspection unchecked
            minionTokens = new Optional[0];
            this.faction = faction;
            this.maxMinions = IFaction.isNeutral(this.faction) ? 0 : newCount;
        } else {
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
                    getLordPlayer().ifPresent(player -> player.sendOverlayMessage(Component.translatable("dialogue.factionapi.minion.can_respawn", i.data.getFormattedName())));
                }
            } else {
                IMinionTask.IMinionTaskDesc<MinionData> taskDesc = i.data.getCurrentTaskDesc();
                tickTask(taskDesc.getTask(), taskDesc, i);
            }
        }
    }

    private void activateTask(@NotNull MinionInfo info, @NotNull IMinionTask<?, MinionData> task) {
        @Nullable
        IMinionTask.IMinionTaskDesc<MinionData> desc = task.activateTask(getLordPlayer().orElse(null), getMinionEntity(info).orElse(null), info.data);
        if (desc == null) {
            getLordPlayer().ifPresent(player -> player.sendOverlayMessage(Component.translatable("message.factionapi.minion_task.could_not_activate")));
        } else {
            MinionData d = info.data;
            d.switchTask(d.getCurrentTaskDesc().getTask(), d.getCurrentTaskDesc(), desc);
            this.contactMinion(info.minionID, MinionEntity::onTaskChanged);
        }
    }

    private @NotNull Optional<? extends ILordPlayer> getLord() {
        return getLordPlayer().map(FactionPlayerHandler::get).filter(x -> x.getLordLevel() > 0).flatMap(FactionPlayerHandler::getPlayerLord);
    }

    private @NotNull Optional<Player> getLordPlayer() {
        return Optional.ofNullable(server.getPlayerList().getPlayer(lordID));
    }

    private @NotNull Optional<MinionEntity<?>> getMinionEntity(@NotNull MinionInfo info) {
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
            if (minionTokens[id].map(t -> t == token).orElse(false)) {
                return minions[id];
            }
        }
        return null;
    }

    /**
     * Recall the given minion. Corresponding entity is removed if present, token is invalidated, and slot is released.
     *
     * @return If minion can be reclaimed (isAlive)
     */
    private boolean recallMinion(@NotNull MinionInfo i) {
        contactMinion(i.minionID, MinionEntity::recallMinion);
        if (i.isActive()) {
            LOGGER.debug("Minion still active after recall");
            i.checkin();
        }
        minionTokens[i.minionID] = Optional.empty();
        return !i.isStillRecovering();

    }

    @SuppressWarnings("unchecked")
    private <Q extends IMinionTask.IMinionTaskDesc<MinionData>, T extends IMinionTask<Q, MinionData>> void tickTask(@NotNull T task, IMinionTask.IMinionTaskDesc<MinionData> desc, @NotNull MinionInfo info) {
        if (info.isActive()) {
            task.tickActive((Q) desc, () -> getMinionEntity(info).map(m -> m), info.data);
        } else {
            task.tickBackground((Q) desc, info.data);

        }
    }

    /**
     * creates a collection of ids for all minions that are unique identifier to be used safely in commands
     * <br>
     * since the player is not necessarily available because the player might not be connected, the players name is needed as parameter
     *
     * @param playerName the players name
     * @return collection of minion ids
     */
    public Collection<MinionArgument.MinionId> getMinionIdForName(String playerName) {
        return Arrays.stream(minions).map(i -> new MinionArgument.MinionId(playerName, i.minionID, i.data.getFormattedName().getString())).collect(Collectors.toList());
    }

    private static class MinionInfo {
        private final int minionID;
        private final @NotNull MinionData data;
        private final @Nullable EntityType<? extends MinionEntity<?>> minionType;
        private int entityId = -1;
        private int deathCooldown = 0;
        private @Nullable ResourceKey<Level> dimension;

        private MinionInfo(int id, @NotNull MinionData data, @Nullable EntityType<? extends MinionEntity<?>> minionType) {
            this.minionID = id;
            this.data = data;
            this.minionType = minionType;
        }

        private void checkin() {
            if (this.entityId == -1) {
                LOGGER.debug("Closing minion data for inactive minion");
            }
            this.entityId = -1;
            this.dimension = null;
        }

        private boolean checkout(int entityId, ResourceKey<Level> dim) {
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
