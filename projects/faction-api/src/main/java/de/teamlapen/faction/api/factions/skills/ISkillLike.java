package de.teamlapen.faction.api.factions.skills;

import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;

/**
 * Interface representing objects that are effectively a skill. This is extended by {@link ISkill} and {@link IAction}
 */
public interface ISkillLike<T extends IFactionPlayer<T> & ISkillPlayer<T>> {

    /**
     * Get the object as skill
     */
    ISkill<T> asSkill();
}
