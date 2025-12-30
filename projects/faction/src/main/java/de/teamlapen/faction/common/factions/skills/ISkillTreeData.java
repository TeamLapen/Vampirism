package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public interface ISkillTreeData {

    static ISkillTreeData getData(Level level) {
        return EffectiveSide.get() == LogicalSide.SERVER ? ServerSkillTreeData.instance() : ClientSkillTreeData.instance(level);
    }

    Optional<SkillTreeConfiguration.SkillTreeNodeConfiguration> getNodeForSkill(Collection<Holder<ISkillTree>> availableTrees, Holder<ISkill<?>> skill);

    Optional<SkillTreeConfiguration.SkillTreeNodeConfiguration> getNodeForSkill(Holder<ISkillTree> skillTree, Holder<ISkill<?>> skill);

    Optional<Holder<ISkillNode>> getParent(SkillTreeConfiguration.SkillTreeNodeConfiguration node);

    boolean isRoot(Collection<Holder<ISkillTree>> availableTrees, SkillTreeConfiguration.SkillTreeNodeConfiguration skill);

    Optional<Holder<ISkillNode>> getParent(SkillTreeConfiguration.SkillTreeNodeConfiguration current, SkillTreeConfiguration.SkillTreeNodeConfiguration node);

    @Nullable
    SkillTreeConfiguration.SkillTreeNodeConfiguration getNode(Holder<ISkillTree> tree, Holder<ISkillNode> node);

    @Nullable
    SkillTreeConfiguration.SkillTreeNodeConfiguration getNode(SkillTreeConfiguration.SkillTreeNodeConfiguration start, Holder<ISkillNode> node);

    SkillTreeConfiguration.SkillTreeNodeConfiguration root(Holder<ISkillTree> skillTree);

    Optional<ISkillNode> getAnyLastNode(Holder<ISkillTree> tree, Function<ISkillNode, Boolean> isUnlocked);
}
