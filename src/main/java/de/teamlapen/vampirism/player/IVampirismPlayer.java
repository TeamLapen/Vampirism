package de.teamlapen.vampirism.player;


import de.teamlapen.vampirism.mixin.MixinPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Adds Vampirism's flavor to {@link PlayerEntity} via Mixin {@link MixinPlayerEntity}
 */
public interface IVampirismPlayer {
    /**
     * @return Cached vampirism related attributes
     */
    VampirismPlayerAttributes getVampAtts();
}
