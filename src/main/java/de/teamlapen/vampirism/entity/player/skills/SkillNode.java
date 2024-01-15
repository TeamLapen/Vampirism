package de.teamlapen.vampirism.entity.player.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A node for the skill tree. Can contain multiple skills which only can be activated exclusively to each other.
 */
public record SkillNode(@NotNull List<Holder<ISkill<?>>> skills, @NotNull List<ResourceKey<ISkillNode>> lockingNodes) implements ISkillNode {

    public static final Codec<ISkillNode> CODEC = ExtraCodecs.lazyInitializedCodec(() ->RecordCodecBuilder.create(inst ->
            inst.group(
                    ExtraCodecs.nonEmptyList(ModRegistries.SKILLS.holderByNameCodec().listOf()).fieldOf("skills").forGetter(ISkillNode::skills),
                    ExtraCodecs.strictOptionalField(ExtraCodecs.lazyInitializedCodec(() -> ResourceKey.codec(VampirismRegistries.SKILL_NODE_ID)).listOf(), "locking_nodes", List.of()).forGetter(ISkillNode::lockingNodes)
            ).apply(inst, SkillNode::new)
    ));

    public SkillNode( @NotNull List<Holder<ISkill<?>>> elements) {
        this(elements, new ArrayList<>());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public SkillNode(@NotNull List<Holder<ISkill<?>>> elements, @NotNull Optional<List<ResourceKey<ISkillNode>>> lockingNodes) {
        this(elements, lockingNodes.orElse(List.of()));
    }


    @SafeVarargs
    public SkillNode(@NotNull Holder<ISkill<?>>... skill) {
        this(Arrays.asList(skill), new ArrayList<>());
    }

    @Override
    public boolean containsSkill(ISkill<?> skill) {
        return skills.stream().map(Holder::value).anyMatch(s -> s == skill);
    }

}
