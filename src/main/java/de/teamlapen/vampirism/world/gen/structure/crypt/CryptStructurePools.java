package de.teamlapen.vampirism.world.gen.structure.crypt;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.core.ModStructures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import static de.teamlapen.vampirism.core.ModStructures.createTemplatePool;
import static de.teamlapen.vampirism.world.gen.structure.PoolExtensions.single;

public class CryptStructurePools {
    public static final ResourceKey<StructureTemplatePool> END = createTemplatePool("crypt/end");
    public static final ResourceKey<StructureTemplatePool> CORRIDOR = createTemplatePool("crypt/structures");
    public static final ResourceKey<StructureTemplatePool> STAIRS_1 = createTemplatePool("crypt/stairs1");
    public static final ResourceKey<StructureTemplatePool> STAIRS_2 = createTemplatePool("crypt/stairs2");
    public static final ResourceKey<StructureTemplatePool> STAIRS_BASE = createTemplatePool("crypt/stairs_base");
    public static final ResourceKey<StructureTemplatePool> COFFINS = createTemplatePool("crypt/coffins");
    public static final ResourceKey<StructureTemplatePool> LARGE_COFFINS = createTemplatePool("crypt/large_coffins");
    public static final ResourceKey<StructureTemplatePool> HANGING_LOOT = createTemplatePool("crypt/hanging_loot");

    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureProcessorList> processorLists = context.lookup(Registries.PROCESSOR_LIST);
        Holder<StructureProcessorList> cryptDegradation = processorLists.getOrThrow(ModStructures.CRYPT_DEGRADATION);

        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> end = templatePools.getOrThrow(END);
        Holder<StructureTemplatePool> empty = templatePools.getOrThrow(Pools.EMPTY);

        context.register(END, new StructureTemplatePool(end, ImmutableList.of(Pair.of(single("crypt/corridor/end", cryptDegradation), 1)), StructureTemplatePool.Projection.RIGID));
        context.register(CORRIDOR, new StructureTemplatePool(end, ImmutableList.of(
                Pair.of(single("crypt/corridor/straight_1", cryptDegradation), 2),
                Pair.of(single("crypt/corridor/straight_2", cryptDegradation), 3),
                Pair.of(single("crypt/corridor/corner_1", cryptDegradation), 2),
                Pair.of(single("crypt/corridor/cross_1", cryptDegradation), 2),
                Pair.of(single("crypt/corridor/cross_2", cryptDegradation), 1),
                Pair.of(single("crypt/corridor/room_1", cryptDegradation), 2),
                Pair.of(single("crypt/corridor/room_2", cryptDegradation), 3),
                Pair.of(single("crypt/corridor/room_3", cryptDegradation), 3),
                Pair.of(single("crypt/corridor/room_4", cryptDegradation), 3),
                Pair.of(single("crypt/corridor/stairs_1", cryptDegradation), 3)
        ), StructureTemplatePool.Projection.RIGID));
        context.register(STAIRS_1, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("crypt/stairs1", cryptDegradation), 1)), StructureTemplatePool.Projection.RIGID));
        context.register(STAIRS_2, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("crypt/stairs2", cryptDegradation), 1)), StructureTemplatePool.Projection.RIGID));
        context.register(STAIRS_BASE, new StructureTemplatePool(end, ImmutableList.of(Pair.of(single("crypt/stairs_base", cryptDegradation), 1)), StructureTemplatePool.Projection.RIGID));
        context.register(COFFINS, new StructureTemplatePool(templatePools.getOrThrow(COFFINS), ImmutableList.of(
                Pair.of(single("crypt/coffin/coffin_1"), 1),
                Pair.of(single("crypt/coffin/coffin_2"), 1),
                Pair.of(single("crypt/coffin/coffin_3"), 1),
                Pair.of(single("crypt/coffin/coffin_4"), 1),
                Pair.of(single("crypt/coffin/coffin_5"), 1),
                Pair.of(single("crypt/coffin/coffin_6"), 1)
        ), StructureTemplatePool.Projection.RIGID));
        context.register(LARGE_COFFINS, new StructureTemplatePool(templatePools.getOrThrow(LARGE_COFFINS), ImmutableList.of(
                Pair.of(single("crypt/large_coffin/large_coffin_1"), 2),
                Pair.of(single("crypt/large_coffin/large_coffin_2"), 1),
                Pair.of(single("crypt/large_coffin/large_coffin_3"), 1)
        ), StructureTemplatePool.Projection.RIGID));
        context.register(HANGING_LOOT, new StructureTemplatePool(empty, ImmutableList.of(
                Pair.of(single("crypt/hanging_loot/barrel"), 3),
                Pair.of(single("crypt/hanging_loot/nothing"), 2)
        ), StructureTemplatePool.Projection.RIGID));
    }
}
