package de.teamlapen.vampirism.common.entity.player.lord.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.common.tags.FactionSkillTreeTags;
import de.teamlapen.vampirism.common.entity.player.skills.VampirismSkill;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.tags.TagKey;

public class SimpleLordSkill<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends VampirismSkill<T> {

    public SimpleLordSkill(boolean hasDescription) {
        super(Either.right(FactionSkillTreeTags.LORD), hasDescription);
    }

    public SimpleLordSkill(int skillPointCost, boolean hasDescription) {
        super(Either.right(FactionSkillTreeTags.LORD), skillPointCost, hasDescription);
    }

    @Override
    public TagKey<? extends IFaction<?>> factions() {
        return ModFactionTags.HAS_LORD_SKILLS;
    }
}
