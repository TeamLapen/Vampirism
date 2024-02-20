package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;

public interface IActionSkill<T extends IFactionPlayer<T>> extends ISkill<T> {

    IAction<T> action();
}
