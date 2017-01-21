package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 1.10
 *
 * @author maxanier
 */
public interface IAlchemicalCauldronCraftingManager {

    void addRecipe(IAlchemicalCauldronRecipe recipe);

    /**
     * Add a recipe. Only accepts  as ingre
     *
     * @param liquid     Item, ItemStack or FluidStack
     * @param ingredient ItemStack (WILDCARD allowed), Item or Block
     * @param output     ItemStack, Item or Block
     * @return This recipe for further configuration
     */
    IAlchemicalCauldronRecipe addRecipe(Object liquid, Object ingredient, Object output);

    /**
     * Same as {@link IAlchemicalCauldronCraftingManager#addRecipe(Object, Object, Object)} but calls {@link IAlchemicalCauldronRecipe#configure(int, int, int, ISkill[])} on it
     *
     * @return This recipe
     */
    IAlchemicalCauldronRecipe addRecipe(Object liquid, Object ingredient, Object output, int ticks, int exp, int reqLevel, ISkill<IHunterPlayer>... reqSkills);

    /**
     * Finds the recipe that fits to the given inputs. Does not check level or skill requirements
     */
    @Nullable
    IAlchemicalCauldronRecipe findRecipe(@Nonnull ItemStack liquid, @Nullable ItemStack ingredient);

    /**
     * Finds the recipe that fits to the given inputs. Does not check level or skill requirements
     */
    @Nullable
    IAlchemicalCauldronRecipe findRecipe(FluidStack liquid, ItemStack ingredient);

    /**
     * @return The color registered for this itemstack or -1 if none is registed
     */
    int getLiquidColor(@Nullable ItemStack stack);

    /**
     * A color for this itemstack used to render the liquid in the alchemical cauldron
     */
    void registerLiquidColor(@Nonnull ItemStack stack, int color);

    /**
     * A color for this items used to render the liquid in the alchemical cauldron. Affects all itemstacks with this item, altough registered itemstacks are checked first
     */
    void registerLiquidColor(Item item, int color);

}
