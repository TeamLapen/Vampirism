package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mixin(TargetingConditions.class)
public interface TargetConditionAccessor {

    @Nullable
    @Accessor("selector")
    Predicate<LivingEntity> getSelector();
}
