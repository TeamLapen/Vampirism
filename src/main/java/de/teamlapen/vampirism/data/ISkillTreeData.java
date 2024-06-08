package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public interface ISkillTreeData {

    static ISkillTreeData getData(Level level) {
        return EffectiveSide.get() == LogicalSide.SERVER ? ServerSkillTreeData.instance() : ClientSkillTreeData.instance(level);
    }

    Optional<SkillTreeConfiguration.SkillTreeNodeConfiguration> getNodeForSkill(Collection<Holder<ISkillTree>> availableTrees, Holder<ISkill<?>> skill);

    Optional<Holder<ISkillNode>> getParent(SkillTreeConfiguration.SkillTreeNodeConfiguration node);

    boolean isRoot(Collection<Holder<ISkillTree>> availableTrees, SkillTreeConfiguration.SkillTreeNodeConfiguration skill);

    Optional<Holder<ISkillNode>> getParent(SkillTreeConfiguration.SkillTreeNodeConfiguration current, SkillTreeConfiguration.SkillTreeNodeConfiguration node);

    SkillTreeConfiguration.SkillTreeNodeConfiguration getNode(Holder<ISkillTree> tree, Holder<ISkillNode> node);

    SkillTreeConfiguration.SkillTreeNodeConfiguration getNode(SkillTreeConfiguration.SkillTreeNodeConfiguration start, Holder<ISkillNode> node);

    SkillTreeConfiguration.SkillTreeNodeConfiguration root(Holder<ISkillTree> skillTree);

    @NotNull Optional<ISkillNode> getAnyLastNode(Holder<ISkillTree> tree, Function<ISkillNode, Boolean> isUnlocked);
}
