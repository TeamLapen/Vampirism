package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeProvider.class)
public interface RecipeProviderAccessor {

    @Invoker("chestBoat")
    static void chestBoat(RecipeOutput pRecipeOutput, ItemLike pBoat, ItemLike pMaterial) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
