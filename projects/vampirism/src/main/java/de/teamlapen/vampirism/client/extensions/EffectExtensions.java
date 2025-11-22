package de.teamlapen.vampirism.client.extensions;

import de.teamlapen.vampirism.api.VReference;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

public class EffectExtensions {

    public static final IClientMobEffectExtensions SANGUINARE = new IClientMobEffectExtensions() {

        @Override
        public boolean isVisibleInGui(MobEffectInstance instance) {
            return false;
        }

        @Override
        public boolean renderInventoryText(MobEffectInstance instance, AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
            MutableComponent component = instance.getEffect().value().getDisplayName().copy();
            guiGraphics.drawString(screen.getFont(), component, x + 10 + 18, y + 6, 16777215);
            guiGraphics.drawString(screen.getFont(), Component.translatable("effect.duration.infinite"), x + 10 + 18, y + 6 + 10, 8355711);
            return true;
        }
    };

    public static final IClientMobEffectExtensions NIGHT_VISION = new IClientMobEffectExtensions() {

        @Override
        public boolean isVisibleInInventory(MobEffectInstance instance) {
            return !instance.factions$hasProperty(VReference.PERMANENT_INVISIBLE_MOB_EFFECT);
        }

        @Override
        public boolean isVisibleInGui(MobEffectInstance instance) {
            return !instance.factions$hasProperty(VReference.PERMANENT_INVISIBLE_MOB_EFFECT);
        }

        @Override
        public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
            return true;
        }
    };

    public static final IClientMobEffectExtensions POISON = new IClientMobEffectExtensions() {

//        @Override
//        public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
//            Component component = ((MutableComponent) ((EffectRenderingInventoryScreenAccessor) screen).invoke_getEffectName(instance)).append(" - ").append(MobEffectUtil.formatDuration(instance, 1.0F, Minecraft.getInstance().level.tickRateManager().tickrate()));
//            graphics.drawString(screen.font, component, x + 10 + 18, y + 6, 16777215, true);
//            Component note = Component.translatable("effect.vampirism.wrong_equipment.note").withStyle(ChatFormatting.DARK_RED);
//            graphics.drawString(screen.font, note, x + 10 + 18, y + 6 + 10, -1, true);
//            return true;
//        }
    };
}
