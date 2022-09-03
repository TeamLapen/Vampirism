package de.teamlapen.vampirism.entity.player.lord.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SimpleLordSkill<T extends IFactionPlayer<T>> extends VampirismSkill<T> {

    public SimpleLordSkill(boolean hasDescription) {
        super(hasDescription);
    }

    public SimpleLordSkill(int skillPointCost, boolean hasDescription) {
        super(skillPointCost, hasDescription);
    }

    @Override
    public @NotNull Optional<IPlayableFaction<?>> getFaction() {
        return Optional.empty();
    }

    @Override
    public ISkillType getType() {
        return SkillType.LORD;
    }
}
