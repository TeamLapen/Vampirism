package de.teamlapen.vampirism.common.world.items.recipes;

import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class CustomWeaponTableRecipe implements IWeaponTableRecipe {

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public abstract RecipeSerializer<? extends CustomWeaponTableRecipe> getSerializer();

}
