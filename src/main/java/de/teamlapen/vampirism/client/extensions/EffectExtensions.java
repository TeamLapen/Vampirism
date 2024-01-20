package de.teamlapen.vampirism.client.extensions;

import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import de.teamlapen.vampirism.effects.VampirismNightVisionPotion;
import de.teamlapen.vampirism.mixin.client.EffectRenderingInventoryScreenAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

public class EffectExtensions {

    public static final IClientMobEffectExtensions SANGUINARE = new IClientMobEffectExtensions() {

        @Override
        public boolean isVisibleInGui(MobEffectInstance instance) {
            return false;
        }

        @Override
        public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
            Component component = ((EffectRenderingInventoryScreenAccessor) screen).getEffectName(instance);
            graphics.drawString(screen.font, component, x + 10 + 18, y + 6, 16777215, true);
            graphics.drawString(screen.font, "**:**", x + 10 + 18, y + 6 + 10, 8355711, true);
            return true;
        }
    };

    public static final IClientMobEffectExtensions NIGHT_VISION = new IClientMobEffectExtensions() {
        @Override
        public boolean isVisibleInInventory(MobEffectInstance instance) {
            if (instance instanceof EffectInstanceWithSource withSource) {
                return !withSource.hasSource() || !VampirismNightVisionPotion.ID.equals(withSource.getSource());
            }
            return true;
        }

        @Override
        public boolean isVisibleInGui(MobEffectInstance instance) {
            if (instance instanceof EffectInstanceWithSource withSource) {
                return !withSource.hasSource() || !VampirismNightVisionPotion.ID.equals(withSource.getSource());
            }
            return true;
        }

        @Override
        public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
            return true;
        }

        @Override
        public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
            return true;
        }

        @Override
        public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
            return true;
        }
    };

    public static final IClientMobEffectExtensions POISON = new IClientMobEffectExtensions() {
        @Override
        public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
            Component component = ((MutableComponent) ((EffectRenderingInventoryScreenAccessor) screen).getEffectName(instance)).append(" - ").append(MobEffectUtil.formatDuration(instance, 1.0F, Minecraft.getInstance().level.tickRateManager().tickrate()));
            graphics.drawString(screen.font, component, x + 10 + 18, y + 6, 16777215, true);
            Component note = Component.translatable("effect.vampirism.wrong_equipment.note").withStyle(ChatFormatting.DARK_RED);
            graphics.drawString(screen.font, note, x + 10 + 18, y + 6 + 10, -1, true);
            return true;
        }
    };
}
