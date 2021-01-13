package de.teamlapen.vampirism.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Collection of accessor mixins
 */
public class MixinAccessors {
    @Mixin(LivingEntity.class)
    public interface LivingEntityExperienceAccessor {
        @Invoker("getExperiencePoints")
        int accessGetExperiencePoints(PlayerEntity player);
    }
}
