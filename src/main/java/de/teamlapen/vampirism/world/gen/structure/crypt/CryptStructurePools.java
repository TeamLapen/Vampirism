package de.teamlapen.vampirism.world.gen.structure.crypt;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import static de.teamlapen.vampirism.world.gen.structure.PoolExtensions.single;

public class CryptStructurePools {
    public static final ResourceKey<StructureTemplatePool> END = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(REFERENCE.MODID, "crypt/end"));
    public static final ResourceKey<StructureTemplatePool> CORRIDOR = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(REFERENCE.MODID, "crypt/structures"));
    public static final ResourceKey<StructureTemplatePool> STAIRS_1 = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(REFERENCE.MODID, "crypt/stairs1"));
    public static final ResourceKey<StructureTemplatePool> STAIRS_2 = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(REFERENCE.MODID, "crypt/stairs2"));
    public static final ResourceKey<StructureTemplatePool> STAIRS_BASE = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(REFERENCE.MODID, "crypt/stairs_base"));

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> end = templatePools.getOrThrow(END);
        Holder<StructureTemplatePool> empty = templatePools.getOrThrow(Pools.EMPTY);

        context.register(END, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("crypt/corridor/end"), 1)), StructureTemplatePool.Projection.RIGID));
        context.register(CORRIDOR, new StructureTemplatePool(end, ImmutableList.of(
                Pair.of(single("crypt/corridor/straight_1"), 1),
                Pair.of(single("crypt/corridor/straight_2"), 3),
                Pair.of(single("crypt/corridor/corner_1"), 2),
                Pair.of(single("crypt/corridor/cross_1"), 3),
                Pair.of(single("crypt/corridor/cross_2"), 3),
                Pair.of(single("crypt/corridor/room_1"), 2),
                Pair.of(single("crypt/corridor/room_2"), 2),
                Pair.of(single("crypt/corridor/room_3"), 2),
                Pair.of(single("crypt/corridor/room_4"), 2),
                Pair.of(single("crypt/corridor/stairs_1"), 3)
        ), StructureTemplatePool.Projection.RIGID));
        context.register(STAIRS_1, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("crypt/stairs1"), 1)), StructureTemplatePool.Projection.RIGID));
        context.register(STAIRS_2, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("crypt/stairs2"), 1)), StructureTemplatePool.Projection.RIGID));
        context.register(STAIRS_BASE, new StructureTemplatePool(end, ImmutableList.of(Pair.of(single("crypt/stairs_base"), 1)), StructureTemplatePool.Projection.RIGID));
    }
}
