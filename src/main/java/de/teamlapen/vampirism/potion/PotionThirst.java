package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;


public class PotionThirst extends VampirismPotion {
    public PotionThirst(String name, boolean badEffect, int potionColor) {
        super(name, badEffect, potionColor);
        setIconIndex(0, 0);
        registerPotionAttributeModifier(VReference.bloodExhaustion, "f6d9889e-dfdc-11e5-b86d-9a79f06e9478", 0.5F, 1);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity instanceof EntityPlayer) {
            VampirePlayer.get((EntityPlayer) entity).addExhaustion(0.005F * (amplifier + 1));
        }
    }
}
