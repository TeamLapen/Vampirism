package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface IAlchemicalCauldronCraftingManager {

    void addRecipe(IAlchemicalCauldronRecipe recipe);

    /**
     * Add a recipe.
     *
     * @param liquid     Item, ItemStack or FluidStack
     * @param ingredient ItemStack (WILDCARD allowed), Item or Block. If no ingredient is required, this can be null
     * @param output     ItemStack, Item or Block
     * @return This recipe for further configuration
     */
    IAlchemicalCauldronRecipe addRecipe(@Nonnull Object output, @Nonnull Object liquid, @Nullable Object ingredient);

    /**
     * Same as {@link IAlchemicalCauldronCraftingManager#addRecipe(Object, Object, Object)} but calls {@link IAlchemicalCauldronRecipe#configure(int, float, int, ISkill[])} on it
     *
     * @return This recipe
     */
    IAlchemicalCauldronRecipe addRecipe(@Nonnull Object output, @Nonnull Object liquid, @Nullable Object ingredient, int ticks, int exp, int reqLevel, ISkill... reqSkills);

    /**
     * Finds the recipe that fits to the given inputs. Does not check level or skill requirements
     *
     * @param liquid     must not be EMPTY
     * @param ingredient may be EMPTY
     */
    @Nullable
    IAlchemicalCauldronRecipe findRecipe(@Nonnull ItemStack liquid, @Nonnull ItemStack ingredient);

    /**
     * Finds the recipe that fits to the given inputs. Does not check level or skill requirements
     *
     * @param ingredient may be EMPTY
     */
    @Nullable
    IAlchemicalCauldronRecipe findRecipe(FluidStack liquid, @Nonnull ItemStack ingredient);

    /**
     * @param stack may be EMPTY
     * @return The color registered for this itemstack or -1 if none is registed
     */
    int getLiquidColor(@Nonnull ItemStack stack);

    /**
     * A color for this itemstack used to render the liquid in the alchemical cauldron
     *
     * @param stack must not be EMPTY
     */
    void registerLiquidColor(@Nonnull ItemStack stack, int color);

    /**
     * A color for this items used to render the liquid in the alchemical cauldron. Affects all itemstacks with this item, altough registered itemstacks are checked first
     */
    void registerLiquidColor(Item item, int color);

}
