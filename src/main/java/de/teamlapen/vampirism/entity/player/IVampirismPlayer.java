package de.teamlapen.vampirism.entity.player;


/**
 * Adds Vampirism's flavor to {@link net.minecraft.world.entity.player.Player} via Mixin {@link de.teamlapen.vampirism.mixin.MixinPlayerEntity}
 */
public interface IVampirismPlayer {
    /**
     * @return Cached vampirism related attributes
     */
    VampirismPlayerAttributes getVampAtts();
}
