package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MotherTrophyBlockEntity extends BlockEntity {

    public MotherTrophyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModTiles.MOTHER_TROPHY.get(), pPos, pBlockState);
    }
}
