package de.teamlapen.vampirism.player;

import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * Basic class for all of Vampirism's players.
 * Implements basic methods for level or minion handling
 */
public abstract class VampirismPlayer<T extends IFactionPlayer<?>> implements IFactionPlayer<T>, ISyncable.ISyncableEntityCapabilityInst, IPlayerEventListener {

    private static final Logger LOGGER = LogManager.getLogger(VampirismPlayer.class);
    private final TaskManager taskManager;
    protected final PlayerEntity player;

    public VampirismPlayer(PlayerEntity player) {
        this.player = player;
        this.taskManager = new TaskManager(player, this.getFaction());
    }


    @Override
    public int getLevel() {
        return VampirismAPI.getFactionPlayerHandler(player).map(handler -> handler.getCurrentLevel(getFaction())).orElse(0);
    }

    @Nonnull
    @Override
    public TaskManager getTaskManager() {
        return taskManager;
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


    @Override
    public int getTheEntityID() {
        return player.getEntityId();
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean isRemote() {
        if (player.getEntityWorld() == null) {
            LOGGER.error("Trying to check if remote, but world is not set yet", new Throwable("World not loaded").fillInStackTrace());
            return false;
        }
        return player.getEntityWorld().isRemote;
    }


    @Override
    public final void loadUpdateFromNBT(CompoundNBT nbt) {
        loadUpdate(nbt);
    }

    @Override
    public void onPlayerClone(PlayerEntity original, boolean wasDeath) {
        copyFrom(original);
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
        this.taskManager.readNBT(nbt);
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
        this.taskManager.writeNBT(nbt);
    }

    private void copyFrom(PlayerEntity old) {
        VampirismPlayer<T> p = copyFromPlayer(old);
    }

    public void loadData(CompoundNBT nbt) {
        this.taskManager.readNBT(nbt);
    }

    public void saveData(CompoundNBT nbt) {
        this.taskManager.writeNBT(nbt);
    }
}
