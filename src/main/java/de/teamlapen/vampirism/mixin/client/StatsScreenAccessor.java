package de.teamlapen.vampirism.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StatsScreen.class)
public interface StatsScreenAccessor {

    @Invoker("getColumnX")
    int invokeGetColumnX(int index);

    @Invoker("blitSlotIcon")
    void invokeBlitSlotIcon(GuiGraphics pGuiGraphics, int pX, int pY, ResourceLocation pSprite);

    @Accessor("stats")
    StatsCounter getStats();

    @Accessor("NO_VALUE_DISPLAY")
    static Component getNO_VALUE_DISPLAY() {
        throw new IllegalStateException("Mixin failed to apply");
    }

}
