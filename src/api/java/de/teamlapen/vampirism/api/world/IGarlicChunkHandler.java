package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.EnumStrength;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nonnull;

/**
 * Handles garlic in world
 */
public interface IGarlicChunkHandler {

    /**
     * Clear all emitters. E.g. at world unload
     */
    void clear();

    /**
     * @return The garlic strength at the given position
     */
    @Nonnull
    EnumStrength getStrengthAtChunk(ChunkPos pos);

    /**
     * Registers a garlic "emitter".
     *
     * @param strength Strength
     * @param pos      All affected chunk pos. Cannot be null
     * @return A unique hash which is required to remove the registration again
     */
    int registerGarlicBlock(EnumStrength strength, ChunkPos... pos);

    /**
     * Removes a garlic "emitter" registration"
     *
     * @param id The unique hash obtained during registration
     */
    void removeGarlicBlock(int id);

    /**
     * FOR INTERNAL USAGE ONLY
     */
    @Deprecated
    interface Provider {
        /**
         * Clear all garlic chunk handlers. E.g. at client stop.
         */
        void clear();

        @Nonnull
        IGarlicChunkHandler getHandler(IWorld world);
    }
}
