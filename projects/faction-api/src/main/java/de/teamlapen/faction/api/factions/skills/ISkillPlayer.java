package de.teamlapen.faction.api.factions.skills;

import de.teamlapen.faction.api.factions.actions.IActionHandler;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;

public interface ISkillPlayer<T extends ISkillPlayer<T>> extends IFactionPlayer<T> {

    static <T extends ISkillPlayer<T>> Optional<T> get(Player player) {
        return FactionsApi.factionPlayerHandler(player).getCurrentSkillPlayer();
    }

    /**
     * @return The skill handler for this player
     */
    ISkillHandler<T> getSkillHandler();

    IActionHandler<T> getActionHandler();
}
