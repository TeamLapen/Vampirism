package de.teamlapen;

import de.teamlapen.factions.api.factions.IFactionPredicates;
import de.teamlapen.factions.api.factions.IFactionHelper;
import de.teamlapen.factions.api.factions.IFactionTags;

public interface IFactionServices {

    IFactionHelper factionHelper();

    IFactionPredicates factionPredicates();

    IFactionTags factionTags();
}
