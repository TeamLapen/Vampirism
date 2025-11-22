package de.teamlapen.factions.api.skills;

import de.teamlapen.factions.api.actions.IActionHandler;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;

public interface ISkillPlayer<T extends ISkillPlayer<T>> extends IFactionPlayer<T> {

    /**
     * @return The skill handler for this player
     */
    ISkillHandler<T> getSkillHandler();

    IActionHandler<T> getActionHandler();
}
