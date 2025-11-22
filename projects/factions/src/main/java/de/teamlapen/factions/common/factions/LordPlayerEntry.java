package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.factions.lord.ILordPlayerEntry;
import de.teamlapen.factions.api.factions.lord.ILordTitleProvider;

public record LordPlayerEntry(int maxLevel, ILordTitleProvider lordTitleFunction) implements ILordPlayerEntry {
}
