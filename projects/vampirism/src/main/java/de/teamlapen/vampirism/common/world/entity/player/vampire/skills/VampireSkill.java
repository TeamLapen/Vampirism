package de.teamlapen.vampirism.common.world.entity.player.vampire.skills;

import de.teamlapen.faction.api.factions.skills.SkillProperties;
import de.teamlapen.faction.common.factions.skills.Skill;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;

public class VampireSkill extends Skill<IVampirePlayer> {

    public VampireSkill(SkillProperties<IVampirePlayer> properties) {
        super(properties.factions(VampirismTags.Factions.IS_VAMPIRE));
    }
}
