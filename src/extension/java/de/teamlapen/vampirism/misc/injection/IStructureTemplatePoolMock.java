package de.teamlapen.vampirism.misc.injection;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.misc.extension.IStructureTemplatePool;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

import java.util.List;

public interface IStructureTemplatePoolMock extends IStructureTemplatePool {
    @Override
    default List<Pair<StructurePoolElement, Integer>> getRawTemplates() {
        return List.of();
    }

    @Override
    default void setRawTemplates(List<Pair<StructurePoolElement, Integer>> rawTemplates) {

    }

    @Override
    default ObjectArrayList<StructurePoolElement> getTemplates() {
        return null;
    }
}
