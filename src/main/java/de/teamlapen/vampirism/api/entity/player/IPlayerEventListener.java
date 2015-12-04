package de.teamlapen.vampirism.api.entity.player;

import net.minecraft.util.DamageSource;

/**
 * Provides serveral event related methods, which should be called by a dedicated EventHandler.
 * Vampirism automatically does this for all ExtendedPlayerProperties which implement this and are registered as Fraction in {@link FractionRegistry}
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
}
