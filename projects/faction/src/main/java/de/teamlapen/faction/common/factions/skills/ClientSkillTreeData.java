package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientSkillTreeData extends CommonSkillTreeData {

    private final RegistryAccess access;
    private Map<ResourceKey<ISkillTree>, SkillTreeConfiguration> configuration = new HashMap<>();

    @Nullable
    private static List<ClientboundSkillTreePacket.ConfigHolder> SERVER_DATA = null;
    @Nullable
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
        configuration = trees.stream().map(s -> s.toConfiguration(this.access.lookupOrThrow(FactionRegistries.Keys.SKILL_TREE), this.access.lookupOrThrow(FactionRegistries.Keys.SKILL_NODE))).flatMap(s -> s.skillTree().unwrapKey().map(x -> Pair.of(x, s)).stream()).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public ClientSkillTreeData(RegistryAccess access) {
        this.access = access;
        if (SERVER_DATA != null) {
            initData(SERVER_DATA);
            SERVER_DATA = null;
        }
    }

    @Override
    public SkillTreeConfiguration getConfiguration(Holder<ISkillTree> tree) {
        return this.configuration.get(tree.unwrapKey().orElseThrow());
    }

    public int getTreeWidth(Holder<ISkillTree> tree) {
        SkillTreeConfiguration configuration1 = getConfiguration(tree);
        return getNodeWidth(root(configuration1.skillTree()));
    }

    public int getTreeHeight(Holder<ISkillTree> tree) {
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


}
