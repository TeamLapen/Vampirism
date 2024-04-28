package de.teamlapen.vampirism.world.gen.structure.hunteroutpost;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import static de.teamlapen.vampirism.core.ModStructures.createTemplatePool;
import static de.teamlapen.vampirism.world.gen.structure.PoolExtensions.single;

public class BadlandsHunterOutpostPools {

    public static final ResourceKey<StructureTemplatePool> START = createTemplatePool("hunter_outpost/badlands/plate");
    public static final ResourceKey<StructureTemplatePool> PLATE1_SIDE1 = createTemplatePool("hunter_outpost/badlands/plate/plate_1/side_1");
    public static final ResourceKey<StructureTemplatePool> PLATE1_SIDE2 = createTemplatePool("hunter_outpost/badlands/plate/plate_1/side_2");
    public static final ResourceKey<StructureTemplatePool> PLATE1_SIDE3 = createTemplatePool("hunter_outpost/badlands/plate/plate_1/side_3");
    public static final ResourceKey<StructureTemplatePool> PLATE1_SIDE4 = createTemplatePool("hunter_outpost/badlands/plate/plate_1/side_4");
    public static final ResourceKey<StructureTemplatePool> PLATE2_SIDE1 = createTemplatePool("hunter_outpost/badlands/plate/plate_2/side_1");
    public static final ResourceKey<StructureTemplatePool> PLATE2_SIDE2 = createTemplatePool("hunter_outpost/badlands/plate/plate_2/side_2");
    public static final ResourceKey<StructureTemplatePool> PLATE2_SIDE3 = createTemplatePool("hunter_outpost/badlands/plate/plate_2/side_3");
    public static final ResourceKey<StructureTemplatePool> PLATE2_SIDE4 = createTemplatePool("hunter_outpost/badlands/plate/plate_2/side_4");
    public static final ResourceKey<StructureTemplatePool> PLATE3_SIDE1 = createTemplatePool("hunter_outpost/badlands/plate/plate_3/side_1");
    public static final ResourceKey<StructureTemplatePool> PLATE3_SIDE2 = createTemplatePool("hunter_outpost/badlands/plate/plate_3/side_2");
    public static final ResourceKey<StructureTemplatePool> PLATE3_SIDE3 = createTemplatePool("hunter_outpost/badlands/plate/plate_3/side_3");
    public static final ResourceKey<StructureTemplatePool> PLATE3_SIDE4 = createTemplatePool("hunter_outpost/badlands/plate/plate_3/side_4");
    public static final ResourceKey<StructureTemplatePool> WALL_STRAIGHT = createTemplatePool("hunter_outpost/badlands/wall/straight");
    public static final ResourceKey<StructureTemplatePool> WALL_STRAIGHT_LONG = createTemplatePool("hunter_outpost/badlands/wall/straight_long");
    public static final ResourceKey<StructureTemplatePool> WALL_CORNER = createTemplatePool("hunter_outpost/badlands/wall/corner");
    public static final ResourceKey<StructureTemplatePool> WALL_GATE = createTemplatePool("hunter_outpost/badlands/wall/gate");


    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);

        Holder<StructureTemplatePool> empty = templatePools.getOrThrow(Pools.EMPTY);

        context.register(START, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_1/base_1"), 1), Pair.of(single("hunter_outpost/badlands/plate/plate_2/base_1"), 1), Pair.of(single("hunter_outpost/badlands/plate/plate_2/base_2"), 1), Pair.of(single("hunter_outpost/badlands/plate/plate_2/base_3"), 1), Pair.of(single("hunter_outpost/badlands/plate/plate_2/base_4"), 1), Pair.of(single("hunter_outpost/badlands/plate/plate_3/base_1"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE1_SIDE1, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_1/side_1"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE1_SIDE2, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_1/side_2"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE1_SIDE3, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_1/side_3"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE1_SIDE4, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_1/side_4"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE2_SIDE1, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_2/side_1"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE2_SIDE2, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_2/side_2"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE2_SIDE3, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_2/side_3"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE2_SIDE4, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_2/side_4"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE3_SIDE1, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_3/side_1"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE3_SIDE2, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_3/side_2"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE3_SIDE3, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_3/side_3"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(PLATE3_SIDE4, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/plate/plate_3/side_4"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));

        context.register(WALL_STRAIGHT, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/wall/wall_straight"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(WALL_STRAIGHT_LONG, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/wall/wall_straight_long"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(WALL_CORNER, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/wall/wall_corner"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        context.register(WALL_GATE, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("hunter_outpost/badlands/wall/wall_gate"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
    }
}
