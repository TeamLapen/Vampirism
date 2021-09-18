package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.EnumStrength;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nonnull;

/**
 * Handles garlic in world
 */
public interface IGarlicChunkHandler {


    /**
     * TODO 1.17 rename ?
     *
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
     * Removes a garlic "emitter" registration
     *
     * @param id The unique hash obtained during registration
     */
    void removeGarlicBlock(int id);
}
