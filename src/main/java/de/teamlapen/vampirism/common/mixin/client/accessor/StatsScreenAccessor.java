package de.teamlapen.vampirism.common.mixin.client.accessor;

import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatsScreen.class)
public interface StatsScreenAccessor {

    @Accessor("stats")
    StatsCounter getStats();
}
