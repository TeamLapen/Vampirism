package de.teamlapen.vampirism.entity.player.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;

import java.util.List;
import java.util.Optional;

public record SkillTreeConfiguration(Holder<ISkillTree> skillTree, Holder<ISkillNode> root, List<SkillTreeNodeConfiguration> children) {

    public static final Codec<SkillTreeConfiguration> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    RegistryFixedCodec.create(VampirismRegistries.Keys.SKILL_TREE).fieldOf("skill_tree").forGetter(SkillTreeConfiguration::skillTree),
                    RegistryFixedCodec.create(VampirismRegistries.Keys.SKILL_NODE).fieldOf("node").forGetter(SkillTreeConfiguration::root),
                    SkillTreeNodeConfiguration.CODEC.listOf().fieldOf("children").forGetter(SkillTreeConfiguration::children)
            ).apply(inst, SkillTreeConfiguration::new)
    );

    public SkillTreeConfiguration(Holder<ISkillTree> skillTree, Holder<ISkillNode> root, SkillTreeNodeConfiguration... children) {
        this(skillTree, root, List.of(children));
    }

    public SkillTreeConfiguration {
        children.forEach(c -> c.setTreeConfig(this));
    }

    public boolean contains(ISkill<?> skill) {
        if (root.value().containsSkill(skill)) {
            return true;
        }
        for (SkillTreeNodeConfiguration child : children) {
            if (child.contains(skill)) {
                return true;
            }
        }
        return false;
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
                        RegistryFixedCodec.create(VampirismRegistries.Keys.SKILL_NODE).fieldOf("node").forGetter(SkillTreeNodeConfiguration::node),
                        Codec.lazyInitialized(() -> SkillTreeNodeConfiguration.CODEC).listOf().optionalFieldOf( "children", List.of()).forGetter(SkillTreeNodeConfiguration::children)
                ).apply(inst, SkillTreeNodeConfiguration::new)
        );

        private final Holder<ISkillNode> node;
        private final List<SkillTreeNodeConfiguration> children;
        private final boolean isRoot;
        private SkillTreeConfiguration treeConfig;

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

        public boolean contains(ISkill<?> skill) {
            if (node.value().containsSkill(skill)) {
                return true;
            }
            for (SkillTreeNodeConfiguration child : children) {
                if (child.contains(skill)) {
                    return true;
                }
            }
            return false;
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
}
