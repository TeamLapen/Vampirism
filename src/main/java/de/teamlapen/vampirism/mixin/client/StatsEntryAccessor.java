package de.teamlapen.vampirism.mixin.client;

import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatsScreen.GeneralStatisticsList.Entry.class)
public interface StatsEntryAccessor {

    @Mutable
    @Accessor("statDisplay")
    void setStatDisplay(Component statDisplay);

    @Accessor("stat")
    Stat<ResourceLocation> getStat();
}
