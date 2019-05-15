package de.teamlapen.vampirism.modcompat.guide;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.IRecipeRenderer;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.page.*;
import com.google.common.collect.Lists;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronCraftingManager;
import de.teamlapen.vampirism.inventory.HunterWeaponCraftingManager;
import de.teamlapen.vampirism.inventory.ShapedHunterWeaponRecipe;
import de.teamlapen.vampirism.inventory.ShapelessHunterWeaponRecipe;
import de.teamlapen.vampirism.modcompat.guide.pages.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Collection of helper methods
 */
public class GuideHelper {

    /**
     * Adds multiple strings together seperated by a double line break
     *
     * @param unlocalized Unlocalized strings
     */
    public static String append(String... unlocalized) {
        StringBuilder s = new StringBuilder();
        for (String u : unlocalized) {
            s.append(UtilLib.translate(u)).append("\n\n");
        }
        return s.toString();
    }

    /**
     * Converts the given pages to {@link PageHolderWithLinks} and adds the given links
     *
     * @return The SAME list
     */
    public static List<IPage> addLinks(List<IPage> pages, Object... links) {
        List<PageHolderWithLinks> linkPages = Lists.newArrayList();
        for (IPage p : pages) {
            linkPages.add(new PageHolderWithLinks(p));
        }

        for (Object l : links) {
            if (l instanceof ResourceLocation) {
                for (PageHolderWithLinks p : linkPages) {
                    p.addLink((ResourceLocation) l);
                }
            } else if (l instanceof EntryAbstract) {
                for (PageHolderWithLinks p : linkPages) {
                    p.addLink((EntryAbstract) l);
                }
            } else if (l instanceof PageHolderWithLinks.URLLink) {
                for (PageHolderWithLinks p : linkPages) {
                    p.addLink((PageHolderWithLinks.URLLink) l);
                }
            } else {
                VampirismMod.log.w("GuideHelper", "Given link object cannot be linked %s", l);
            }
        }
        pages.clear();
        pages.addAll(linkPages);
        return pages;
    }

    public static IRecipe getRecipeForOutput(ItemStack stack) {

        for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (checkRecipeOutput(recipe, stack, true)) return recipe;
        }

