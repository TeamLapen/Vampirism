package de.teamlapen.vampirism.common.world.entity.player.hunter.skills;

import de.teamlapen.faction.api.factions.skills.SkillProperties;
import de.teamlapen.faction.common.factions.skills.Skill;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;

public class HunterSkill extends Skill<IHunterPlayer> {

    public HunterSkill(SkillProperties<IHunterPlayer> properties) {
        super(properties.factions(VampirismTags.Factions.IS_HUNTER).tree(ModSkillTreeTags.HUNTER));
    }
}
