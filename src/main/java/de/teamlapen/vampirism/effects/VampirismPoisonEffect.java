package de.teamlapen.vampirism.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.util.DamageHandler;
import net.minecraft.ChatFormatting;
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
        float damage = amplifier >= DEADLY_AMPLIFIER ? amplifier : Math.min(entityLivingBaseIn.getHealth() - 1, amplifier);
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
            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, PoseStack poseStack, int x, int y, int blitOffset) {
                Component component = ((MutableComponent) screen.getEffectName(instance)).append(" - ").append(MobEffectUtil.formatDuration(instance, 1.0F));
                screen.font.drawShadow(poseStack, component, (float)(x + 10 + 18), (float)(y + 6), 16777215);
                Component note = Component.translatable("effect.vampirism.wrong_equipment.note").withStyle(ChatFormatting.DARK_RED);
                screen.font.drawShadow(poseStack, note, (float)(x + 10 + 18), (float)(y + 6 + 10), -1);
                return true;
            }
        });
    }
}
