package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import java.util.stream.Stream;


public class CrossBlock extends VampirismHorizontalBlock{

    private static VoxelShape makeCross() {
        return Stream.of(
                Block.makeCuboidShape(1, 0, 1, 15, 2, 15),
                Block.makeCuboidShape(3, 2, 3, 13, 3, 13),
                Block.makeCuboidShape(6, 3, 6, 10, 30, 10),
                Block.makeCuboidShape(10, 19, 6, 16, 23, 10),
                Block.makeCuboidShape(0, 19, 6, 6, 23, 10)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.empty());
    }

    public CrossBlock() {
        super("cross", Block.Properties.create(Material.WOOD).hardnessAndResistance(2).notSolid(), makeCross());
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).isAir(worldIn,pos); //TODO 1.17
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.UP && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}
