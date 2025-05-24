package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeProvider.class)
public interface RecipeProviderAccessor {

    @Invoker("has")
    static Criterion<InventoryChangeTrigger.TriggerInstance> invokeHas(ItemLike itemLike) {
        throw new AssertionError();
    }
}
