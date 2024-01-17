package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

public interface ISkillLike<T extends IFactionPlayer<T>> {

    ISkill<T> asSkill();
}
