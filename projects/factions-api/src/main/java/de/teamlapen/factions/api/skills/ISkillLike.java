package de.teamlapen.factions.api.skills;

import de.teamlapen.factions.api.actions.IAction;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;

/**
 * Interface representing objects that are effectively a skill. This is extended by {@link ISkill} and {@link IAction}
 */
public interface ISkillLike<T extends IFactionPlayer<T> & ISkillPlayer<T>> {

    /**
     * Get the object as skill
     */
    ISkill<T> asSkill();
}
