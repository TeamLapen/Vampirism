package de.teamlapen.vampirism.misc.extension;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

import java.util.List;

public interface IStructureTemplatePool {

    List<Pair<StructurePoolElement, Integer>> getRawTemplates();

    void setRawTemplates(List<Pair<StructurePoolElement, Integer>> rawTemplates);

    ObjectArrayList<StructurePoolElement> getTemplates();
}
