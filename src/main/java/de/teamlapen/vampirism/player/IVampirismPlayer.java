package de.teamlapen.vampirism.player;


/**
 * Adds Vampirism's flavor to {@link net.minecraft.world.entity.player.Player} via Mixin {@link MixinPlayerEntity}
 */
public interface IVampirismPlayer {
    /**
     * @return Cached vampirism related attributes
     */
    VampirismPlayerAttributes getVampAtts();
}
