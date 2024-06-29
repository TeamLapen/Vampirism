package de.teamlapen.vampirism.entity.player.lord.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.core.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import net.minecraft.tags.TagKey;

public class SimpleLordSkill<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends VampirismSkill<T> {

    public SimpleLordSkill(boolean hasDescription) {
        super(Either.right(ModSkillTreeTags.LORD), hasDescription);
    }

    public SimpleLordSkill(int skillPointCost, boolean hasDescription) {
        super(Either.right(ModSkillTreeTags.LORD), skillPointCost, hasDescription);
    }

    @Override
    public TagKey<? extends IFaction<?>> factions() {
        return ModFactionTags.HAS_LORD_SKILLS;
    }
}
