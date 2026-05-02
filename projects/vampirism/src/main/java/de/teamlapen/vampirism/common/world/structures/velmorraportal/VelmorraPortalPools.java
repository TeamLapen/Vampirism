package de.teamlapen.vampirism.common.world.structures.velmorraportal;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import static de.teamlapen.vampirism.common.core.ModStructures.createTemplatePool;
import static de.teamlapen.vampirism.common.world.structures.PoolExtensions.single;

public class VelmorraPortalPools {

    public static final ResourceKey<StructureTemplatePool> START = createTemplatePool("velmorra_portal");

    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> empty = templatePools.getOrThrow(Pools.EMPTY);

        context.register(START, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("velmorra_portal"), 1)), StructureTemplatePool.Projection.RIGID));
    }

}
