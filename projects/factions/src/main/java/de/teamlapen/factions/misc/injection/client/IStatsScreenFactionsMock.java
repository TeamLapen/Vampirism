package de.teamlapen.factions.misc.injection.client;

import de.teamlapen.factions.misc.extensions.client.IStatsScreen;
import net.minecraft.stats.StatsCounter;

public interface IStatsScreenFactionsMock extends IStatsScreen {
    @Override
    default StatsCounter getStats() {
        return null;
    }
}
