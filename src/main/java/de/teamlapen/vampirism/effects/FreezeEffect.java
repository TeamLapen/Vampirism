package de.teamlapen.vampirism.effects;


import de.teamlapen.vampirism.config.VampirismConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class FreezeEffect extends ConfigAwareEffect {


    public FreezeEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
        addAttributeModifier(Attributes.ATTACK_SPEED, "3e327afe-d3e8-429b-9cd0-5ee5487119f8", () -> - VampirismConfig.BALANCE.vaFreezeAttackSpeedModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
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
