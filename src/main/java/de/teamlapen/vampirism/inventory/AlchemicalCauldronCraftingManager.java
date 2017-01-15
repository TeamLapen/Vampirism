package de.teamlapen.vampirism.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronCraftingManager;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * 1.10
 *
 * @author maxanier
 */
public class AlchemicalCauldronCraftingManager implements IAlchemicalCauldronCraftingManager {

    private final static String TAG = "ACCraftingManager";
    private static AlchemicalCauldronCraftingManager ourInstance = new AlchemicalCauldronCraftingManager();

    public static AlchemicalCauldronCraftingManager getInstance() {
        return ourInstance;
    }

    private final List<IAlchemicalCauldronRecipe> recipes = Lists.newLinkedList();
    private Map<Object, Integer> liquidColors = Maps.newHashMap();

    private AlchemicalCauldronCraftingManager() {
    }

    @Override
    public void addRecipe(IAlchemicalCauldronRecipe recipe) {
        for (IAlchemicalCauldronRecipe r : recipes) {
            if (r.areSameIngredients(recipe)) {
                throw new IllegalArgumentException(TAG + ": Duplicate recipe " + r + " and " + recipe);
            }
        }
        recipes.add(recipe);
    }

    @Override
    public IAlchemicalCauldronRecipe addRecipe(Object liquid, Object ingredient, Object output) {
        IAlchemicalCauldronRecipe recipe;
        if (liquid instanceof Item) {
            liquid = new ItemStack((Item) liquid);
        } else if (liquid instanceof Block) {
            liquid = new ItemStack((Block) liquid);
        }
        if (liquid instanceof ItemStack) {
            recipe = new AlchemicalCauldronRecipe(((ItemStack) liquid).copy(), getItemStackCopy(ingredient), getItemStackCopy(output));
        } else if (liquid instanceof FluidStack) {
            recipe = new AlchemicalCauldronRecipe(((FluidStack) liquid).copy(), getItemStackCopy(ingredient), getItemStackCopy(output));
        } else {
            throw new IllegalArgumentException(TAG + ": Liquid has to be either a ItemStack or a FluidStack");
        }
        addRecipe(recipe);
        return recipe;
    }

    @Override
    public IAlchemicalCauldronRecipe addRecipe(Object liquid, Object ingredient, Object output, int ticks, int exp, int reqLevel, ISkill<IHunterPlayer>... reqSkills) {
        IAlchemicalCauldronRecipe recipe = addRecipe(liquid, ingredient, output);
        recipe.configure(ticks, exp, reqLevel, reqSkills);
        return recipe;
    }

    @Nullable
    @Override
    public IAlchemicalCauldronRecipe findRecipe(ItemStack liquid, ItemStack ingredient) {
        for (IAlchemicalCauldronRecipe r : recipes) {
            if (r.isValidLiquidItem(liquid) || r.isValidFluidItem(liquid) != null) {
                if (UtilLib.doesStackContain(ingredient, r.getIngredient())) {
                    return r;
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IAlchemicalCauldronRecipe findRecipe(FluidStack liquid, ItemStack ingredient) {

        for (IAlchemicalCauldronRecipe r : recipes) {
            if (r.isValidFluidStack(liquid) != null) {
                if (UtilLib.doesStackContain(ingredient, r.getIngredient())) {
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public int getLiquidColor(@Nullable ItemStack stack) {
        if (stack != null) {
            if (liquidColors.containsKey(stack)) {
                return liquidColors.get(stack);
            }
            if (liquidColors.containsKey(stack.getItem())) {
                return liquidColors.get(stack.getItem());
            }
        }
        return -1;
    }

    @Override
    public void registerLiquidColor(@Nonnull ItemStack stack, int color) {
        liquidColors.put(stack, color);
    }

    @Override
    public void registerLiquidColor(Item item, int color) {
        liquidColors.put(item, color);
    }

    private ItemStack getItemStackCopy(Object o) {
        if (o == null) return null;
        if (o instanceof ItemStack) return ((ItemStack) o).copy();
        if (o instanceof Item) return new ItemStack((Item) o);
        if (o instanceof Block) return new ItemStack((Block) o);
        throw new IllegalArgumentException(TAG + ": Argument has to be one of the following: ItemStack, Item or Block");

    }
}
