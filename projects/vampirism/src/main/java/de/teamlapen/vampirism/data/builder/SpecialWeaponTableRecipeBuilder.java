package de.teamlapen.vampirism.data.builder;

import de.teamlapen.vampirism.common.world.items.recipes.CustomWeaponTableRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.Supplier;

public class SpecialWeaponTableRecipeBuilder {

    private final Supplier<CustomWeaponTableRecipe> factory;

    public SpecialWeaponTableRecipeBuilder(Supplier<CustomWeaponTableRecipe> factory) {
        this.factory = factory;
    }

    public static SpecialWeaponTableRecipeBuilder special(Supplier<CustomWeaponTableRecipe> factory) {
        return new SpecialWeaponTableRecipeBuilder(factory);
    }

    public void save(RecipeOutput output, String recipeId) {
        this.save(output, ResourceKey.create(Registries.RECIPE, Identifier.parse(recipeId)));
    }

    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        output.accept(id, this.factory.get(), null);
    }
}
