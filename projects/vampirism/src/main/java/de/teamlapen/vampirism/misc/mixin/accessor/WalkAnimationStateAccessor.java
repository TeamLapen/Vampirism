package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IWalkAnimationState;
import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WalkAnimationState.class)
public interface WalkAnimationStateAccessor extends IWalkAnimationState {

    @Override
    @Accessor("speedOld")
    float oldSpeed();

    @Override
    @Accessor("speedOld")
    void oldSpeed(float speedOld);
}
