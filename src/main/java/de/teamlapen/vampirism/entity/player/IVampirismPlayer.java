package de.teamlapen.vampirism.entity.player;


import de.teamlapen.vampirism.mixin.PlayerMixin;

/**
 * Adds Vampirism's flavor to {@link net.minecraft.world.entity.player.Player} via Mixin {@link PlayerMixin}
 */
public interface IVampirismPlayer {
    /**
     * @return Cached vampirism related attributes
     */
    VampirismPlayerAttributes vampirism$getVampAtts();
}
