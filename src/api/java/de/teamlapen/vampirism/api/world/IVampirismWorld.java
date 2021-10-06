package de.teamlapen.vampirism.api.world;


import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    /**
     * adds/updates/removes the bounding box of a fog generating block
     *
     * @param sourcePos position of the fog generating block
     * @param area      new bounding box of the fog protected area or null if the area should be removed
     */
    void updateArtificialFogBoundingBox(@Nonnull BlockPos sourcePos, @Nullable AxisAlignedBB area);

    /**
     * adds/updates/removes the bounding box of a temporary event
     *
     * @param sourcePos position of the fog generating event
     * @param area      new bounding box of the fog protected area or null if the area should be removed
     */
    void updateTemporaryArtificialFog(@Nonnull BlockPos sourcePos, @Nullable AxisAlignedBB area);
}
