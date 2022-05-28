package de.teamlapen.vampirism.blocks;


import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class VampirismSplitBlock extends VampirismBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    private final VoxelShape NORTH1;
    private final VoxelShape EAST1;
    private final VoxelShape SOUTH1;
    private final VoxelShape WEST1;
    private final VoxelShape NORTH2;
    private final VoxelShape EAST2;
    private final VoxelShape SOUTH2;
    private final VoxelShape WEST2;
    private final boolean vertical;


    public VampirismSplitBlock(Properties properties, VoxelShape mainShape, VoxelShape subShape, boolean vertical) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(PART, Part.MAIN));
        NORTH1 = mainShape;
        EAST1 = UtilLib.rotateShape(NORTH1, UtilLib.RotationAmount.NINETY);
        SOUTH1 = UtilLib.rotateShape(NORTH1, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        WEST1 = UtilLib.rotateShape(NORTH1, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
        NORTH2 = subShape;
        EAST2 = UtilLib.rotateShape(NORTH2, UtilLib.RotationAmount.NINETY);
        SOUTH2 = UtilLib.rotateShape(NORTH2, UtilLib.RotationAmount.HUNDRED_EIGHTY);
        WEST2 = UtilLib.rotateShape(NORTH2, UtilLib.RotationAmount.TWO_HUNDRED_SEVENTY);
        this.vertical = vertical;
    }

    @Nonnull
    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return p_149645_1_.getValue(PART) == Part.MAIN ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        boolean main = state.getValue(PART) == Part.MAIN;
        switch (state.getValue(FACING)) {
            case NORTH:
                return main ? NORTH1 : NORTH2;
            case EAST:
                return main ? EAST1 : EAST2;
            case SOUTH:
                return main ? SOUTH1 : SOUTH2;
            case WEST:
                return main ? WEST1 : WEST2;
        }
        return NORTH1;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction enumfacing = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(enumfacing);
        return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(HORIZONTAL_FACING, enumfacing) : null;
    }

    @Override
    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public void playerWillDestroy(World world, BlockPos blockPos, BlockState blockState, PlayerEntity player) {
        if (!world.isClientSide && player.isCreative()) {
            Part part = blockState.getValue(PART);
            if (part == Part.SUB) {
                BlockPos blockpos = blockPos.relative(getOtherBlockDirection(blockState));
                BlockState otherState = world.getBlockState(blockpos);
                if (otherState.getBlock() == this && otherState.getValue(PART) == Part.MAIN) {
                    world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    world.levelEvent(player, 2001, blockpos, Block.getId(otherState));
                }
            }
        }

        super.playerWillDestroy(world, blockPos, blockState, player);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isClientSide) {
            BlockPos blockpos = pos.relative(getOtherBlockDirection(state));
            world.setBlock(blockpos, state.setValue(PART, Part.SUB), 3);
            world.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(world, pos, 3);
        }

    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == getOtherBlockDirection(stateIn)) {
            return facingState.getBlock() == this && facingState.getValue(PART) != stateIn.getValue(PART) ? updateFromOther(stateIn, facingState) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    protected Direction getOtherBlockDirection(BlockState blockState) {
        if (vertical) {
            return blockState.getValue(PART) == Part.MAIN ? Direction.UP : Direction.DOWN;
        }
        Direction rotation = blockState.getValue(FACING);
        return blockState.getValue(PART) == Part.MAIN ? rotation.getClockWise() : rotation.getCounterClockWise();
    }

    protected BlockState updateFromOther(BlockState thisState, BlockState otherState) {
        return thisState;
    }

    public enum Part implements IStringSerializable {
        MAIN("main"),
        SUB("sub");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }


        @Override
        public String toString() {
            return name;
        }
    }

}
