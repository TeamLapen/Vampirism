package de.teamlapen.faction.api.factions.lord;

import de.teamlapen.faction.api.world.entities.minion.IMinionEntry;
import de.teamlapen.faction.api.world.entities.minion.MinionStat;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;

public interface IMinionEntryBuilder<T extends IFactionPlayer<T>> {

    IMinionEntryBuilder<T> withStats(MinionStat... stat);

    IMinionEntryBuilder<T> maxLevel(int maxLevel);

    IMinionEntry<T> build();

}
