package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

/**
 * Interface for the hunter player data
 * Attached to all players as capability
 */
public interface IHunterPlayer extends IFactionPlayer<IHunterPlayer>, IHunter {

    /**
     * Call when the player does something that would break their disguise (Call regardless of the current disguise state)
     */
    void breakDisguise();

    @Override
    default IPlayableFaction<IHunterPlayer> getFaction() {
        return VReference.HUNTER_FACTION;
    }

    /**
     * updates attributes of all minions
     */
    void updateMinionAttributes(boolean increasedStats);
}
