package de.teamlapen.vampirism.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IHunterWeaponCraftingManager;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


public class HunterWeaponCraftingManager implements IHunterWeaponCraftingManager {

    private final static String TAG = "HWCraftingManager";
    private static final HunterWeaponCraftingManager INSTANCE = new HunterWeaponCraftingManager();

    public static HunterWeaponCraftingManager getInstance() {
        return INSTANCE;
    }

    private final List<IHunterWeaponRecipe> recipes = Lists.newLinkedList();

    @Override
    public IHunterWeaponRecipe addRecipe(ItemStack output, int reqLevel, @Nonnull ISkill<IHunterPlayer>[] reqSkills, int reqLava, Object... recipeComponents) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[]) {
            String[] astring = (String[]) recipeComponents[i++];

            for (int l = 0; l < astring.length; ++l) {
                String s2 = astring[l];
                ++k;
                j = s2.length();
                s = s + s2;
            }
        } else {
            while (recipeComponents[i] instanceof String) {
                String s1 = (String) recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.newHashMap(); i < recipeComponents.length; i += 2) {
            Character character = (Character) recipeComponents[i];
            ItemStack itemstack = null;

            if (recipeComponents[i + 1] instanceof Item) {
                itemstack = new ItemStack((Item) recipeComponents[i + 1]);
            } else if (recipeComponents[i + 1] instanceof Block) {
                itemstack = new ItemStack((Block) recipeComponents[i + 1], 1, 32767);
            } else if (recipeComponents[i + 1] instanceof ItemStack) {
                itemstack = (ItemStack) recipeComponents[i + 1];
            } else {
                VampirismMod.log.e(TAG, "Cannot add %s to recipe as %s since it is not supported", recipeComponents[i + 1], character);
            }

            map.put(character, itemstack);
        }
        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1) {
            char c0 = s.charAt(i1);

            if (map.containsKey(c0)) {
                aitemstack[i1] = (map.get(c0)).copy();
            } else {
                aitemstack[i1] = null;
            }
        }

        ShapedHunterWeaponRecipe recipe = new ShapedHunterWeaponRecipe(j, k, aitemstack, output, reqLevel, reqSkills, reqLava);
        this.recipes.add(recipe);
        return recipe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IHunterWeaponRecipe addRecipe(ItemStack output, int reqLevel, @Nullable ISkill<IHunterPlayer> reqSkill, int reqLava, Object... recipeComponents) {
        ISkill<IHunterPlayer>[] reqSkills;
        if (reqSkill == null) {
            reqSkills = new ISkill[0];
        } else {
            reqSkills = new ISkill[]{reqSkill};
        }
        return addRecipe(output, reqLevel, reqSkills, reqLava, recipeComponents);
    }

    @Override
    public void addRecipe(IHunterWeaponRecipe recipe) {
        this.recipes.add(recipe);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IHunterWeaponRecipe addShapelessRecipe(ItemStack output, int reqLevel, @Nullable ISkill<IHunterPlayer> reqSkill, int reqLava, Object... recipeComponents) {
        ISkill<IHunterPlayer>[] reqSkills;
        if (reqSkill == null) {
            reqSkills = new ISkill[0];
        } else {
            reqSkills = new ISkill[]{reqSkill};
        }
        return addShapelessRecipe(output, reqLevel, reqSkills, reqLava, recipeComponents);
    }

    @Override
    public IHunterWeaponRecipe addShapelessRecipe(ItemStack output, int reqLevel, @Nonnull ISkill<IHunterPlayer>[] reqSkills, int reqLava, Object... recipeComponents) {
        List<ItemStack> list = Lists.newArrayList();

        for (Object object : recipeComponents) {
            if (object instanceof ItemStack) {
                list.add(((ItemStack) object).copy());
            } else if (object instanceof Item) {
                list.add(new ItemStack((Item) object));
            } else {
                if (!(object instanceof Block)) {
                    throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                }

                list.add(new ItemStack((Block) object));
            }
        }
        IHunterWeaponRecipe recipe = new ShapelessHunterWeaponRecipe(list, output, reqLevel, reqSkills, reqLava);
        this.recipes.add(recipe);
        return recipe;
    }

    @Nullable
    @Override
    public IHunterWeaponRecipe findMatchingRecipe(InventoryCrafting craftMatrix, World world, int playerLevel, ISkillHandler<IHunterPlayer> skillHandler, int lava) {
        for (IHunterWeaponRecipe iRecipe : this.recipes) {
            if (iRecipe.matches(craftMatrix, world)) {
                if (playerLevel >= iRecipe.getMinHunterLevel() && lava >= iRecipe.getRequiredLavaUnits() && Helper.areSkillsEnabled(skillHandler, iRecipe.getRequiredSkills())) {
                    return iRecipe;
                }
            }
        }
        return null;
    }

    @Override
    public
    @Nullable
    ItemStack findMatchingRecipeResult(InventoryCrafting craftMatrix, World world, int playerLevel, ISkillHandler<IHunterPlayer> skillHandler, int lava) {
        IHunterWeaponRecipe recipe = findMatchingRecipe(craftMatrix, world, playerLevel, skillHandler, lava);
        return recipe == null ? null : recipe.getCraftingResult(craftMatrix);
    }

    public List<IHunterWeaponRecipe> getRecipes() {
        return recipes;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting craftMatrix, World world, int playerLevel, ISkillHandler<IHunterPlayer> skillHandler, int lava) {
        IHunterWeaponRecipe recipe = findMatchingRecipe(craftMatrix, world, playerLevel, skillHandler, lava);
        if (recipe != null) return recipe.getRemainingItems(craftMatrix);
        NonNullList<ItemStack> remaining = NonNullList.create();

        for (int i = 0; i < craftMatrix.getSizeInventory(); ++i) {
            ItemStack stack = craftMatrix.getStackInSlot(i);
            if (!stack.isEmpty()) {
                remaining.add(stack);
            }
        }

        return remaining;
    }
}
