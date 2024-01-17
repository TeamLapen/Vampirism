package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.storage.IAttachment;
import de.teamlapen.lib.lib.storage.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IDisguise;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Basic class for all of Vampirism's players.
 * Implements basic methods for level or minion handling
 */
public abstract class FactionBasePlayer<T extends IFactionPlayer<T>> implements IFactionPlayer<T>, IAttachment, IPlayerEventListener {

    private static final Logger LOGGER = LogManager.getLogger(FactionBasePlayer.class);

    protected final Player player;
    /**
     * {@code @NotNull} on server, otherwise {@code null}
     */
    private final @Nullable TaskManager taskManager;
    private final IDisguise disguise;

    public FactionBasePlayer(Player player) {
        this.player = player;
        if (player instanceof ServerPlayer) {
            this.taskManager = new TaskManager((ServerPlayer) player, this, this.getFaction());
        } else {
            this.taskManager = null;
        }
        this.disguise = new Disguise(this.getFaction());
    }

    @Override
    public @NotNull Player asEntity() {
        return this.player;
    }

    @Override
    public int getLevel() {
        return VampirismAPI.factionPlayerHandler(player).getCurrentLevel(getFaction());
    }

    /**
     * Only use this if you are dealing with minions.
     * Otherwise use {@link FactionBasePlayer#getRepresentingPlayer()}
     */
    @Override
    public LivingEntity getRepresentingEntity() {
        return player;
    }

    @Override
    public Player getRepresentingPlayer() {
        return player;
    }

    /**
     * null on client and @NotNull on server
     */
    @NotNull
    @Override
    public TaskManager getTaskManager() {
        return taskManager;
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isRemote() {
        if (player.level() == null) {
            LOGGER.error("Trying to check if remote, but world is not set yet", new Throwable("World not loaded").fillInStackTrace());
            return false;
        }
        return player.level().isClientSide;
    }

    @MustBeInvokedByOverriders
    @Override
    public void onDeath(DamageSource src) {
        this.getSkillHandler().damageRefinements();
    }

    @MustBeInvokedByOverriders
    @Override
    public void onUpdate() {
        if (!isRemote()) {
            this.taskManager.tick();
        }
    }

    @Override
    public IDisguise getDisguise() {
        return this.disguise;
    }

    /**
     * Sync all data
     *
     * @param all Whether all tracking players should receive this packet or only the representing player
     */
    public void sync(boolean all) {
        HelperLib.sync(this, player, all);
    }

    /**
     * Sync the capability using the given data
     *
     * @param all Whether all tracking players should receive this packet or only the representing player
     */
    protected void sync(@NotNull CompoundTag data, boolean all) {
        HelperLib.sync(this, data, player, all);
    }

    protected void syncProperty(@NotNull ISyncable object, boolean all) {
        CompoundTag tag = new CompoundTag();
        tag.put(object.nbtKey(), object.serializeUpdateNBT());
        HelperLib.sync(tag, player, all);
    }

    @MustBeInvokedByOverriders
    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {
        if (!isRemote()) {
            if (newLevel <= 0) {
                this.getSkillHandler().reset();
                this.getActionHandler().resetTimers();
                this.sync(true);
            }

        } else {
            if (newLevel == 0) {
                this.getActionHandler().resetTimers();
                this.getSkillHandler().resetRefinements();
            }
        }
    }

    @MustBeInvokedByOverriders
    @Override
    public @NotNull CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put(this.taskManager.nbtKey(), this.taskManager.serializeNBT());
        return tag;
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        this.taskManager.deserializeNBT(nbt.getCompound(this.taskManager.nbtKey()));
    }

    @MustBeInvokedByOverriders
    @Override
    public void deserializeUpdateNBT(@NotNull CompoundTag nbt) {

    }

    @MustBeInvokedByOverriders
    @Override
    public @NotNull CompoundTag serializeUpdateNBT() {
        return new CompoundTag();
    }

    private static class Disguise implements IDisguise {

        private final IPlayableFaction<?> faction;

        public Disguise(IPlayableFaction<?> faction) {
            this.faction = faction;
        }

        @Override
        public @NotNull IPlayableFaction<?> getOriginalFaction() {
            return this.faction;
        }

        @Override
        public @Nullable IPlayableFaction<?> getViewedFaction(@Nullable IFaction<?> viewerFaction) {
            return this.faction;
        }

        @Override
        public void disguiseAs(@Nullable IPlayableFaction<?> faction) {

        }

        @Override
        public void unDisguise() {

        }

        @Override
        public boolean isDisguised() {
            return false;
        }
    }
}
