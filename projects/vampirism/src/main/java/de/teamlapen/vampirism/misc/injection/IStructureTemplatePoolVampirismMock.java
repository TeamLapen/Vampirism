package de.teamlapen.vampirism.misc.injection;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.misc.extension.IStructureTemplatePool;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

import java.util.List;

@Deprecated
public interface IStructureTemplatePoolVampirismMock extends IStructureTemplatePool {
    @Override
    default List<Pair<StructurePoolElement, Integer>> getRawTemplates() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void setRawTemplates(List<Pair<StructurePoolElement, Integer>> rawTemplates) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default ObjectArrayList<StructurePoolElement> getTemplates() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
