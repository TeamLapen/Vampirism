package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.ITargetingConditions;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(TargetingConditions.class)
public interface TargetConditionAccessor extends ITargetingConditions {

    @Override
    @Nullable
    @Accessor("selector")
    TargetingConditions.Selector getSelector();
}
