package de.teamlapen.vampirism.common.world.entity.player.vampire.skills;

import de.teamlapen.faction.api.factions.skills.SkillProperties;
import de.teamlapen.faction.api.tags.FactionSkillTreeTags;
import de.teamlapen.faction.common.factions.skills.Skill;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;

public class VampireLordSkill extends Skill<IVampirePlayer> {

    public VampireLordSkill(SkillProperties<IVampirePlayer> properties) {
        super(properties.factions(VampirismTags.Factions.IS_VAMPIRE).tree(FactionSkillTreeTags.LORD));
    }
}
