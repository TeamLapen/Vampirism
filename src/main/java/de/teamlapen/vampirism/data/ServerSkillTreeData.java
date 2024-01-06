package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServerSkillTreeData implements ISkillTreeData {

    private static ServerSkillTreeData INSTANCE = new ServerSkillTreeData(List.of());

    public static void init(List<SkillTreeConfiguration> trees) {
        INSTANCE = new ServerSkillTreeData(trees);
    }

    public static ServerSkillTreeData instance() {
        return INSTANCE;
    }

    private final Map<ResourceKey<ISkillTree>, SkillTreeConfiguration> configuration;

    public ServerSkillTreeData(List<SkillTreeConfiguration> trees) {
        this.configuration = trees.stream().flatMap(s -> s.skillTree().unwrapKey().map(x -> Pair.of(x, s)).stream()).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public List<SkillTreeConfiguration> getConfigurations() {
        return configuration.values().stream().toList();
    }

    @Override
    public Optional<SkillTreeConfiguration.SkillTreeNodeConfiguration> getNodeForSkill(Collection<Holder<ISkillTree>> availableTrees, ISkill<?> skill) {
        return availableTrees.stream().filter(tree -> skill.allowedSkillTrees().map(tree::is, tree::is)).map(this::getConfiguration).flatMap(x -> x.getNode(skill).stream()).findAny();
    }

    @Override
    public Optional<Holder<ISkillNode>> getParent(SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        SkillTreeConfiguration treeConfig = node.getTreeConfig();
        for (SkillTreeConfiguration.SkillTreeNodeConfiguration child : treeConfig.children()) {
            if (child == node) {
                return Optional.ofNullable(treeConfig.root());
            }
            var result = getParent(child, node);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isRoot(Collection<Holder<ISkillTree>> availableTrees, SkillTreeConfiguration.SkillTreeNodeConfiguration skill) {
        return availableTrees.stream().map(this::getConfiguration).anyMatch(s -> s.root() == skill.node());
    }

    @Override
    public Optional<Holder<ISkillNode>> getParent(SkillTreeConfiguration.SkillTreeNodeConfiguration current, SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        for (SkillTreeConfiguration.SkillTreeNodeConfiguration child : current.children()) {
            if (child == node) {
                return Optional.ofNullable(current.node());
            }
            var result = getParent(child, node);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    public SkillTreeConfiguration getConfiguration(Holder<ISkillTree> tree) {
        return configuration.get(tree.unwrapKey().orElseThrow());
    }

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
    public SkillTreeConfiguration.SkillTreeNodeConfiguration root(Holder<ISkillTree> skillTree) {
        SkillTreeConfiguration config = getConfiguration(skillTree);
        var root = new SkillTreeConfiguration.SkillTreeNodeConfiguration(config.root(), config.children(), true);
        root.setTreeConfig(config);
        return root;
    }

    @Override
    public @NotNull Optional<ISkillNode> getAnyLastNode(Holder<ISkillTree> tree, Function<ISkillNode, Boolean> isUnlocked) {
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
}
