package de.teamlapen.vampirism.blocks;

import net.minecraft.block.AbstractBlock;
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


public class CandelabraBlock extends VampirismHorizontalBlock {
    private static VoxelShape makeCandelabraShape() {
        return Stream.of(
                Block.box(6, 0, 6, 10, 1, 10),
                Block.box(6.5, 1, 6.5, 9.5, 2, 9.5),
                Block.box(7, 2, 7, 9, 9, 9),
                Block.box(9, 4, 7, 14, 6, 9),
                Block.box(12, 6, 7, 14, 7, 9),
                Block.box(2, 4, 7, 7, 6, 9),
                Block.box(2, 6, 7, 4, 7, 9),
                VoxelShapes.join(Block.box(7, 10, 7, 9, 14, 9), Block.box(6.5, 9, 6.5, 9.5, 10, 9.5), IBooleanFunction.OR),
                VoxelShapes.join(Block.box(2, 8, 7, 4, 12, 9), Block.box(1.5, 7, 6.5, 4.5, 8, 9.5), IBooleanFunction.OR),
                VoxelShapes.join(Block.box(12, 8, 7, 14, 12, 9), Block.box(11.5, 7, 6.5, 14.5, 8, 9.5), IBooleanFunction.OR)
        ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElseGet(VoxelShapes::empty);
    }

    public CandelabraBlock() {
        super(AbstractBlock.Properties.of(Material.METAL).strength(2f).lightLevel(s -> 14).noOcclusion(), makeCandelabraShape());
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return canSupportCenter(worldIn, pos.below(), Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !this.canSurvive(stateIn, worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}
