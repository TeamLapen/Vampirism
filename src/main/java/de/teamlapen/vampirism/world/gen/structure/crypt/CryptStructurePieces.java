package de.teamlapen.vampirism.world.gen.structure.crypt;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.core.ModStructures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import static de.teamlapen.vampirism.world.gen.structure.PoolExtensions.single;

public class CryptStructurePieces {
    public static final ResourceKey<StructureTemplatePool> START = ModStructures.createTemplatePool("crypt/church");

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> holdergetter1 = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> empty = holdergetter1.getOrThrow(Pools.EMPTY);
        context.register(START, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(single("crypt/church"), 1)), StructureTemplatePool.Projection.RIGID));
        CryptStructurePools.bootstrap(context);
    }
}
