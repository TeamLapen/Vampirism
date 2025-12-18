package de.teamlapen.vampirism.common.world.effects;

import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FreezeMobEffect extends ConfigAwareMobEffect {

    public FreezeMobEffect(MobEffectCategory category, int color) {
        super(category, color);
        addAttributeModifier(Attributes.ATTACK_SPEED, ModEffects.FREEZE.getId(), () -> -ModConfig.BALANCE.vaFreezeAttackSpeedModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (entity.canFreeze()) {
            entity.setTicksFrozen(Math.max(Math.min(entity.getTicksRequiredToFreeze(), Entity.BASE_TICKS_REQUIRED_TO_FREEZE) + 40, entity.getTicksFrozen()));
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return "action.vampirism.freeze";
    }
}
