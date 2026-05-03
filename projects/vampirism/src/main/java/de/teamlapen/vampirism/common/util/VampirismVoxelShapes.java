package de.teamlapen.vampirism.common.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

/**
 * Store voxel shapes for blocks without dedicated classes
 */
public class VampirismVoxelShapes {

    public static final VoxelShape GRAVE_CAGE = Stream.of(
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

    public static final VoxelShape TOMBSTONE_SHORT = Shapes.join(Block.box(3, 2, 3, 13, 12, 7), Block.box(2, 0, 2, 14, 2, 8), BooleanOp.OR);
    public static final VoxelShape TOMBSTONE_MEDIUM = Shapes.join(Block.box(2, 0, 2, 14, 2, 8), Block.box(3, 2, 3, 13, 16, 7), BooleanOp.OR);
    public static final VoxelShape TOMBSTONE_CROSS = Stream.of(
            Block.box(5, 23, 2, 11, 29, 8),
            Block.box(2, 0, 1, 14, 2, 9),
            Block.box(5, 2, 2, 11, 17, 8),
            Block.box(0, 17, 2, 16, 23, 8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape TOMBSTONE_CROSS_BOTTOM = Shapes.join(TOMBSTONE_CROSS, Shapes.block(), BooleanOp.AND);
    public static final VoxelShape TOMBSTONE_CROSS_TOP = Shapes.join(TOMBSTONE_CROSS, Shapes.block().move(0, 1, 0), BooleanOp.AND).move(0, -1, 0);

    public static final VoxelShape VAMPIRE_RACK = Block.box(3, 0, 0, 13, 15.55, 3.23);

    public static final VoxelShape CROSS_BOTTOM = Stream.of(
            Block.box(1, 0, 1, 15, 2, 15),
            Block.box(3, 2, 3, 13, 3, 13),
            Block.box(6, 3, 6, 10, 16, 10)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);

    public static final VoxelShape CROSS_TOP = Stream.of(
            Block.box(6, 0, 6, 10, 14, 10),
            Block.box(10, 3, 6, 16, 7, 10),
            Block.box(0, 3, 6, 6, 7, 10)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::empty);
}
