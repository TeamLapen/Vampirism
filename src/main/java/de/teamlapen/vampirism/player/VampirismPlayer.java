package de.teamlapen.vampirism.player;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * Basic class for all of Vampirism's players.
 * Implements basic methods for level or minion handling
 * TODO 1.17 refactor to FactionBasePlayer
 */
public abstract class VampirismPlayer<T extends IFactionPlayer<?>> implements IFactionPlayer<T>, ISyncable.ISyncableEntityCapabilityInst, IPlayerEventListener {

    private static final Logger LOGGER = LogManager.getLogger(VampirismPlayer.class);
    protected final PlayerEntity player;
    /**
     * {@code @Nonnull} on server, otherwise {@code null}
     */
    private final TaskManager taskManager;

    public VampirismPlayer(PlayerEntity player) {
        this.player = player;
        if (player instanceof ServerPlayerEntity) {
            this.taskManager = new TaskManager((ServerPlayerEntity) player, this, this.getFaction());
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
     * Otherwise use {@link VampirismPlayer#getRepresentingPlayer()}
     */
    @Override
    public LivingEntity getRepresentingEntity() {
        return player;
    }

    @Override
    public PlayerEntity getRepresentingPlayer() {
        return player;
    }

    /**
     * null on client & @Nonnull on server
     */
    @Nonnull
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

    public void loadData(CompoundNBT nbt) {
        if (this.taskManager != null) {
            this.taskManager.readNBT(nbt);
        } else {
            LOGGER.debug("The player is loaded on the client side and therefore taskmaster related data is missing");
        }
    }

    @Override
    public final void loadUpdateFromNBT(CompoundNBT nbt) {
        loadUpdate(nbt);
    }

    @Override
    public void onDeath(DamageSource src) {
        this.getSkillHandler().damageRefinements();
    }

    @Override
    public void onPlayerClone(PlayerEntity original, boolean wasDeath) {
        copyFrom(original);
    }

    @Override
    public void onUpdate() {
        if (!isRemote()) {
            this.taskManager.tick();
        }
    }

    public void saveData(CompoundNBT nbt) {
        if (this.taskManager != null) {
            this.taskManager.writeNBT(nbt);
        } else {
            LOGGER.debug("The player is saved on the client side and therefore taskmaster related data is missing");
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

    @Override
    public final void writeFullUpdateToNBT(CompoundNBT nbt) {
        writeFullUpdate(nbt);
    }

    /**
     * Copy all relevant values from the given player and return a instance of the old players VampirismPlayer, so {@link VampirismPlayer} can copy it's values as well
     *
     * @param old
     * @return
     */
    protected abstract VampirismPlayer<T> copyFromPlayer(PlayerEntity old);

    /**
     * Can be overridden to load data from updates in subclasses
     *
     * @param nbt
     */
    protected void loadUpdate(CompoundNBT nbt) {
    }

    /**
     * Sync the capability using the given data
     *
     * @param data
     * @param all  Whether all tracking players should receive this packet or only the representing player
     */
    protected void sync(CompoundNBT data, boolean all) {
        HelperLib.sync(this, data, player, all);
    }

    /**
     * Can be overridden to put data into updates in subclasses
     *
     * @param nbt
     */
    protected void writeFullUpdate(CompoundNBT nbt) {
    }

    private void copyFrom(PlayerEntity old) {
        VampirismPlayer<T> p = copyFromPlayer(old);
    }
}
