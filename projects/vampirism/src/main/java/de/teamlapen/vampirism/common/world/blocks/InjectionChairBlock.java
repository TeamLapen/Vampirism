package de.teamlapen.vampirism.common.world.blocks;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.world.blocks.base.BaseSplitBlock;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.world.items.InjectionItem;
import de.teamlapen.vampirism.misc.sit.ISittableBlock;
import de.teamlapen.vampirism.misc.sit.SitEntity;
import de.teamlapen.vampirism.misc.sit.SitUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class InjectionChairBlock extends BaseSplitBlock implements ISittableBlock {

    public static final VoxelShape BOTTOM_SHAPE = Stream.of(
            Block.box(0, 0, -1, 3, 13, 13),
            Block.box(13, 0, -1, 16, 13, 13),
            Block.box(3, 6, 1, 13, 8, 12),
            Block.box(3, 3, 0, 13, 6, 2),
            Block.box(3, 0, -1, 13, 3, 1),
            Block.box(3, 8, 11, 13, 12, 13),
            Block.box(3, 12, 12.33, 13, 17, 14.33)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape TOP_SHAPE = Block.box(3, 1, 13.66, 13, 6, 15.66);

    public InjectionChairBlock(Properties properties) {
        super(properties, BOTTOM_SHAPE, TOP_SHAPE, true);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isAlive()) {
            if (stack.getItem() instanceof InjectionItem injectionItem) {
                if (handleInjections(stack, injectionItem, level, pos, player, hand)) {
                    return InteractionResult.SUCCESS_SERVER;
                }
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public boolean handleInjections(ItemStack stack, InjectionItem injectionItem, Level level, BlockPos pos, Player player, InteractionHand hand) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        Holder<? extends IPlayableFaction<?>> faction = handler.getFaction();

        if (injectionItem.handleInjection(level, pos, player, handler, faction)) {
            injectionItem.consumeInjectionItem(stack, player, hand);
            player.awardStat(ModStats.INTERACT_WITH_INJECTION_CHAIR.get());

            return true;
        }

        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        player.awardStat(ModStats.INTERACT_WITH_INJECTION_CHAIR.get());

        Part part = state.getValue(PART);
        Direction backDirection = state.getValue(FACING);
        Direction hitDirection = hitResult.getDirection();

        if (part.isMain() && (hitDirection == Direction.UP || hitDirection == backDirection)) {
            SitUtil.startSitting(player, level, pos, 0.525);
            return InteractionResult.SUCCESS;
        }

        if (part.isSub() && hitDirection == backDirection && level.getBlockState(pos.below()).is(this)) {
            SitUtil.startSitting(player, level, pos.below(), 0.525);
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

        SitEntity entity = SitUtil.getSitEntity(level, pos);
        if (entity != null) {
            entity.discard();
        }
    }
}
