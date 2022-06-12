package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.util.Collections;
import java.util.List;

/**
 * Base class for Vampirism's potions
 */
public class VampirismEffect extends Effect {
    public VampirismEffect(EffectType effectType, int potionColor) {
        super(effectType, potionColor);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return (this == ModEffects.ARMOR_REGENERATION.get() || this == ModEffects.NEONATAL.get() || this == ModEffects.DISGUISE_AS_VAMPIRE.get()) ? Collections.emptyList() : super.getCurativeItems();
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
        if (this == ModEffects.ARMOR_REGENERATION.get()) {
            if (entityLivingBaseIn instanceof PlayerEntity) {
                VampirePlayer.getOpt((PlayerEntity) entityLivingBaseIn).ifPresent(VampirePlayer::requestNaturalArmorUpdate);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return this == ModEffects.ARMOR_REGENERATION.get() && duration % 100 == 1;
    }
}
