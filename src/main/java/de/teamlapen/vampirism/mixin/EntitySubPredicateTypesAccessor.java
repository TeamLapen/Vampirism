package de.teamlapen.vampirism.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntitySubPredicate.Types.class)
public interface EntitySubPredicateTypesAccessor {

    @Final
    @Mutable
    @Accessor("TYPES")
    static void setTypes(BiMap<String, EntitySubPredicate.Type> types) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
