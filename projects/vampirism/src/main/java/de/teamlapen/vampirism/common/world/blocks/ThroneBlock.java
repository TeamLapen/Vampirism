package de.teamlapen.vampirism.common.world.blocks;

import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.factions.common.world.blocks.base.BaseSplitBlock;
import de.teamlapen.vampirism.misc.sit.SitEntity;
import de.teamlapen.vampirism.misc.sit.SitHandler;
import de.teamlapen.vampirism.misc.sit.SitUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class ThroneBlock extends BaseSplitBlock {

    public static final VoxelShape BOTTOM_SHAPE = Stream.of(
            Block.box(2.0, 0, 2.2, 13.5, 10.4, 14),
            Block.box(2.0, 9, 1.2, 13.5, 16, 3),
            Block.box(0.5, 13.5, 2.2, 2.7, 15.5, 14.2),
            Block.box(13.3, 13.5, 2.2, 15.5, 15.5, 14.2)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
    public static final VoxelShape TOP_SHAPE = Block.box(2.0, 0, 1.2, 13.5, 10, 3);

    public ThroneBlock(Properties properties) {
        super(properties, BOTTOM_SHAPE, TOP_SHAPE, true);
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
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        SitEntity entity = SitUtil.getSitEntity(level, pos);
        if (entity != null) {
            entity.discard();
        }
    }


}
