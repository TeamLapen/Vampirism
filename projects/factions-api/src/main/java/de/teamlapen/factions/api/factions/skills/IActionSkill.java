package de.teamlapen.factions.api.factions.skills;

import de.teamlapen.factions.api.factions.actions.IAction;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;

/**
 * Base skill that unlocks an action
 */
public interface IActionSkill<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends ISkill<T> {

    default IAction<T> action() {
        return actionHolder().value();
    }

    Holder<? extends IAction<T>> actionHolder();
}
