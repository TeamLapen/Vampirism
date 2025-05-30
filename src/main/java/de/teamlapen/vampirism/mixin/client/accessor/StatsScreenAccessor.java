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
    static Component getNoValueDisplay() {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor("SLOT_SPRITE")
    static ResourceLocation getSlotSprite() {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor("HEADER_SPRITE")
    static ResourceLocation getHeaderSprite() {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor("SORT_UP_SPRITE")
    static ResourceLocation getSortUpSprite() {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor("SORT_DOWN_SPRITE")
    static ResourceLocation getSortDownSprite() {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
