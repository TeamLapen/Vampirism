package de.teamlapen.vampirism.mixin.client.accessor;

import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatsScreen.class)
public interface StatsScreenAccessor {

    @Accessor("stats")
    StatsCounter getStats();

    @Accessor("NO_VALUE_DISPLAY")
    static Component getNO_VALUE_DISPLAY() {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor("SLOT_SPRITE")
    static ResourceLocation getSLOT_SPRITE() {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor("HEADER_SPRITE")
    static ResourceLocation getHEADER_SPRITE() {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor("SORT_UP_SPRITE")
    static ResourceLocation getSORT_UP_SPRITE() {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor("SORT_DOWN_SPRITE")
    static ResourceLocation getSORT_DOWN_SPRITE() {
        throw new IllegalStateException("Mixin failed to apply");
    }

}
