package de.teamlapen.factions.api.skills;

import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;

public interface IActionSkill<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends ISkill<T> {

    default IAction<T> action() {
        return actionHolder().value();
    }

    Holder<? extends IAction<T>> actionHolder();
}
