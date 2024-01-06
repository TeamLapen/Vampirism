package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ISkillNode {

    @NotNull
    List<Holder<ISkill<?>>> elements();

    @NotNull
    List<Holder<ISkillNode>> lockingNodes();

    boolean containsSkill(ISkill<?> skill);
}
