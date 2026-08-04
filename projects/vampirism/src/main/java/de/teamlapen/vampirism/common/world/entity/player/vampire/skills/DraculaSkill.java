package de.teamlapen.vampirism.common.world.entity.player.vampire.skills;

import de.teamlapen.faction.api.factions.skills.SkillProperties;
import de.teamlapen.faction.common.factions.skills.Skill;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;

public class DraculaSkill extends Skill<IVampirePlayer> {

    public DraculaSkill(SkillProperties<IVampirePlayer> properties) {
        super(properties.factions(VampirismTags.Factions.IS_VAMPIRE).tree(ModSkillTreeTags.DRACULA));
    }
}
