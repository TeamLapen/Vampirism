package de.teamlapen.vampirism.common.world.blocks;

import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.faction.common.world.blocks.base.BaseSplitBlock;
import de.teamlapen.vampirism.misc.sit.ISittableBlock;
import de.teamlapen.vampirism.misc.sit.SitEntity;
import de.teamlapen.vampirism.misc.sit.SitUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class ThroneBlock extends BaseSplitBlock implements ISittableBlock {

    public static final VoxelShape BOTTOM_SHAPE = Stream.of(
            Block.box(1, 0, 1, 15, 10, 16),
            Block.box(0, 7, 2, 3, 15, 16),
            Block.box(13, 7, 2, 16, 15, 16),
            Block.box(1, 10, 14, 15, 16, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape TOP_SHAPE = Block.box(1, 0, 14, 15, 11, 16);

    public ThroneBlock(Properties properties) {
        super(properties, BOTTOM_SHAPE, TOP_SHAPE, true);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        player.awardStat(ModStats.INTERACT_WITH_THRONE.get());

        Part part = state.getValue(PART);
        Direction backDirection = state.getValue(FACING);
        Direction hitDirection = hitResult.getDirection();

        if (part.isMain() && (hitDirection == Direction.UP || hitDirection == backDirection)) {
            SitUtil.startSitting(player, level, pos, 0.625);
            return InteractionResult.SUCCESS;
        }

        if (part.isSub() && hitDirection == backDirection && level.getBlockState(pos.below()).is(this)) {
            SitUtil.startSitting(player, level, pos.below(), 0.625);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public Vec3 getStandUpLocation(Level level, BlockPos pos, Entity entity, Direction facing) {
        Vec3 result = SitUtil.tryMultipleStandUpLocations(entity, level,
                pos.relative(facing),
                pos.above(),
                pos.relative(facing.getCounterClockWise()),
                pos.relative(facing.getClockWise())
        );
        return result != null ? result : Vec3.atBottomCenterOf(pos);
    }

    @Override
    public float getSitRotation(BlockState state, SitEntity entity, Player player) {
        return state.getValue(FACING).toYRot();
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);

        SitEntity sit = SitUtil.getSitEntity(level, pos);
        if (sit != null) {
            sit.discard();
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        entity.causeFallDamage(fallDistance, state.getValue(PART).isMain() ? 0.5f : 1.0f, entity.damageSources().fall());
    }

    @Override
    public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityMovementAfterFallOn(level, entity);
        } else {
            this.bounceUp(entity);
        }
    }

    private void bounceUp(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0) {
            entity.setDeltaMovement(vec3.x, -vec3.y * 0.35F, vec3.z);
        }
    }
}
