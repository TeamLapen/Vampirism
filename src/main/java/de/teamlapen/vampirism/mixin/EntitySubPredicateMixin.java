package de.teamlapen.vampirism.mixin;

import com.google.common.collect.ImmutableBiMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.core.ModAdvancements;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntitySubPredicate.Types.class)
public class EntitySubPredicateMixin {

    @WrapOperation(method = "<clinit>", at = @At(value= "INVOKE", target = "Lcom/google/common/collect/ImmutableBiMap$Builder;buildOrThrow()Lcom/google/common/collect/ImmutableBiMap;"))
    private static ImmutableBiMap<String, EntitySubPredicate.Type> builder(ImmutableBiMap.Builder<String, EntitySubPredicate.Type> builder, Operation<ImmutableBiMap<String, EntitySubPredicate.Type>> operation) {
        builder.put("vampirism:faction", ModAdvancements.FACTION);
        return operation.call(builder);
    }
}
