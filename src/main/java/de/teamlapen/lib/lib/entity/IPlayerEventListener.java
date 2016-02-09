package de.teamlapen.lib.lib.entity;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

/**
 * Provides serveral event related methods, which should be called by a dedicated EventHandler.
 * You can register implementing {@link net.minecraftforge.common.IExtendedEntityProperties} in {@link de.teamlapen.lib.HelperRegistry} to let the library call this.
 */
public interface IPlayerEventListener {

    void onJoinWorld();

    /**
     * Called when the corrosponding player is attacked.
     * @return If false the damage will be canceled
     */
    boolean onEntityAttacked(DamageSource src, float amt);

    void onDeath(DamageSource src);

    void onUpdate();

    void onChangedDimension(int from,int to);

    void onPlayerLoggedIn();

    void onPlayerLoggedOut();

    void onPlayerClone(EntityPlayer original);
}
