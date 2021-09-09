package de.teamlapen.vampirism.api.world;


import net.minecraft.core.BlockPos;

public interface IVampirismWorld extends IGarlicChunkHandler {

    /**
     * Clear any caches upon world unload.
     */
    void clearCaches();

    /**
     * checks if the position is in a vampire village
     *
     * @param blockPos pos to check
     * @return true if in a vampire controlled village otherwise false
     */
    boolean isInsideArtificialVampireFogArea(BlockPos blockPos);
}
