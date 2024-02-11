package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Base class for Vampirism's potions
 */
public class VampirismEffect extends MobEffect {
    public VampirismEffect(@NotNull MobEffectCategory effectType, int potionColor) {
        super(effectType, potionColor);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        if (this != ModEffects.ARMOR_REGENERATION.get() && this != ModEffects.NEONATAL.get() && this != ModEffects.DISGUISE_AS_VAMPIRE.get()) {
            super.fillEffectCures(cures, effectInstance);
        }
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        if (this == ModEffects.ARMOR_REGENERATION.get()) {
            if (entityLivingBaseIn instanceof Player player && entityLivingBaseIn.isAlive()) {
                VampirePlayer.get(player).requestNaturalArmorUpdate();
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return this == ModEffects.ARMOR_REGENERATION.get() && duration % 100 == 1;
    }
}