        return null;
    }

    public static IAlchemicalCauldronRecipe getAlchemicalCauldronRecipeForOutput(ItemStack stack) {
        for (IAlchemicalCauldronRecipe recipe : AlchemicalCauldronCraftingManager.getInstance().getRecipes()) {
            if (checkOutput(recipe.getOutput(), stack, true)) return recipe;
        }
        return null;
    }

    public static IWeaponTableRecipe getWeaponTableRecipeForOutput(ItemStack stack) {
        for (IWeaponTableRecipe recipe : HunterWeaponCraftingManager.getInstance().getRecipes()) {
            if (checkRecipeOutput(recipe, stack, true)) return recipe;
        }
        return null;
    }

    public static ItemStack getFurnaceRecipe(ItemStack stack) {
        for (Map.Entry<ItemStack, ItemStack> e : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
            if (stack.isItemEqual(e.getValue())) {
                return e.getKey();
            }
        }
        return null;
    }

    public static BrewingRecipe getBrewingRecipe(ItemStack stack) {
        return (BrewingRecipe) BrewingRecipeRegistry.getRecipes().stream().filter(iBrewingRecipe -> iBrewingRecipe instanceof BrewingRecipe && ItemStack.areItemStacksEqual(((BrewingRecipe) iBrewingRecipe).getOutput(), stack)).findFirst().orElse(null);
    }

    private static boolean checkRecipeOutput(IRecipe recipe, ItemStack stack, boolean checkNBT) {
        if (recipe != null) {
            ItemStack resultStack = recipe.getRecipeOutput();
            return checkOutput(resultStack, stack, checkNBT);
        }
        return false;
    }

    private static boolean checkRecipeOutput(IWeaponTableRecipe recipe, ItemStack stack, boolean checkNBT) {
        if (recipe != null) {
            ItemStack resultStack = recipe.getRecipeOutput();
            return checkOutput(resultStack, stack, checkNBT);
        }
        return false;
    }

    private static boolean checkOutput(ItemStack resultStack, ItemStack stack, boolean checkNBT) {
        if (!stack.isEmpty() && !resultStack.isEmpty()) {
            if (resultStack.getItem() == stack.getItem() && resultStack.getItemDamage() == stack.getItemDamage()) {
                if (!checkNBT || resultStack.getTagCompound() == null && stack.getTagCompound() == null || resultStack.getTagCompound() != null && resultStack.getTagCompound().equals(stack.getTagCompound())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static
    @Nullable
    IPage getRecipePage(ItemStack stack, RECIPE_TYPE type) {
        try {
            switch (type) {
                case WORKBENCH:
                    IRecipe r = checkNotNull(getRecipeForOutput(stack));
                    return new PageIRecipe(r);
                case FURNACE:
                    ItemStack s = checkNotNull(getFurnaceRecipe(stack));
                    return new PageFurnaceRecipe(s);
                case BREWING_STAND:
                    BrewingRecipe b = checkNotNull(getBrewingRecipe(stack));
                    return new PageBrewingRecipe(b);
                case WEAPON_TABLE:
                    IWeaponTableRecipe r2 = checkNotNull(getWeaponTableRecipeForOutput(stack));
                    IRecipeRenderer renderer = null;
                    if (r2 instanceof ShapedHunterWeaponRecipe) {
                        renderer = new ShapedWeaponTableRecipeRenderer((ShapedHunterWeaponRecipe) r2);
                    } else if (r2 instanceof ShapelessHunterWeaponRecipe) {
                        renderer = new ShapelessWeaponTableRecipeRenderer((ShapelessHunterWeaponRecipe) r2);
                    }
                    checkNotNull(renderer);
                    return new PageRecipe<>(r2, renderer);
                case ALCHEMICAL_CAULDRON:
                    IAlchemicalCauldronRecipe r3 = checkNotNull(getAlchemicalCauldronRecipeForOutput(stack));
                    return new AlchemicalCauldronRecipePage(r3);
                default:
                    throw new IllegalArgumentException("Type unknown " + type);
            }
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static void addArmorWithTier(Map<ResourceLocation, EntryAbstract> entries, String name, IItemWithTier helmet, IItemWithTier chest, IItemWithTier legs, IItemWithTier boots, RECIPE_TYPE recipeType) {
        List<Object> craftable = new ArrayList<>();
        for (IItemWithTier.TIER t : IItemWithTier.TIER.values()) {
            craftable.add(ModItems.createStack(helmet, t));
            craftable.add(RECIPE_TYPE.WEAPON_TABLE);
            craftable.add(ModItems.createStack(chest, t));
            craftable.add(RECIPE_TYPE.WEAPON_TABLE);
            craftable.add(ModItems.createStack(legs, t));
            craftable.add(RECIPE_TYPE.WEAPON_TABLE);
            craftable.add(ModItems.createStack(boots, t));
            craftable.add(RECIPE_TYPE.WEAPON_TABLE);
        }
        ItemInfoBuilder builder = new ItemInfoBuilder(ModItems.createStack(helmet, IItemWithTier.TIER.NORMAL), false);
        builder.setName(name).ignoreMissingRecipes().craftableStacks(craftable).customName();
        builder.build(entries);
    }

    public static ItemInfoBuilder addItemWithTier(IItemWithTier item, RECIPE_TYPE recipeType) {
        List<Object> craftable = new ArrayList<>();
        for (IItemWithTier.TIER t : IItemWithTier.TIER.values()) {
            craftable.add(ModItems.createStack(item, t));
            craftable.add(recipeType);
        }
        ItemInfoBuilder builder = new ItemInfoBuilder(ModItems.createStack(item, IItemWithTier.TIER.NORMAL), false);
        builder.craftableStacks(craftable).ignoreMissingRecipes();
        return builder;
    }

    /**
     * Creates as many pages as required to display the given string while displaying the given stack on the first page.
     * Displays the given maximum of lines of text on the first page and maximum+5  on the following
     *
     * @param pageLength The maximum number of text lines on the first page
     */
    public static List<IPage> pagesForLongText(String locText, ItemStack stack, int pageLength) {
        List<IPage> pageList = new ArrayList<>();
        List<String> lines = new ArrayList<>(VampLib.proxy.listFormattedStringToWidth(locText, 115));
        List<String> page1 = lines.size() > pageLength ? lines.subList(0, pageLength) : lines;
        pageList.add(new PageItemStack(StringUtils.join(page1, "\n"), stack));
        page1.clear();
        pageLength += 5;
        while (lines.size() > 0) {
            List<String> page = lines.size() > pageLength ? lines.subList(0, pageLength) : lines;
            pageList.add(new PageText(StringUtils.join(page, "\n")));
            page.clear();
        }
        return pageList;
    }

    /**
     * Creates as many pages as required to display the given string while displaying the given stack on the first page.
     * Displays a maximum of 8 lines of text on the first page and 13 on the following
     */
    public static List<IPage> pagesForLongText(String locText, ItemStack stack) {
        return pagesForLongText(locText, stack, 8);
    }

    /**
     * Creates as many pages as required to display the given string.
     * Displays the given maximum number of lines per page
     *
     * @param s
     * @param pageLength
     */
    public static List<IPage> pagesForLongText(String s, int pageLength) {
        List<IPage> pageList = new ArrayList<>();
        List<String> lines = new ArrayList<>(VampLib.proxy.listFormattedStringToWidth(s, 115));
        while (lines.size() > 0) {
            List<String> page = lines.size() > pageLength ? lines.subList(0, pageLength) : lines;
            pageList.add(new PageText(StringUtils.join(page, "\n")));
            page.clear();
        }
        return pageList;
    }

    /**
     * Creates as many pages as required to display the given string.
     * Displays a maximum of 13  lines per page
     */
    public static List<IPage> pagesForLongText(String s) {
        return pagesForLongText(s, 13);
    }

    public enum RECIPE_TYPE {
        WORKBENCH, FURNACE, WEAPON_TABLE, ALCHEMICAL_CAULDRON, BREWING_STAND
    }


}
