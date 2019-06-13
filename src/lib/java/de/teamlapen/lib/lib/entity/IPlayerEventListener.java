package de.teamlapen.lib.lib.entity;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Provides serveral event related methods, which should be called by a dedicated EventHandler.
 * You can register a {@link Capability}, which instances implement this interface, in {@link de.teamlapen.lib.HelperRegistry} to let the library call this.
 */
public interface IPlayerEventListener {

    void onChangedDimension(DimensionType from, DimensionType to);

    void onDeath(DamageSource src);

    /**
     * Called when the corrosponding player is attacked.
     *
     * @return If true the damage will be canceled
     */
    boolean onEntityAttacked(DamageSource src, float amt);

    void onJoinWorld();

    void onPlayerClone(EntityPlayer original, boolean wasDeath);

    void onPlayerLoggedIn();

    void onPlayerLoggedOut();

    /**
     * Called during EntityLiving Update. Somewhere in the middle of {@link EntityPlayer}'s onUpdate
     */
    void onUpdate();

    /**
     * Called at the beginning and at the end of {@link EntityPlayer}'s onUpdate. {@link IPlayerEventListener#onUpdate()} is called in between.
     * Should only be used for stuff that requires to run at the beginning or end
     *
     * @param phase
     */
    void onUpdatePlayer(TickEvent.Phase phase);
}
