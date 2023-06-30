package de.teamlapen.vampirism.mixin;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mixin(EntityPredicate.class)
public interface EntityPredicateAccessor {

    @Nullable
    @Accessor("selector")
    Predicate<LivingEntity> getSelector();
}
