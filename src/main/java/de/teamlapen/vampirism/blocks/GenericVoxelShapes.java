package de.teamlapen.vampirism.blocks;

import net.minecraft.block.Block;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.stream.Stream;

/**
 * Holds voxel shapes of blocks that don't have their own class
 */
public class GenericVoxelShapes {

    public static VoxelShape makeCandelabraShape() {
        return Stream.of(
                Block.makeCuboidShape(6, 0, 6, 10, 1, 10),
                Block.makeCuboidShape(6.5, 1, 6.5, 9.5, 2, 9.5),
                Block.makeCuboidShape(7, 2, 7, 9, 9, 9),
                Block.makeCuboidShape(9, 4, 7, 14, 6, 9),
                Block.makeCuboidShape(12, 6, 7, 14, 7, 9),
                Block.makeCuboidShape(2, 4, 7, 7, 6, 9),
                Block.makeCuboidShape(2, 6, 7, 4, 7, 9),
                VoxelShapes.combineAndSimplify(Block.makeCuboidShape(7, 10, 7, 9, 14, 9), Block.makeCuboidShape(6.5, 9, 6.5, 9.5, 10, 9.5), IBooleanFunction.OR),
                VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 8, 7, 4, 12, 9), Block.makeCuboidShape(1.5, 7, 6.5, 4.5, 8, 9.5), IBooleanFunction.OR),
                VoxelShapes.combineAndSimplify(Block.makeCuboidShape(12, 8, 7, 14, 12, 9), Block.makeCuboidShape(11.5, 7, 6.5, 14.5, 8, 9.5), IBooleanFunction.OR)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.empty());
    }
}
