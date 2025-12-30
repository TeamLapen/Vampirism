package de.teamlapen.faction.common.factions.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.data.provider.base.SkillTreeProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record SkillTreeConfiguration(Holder<ISkillTree> skillTree, Holder<ISkillNode> root, List<SkillTreeNodeConfiguration> children, List<ResourceKey<ISkillTree>> orderAfter) {

    public static final Codec<SkillTreeConfiguration> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    RegistryFixedCodec.create(FactionRegistries.Keys.SKILL_TREE).fieldOf("skill_tree").forGetter(SkillTreeConfiguration::skillTree),
                    RegistryFixedCodec.create(FactionRegistries.Keys.SKILL_NODE).fieldOf("node").forGetter(SkillTreeConfiguration::root),
                    SkillTreeNodeConfiguration.CODEC.listOf().fieldOf("children").forGetter(SkillTreeConfiguration::children),
                    ResourceKey.codec(FactionRegistries.Keys.SKILL_TREE).listOf().optionalFieldOf("orderAfter", List.of()).forGetter(SkillTreeConfiguration::orderAfter)
            ).apply(inst, SkillTreeConfiguration::new)
    );

    public SkillTreeConfiguration(Holder<ISkillTree> skillTree, Holder<ISkillNode> root, SkillTreeNodeConfiguration... children) {
        this(skillTree, root, List.of(children), List.of());
    }

    public SkillTreeConfiguration {
        children.forEach(c -> c.setTreeConfig(this));
    }

    public static RootBuilder builder(Holder<ISkillTree> skillTree, Holder<ISkillNode> root) {
        return new RootBuilder(skillTree, root);
    }

    public Optional<SkillTreeNodeConfiguration> getNode(Holder<ISkill<?>> skill) {
        for (SkillTreeNodeConfiguration child : children) {
            var result = child.getNode(skill);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    public static final class SkillTreeNodeConfiguration {

        public static final Codec<SkillTreeNodeConfiguration> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        RegistryFixedCodec.create(FactionRegistries.Keys.SKILL_NODE).fieldOf("node").forGetter(SkillTreeNodeConfiguration::node),
                        Codec.lazyInitialized(() -> SkillTreeNodeConfiguration.CODEC).listOf().optionalFieldOf("children", List.of()).forGetter(SkillTreeNodeConfiguration::children)
                ).apply(inst, SkillTreeNodeConfiguration::new)
        );

        private final Holder<ISkillNode> node;
        private final List<SkillTreeNodeConfiguration> children;
        private final boolean isRoot;
        private @UnknownNullability SkillTreeConfiguration treeConfig;

        public SkillTreeNodeConfiguration(Holder<ISkillNode> node, List<SkillTreeNodeConfiguration> children, boolean isRoot) {
            this.node = node;
            this.children = children;
            this.isRoot = isRoot;
        }

        public SkillTreeNodeConfiguration(Holder<ISkillNode> node, List<SkillTreeNodeConfiguration> children) {
            this(node, children, false);
        }

        public SkillTreeNodeConfiguration(Holder<ISkillNode> node, SkillTreeNodeConfiguration... children) {
            this(node, List.of(children));
        }

        public int elementCount() {
            return node.value().skills().size();
        }

        public List<Holder<ISkill<?>>> elements() {
            return node.value().skills();
        }

        public int childrenCount() {
            return children.size();
        }

        public Holder<ISkillNode> node() {
            return node;
        }

        public List<SkillTreeNodeConfiguration> children() {
            return children;
        }

        public boolean isRoot() {
            return isRoot;
        }

        public void setTreeConfig(SkillTreeConfiguration treeConfig) {
            this.treeConfig = treeConfig;
            this.children.forEach(x -> x.setTreeConfig(treeConfig));
        }

        public SkillTreeConfiguration getTreeConfig() {
            return treeConfig;
        }

        public Optional<SkillTreeNodeConfiguration> getNode(Holder<ISkill<?>> skill) {
            if (node().value().containsSkill(skill)) {
                return Optional.of(this);
            }
            for (SkillTreeNodeConfiguration child : children) {
                var result = child.getNode(skill);
                if (result.isPresent()) {
                    return result;
                }
            }
            return Optional.empty();
        }
    }

    public static class RootBuilder {

        private final Holder<ISkillTree> skillTree;
        private final Holder<ISkillNode> root;
        private final List<Builder> children = new ArrayList<>();
        private final List<ResourceKey<ISkillTree>> after = new ArrayList<>();

        public RootBuilder(Holder<ISkillTree> skillTree, Holder<ISkillNode> root) {
            this.skillTree = skillTree;
            this.root = root;
        }

        public RootBuilder addNode(Builder node) {
            this.children.add(node);
            return this;
        }

        public RootBuilder addAfter(ResourceKey<ISkillTree> id) {
            this.after.add(id);
            return this;
        }

        public Identifier build(SkillTreeProvider.SkillTreeOutput output, Identifier id) {
            return output.accept(id, new SkillTreeConfiguration(this.skillTree, this.root, this.children.stream().map(Builder::build).toList(), Collections.unmodifiableList(this.after)));
        }

        public static class Builder {
            private final Holder<ISkillNode> node;
            private final List<Builder> children = new ArrayList<>();

            public Builder(Holder<ISkillNode> node) {
                this.node = node;
            }

            public Builder addNode(Builder builder) {
                children.add(builder);
                return this;
            }

            private SkillTreeNodeConfiguration build() {
                return new SkillTreeNodeConfiguration(this.node, this.children.stream().map(Builder::build).toArray(SkillTreeNodeConfiguration[]::new));
            }
        }
    }
}
