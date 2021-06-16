package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
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
    public VampirismEffect(String name, EffectType effectType, int potionColor) {
        super(effectType, potionColor);
        this.setRegistryName(REFERENCE.MODID, name);
    }

    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        if(this == ModEffects.armor_regeneration){
            if(entityLivingBaseIn instanceof PlayerEntity){
                VampirePlayer.getOpt((PlayerEntity) entityLivingBaseIn).ifPresent(VampirePlayer::requestNaturalArmorUpdate);
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        if(this== ModEffects.armor_regeneration&& duration%100==1){
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return (this == ModEffects.armor_regeneration || this == ModEffects.neonatal || this == ModEffects.disguise_as_vampire) ? Collections.emptyList() : super.getCurativeItems();
    }
}
