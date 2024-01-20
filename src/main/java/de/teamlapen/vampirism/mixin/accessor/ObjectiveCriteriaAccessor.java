package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ObjectiveCriteria.class)
public interface ObjectiveCriteriaAccessor {

    @Invoker("registerCustom(Ljava/lang/String;)Lnet/minecraft/world/scores/criteria/ObjectiveCriteria")
    static ObjectiveCriteria registerCustom(String name) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
