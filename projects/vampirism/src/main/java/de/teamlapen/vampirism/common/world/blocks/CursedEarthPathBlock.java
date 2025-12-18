package de.teamlapen.vampirism.common.world.blocks;

import de.teamlapen.vampirism.common.core.ModBlocks;
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

    public CursedEarthPathBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? Block.pushEntitiesUp(this.defaultBlockState(), ModBlocks.CURSED_EARTH.get().defaultBlockState(), context.getLevel(), context.getClickedPos())
                : super.getStateForPlacement(context);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        turnToCursedDirt(null, state, level, pos);
    }

    public static void turnToCursedDirt(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState blockstate = pushEntitiesUp(state, ModBlocks.CURSED_EARTH.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, blockstate);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
    }
}
