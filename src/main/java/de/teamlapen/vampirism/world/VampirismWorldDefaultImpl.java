package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class VampirismWorldDefaultImpl implements IVampirismWorld {
    @Override
    public void clear() { }

    @Nonnull
    @Override
    public EnumStrength getStrengthAtChunk(ChunkPos pos) {
        return EnumStrength.NONE;
    }

    @Override
    public boolean isInsideArtificialVampireFogArea(BlockPos blockPos) {
        return false;
    }

    @Override
    public int registerGarlicBlock(EnumStrength strength, ChunkPos... pos) {
        return 0;
    }

    @Override
    public void removeGarlicBlock(int id) { }

    @Override
    public void updateArtificialFogBoundingBox(@Nonnull BlockPos sourcePos, @Nullable AxisAlignedBB area) { }

    @Override
    public void updateTemporaryArtificialFog(@Nonnull BlockPos sourcePos, @Nullable AxisAlignedBB area) { }
}
