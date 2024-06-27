package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.ILordPlayerEntry;
import de.teamlapen.vampirism.api.entity.factions.ILordTitleProvider;

public record LordPlayerEntry(int maxLevel, ILordTitleProvider lordTitleFunction) implements ILordPlayerEntry {
}
