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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for Vampirism's potions
 */
public class VampirismEffect extends MobEffect {

    private boolean disableDefaultCures = false;
    private final Set<EffectCure> effectCures = new HashSet<>();

    public VampirismEffect(@NotNull MobEffectCategory effectType, int potionColor) {
        super(effectType, potionColor);
    }

    @Override
    public void fillEffectCures(@NotNull Set<EffectCure> cures, @NotNull MobEffectInstance effectInstance) {
        if (!disableDefaultCures) {
            super.fillEffectCures(cures, effectInstance);
        }
        cures.addAll(effectCures);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        if (this == ModEffects.ARMOR_REGENERATION.get()) {
            if (entityLivingBaseIn instanceof Player player && entityLivingBaseIn.isAlive()) {
                VampirePlayer.get(player).requestNaturalArmorUpdate();
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return this == ModEffects.ARMOR_REGENERATION.get() && duration % 100 == 1;
    }

    public VampirismEffect addEffectCures(EffectCure... cures) {
        effectCures.addAll(Arrays.asList(cures));
        return this;
    }

    public VampirismEffect disableDefaultCures() {
        disableDefaultCures = true;
        return this;
    }
}
