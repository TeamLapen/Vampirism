package de.teamlapen.vampirism.potion;


import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;

public class PotionFreeze extends VampirismPotion {
    public PotionFreeze(String name) {
        super(name, EffectType.HARMFUL, 0xFFFFFF);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        entityLivingBaseIn.setMotion(0, Math.min(0, entityLivingBaseIn.getMotion().getY()), 0);
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return "action.vampirism.freeze";
    }
}
