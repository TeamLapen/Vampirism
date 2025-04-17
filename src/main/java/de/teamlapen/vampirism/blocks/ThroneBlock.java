package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.sit.SitEntity;
import de.teamlapen.vampirism.sit.SitHandler;
import de.teamlapen.vampirism.sit.SitUtil;
import de.teamlapen.vampirism.util.VampirismVoxelShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ThroneBlock extends VampirismSplitBlock {

    public ThroneBlock(Properties properties) {
        super(properties, VampirismVoxelShapes.THRONE_BOTTOM, VampirismVoxelShapes.THRONE_TOP, true);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        Part part = state.getValue(PART);
        Direction oppFacing = state.getValue(FACING).getOpposite();
        player.awardStat(ModStats.INTERACT_WITH_THRONE.get());
        if (part == Part.MAIN && (hitResult.getDirection() == Direction.UP || hitResult.getDirection() == oppFacing)) {
            SitHandler.startSitting(player, level, pos, 0.5);
            return InteractionResult.SUCCESS;
        } else if (part == Part.SUB && hitResult.getDirection() == oppFacing && level.getBlockState(pos.below()).is(this)) {
            SitHandler.startSitting(player, level, pos.below(), 0.5);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        SitEntity entity = SitUtil.getSitEntity(level, pos);
        if (entity != null) {
            entity.discard();
        }
    }
}
