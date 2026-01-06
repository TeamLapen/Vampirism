package de.teamlapen.faction.common.util;

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

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            x1 = (x1 * 16) - 8;
            x2 = (x2 * 16) - 8;
            y1 *= 16;
            y2 *= 16;
            z1 = (z1 * 16) - 8;
            z2 = (z2 * 16) - 8;

            double nx1, nz1, nx2, nz2;

            switch (rotation) {
                case NINETY -> {
                    nx1 = 8 - z1;
                    nz1 = 8 + x1;
                    nx2 = 8 - z2;
                    nz2 = 8 + x2;
                }
                case HUNDRED_EIGHTY -> {
                    nx1 = 8 - x1;
                    nz1 = 8 - z1;
                    nx2 = 8 - x2;
                    nz2 = 8 - z2;
                }
                case TWO_HUNDRED_SEVENTY -> {
                    nx1 = 8 + z1;
                    nz1 = 8 - x1;
                    nx2 = 8 + z2;
                    nz2 = 8 - x2;
                }
                default -> {
                    nx1 = x1;
                    nz1 = z1;
                    nx2 = x2;
                    nz2 = z2;
                }
            }

            rotatedShapes.add(blockBox(nx1, y1, nz1, nx2, y2, nz2));
        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(() -> Block.box(0, 0, 0, 16, 16, 16));
    }

    /**
     * Rotates shape vertically (pitch) for NORTH/SOUTH facing 90 degrees.<br>
     * <b>Cache the result.</b>
     */
    public static @NotNull VoxelShape rollShape(@NotNull VoxelShape shape, @NotNull Direction direction) {
        Set<VoxelShape> rotatedShapes = new HashSet<>();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            double yMin;
            double yMax;
            double zMin;
            double zMax;
            y1 = (y1 * 16) - 8;
            y2 = (y2 * 16) - 8;
            z1 = (z1 * 16) - 8;
            z2 = (z2 * 16) - 8;
            switch (direction) {
                case NORTH -> {
                    z1 = 8 - z1;
                    z2 = 8 - z2;
                    y1 = 8 + y1;
                    y2 = 8 + y2;
                    yMin = Math.min(y1, y2);
                    yMax = Math.max(y1, y2);
                    zMin = Math.min(z1, z2);
                    zMax = Math.max(z1, z2);
                    rotatedShapes.add(Block.box(x1 * 16, zMin, yMin, x2 * 16, zMax, yMax));
                }
                case SOUTH -> {
                    z1 = 8 + z1;
                    z2 = 8 + z2;
                    y1 = 8 - y1;
                    y2 = 8 - y2;
                    yMin = Math.min(y1, y2);
                    yMax = Math.max(y1, y2);
                    zMin = Math.min(z1, z2);
                    zMax = Math.max(z1, z2);
                    rotatedShapes.add(Block.box(x1 * 16, zMin, yMin, x2 * 16, zMax, yMax));
                }
            }

        });

        return rotatedShapes.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElseGet(() -> Block.box(0, 0, 0, 16, 16, 16));
    }

    public static @NotNull VoxelShape blockBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
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
