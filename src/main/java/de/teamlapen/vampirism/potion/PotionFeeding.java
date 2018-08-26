package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionFeeding extends VampirismPotion {
    public static PotionFeeding POTION = new PotionFeeding("Feeding", false, Integer.parseInt("FF0000", 16));

    public PotionFeeding(String name, boolean badEffect, int potionColor) {
        super(name, badEffect, potionColor);
        registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "d9dd777b-46aa-4768-8bd4-3273bcb4121a", -0.7D, 2);
    }
}
