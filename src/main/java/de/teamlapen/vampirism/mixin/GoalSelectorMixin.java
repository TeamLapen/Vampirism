package de.teamlapen.vampirism.mixin;

import com.google.common.collect.Sets;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

@Mixin(GoalSelector.class)
public class GoalSelectorMixin {

    @Mutable
    @Shadow @Final public Set<WrappedGoal> availableGoals;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void replace(Supplier supplier, CallbackInfo ci) {
        availableGoals = ConcurrentHashMap.newKeySet();
    }
}
