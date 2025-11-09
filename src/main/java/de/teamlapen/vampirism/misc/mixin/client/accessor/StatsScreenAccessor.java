package de.teamlapen.vampirism.misc.mixin.client.accessor;

import de.teamlapen.vampirism.misc.extension.client.IStatsScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatsScreen.class)
public interface StatsScreenAccessor extends IStatsScreen {

    @Override
    @Accessor("stats")
    StatsCounter getStats();
}
