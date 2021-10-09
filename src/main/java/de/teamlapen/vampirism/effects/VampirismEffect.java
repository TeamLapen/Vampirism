package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.REFERENCE;
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
    public VampirismEffect(String name, MobEffectCategory effectType, int potionColor) {
        super(effectType, potionColor);
        this.setRegistryName(REFERENCE.MODID, name);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return (this == ModEffects.armor_regeneration || this == ModEffects.neonatal || this == ModEffects.disguise_as_vampire) ? Collections.emptyList() : super.getCurativeItems();
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entityLivingBaseIn, int amplifier) {
        if (this == ModEffects.armor_regeneration) {
            if (entityLivingBaseIn instanceof Player && entityLivingBaseIn.isAlive()) {
                VampirePlayer.getOpt((Player) entityLivingBaseIn).ifPresent(VampirePlayer::requestNaturalArmorUpdate);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return this == ModEffects.armor_regeneration && duration % 100 == 1;
    }
}
