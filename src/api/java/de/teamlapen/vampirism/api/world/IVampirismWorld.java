package de.teamlapen.vampirism.api.world;


import net.minecraft.util.math.BlockPos;

public interface IVampirismWorld extends IGarlicChunkHandler {

    /**
     * TODO 1.17 rename clearCaches
     * Clear any caches upon world unload.
     */
    void clear();

    /**
     * checks if the position is in a vampire village
     *
     * @param blockPos pos to check
     * @return true if in a vampire controlled village otherwise false
     */
    boolean isInsideArtificialVampireFogArea(BlockPos blockPos);
}
