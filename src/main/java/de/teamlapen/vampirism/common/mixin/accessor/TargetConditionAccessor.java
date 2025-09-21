package de.teamlapen.vampirism.common.mixin.accessor;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(TargetingConditions.class)
public interface TargetConditionAccessor {

    @Nullable
    @Accessor("selector")
    TargetingConditions.Selector getSelector();
}
