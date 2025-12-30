package de.teamlapen.faction.common.factions;

import de.teamlapen.faction.api.factions.lord.ILordPlayerEntry;
import de.teamlapen.faction.api.factions.lord.ILordTitleProvider;

public record LordPlayerEntry(int maxLevel, ILordTitleProvider lordTitleFunction) implements ILordPlayerEntry {
}
