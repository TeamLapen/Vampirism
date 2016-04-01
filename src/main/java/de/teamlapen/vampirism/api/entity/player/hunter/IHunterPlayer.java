package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

/**
 * Interface for the hunter player data
 * Attached to all players as capability
 */
public interface IHunterPlayer extends IFactionPlayer<IHunterPlayer>, IHunter, IMinionLord {


}
