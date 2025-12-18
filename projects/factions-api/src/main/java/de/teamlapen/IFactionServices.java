package de.teamlapen;

import de.teamlapen.factions.api.factions.IFactionPredicates;
import de.teamlapen.factions.api.factions.IFactionRegistry;

public interface IFactionServices {

    IFactionRegistry factionRegistry();

    IFactionPredicates factionPredicates();
}
