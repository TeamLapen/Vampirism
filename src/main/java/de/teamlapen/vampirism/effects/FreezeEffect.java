package de.teamlapen.vampirism.effects;


import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public class FreezeEffect extends ConfigAwareEffect {


    public FreezeEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
        addAttributeModifier(Attributes.ATTACK_SPEED, ModEffects.FREEZE.getId(), () -> - VampirismConfig.BALANCE.vaFreezeAttackSpeedModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.canFreeze()) {
            entityLivingBaseIn.setTicksFrozen(Math.max(Math.min(entityLivingBaseIn.getTicksRequiredToFreeze(), Entity.BASE_TICKS_REQUIRED_TO_FREEZE) + 40, entityLivingBaseIn.getTicksFrozen()));
        }
        return true;
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
