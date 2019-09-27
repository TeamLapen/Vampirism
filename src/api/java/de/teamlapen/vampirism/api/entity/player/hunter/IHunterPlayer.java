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

    @Override
    default IPlayableFaction<IHunterPlayer> getFaction() {
        return VReference.HUNTER_FACTION;
    }

}
