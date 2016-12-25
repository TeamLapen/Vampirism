package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.EnumStrength;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Handles garlic in world
 */
public interface IGarlicChunkHandler {

    /**
     * @param pos
     * @return The garlic strength at the given position
     */
    @Nonnull
    EnumStrength getStrengthAtChunk(ChunkPos pos);

    /**
     * Registers a garlic "emitter".
     *
     * @param strength Strength
     * @param pos      All affected chunk pos
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
     * Clear all emitters. E.g. at world unload
     */
    void clear();

    /**
     * FOR INTERNAL USAGE ONLY
     */
    @Deprecated
    interface Provider {
        @Nonnull
        IGarlicChunkHandler getHandler(World world);

        /**
         * Clear all garlic chunk handlers. E.g. at client stop.
         */
        void clear();
    }
}
