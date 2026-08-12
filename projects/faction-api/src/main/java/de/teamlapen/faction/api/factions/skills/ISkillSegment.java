package de.teamlapen.faction.api.factions.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public interface ISkillSegment {

    Holder<ISkillTree> tree();

    List<Holder<? extends ISkill<?>>> skills();

    List<ResourceKey<ISkillSegment>> parents();

    List<ResourceKey<ISkillSegment>> lockingSegments();

    int priority();

    boolean containsSkill(Holder<? extends ISkill<?>> skill);

    default boolean isRoot() {
        return parents().isEmpty();
    }
}
