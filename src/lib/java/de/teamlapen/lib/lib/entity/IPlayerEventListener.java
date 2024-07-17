package de.teamlapen.lib.lib.entity;


import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Provides several event related methods, which should be called by a dedicated EventHandler.
 * You can register a {@link net.neoforged.neoforge.capabilities.EntityCapability}, which instances implement this interface, in {@link de.teamlapen.lib.HelperRegistry} to let the library call this.
 */
public interface IPlayerEventListener {

    default void onChangedDimension(ResourceKey<Level> from, ResourceKey<Level> to) {

    }

    default void onDeath(DamageSource src) {

    }

    /**
     * Called when the corresponding player is attacked.
     *
     * @return If true the damage will be canceled
     */
    default boolean onEntityAttacked(DamageSource src, float amt) {
        return false;
    }

    /**
     * Called when the player killed a living entity
     *
     * @param victim The killed entity
     * @param src    The lethal damage source
     */
    default void onEntityKilled(LivingEntity victim, DamageSource src) {
    }

    default void onJoinWorld() {

    }

    default void onPlayerLoggedIn() {

    }

    default void onPlayerLoggedOut() {

    }

    /**
     * Called during EntityLiving Update. Somewhere in the middle of {@link Player}'s onUpdate
     */
    default void onUpdate() {
    }

    /**
     * Called at the beginning and at the end of {@link Player}'s onUpdate. {@link IPlayerEventListener#onUpdate()} is called in between.
     * Should only be used for stuff that requires to run at the beginning or end
     */
    default void onUpdatePlayer(PlayerTickEvent event) {

    }
}
