package de.teamlapen.vampirism.effects;


import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;

public class FreezeEffect extends VampirismEffect {
    public FreezeEffect() {
        super(EffectType.HARMFUL, 0xFFFFFF);
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
        entityLivingBaseIn.setDeltaMovement(0, Math.min(0, entityLivingBaseIn.getDeltaMovement().y()), 0);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return "action.vampirism.freeze";
    }
}
