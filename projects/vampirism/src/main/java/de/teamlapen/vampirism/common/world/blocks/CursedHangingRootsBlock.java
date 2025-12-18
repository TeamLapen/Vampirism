package de.teamlapen.vampirism.common.world.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CursedHangingRootsBlock extends HangingRootsBlock {

    public CursedHangingRootsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        return aboveState.isFaceSturdy(level, abovePos, Direction.DOWN) && CursedRootsBlock.isProperSoil(aboveState);
    }
}
