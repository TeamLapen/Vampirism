package de.teamlapen;

import de.teamlapen.faction.api.factions.IFactionPredicates;
import de.teamlapen.faction.api.factions.IFactionHelper;
import de.teamlapen.faction.api.factions.IFactionTags;

public interface IFactionServices {

    IFactionHelper factionHelper();

    IFactionPredicates factionPredicates();

    IFactionTags factionTags();
}
