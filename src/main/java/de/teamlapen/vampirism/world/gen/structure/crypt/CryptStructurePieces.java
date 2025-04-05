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

import static de.teamlapen.vampirism.world.gen.structure.PoolExtensions.single;

public class CryptStructurePieces {
    public static final ResourceKey<StructureTemplatePool> START = ModStructures.createTemplatePool("crypt/church");

    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureProcessorList> processorLists = context.lookup(Registries.PROCESSOR_LIST);
        Holder<StructureProcessorList> cryptDegradation = processorLists.getOrThrow(ModStructures.CRYPT_DEGRADATION);

        HolderGetter<StructureTemplatePool> templatePools = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> empty = templatePools.getOrThrow(Pools.EMPTY);

        context.register(START, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("crypt/church", cryptDegradation), 1)), StructureTemplatePool.Projection.RIGID));
        CryptStructurePools.bootstrap(context);
    }
}
