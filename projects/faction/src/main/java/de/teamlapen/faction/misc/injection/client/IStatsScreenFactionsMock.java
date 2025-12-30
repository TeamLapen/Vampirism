package de.teamlapen.faction.misc.injection.client;

import de.teamlapen.faction.misc.extensions.client.IStatsScreen;
import net.minecraft.stats.StatsCounter;

@Deprecated
public interface IStatsScreenFactionsMock extends IStatsScreen {
    @Override
    default StatsCounter getStats() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
