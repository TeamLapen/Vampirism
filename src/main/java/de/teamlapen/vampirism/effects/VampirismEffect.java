package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Base class for Vampirism's potions
 */
public class VampirismEffect extends MobEffect {
    public VampirismEffect(MobEffectCategory effectType, int potionColor) {
        super(effectType, potionColor);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return (this == ModEffects.ARMOR_REGENERATION.get() || this == ModEffects.NEONATAL.get() || this == ModEffects.DISGUISE_AS_VAMPIRE.get()) ? Collections.emptyList() : super.getCurativeItems();
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entityLivingBaseIn, int amplifier) {
        if (this == ModEffects.ARMOR_REGENERATION.get()) {
            if (entityLivingBaseIn instanceof Player && entityLivingBaseIn.isAlive()) {
                VampirePlayer.getOpt((Player) entityLivingBaseIn).ifPresent(VampirePlayer::requestNaturalArmorUpdate);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return this == ModEffects.ARMOR_REGENERATION.get() && duration % 100 == 1;
    }
}
