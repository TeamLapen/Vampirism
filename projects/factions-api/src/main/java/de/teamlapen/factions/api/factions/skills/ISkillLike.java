package de.teamlapen.factions.api.factions.skills;

import de.teamlapen.factions.api.factions.actions.IAction;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;

/**
 * Interface representing objects that are effectively a skill. This is extended by {@link ISkill} and {@link IAction}
 */
public interface ISkillLike<T extends IFactionPlayer<T> & ISkillPlayer<T>> {

    /**
     * Get the object as skill
     */
    ISkill<T> asSkill();
}
