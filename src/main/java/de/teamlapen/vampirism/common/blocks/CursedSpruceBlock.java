package de.teamlapen.vampirism.common.blocks;

import de.teamlapen.vampirism.api.blocks.HolyWaterEffectConsumer;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.items.HolyWaterBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CursedSpruceBlock extends RotatedPillarBlock implements HolyWaterEffectConsumer {

    public static final BooleanProperty ACTIVE = BlockStateProperties.ACTIVE;

    public CursedSpruceBlock(Properties properties) {
        super(properties.randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
    }

    public BlockState activeBlockState() {
        return this.defaultBlockState().setValue(ACTIVE, true);
    }

    public ItemStack getActiveBlockItem() {
        ItemStack stack = this.asItem().getDefaultInstance();
        stack.set(ModDataComponents.ACTIVE, Unit.INSTANCE);

        return stack;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return HolyWaterBottleItem.onHolyWaterUsedOnBlock(stack, player, () -> level.setBlockAndUpdate(pos, state.setValue(ACTIVE, false)));
    }

    @Override
    public void onHolyWaterEffect(Level level, BlockState state, BlockPos pos, ItemStack holyWaterStack, IItemWithTier.Tier tier) {
        level.setBlockAndUpdate(pos, state.setValue(ACTIVE, false));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE)) return;

        Block block = state.getBlock();

        Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
        List<Direction> possibleDirections = new ArrayList<>(Arrays.asList(Direction.values()));

        if (block == ModBlocks.CURSED_SPRUCE_LOG.get()) {
            switch (axis) {
                case X -> possibleDirections.removeAll(List.of(Direction.WEST, Direction.EAST));
                case Y -> possibleDirections.removeAll(List.of(Direction.UP, Direction.DOWN));
                case Z -> possibleDirections.removeAll(List.of(Direction.NORTH, Direction.SOUTH));
            }
        }

        Direction targetDirection = possibleDirections.get(random.nextInt(possibleDirections.size()));
        BlockPos targetPos = pos.relative(targetDirection);
        BlockState targetState = level.getBlockState(targetPos);

        DirectCursedBarkBlock.Type type = chooseType(block, axis, targetDirection);

        if (targetState.isAir() || targetState.is(ModBlocks.DIAGONAL_CURSED_BARK.get())) {
            BlockState newState = ModBlocks.DIRECT_CURSED_BARK.get().defaultBlockState().setValue(DirectCursedBarkBlock.SIDE_MAP.get(targetDirection.getOpposite()), type);
            level.setBlockAndUpdate(targetPos, newState);
            targetState = newState;
        }

        if (!targetState.is(ModBlocks.DIRECT_CURSED_BARK.get())) {
            return;
        }

        for (Direction side : Direction.values()) {
            if (side == targetDirection || side == targetDirection.getOpposite()) continue;

            BlockState neighbor = level.getBlockState(pos.relative(side));
            if (neighbor.is(ModBlocks.DIRECT_CURSED_BARK.get()) && neighbor.getValue(DirectCursedBarkBlock.SIDE_MAP.get(side.getOpposite())) != DirectCursedBarkBlock.Type.NONE) {
                BlockPos diagonalPos = targetPos.relative(side);
                BlockState diagonal = level.getBlockState(diagonalPos);

                if (diagonal.isAir()) {
                    diagonal = ModBlocks.DIAGONAL_CURSED_BARK.get().defaultBlockState();
                }
                if (diagonal.is(ModBlocks.DIAGONAL_CURSED_BARK.get())) {
                    BooleanProperty property = DiagonalCursedBarkBlock.PROPERTY_TABLE.get(targetDirection.getOpposite(), side.getOpposite());

                    if (property != null) {
                        level.setBlockAndUpdate(diagonalPos, diagonal.setValue(property, true));
                    }
                }
            }
        }
    }

    private DirectCursedBarkBlock.Type chooseType(Block block, Direction.Axis axis, Direction dir) {
        if (block == ModBlocks.CURSED_SPRUCE_LOG.get()) {
            return (axis == Direction.Axis.Z && dir.getAxis() == Direction.Axis.X) ? DirectCursedBarkBlock.Type.HORIZONTAL : DirectCursedBarkBlock.Type.VERTICAL;
        } else {
            return switch (axis) {
                case X -> (dir.getAxis() == Direction.Axis.X ? DirectCursedBarkBlock.Type.VERTICAL : DirectCursedBarkBlock.Type.HORIZONTAL);
                case Y -> DirectCursedBarkBlock.Type.VERTICAL;
                case Z -> (dir.getAxis() == Direction.Axis.X ? DirectCursedBarkBlock.Type.HORIZONTAL : DirectCursedBarkBlock.Type.VERTICAL);
            };
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }
}
