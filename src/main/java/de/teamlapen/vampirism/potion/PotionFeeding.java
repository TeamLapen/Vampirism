package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionFeeding extends VampirismPotion {

    int victim;

    public PotionFeeding(String name, boolean badEffect, int potionColor, int victim) {
        super(name, badEffect, potionColor);
        this.victim = victim;
        registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "d9dd777b-46aa-4768-8bd4-3273bcb4121a", -0.7D, 2);
    }

    @Override
    public boolean isReady(int duration, int p_76397_2_) {
        if (duration % 20 == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int p_76394_2_) {
        Entity e = entity.getEntityWorld().getEntityByID(victim);
        if (e == null || !(e.getDistance(entity) <= entity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 1) || e instanceof  EntityPlayer) {
            entity.removePotionEffect(this);
            return;
        }
        PotionEffect effect = new PotionEffect(Potion.getPotionById(2), 1, 127);
        if (e instanceof EntityLivingBase) ((EntityLivingBase) e).addPotionEffect(effect);
        if (entity instanceof EntityPlayer) {
            VampirePlayer player = VampirePlayer.get((EntityPlayer) entity);
            player.biteEntity(victim);
        }
    }
}
