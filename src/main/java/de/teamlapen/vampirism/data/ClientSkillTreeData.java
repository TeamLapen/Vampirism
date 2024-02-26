package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeConfiguration;
import de.teamlapen.vampirism.network.ClientboundSkillTreePacket;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClientSkillTreeData implements ISkillTreeData {

    private final RegistryAccess access;
    private Map<ResourceKey<ISkillTree>, SkillTreeConfiguration> configuration = new HashMap<>();

    private static List<ClientboundSkillTreePacket.ConfigHolder> SERVER_DATA = null;
    private static ClientSkillTreeData cache;

    public static ClientSkillTreeData instance(Level level) {
        if (cache == null) {
            cache = new ClientSkillTreeData(level.registryAccess());
        }
        return cache;
    }

    public static void reset() {
        cache = null;
        SERVER_DATA = null;
    }

    public static void init(List<ClientboundSkillTreePacket.ConfigHolder> trees) {
        if (cache == null) {
            SERVER_DATA = trees;
        } else {
            SERVER_DATA = null;
            cache.initData(trees);
        }
    }

    private void initData(List<ClientboundSkillTreePacket.ConfigHolder> trees) {
        configuration.clear();
        configuration = trees.stream().map(s -> s.toConfiguration(this.access.registryOrThrow(VampirismRegistries.Keys.SKILL_TREE), this.access.registryOrThrow(VampirismRegistries.Keys.SKILL_NODE))).flatMap(s -> s.skillTree().unwrapKey().map(x -> Pair.of(x, s)).stream()).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public ClientSkillTreeData(RegistryAccess access) {
        this.access = access;
        if (SERVER_DATA != null) {
            initData(SERVER_DATA);
            SERVER_DATA = null;
        }
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

    public int getTreeWidth(@NotNull Holder<ISkillTree> tree) {
        SkillTreeConfiguration configuration1 = getConfiguration(tree);
        return getNodeWidth(root(configuration1.skillTree()));
    }

    public int getTreeHeight(@NotNull Holder<ISkillTree> tree) {
        SkillTreeConfiguration configuration1 = getConfiguration(tree);
        return getNodeHeight(root(configuration1.skillTree()));
    }

    public int getNodeWidth(SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        ISkillNode value = node.node().value();
        int count = value.skills().size();
        int max = count * 26 + ((count - 1) * 10);

        int children = !node.children().isEmpty() ? (node.children().size() - 1) * 30 : 0;
        for (var child : node.children()) {
            children += getNodeWidth(child);
        }

        return Math.max(max, children);
    }

    public int getNodeHeight(SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        int max = 0;
        for (var child : node.children()) {
            // this value should be synced with the skill screen renderer
            max = Math.max(max, getNodeHeight(child) + 60);
        }
        return max;
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
