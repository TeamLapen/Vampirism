package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.tileentity.AlchemyTableTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchemyTableBlock extends HorizontalContainerBlock {
    public static final BooleanProperty HAS_BOTTLE_INPUT_0 = BooleanProperty.create("has_bottle_input_0");;
    public static final BooleanProperty HAS_BOTTLE_INPUT_1 = BooleanProperty.create("has_bottle_input_1");;
    public static final BooleanProperty HAS_BOTTLE_OUTPUT_0 = BooleanProperty.create("has_bottle_output_0");;
    public static final BooleanProperty HAS_BOTTLE_OUTPUT_1 = BooleanProperty.create("has_bottle_output_1");;
    private static final VoxelShape shape = makeShape();

    public AlchemyTableBlock() {
        super(AbstractBlock.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(0.5F).lightLevel((p_235461_0_) -> 1).noOcclusion(), shape);
        this.registerDefaultState(this.defaultBlockState().setValue(HAS_BOTTLE_INPUT_0, false).setValue(HAS_BOTTLE_INPUT_1, false).setValue(HAS_BOTTLE_OUTPUT_0, false).setValue(HAS_BOTTLE_OUTPUT_1, false));
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader level) {
        return new AlchemyTableTileEntity();
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, World level, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rayTrace) {
        if (level.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            TileEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof AlchemyTableTileEntity) {
                player.openMenu((AlchemyTableTileEntity)tileentity);
                player.awardStat(ModStats.interact_with_alchemy_table);
            }

            return ActionResultType.CONSUME;
        }
    }

    @Override
    public void setPlacedBy(@Nonnull World level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            TileEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof AlchemyTableTileEntity) {
                ((AlchemyTableTileEntity)tileentity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, @Nonnull World level, @Nonnull BlockPos pos, BlockState state1, boolean p_196243_5_) {
        if (!state.is(state1.getBlock())) {
            TileEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof AlchemyTableTileEntity){
                InventoryHelper.dropContents(level, pos, ((AlchemyTableTileEntity) tileEntity));
            }
            super.onRemove(state, level, pos, state1, p_196243_5_);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_BOTTLE_INPUT_0).add(HAS_BOTTLE_INPUT_1).add(HAS_BOTTLE_OUTPUT_0).add(HAS_BOTTLE_OUTPUT_1);
    }

    private static VoxelShape makeShape() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.625, 0.3125, 1, 0.6875, 0.6875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.625, 0.25, 0.9375, 0.6875, 0.3125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.125, 0.625, 0.1875, 0.875, 0.6875, 0.3125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.1875, 0.625, 0.125, 0.8125, 0.6875, 0.1875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.3125, 0.625, 0, 0.6875, 0.6875, 0.0625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.625, 0.6875, 0.9375, 0.6875, 0.75), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.125, 0.625, 0.75, 0.875, 0.6875, 0.8125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.625, 0.875, 0.75, 0.6875, 0.9375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.3125, 0.625, 0.9375, 0.6875, 0.6875, 1), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.1875, 0.625, 0.8125, 0.8125, 0.6875, 0.875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.625, 0.0625, 0.75, 0.6875, 0.125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.6875, 0.3125, 0.4375, 0.75, 0.6875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.5625, 0.6875, 0.3125, 1, 0.75, 0.6875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.6875, 0.25, 0.9375, 0.75, 0.3125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.125, 0.6875, 0.1875, 0.875, 0.75, 0.3125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.1875, 0.6875, 0.125, 0.8125, 0.75, 0.1875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.3125, 0.6875, 0, 0.6875, 0.75, 0.0625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.625, 0.6875, 0.6875, 0.9375, 0.75, 0.75), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.6875, 0.6875, 0.375, 0.75, 0.75), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.125, 0.6875, 0.75, 0.375, 0.75, 0.8125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.625, 0.6875, 0.75, 0.875, 0.75, 0.8125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.6875, 0.875, 0.75, 0.75, 0.9375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.3125, 0.6875, 0.9375, 0.6875, 0.75, 1), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.1875, 0.6875, 0.8125, 0.4375, 0.75, 0.875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.5625, 0.6875, 0.8125, 0.8125, 0.75, 0.875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.6875, 0.0625, 0.75, 0.75, 0.125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.4375, 0.6875, 0.3125, 0.5625, 0.75, 0.625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.125, 0, 0.3125, 0.25, 0.625, 0.4375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.4375, 0, 0.0625, 0.5625, 0.625, 0.1875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.75, 0, 0.3125, 0.875, 0.625, 0.4375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.1875, 0, 0.6875, 0.3125, 0.625, 0.8125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.6875, 0, 0.6875, 0.8125, 0.625, 0.8125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.1875, 0.875, 0.3125, 0.25, 0.9375, 0.4375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.375, 0.75, 0.0625, 0.4375, 0.875, 0.125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.125, 0.75, 0.3125, 0.1875, 0.875, 0.375), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.75, 0.1875, 0.3125, 0.875, 0.25), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.75, 0.4375, 0.125, 0.875, 0.5), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.875, 0.3125, 0.1875, 0.9375, 0.5625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.3125, 0.875, 0, 0.4375, 0.9375, 0.1875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.1875, 0.875, 0.125, 0.3125, 0.9375, 0.3125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.25, 0.875, 0.0625, 0.3125, 0.9375, 0.125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.0625, 0.875, 0.25, 0.1875, 0.9375, 0.3125), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.125, 0.875, 0.1875, 0.1875, 0.9375, 0.25), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0, 0.875, 0.3125, 0.0625, 0.9375, 0.5625), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.4375, 0.875, 0, 0.5, 0.9375, 0.1875), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, VoxelShapes.box(0.3125, 0.875, 0.1875, 0.375, 0.9375, 0.3125), IBooleanFunction.OR);

        return shape;
    }
}
