package de.teamlapen.vampirism.entity.player.lord.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.core.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SimpleLordSkill<T extends IFactionPlayer<T>> extends VampirismSkill<T> {

    public SimpleLordSkill(boolean hasDescription) {
        super(Either.right(ModSkillTreeTags.LORD), hasDescription);
    }

    public SimpleLordSkill(int skillPointCost, boolean hasDescription) {
        super(Either.right(ModSkillTreeTags.LORD), skillPointCost, hasDescription);
    }

    @Override
    public @NotNull Optional<IPlayableFaction<?>> getFaction() {
        return Optional.empty();
    }
}
