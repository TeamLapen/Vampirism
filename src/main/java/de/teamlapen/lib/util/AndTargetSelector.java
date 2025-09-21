package de.teamlapen.lib.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.NotNull;

public class AndTargetSelector implements TargetingConditions.Selector {

    private final TargetingConditions.Selector first;
    private final TargetingConditions.Selector second;

    public AndTargetSelector(TargetingConditions.Selector first, TargetingConditions.Selector second) {
        this.first = first;
        this.second = second;
    }
    @Override
    public boolean test(@NotNull LivingEntity entity, @NotNull ServerLevel level) {
        return this.first.test(entity, level) && this.second.test(entity, level);
    }
}
