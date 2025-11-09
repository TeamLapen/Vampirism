package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IStatsScreen;
import net.minecraft.stats.StatsCounter;

public interface IStatsScreenMock extends IStatsScreen {
    @Override
    default StatsCounter getStats() {
        return null;
    }
}
