package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.blockentity.AlchemyTableBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchemyTableBlock extends HorizontalContainerBlock {
    public static final BooleanProperty HAS_BOTTLE_INPUT_0 = BooleanProperty.create("has_bottle_input_0");;
    public static final BooleanProperty HAS_BOTTLE_INPUT_1 = BooleanProperty.create("has_bottle_input_1");;
    public static final BooleanProperty HAS_BOTTLE_OUTPUT_0 = BooleanProperty.create("has_bottle_output_0");;
    public static final BooleanProperty HAS_BOTTLE_OUTPUT_1 = BooleanProperty.create("has_bottle_output_1");;
    private static final VoxelShape shape = makeShape();

    public AlchemyTableBlock() {
        super(Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(0.5F).lightLevel((p_235461_0_) -> 1).noOcclusion(), shape);
        this.registerDefaultState(this.defaultBlockState().setValue(HAS_BOTTLE_INPUT_0, false).setValue(HAS_BOTTLE_INPUT_1, false).setValue(HAS_BOTTLE_OUTPUT_0, false).setValue(HAS_BOTTLE_OUTPUT_1, false));
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemyTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModTiles.ALCHEMICAL_TABLE.get(), AlchemyTableBlockEntity::serverTick);
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult rayTrace) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof AlchemyTableBlockEntity) {
                player.openMenu((AlchemyTableBlockEntity)tileentity);
                player.awardStat(ModStats.interact_with_alchemy_table);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof AlchemyTableBlockEntity) {
                ((AlchemyTableBlockEntity)tileentity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, BlockState state1, boolean p_196243_5_) {
        if (!state.is(state1.getBlock())) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof AlchemyTableBlockEntity){
                Containers.dropContents(level, pos, ((AlchemyTableBlockEntity) tileEntity));
            }
            super.onRemove(state, level, pos, state1, p_196243_5_);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_BOTTLE_INPUT_0).add(HAS_BOTTLE_INPUT_1).add(HAS_BOTTLE_OUTPUT_0).add(HAS_BOTTLE_OUTPUT_1);
    }

    private static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0.3125, 1, 0.6875, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.625, 0.25, 0.9375, 0.6875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.625, 0.1875, 0.875, 0.6875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.625, 0.125, 0.8125, 0.6875, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.625, 0, 0.6875, 0.6875, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.625, 0.6875, 0.9375, 0.6875, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.625, 0.75, 0.875, 0.6875, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.625, 0.875, 0.75, 0.6875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.625, 0.9375, 0.6875, 0.6875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.625, 0.8125, 0.8125, 0.6875, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.625, 0.0625, 0.75, 0.6875, 0.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.6875, 0.3125, 0.4375, 0.75, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.6875, 0.3125, 1, 0.75, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.6875, 0.25, 0.9375, 0.75, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.6875, 0.1875, 0.875, 0.75, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.6875, 0.125, 0.8125, 0.75, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.6875, 0, 0.6875, 0.75, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.6875, 0.6875, 0.9375, 0.75, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.6875, 0.6875, 0.375, 0.75, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.6875, 0.75, 0.375, 0.75, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.6875, 0.75, 0.875, 0.75, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.6875, 0.875, 0.75, 0.75, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.6875, 0.9375, 0.6875, 0.75, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.6875, 0.8125, 0.4375, 0.75, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.6875, 0.8125, 0.8125, 0.75, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.6875, 0.0625, 0.75, 0.75, 0.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.6875, 0.3125, 0.5625, 0.75, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.3125, 0.25, 0.625, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0, 0.0625, 0.5625, 0.625, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.3125, 0.875, 0.625, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.6875, 0.3125, 0.625, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.6875, 0.8125, 0.625, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.875, 0.3125, 0.25, 0.9375, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.75, 0.0625, 0.4375, 0.875, 0.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.75, 0.3125, 0.1875, 0.875, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.75, 0.1875, 0.3125, 0.875, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.75, 0.4375, 0.125, 0.875, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.875, 0.3125, 0.1875, 0.9375, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.875, 0, 0.4375, 0.9375, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.875, 0.125, 0.3125, 0.9375, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.875, 0.0625, 0.3125, 0.9375, 0.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.875, 0.25, 0.1875, 0.9375, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.875, 0.1875, 0.1875, 0.9375, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0.3125, 0.0625, 0.9375, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.875, 0, 0.5, 0.9375, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.875, 0.1875, 0.375, 0.9375, 0.3125), BooleanOp.OR);

        return shape;
    }
}
