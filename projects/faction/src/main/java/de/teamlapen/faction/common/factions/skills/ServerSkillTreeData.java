package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.factions.skills.ISkillTree;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerSkillTreeData extends CommonSkillTreeData {

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
    public SkillTreeConfiguration getConfiguration(Holder<ISkillTree> tree) {
        return this.configuration.get(tree.unwrapKey().orElseThrow());
    }


}
