package de.teamlapen.vampirism.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

/**
 * Store voxel shapes for blocks without dedicated classes
 */
public class BlockVoxelshapes {
    public static final VoxelShape grave_cage = Stream.of(
            Block.box(0, 0, 0, 2, 10, 2),
            Block.box(0, 0, 6, 2, 10, 8),
            Block.box(0, 0, 12, 2, 10, 14),
            Block.box(14, 0, 0, 16, 10, 2),
            Block.box(14, 0, 6, 16, 10, 8),
            Block.box(14, 0, 12, 16, 10, 14),
            Block.box(9, 0, 0, 11, 10, 2),
            Block.box(5, 0, 0, 7, 10, 2),
            Block.box(0, 10, 0, 16, 12, 2),
            Block.box(14, 10, 2, 16, 12, 16),
            Block.box(0, 10, 2, 2, 12, 16),
            Block.box(0, 4, 2, 2, 6, 6),
            Block.box(0, 4, 8, 2, 6, 12),
            Block.box(0, 4, 14, 2, 6, 16),
            Block.box(14, 4, 2, 16, 6, 6),
            Block.box(14, 4, 8, 16, 6, 12),
            Block.box(14, 4, 14, 16, 6, 16),
            Block.box(9, 10, 6, 14, 12, 8),
            Block.box(2, 10, 6, 7, 12, 8),
            Block.box(7, 10, 2, 9, 12, 16),
            Block.box(9, 10, 12, 14, 12, 14),
            Block.box(2, 10, 12, 7, 12, 14),
            Block.box(11, 4, 0, 14, 6, 2),
            Block.box(2, 4, 0, 5, 6, 2),
            Block.box(7, 4, 0, 9, 6, 2)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);

    public static final VoxelShape tomb1 = Stream.of(
            Block.box(2, 0, 2, 14, 1, 7),
            Block.box(3, 1, 3, 13, 9, 6),
            Block.box(4, 9, 3, 12, 10, 6),
            Block.box(6, 10, 3, 10, 11, 6)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);

    public static final VoxelShape tomb2 = Stream.of(
            Block.box(2, 0, 2, 14, 2, 7),
            Block.box(3, 2, 3, 13, 14, 6),
            Block.box(4, 14, 3, 12, 15, 6),
            Block.box(6, 15, 3, 10, 16, 6)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);

    public static final VoxelShape tomb3 = Stream.of(
            Block.box(2, 0, 2, 14, 2, 10),
            Block.box(4, 2, 4, 12, 14, 8),
            Block.box(0, 14, 4, 16, 18, 8),
            Block.box(5, 18, 4, 11, 20, 8),
            Block.box(4, 20, 4, 12, 26, 8),
            Block.box(5, 26, 4, 11, 27, 8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);

}
