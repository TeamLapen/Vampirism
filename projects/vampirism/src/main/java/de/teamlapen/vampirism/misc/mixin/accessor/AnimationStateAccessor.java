package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IAnimationState;
import net.minecraft.world.entity.AnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimationState.class)
public interface AnimationStateAccessor extends IAnimationState {
    @Override
    @Accessor("startTick")
    int vampirism$startTick();
}
