package de.teamlapen.factions.common.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShapeUtil {

    /**
     * Rotates a VoxelShape around the Y-axis (vertical axis).<br>
     * <b>Cache the result.</b>
     * <p>
     * Credits to JTK222|Lukas
     */
    public static @NotNull VoxelShape rotateY(@NotNull VoxelShape shape, RotationAmount rotation) {
        Set<VoxelShape> rotatedShapes = new HashSet<>();

        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double x1 = minX - 8;
            double x2 = maxX - 8;
            double z1 = minZ - 8;
            double z2 = maxZ - 8;

            double nx1, nz1, nx2, nz2;

            switch (rotation) {
                case NINETY -> {
                    nx1 = -z2;
                    nx2 = -z1;
                    nz1 = x1;
                    nz2 = x2;
                }
                case HUNDRED_EIGHTY -> {
                    nx1 = -x2;
                    nx2 = -x1;
                    nz1 = -z2;
                    nz2 = -z1;
                }
                case TWO_HUNDRED_SEVENTY -> {
                    nx1 = z1;
                    nx2 = z2;
                    nz1 = -x2;
                    nz2 = -x1;
                }
                default -> {
                    nx1 = x1;
                    nx2 = x2;
                    nz1 = z1;
                    nz2 = z2;
                }
            }

            rotatedShapes.add(Block.box(nx1 + 8, minY, nz1 + 8, nx2 + 8, maxY, nz2 + 8));
        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::block);
    }

    /**
     * Rotates shape vertically (pitch) for NORTH/SOUTH facing.<br>
     * <b>Cache the result.</b>
     */
    public static @NotNull VoxelShape rotateXZ(@NotNull VoxelShape shape, @NotNull Direction direction) {
        Set<VoxelShape> rotatedShapes = new HashSet<>();

        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            switch (direction) {
                case NORTH -> rotatedShapes.add(Block.box(minX, minZ, 16 - maxZ, maxX, maxZ, 16 - minZ));
                case SOUTH -> rotatedShapes.add(Block.box(minX, 16 - maxZ, minZ, maxX, 16 - minZ, maxZ));
                default -> rotatedShapes.add(Block.box(minX, minY, minZ, maxX, maxY, maxZ));
            }
        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(Shapes::block);
    }

    public static Map<Direction, VoxelShape> getShapesRotatedFromNorth(VoxelShape shapeOnNorth) {
        return new EnumMap<>(Direction.class) {{
            put(Direction.NORTH, shapeOnNorth);
            put(Direction.WEST, rotateY(shapeOnNorth, RotationAmount.TWO_HUNDRED_SEVENTY));
            put(Direction.SOUTH, rotateY(shapeOnNorth, RotationAmount.HUNDRED_EIGHTY));
            put(Direction.EAST, rotateY(shapeOnNorth, RotationAmount.NINETY));
        }};
    }

    public static Pair<VoxelShape, VoxelShape> getShapesRotatedSymmetrically(VoxelShape northShape) {
        return Pair.of(northShape, rotateY(northShape, RotationAmount.NINETY));
    }

    public enum RotationAmount {
        NINETY,
        HUNDRED_EIGHTY,
        TWO_HUNDRED_SEVENTY
    }
}
