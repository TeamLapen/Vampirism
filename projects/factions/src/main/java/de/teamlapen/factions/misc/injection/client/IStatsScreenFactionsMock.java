package de.teamlapen.factions.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IStatsScreen;
import net.minecraft.stats.StatsCounter;

@Deprecated
public interface IStatsScreenFactionsMock extends IStatsScreen {
    @Override
    default StatsCounter getStats() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
