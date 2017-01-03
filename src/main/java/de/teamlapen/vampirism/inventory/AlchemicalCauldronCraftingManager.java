package de.teamlapen.vampirism.inventory;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 1.10
 *
 * @author maxanier
 */
public class AlchemicalCauldronCraftingManager {
    private final static String TAG = "AlchemicalCauldronCraftingManager";
    private static AlchemicalCauldronCraftingManager ourInstance = new AlchemicalCauldronCraftingManager();

    public static AlchemicalCauldronCraftingManager getInstance() {
        return ourInstance;
    }

    private final List<Recipe> recipes = Lists.newLinkedList();

    private AlchemicalCauldronCraftingManager() {
        this.addCookingRecipe(new ItemStack(ModItems.holyWaterBottle), Items.COAL, Item.getItemFromBlock(ModBlocks.alchemicalFire), 0.35F);
    }

    public void addCookingRecipe(ItemStack liquid, ItemStack ingredient, ItemStack output, float exp) {
        addCookingRecipe(new Recipe(liquid, ingredient, output, exp));
    }

    public void addCookingRecipe(ItemStack liquid, ItemStack ingredient, Item output, float exp) {
        addCookingRecipe(new Recipe(liquid, ingredient, new ItemStack(output), exp));
    }

    public void addCookingRecipe(ItemStack liquid, Item ingredient, ItemStack output, float exp) {
        addCookingRecipe(new Recipe(liquid, new ItemStack(ingredient, 1, 32767), output, exp));
    }

    public void addCookingRecipe(ItemStack liquid, Item ingredient, Item output, float exp) {
        addCookingRecipe(new Recipe(liquid, new ItemStack(ingredient, 1, 32767), new ItemStack(output), exp));
    }

    public void addCookingRecipe(ItemStack liquid, Block ingredient, ItemStack output, float exp) {
        addCookingRecipe(liquid, Item.getItemFromBlock(ingredient), output, exp);
    }

    public float getCookingExperience(ItemStack liquid, ItemStack ingredient) {
        for (Recipe r : recipes) {
            if (r.isSameInput(liquid, ingredient)) {
                return r.exp;
            }
        }
        return 0F;
    }

    @Nullable
    public ItemStack getCookingResult(ItemStack liquid, ItemStack ingredient) {
        for (Recipe r : recipes) {
            if (r.isSameInput(liquid, ingredient)) {
                return r.output;
            }
        }
        return null;
    }

    private void addCookingRecipe(Recipe r) {
        if (getCookingResult(r.liquid, r.ingredient) != null) {
            VampirismMod.log.w(TAG, "Ingnoring cooking recipe with conflicting input (L: %s, I: %s, O: %s)", r.liquid, r.ingredient, r.output);
            return;
        }
        recipes.add(r);
    }

    private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
        return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
    }

    private class Recipe {
        public final ItemStack liquid;
        public final ItemStack ingredient;
        public final ItemStack output;
        public final float exp;

        private Recipe(ItemStack liquid, ItemStack ingredient, ItemStack output, float exp) {
            this.liquid = liquid;
            this.ingredient = ingredient;
            this.output = output;
            this.exp = exp;
        }

        private boolean isSameInput(ItemStack liquid, ItemStack ingredient) {
            return compareItemStacks(liquid, this.liquid) && compareItemStacks(ingredient, this.ingredient);
        }

    }
}
