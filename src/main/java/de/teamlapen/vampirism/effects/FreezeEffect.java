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
        if (entityLivingBaseIn.canFreeze()) {
            entityLivingBaseIn.setTicksFrozen(Math.max(entityLivingBaseIn.getTicksRequiredToFreeze() + 40, entityLivingBaseIn.getTicksFrozen()));
        }
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @NotNull
    @Override
    protected String getOrCreateDescriptionId() {
        return "action.vampirism.freeze";
    }
}
