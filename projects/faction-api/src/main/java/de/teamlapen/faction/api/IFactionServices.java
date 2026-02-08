package de.teamlapen.faction.api;

import de.teamlapen.faction.api.factions.IFactionPredicates;
import de.teamlapen.faction.api.factions.IFactionHelper;
import de.teamlapen.faction.api.factions.IFactionSpecificTags;

public interface IFactionServices {

    IFactionHelper factionHelper();

    IFactionPredicates factionPredicates();

    IFactionSpecificTags factionTags();
}
