package de.teamlapen.vampirism.potion;


import net.minecraft.entity.EntityLivingBase;

public class PotionFreeze extends VampirismPotion {
    public PotionFreeze(String name) {
        super(name, true, 0xFFFFFF);
        this.setIconIndex(0, 1);
        this.setPotionName("action.vampirism.vampire.freeze");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        entityLivingBaseIn.motionX = 0;
        entityLivingBaseIn.motionY = 0;//Math.min(entityLivingBaseIn.motionY,0);
        entityLivingBaseIn.motionZ = 0;
    }
}
