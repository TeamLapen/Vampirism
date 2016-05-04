package de.teamlapen.vampirism.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

/**
 * Potion effect which pretends to be night vision.
 */
public class FakeNightVisionPotionEffect extends PotionEffect {
    /**
     */
    public FakeNightVisionPotionEffect() {
        super(MobEffects.NIGHT_VISION, 10000, 0, false, false);
        setCurativeItems(new ArrayList<ItemStack>());
    }

    @Override
    public void combine(PotionEffect other) {
        //Don't change anything
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return p_equals_1_ == this;
    }

    @Override
    public String getEffectName() {
        return "Vampire " + "effect.nightVision";
    }

    @Override
    public boolean getIsPotionDurationMax() {
        return true;
    }

    @Override
    public boolean onUpdate(EntityLivingBase entityIn) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entityIn) {

    }

    @Override
    public NBTTagCompound writeCustomPotionEffectToNBT(NBTTagCompound nbt) {
        return nbt;
    }
}
