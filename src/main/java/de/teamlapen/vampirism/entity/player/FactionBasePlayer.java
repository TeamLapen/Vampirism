package de.teamlapen.vampirism.entity.player;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
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
public abstract class FactionBasePlayer<T extends IFactionPlayer<T>> implements IFactionPlayer<T>, ISyncable.ISyncableAttachment, IPlayerEventListener {

    private static final Logger LOGGER = LogManager.getLogger(FactionBasePlayer.class);
    protected final Player player;
    /**
     * {@code @NotNull} on server, otherwise {@code null}
     */
    private final @Nullable TaskManager taskManager;

    public FactionBasePlayer(Player player) {
        this.player = player;
        if (player instanceof ServerPlayer) {
            this.taskManager = new TaskManager((ServerPlayer) player, this, this.getFaction());
        } else {
            this.taskManager = null;
        }
    }


    @Override
    public int getLevel() {
        return VampirismAPI.getFactionPlayerHandler(player).map(handler -> handler.getCurrentLevel(getFaction())).orElse(0);
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

    @Override
    public int getTheEntityID() {
        return player.getId();
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isRemote() {
        if (player.getCommandSenderWorld() == null) {
            LOGGER.error("Trying to check if remote, but world is not set yet", new Throwable("World not loaded").fillInStackTrace());
            return false;
        }
        return player.getCommandSenderWorld().isClientSide;
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

    @MustBeInvokedByOverriders
    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {
        if (!isRemote()) {
            if (newLevel > 0) {
                this.getSkillHandler().addSkillPoints((int) ((newLevel - oldLevel) * VampirismConfig.BALANCE.skillPointsPerLevel.get()));
            } else {
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

    @Override
    public void loadFromNBT(CompoundTag nbt) {
        this.taskManager.readNBT(nbt.getCompound("task_manager"));
    }

    @Override
    public void loadUpdateFromNBT(CompoundTag nbt) {

    }

    @Override
    public CompoundTag writeFullUpdateToNBT() {
        return new CompoundTag();
    }

    @Override
    public CompoundTag writeToNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag taskManager = new CompoundTag();
        this.taskManager.writeNBT(taskManager);
        tag.put("task_manager", taskManager);
        return tag;
    }
}
