package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.DamageHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


public class VampirismPoisonEffect extends VampirismEffect {

    public static final int DEADLY_AMPLIFIER = 4;

    public VampirismPoisonEffect(int potionColor) {
        super(MobEffectCategory.HARMFUL, potionColor);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        float damage = amplifier >= DEADLY_AMPLIFIER ? amplifier : Math.min(entityLivingBaseIn.getHealth() - 1, Math.max(1,amplifier));
        if (damage > 0) {
            DamageHandler.hurtVanilla(entityLivingBaseIn, DamageSources::magic, damage);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int j = 25 >> amplifier;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
                Component component = ((MutableComponent) screen.getEffectName(instance)).append(" - ").append(MobEffectUtil.formatDuration(instance, 1.0F));
                graphics.drawString(screen.font, component, x + 10 + 18, y + 6, 16777215, true);
                Component note = Component.translatable("effect.vampirism.wrong_equipment.note").withStyle(ChatFormatting.DARK_RED);
                graphics.drawString(screen.font, note, x + 10 + 18, y + 6 + 10, -1, true);
                return true;
            }
        });
    }

    public static MobEffectInstance createThrowableEffect() {
        return new MobEffectInstance(ModEffects.POISON.get(), 40, 1);
    }

    public static MobEffectInstance createEffectCloudEffect() {
        return new MobEffectInstance(ModEffects.POISON.get(), 60, 1);
    }
}
