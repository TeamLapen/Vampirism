package de.teamlapen.vampirism.modcompat.guide;

import com.google.common.collect.Lists;
import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.api.IRecipeRenderer;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.page.PageIRecipe;
import de.maxanier.guideapi.page.PageJsonRecipe;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.inventory.recipes.AlchemicalCauldronRecipe;
import de.teamlapen.vampirism.inventory.recipes.ShapedWeaponTableRecipe;
import de.teamlapen.vampirism.inventory.recipes.ShapelessWeaponTableRecipe;
import de.teamlapen.vampirism.modcompat.guide.pages.PageHolderWithLinks;
import de.teamlapen.vampirism.modcompat.guide.recipes.AlchemicalCauldronRecipeRenderer;
import de.teamlapen.vampirism.modcompat.guide.recipes.ShapedWeaponTableRecipeRenderer;
import de.teamlapen.vampirism.modcompat.guide.recipes.ShapelessWeaponTableRecipeRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Collection of helper methods
 */
public class GuideHelper {

    private static final Logger LOGGER = LogManager.getLogger();

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
                LOGGER.warn("Given link object cannot be linked {}", l);
            }
        }
        pages.clear();
        pages.addAll(linkPages);
        return pages;
    }


    @Nullable
    public static BrewingRecipe getBrewingRecipe(ItemStack stack) {
        return (BrewingRecipe) BrewingRecipeRegistry.getRecipes().stream().filter(iBrewingRecipe -> iBrewingRecipe instanceof BrewingRecipe && ItemStack.areItemStacksEqual(((BrewingRecipe) iBrewingRecipe).getOutput(), stack)).findFirst().orElse(null);
    }

    @Nullable
    private static IRecipeRenderer getRenderer(IRecipe<?> recipe) {
        IRecipeRenderer recipeRenderer = PageIRecipe.getRenderer(recipe);
        if (recipeRenderer != null) return recipeRenderer;
        if (recipe instanceof ShapedWeaponTableRecipe) {
            return new ShapedWeaponTableRecipeRenderer((ShapedWeaponTableRecipe) recipe);
        } else if (recipe instanceof ShapelessWeaponTableRecipe) {
            return new ShapelessWeaponTableRecipeRenderer((ShapelessWeaponTableRecipe) recipe);
        } else if (recipe instanceof AlchemicalCauldronRecipe) {
            return new AlchemicalCauldronRecipeRenderer((AlchemicalCauldronRecipe) recipe);
        }
        LOGGER.warn("Did not find renderer for recipe {}", recipe);
        return null;
    }

    public static IPage getRecipePage(ResourceLocation id) {
        return new PageJsonRecipe(id, GuideHelper::getRenderer);
    }


}
