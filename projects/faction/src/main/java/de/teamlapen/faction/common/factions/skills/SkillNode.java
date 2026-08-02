package de.teamlapen.faction.common.factions.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A node for the skill tree. Can contain multiple skills which only can be activated exclusively to each other.
 */
public record SkillNode(List<Holder<? extends ISkill<?>>> skills, List<ResourceKey<ISkillNode>> lockingNodes) implements ISkillNode {

    public static final Codec<ISkillNode> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(inst ->
            inst.group(
                    ExtraCodecs.nonEmptyList(ISkill.CODEC.listOf()).fieldOf("skills").forGetter(ISkillNode::skills),
                    Codec.lazyInitialized(() -> ResourceKey.codec(FactionRegistries.Keys.SKILL_NODE)).listOf().optionalFieldOf("locking_nodes", List.of()).forGetter(ISkillNode::lockingNodes)
            ).apply(inst, SkillNode::new)
    ));

    public SkillNode(List<Holder<? extends ISkill<?>>> elements) {
        this(elements, new ArrayList<>());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public SkillNode(List<Holder<? extends ISkill<?>>> elements, Optional<List<ResourceKey<ISkillNode>>> lockingNodes) {
        this(elements, lockingNodes.orElse(List.of()));
    }


    @SafeVarargs
    public SkillNode(Holder<? extends ISkill<?>>... skill) {
        this(Arrays.asList(skill), new ArrayList<>());
    }

    @Override
    public boolean containsSkill(Holder<? extends ISkill<?>> skill) {
        //noinspection rawtypes,unchecked,deprecation
        return skills.stream().anyMatch(s -> s.is((Holder)skill));
    }

}
