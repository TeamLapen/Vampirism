package de.teamlapen.vampirism.api.world.entity.player.vampire;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IVampireVisionUser {
    /**
     * Force enables the vision
     * Does NOT unlock the vision
     *
     * @param vision Null to disable all
     */
    void activateVision(@Nullable ResourceKey<IVampireVision> vision);

    /**
     * @return The currently active vision. May be null
     */
    @Nullable
    Holder<IVampireVision> getActiveVision();

    /**
     * Locks the vision again, preventing the player from using it
     */
    void unUnlockVision(@NotNull ResourceKey<IVampireVision> vision);

    /**
     * Unlocks the given vision, so the player can activate it.
     * Is not saved to nbt
     */
    void unlockVision(@NotNull ResourceKey<IVampireVision> vision);
}
