package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

public class CursedEarthPathBlock extends DirtPathBlock {
    public CursedEarthPathBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_153131_) {
        return !this.defaultBlockState().canSurvive(p_153131_.getLevel(), p_153131_.getClickedPos()) ? Block.pushEntitiesUp(this.defaultBlockState(), ModBlocks.CURSED_EARTH.get().defaultBlockState(), p_153131_.getLevel(), p_153131_.getClickedPos()) : super.getStateForPlacement(p_153131_);
    }

    @Override
    public void tick(BlockState p_221070_, ServerLevel p_221071_, BlockPos p_221072_, RandomSource p_221073_) {
        turnToCursedDirt(null, p_221070_, p_221071_, p_221072_);
    }

    public static void turnToCursedDirt(@Nullable Entity pEntity, BlockState pState, Level pLevel, BlockPos pPos) {
        BlockState blockstate = pushEntitiesUp(pState, ModBlocks.CURSED_EARTH.get().defaultBlockState(), pLevel, pPos);
        pLevel.setBlockAndUpdate(pPos, blockstate);
        pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(pEntity, blockstate));
    }
}
