package de.teamlapen.vampirism.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CursedBorkBlock extends Block {

    private static final VoxelShape shape =  VoxelShapes.empty();
    public static final DirectionProperty FACING = BlockStateProperties.FACING;


    public CursedBorkBlock() {
        super(AbstractBlock.Properties.of(Material.REPLACEABLE_PLANT).noCollission().randomTicks().strength(0.2F).sound(SoundType.VINE));
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public void entityInside(@Nonnull BlockState state, @Nonnull World level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        entity.makeStuckInBlock(state, new Vector3d(0.5F, 0.5D, 0.5F));
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState p_220053_1_, @Nonnull IBlockReader p_220053_2_, @Nonnull BlockPos p_220053_3_, @Nonnull ISelectionContext p_220053_4_) {
        return shape;
    }

    private boolean canAttachTo(IBlockReader p_196471_1_, BlockPos p_196471_2_, Direction p_196471_3_) {
        BlockState blockstate = p_196471_1_.getBlockState(p_196471_2_);
        return blockstate.isFaceSturdy(p_196471_1_, p_196471_2_, p_196471_3_);
    }

    @Override
    public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        Direction direction = p_196260_1_.getValue(FACING);
        return this.canAttachTo(p_196260_2_, p_196260_3_.relative(direction), direction);
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState blockState, @Nonnull Direction direction, @Nonnull BlockState otherState, @Nonnull IWorld level, @Nonnull BlockPos pos, @Nonnull BlockPos otherPos) {
        if (blockState.getValue(FACING) == direction && !blockState.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(blockState, direction, otherState, level, pos, otherPos);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
