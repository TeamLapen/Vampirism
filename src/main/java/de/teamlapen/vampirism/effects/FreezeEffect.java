package de.teamlapen.vampirism.effects;


import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class FreezeEffect extends VampirismEffect {
    public FreezeEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        entityLivingBaseIn.setDeltaMovement(0, Math.min(0, entityLivingBaseIn.getDeltaMovement().y()), 0);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @NotNull
    @Override
    protected String getOrCreateDescriptionId() {
        return "action.vampirism.freeze";
    }
}
