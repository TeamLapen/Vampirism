package de.teamlapen.vampirism.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

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
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElseGet(VoxelShapes::empty);

    public static final VoxelShape tomb1 = Stream.of(
            Block.box(2, 0, 2, 14, 1, 7),
            Block.box(3, 1, 3, 13, 9, 6),
            Block.box(4, 9, 3, 12, 10, 6),
            Block.box(6, 10, 3, 10, 11, 6)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElseGet(VoxelShapes::empty);

    public static final VoxelShape tomb2 = Stream.of(
            Block.box(2, 0, 2, 14, 2, 7),
            Block.box(3, 2, 3, 13, 14, 6),
            Block.box(4, 14, 3, 12, 15, 6),
            Block.box(6, 15, 3, 10, 16, 6)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElseGet(VoxelShapes::empty);

    public static final VoxelShape tomb3 = Stream.of(
            Block.box(2, 0, 2, 14, 2, 10),
            Block.box(4, 2, 4, 12, 14, 8),
            Block.box(0, 14, 4, 16, 18, 8),
            Block.box(5, 18, 4, 11, 20, 8),
            Block.box(4, 20, 4, 12, 26, 8),
            Block.box(5, 26, 4, 11, 27, 8)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElseGet(VoxelShapes::empty);

    public static final VoxelShape tomb3_base = VoxelShapes.join(tomb3, VoxelShapes.block(), IBooleanFunction.AND);
    public static final VoxelShape tomb3_top = VoxelShapes.join(tomb3, VoxelShapes.block().move(0, 1, 0), IBooleanFunction.AND).move(0, -1, 0);

    public static final VoxelShape vampire_rack = Stream.of(
            Block.box(3, 0, 0, 13, 15.55, 3.23)
//            Block.box(5.7182181566259285, -0.0011571834945509063, 0.008185183993054679, 10.318027520627503, 0.9188046893057633, 3.228051738794157),
//            Block.box(3.573750981914033, 2.500150478299289, 0.007265222120253156, 4.493712854714342, 13.539692951903064, 3.2289717006669587),
//            Block.box(-0.2988448564966184, 7.626714566533807, 0.008185183993054679, 0.6211170163037032, 14.066447676136015, 3.228051738794157),
//            Block.box(15.415128660949735, 7.626714566533807, 0.008185183993054679, 16.335090533750048, 14.066447676136015, 3.228051738794157),
//            Block.box(11.542532822539087, 2.500150478299289, 0.007265222120253156, 12.462494695339403, 13.539692951903064, 3.2289717006669587),
//            Block.box(3.9579473209847276, 15.227625898153427, 0.007265222120253156, 12.053611801627493, 16.147587770953734, 3.2289717006669587),
//            Block.box(5.7182181566259285, 0.9188046893057633, 0.4681661203932155, 10.318027520627503, 15.249231308632396, 0.928147056793371),
//            Block.box(4.7982562838256175, 2.298747498506234, 0.4681661203932155, 5.7182181566259285, 15.249231308632396, 0.928147056793371),
//            Block.box(3.8782944110252924, 4.598652180507022, 0.4681661203932155, 4.7982562838256175, 15.249231308632396, 0.928147056793371),
//            Block.box(2.9583325382249885, 6.898556862507809, 0.4681661203932155, 3.8782944110252924, 13.869288499431917, 0.928147056793371),
//            Block.box(2.4983516018248277, 8.278499671708282, 0.4681661203932155, 2.9583325382249885, 12.489345690231445, 0.928147056793371),
//            Block.box(10.318027520627503, 2.298747498506234, 0.4681661203932155, 11.23798939342782, 15.249231308632396, 0.928147056793371),
//            Block.box(11.23798939342782, 4.598652180507022, 0.4681661203932155, 12.157951266228133, 15.249231308632396, 0.928147056793371),
//            Block.box(12.157951266228133, 6.898556862507809, 0.4681661203932155, 13.077913139028446, 13.869288499431917, 0.928147056793371),
//            Block.box(13.077913139028446, 8.278499671708282, 0.4681661203932155, 13.537894075428605, 12.489345690231445, 0.928147056793371),
//            Block.box(4.33827534742546, 4.138671244106866, 0.928147056793371, 11.697970329827974, 4.598652180507022, 2.768070802394),
//            Block.box(2.9583325382249885, 8.278499671708282, 0.928147056793371, 13.077913139028446, 8.73848060810844, 2.768070802394),
//            Block.box(2.9583325382249885, 12.418328099309697, 0.928147056793371, 13.077913139028446, 12.878309035709858, 2.768070802394)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.empty());

    public static final VoxelShape throneBottom = Stream.of(
            Block.box(2.0, 0, 2.2, 13.5, 10.4, 14),
            Block.box(2.0, 9, 1.2, 13.5, 16, 3),
            Block.box(0.5, 13.5, 2.2, 2.7, 15.5, 14.2),
            Block.box(13.3, 13.5, 2.2, 15.5, 15.5, 14.2)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.empty());

    public static final VoxelShape throneTop = Block.box(2.0, 0, 1.2, 13.5, 10, 3);

    public static final VoxelShape crossBottom = Stream.of(
            Block.box(1, 0, 1, 15, 2, 15),
            Block.box(3, 2, 3, 13, 3, 13),
            Block.box(6, 3, 6, 10, 16, 10)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElseGet(VoxelShapes::empty);

    public static final VoxelShape crossTop = Stream.of(
            Block.box(6, 0, 6, 10, 14, 10),
            Block.box(10, 3, 6, 16, 7, 10),
            Block.box(0, 3, 6, 6, 7, 10)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElseGet(VoxelShapes::empty);
}
