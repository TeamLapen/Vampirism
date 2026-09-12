package de.teamlapen.vampirism.api.world;

import net.minecraft.world.level.ChunkPos;

import java.util.List;

/**
 * Handles garlic in world
 */
public interface IGarlicChunkHandler {


    /**
     * @return The garlic strength at the given position
     */
    EnumStrength getStrengthAtChunk(ChunkPos pos);

    /**
     * Registers a garlic "emitter".
     *
     * @param strength Strength
     * @param pos      All affected chunk pos. Cannot be null
     * @return A unique hash which is required to remove the registration again
     */
    int registerGarlicBlock(EnumStrength strength, List<ChunkPos> pos);

    /**
     * Removes a garlic "emitter" registration
     *
     * @param id The unique hash obtained during registration
     */
    void removeGarlicBlock(int id);

    /**
     * Clear any caches upon world unload.
     */
    void clearCache();
}
