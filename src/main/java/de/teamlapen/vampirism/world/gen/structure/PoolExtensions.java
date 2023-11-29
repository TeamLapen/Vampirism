package de.teamlapen.vampirism.world.gen.structure;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.function.Function;

public class PoolExtensions {
    public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String id) {
        return SinglePoolElement.single(REFERENCE.MODID + ":" + id);
    }

    public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String id, Holder<StructureProcessorList> processorList) {
        return SinglePoolElement.single(REFERENCE.MODID + ":" + id, processorList);
    }
}
