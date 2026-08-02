package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public abstract class CommonSkillTreeData implements ISkillTreeData {

    public abstract SkillTreeConfiguration getConfiguration(Holder<ISkillTree> tree);

    @Override
    public Optional<Holder<ISkillNode>> getParent(SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        SkillTreeConfiguration treeConfig = node.getTreeConfig();
        for (SkillTreeConfiguration.SkillTreeNodeConfiguration child : treeConfig.children()) {
            if (child == node) {
                return Optional.of(treeConfig.root());
            }
            var result = getParent(child, node);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Holder<ISkillNode>> getParent(SkillTreeConfiguration.SkillTreeNodeConfiguration current, SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        for (SkillTreeConfiguration.SkillTreeNodeConfiguration child : current.children()) {
            if (child == node) {
                return Optional.of(current.node());
            }
            var result = getParent(child, node);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    @Nullable
    @Override
    public SkillTreeConfiguration.SkillTreeNodeConfiguration getNode(SkillTreeConfiguration.SkillTreeNodeConfiguration start, Holder<ISkillNode> node) {
        if (start.node() == node) {
            return start;
        }
        for (SkillTreeConfiguration.SkillTreeNodeConfiguration child : start.children()) {
            SkillTreeConfiguration.SkillTreeNodeConfiguration result = getNode(child, node);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Optional<ISkillNode> getAnyLastNode(Holder<ISkillTree> tree, Function<ISkillNode, Boolean> isUnlocked) {
        SkillTreeConfiguration.SkillTreeNodeConfiguration root = root(tree);
        if (!isUnlocked.apply(root.node().value())) {
            return Optional.empty();
        }
        Queue<SkillTreeConfiguration.SkillTreeNodeConfiguration> queue = new ArrayDeque<>();
        queue.add(root);

        for (SkillTreeConfiguration.SkillTreeNodeConfiguration node = queue.poll(); node != null; node = queue.poll()) {
            List<SkillTreeConfiguration.SkillTreeNodeConfiguration> list = node.children().stream().filter(s -> isUnlocked.apply(s.node().value())).toList();
            if (!list.isEmpty()) {
                queue.addAll(list);
            } else if (!node.isRoot()) {
                return Optional.of(node.node().value());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<SkillTreeConfiguration.SkillTreeNodeConfiguration> getNodeForSkill(Holder<ISkillTree> skillTree, Holder<? extends ISkill<?>> skill) {
        if (skill.value().allowedSkillTrees().is(skillTree)) {
            return getConfiguration(skillTree).getNode(skill);
        }
        return Optional.empty();
    }

    @Override
    public boolean isRoot(Collection<Holder<ISkillTree>> availableTrees, SkillTreeConfiguration.SkillTreeNodeConfiguration skill) {
        return availableTrees.stream().map(this::getConfiguration).anyMatch(s -> s.root() == skill.node());
    }

    @Override
    public SkillTreeConfiguration.SkillTreeNodeConfiguration root(Holder<ISkillTree> skillTree) {
        SkillTreeConfiguration config = getConfiguration(skillTree);
        var root = new SkillTreeConfiguration.SkillTreeNodeConfiguration(config.root(), config.children(), true);
        root.setTreeConfig(config);
        return root;
    }

    @Nullable
    @Override
    public SkillTreeConfiguration.SkillTreeNodeConfiguration getNode(Holder<ISkillTree> tree, Holder<ISkillNode> node) {
        SkillTreeConfiguration configuration1 = getConfiguration(tree);
        for (SkillTreeConfiguration.SkillTreeNodeConfiguration child : configuration1.children()) {
            SkillTreeConfiguration.SkillTreeNodeConfiguration result = getNode(child, node);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Optional<SkillTreeConfiguration.SkillTreeNodeConfiguration> getNodeForSkill(Collection<Holder<ISkillTree>> availableTrees, Holder<? extends ISkill<?>> skill) {
        return availableTrees.stream().filter(tree -> skill.value().allowedSkillTrees().is(tree)).map(this::getConfiguration).flatMap(x -> x.getNode(skill).stream()).findAny();
    }
}
