package de.teamlapen.vampirism.common.world.entity.player.lord.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.tags.FactionSkillTreeTags;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.world.entity.player.skills.VampirismSkill;
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
