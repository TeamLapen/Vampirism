package de.teamlapen.vampirism.common.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public record AndTargetSelector(TargetingConditions.Selector first,
                                TargetingConditions.Selector second) implements TargetingConditions.Selector {

    @Override
    public boolean test(LivingEntity entity, ServerLevel level) {
        return this.first.test(entity, level) && this.second.test(entity, level);
    }
}
