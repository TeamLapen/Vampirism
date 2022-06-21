package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;

public abstract class MinionRecoverySkill<T extends IFactionPlayer> extends VampirismSkill<T> {

    public MinionRecoverySkill() {
        this.setHasDefaultDescription();
    }

    @Override
    public ISkillType getType() {
        return SkillType.LORD;
    }
}
