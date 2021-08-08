package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nonnull;


public class VampirismWorldDefaultImpl implements IVampirismWorld {
    @Override
    public void clear() {

    }

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
    public void removeGarlicBlock(int id) {

    }
}
