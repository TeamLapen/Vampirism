package de.teamlapen.vampirism.modcompat.guide;

import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.PageHelper;
import de.maxanier.guideapi.entry.EntryItemStack;
import de.maxanier.guideapi.page.PageBrewingRecipe;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.brewing.BrewingRecipe;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to build item or block info pages
 */
public class ItemInfoBuilder {
    public static ItemInfoBuilder create(Item... items) {
        assert items.length > 0;
        Item i0 = items[0];
        String name = i0 instanceof IItemWithTier ? ((IItemWithTier) i0).getBaseRegName() : Helper.getIDSafe(i0).getPath();
        return new ItemInfoBuilder(Ingredient.fromItems(items), new ItemStack(i0), name, false);
    }

    private final boolean block;
    private String name;
    private Object[] formats = new Object[0];
    private Object[] links = null;
    private boolean customName;
    private final List<IPage> additionalPages = new ArrayList<>();

    /**
     * @param block Whether to use "block" or "item" translation keys
     */
    public static ItemInfoBuilder create(boolean block, ItemStack... stacks) {
        assert stacks.length > 0;
        ItemStack i0 = stacks[0];
        Item item = i0.getItem();
        String name = item instanceof IItemWithTier ? ((IItemWithTier) item).getBaseRegName() : Helper.getIDSafe(item).getPath();
        return new ItemInfoBuilder(Ingredient.fromStacks(stacks), i0, name, block);
    }

    @Nonnull
    private List<ResourceLocation> recipes = Collections.emptyList();

    public static ItemInfoBuilder create(Block... blocks) {
        assert blocks.length > 0;
        Block i0 = blocks[0];
        String name = Helper.getIDSafe(i0).getPath();
        return new ItemInfoBuilder(Ingredient.fromItems(blocks), new ItemStack(i0), name, true);
    }

    private final Ingredient ingredient;
    private final ItemStack mainStack;
    @Nullable
    private ItemStack[] brewingStacks;

    /**
     * @param ingredient The relevant item stack. Used for display and strings.
     * @param block      If this entry is a about a block or not
     */
    private ItemInfoBuilder(Ingredient ingredient, ItemStack mainStack, String name, boolean block) {
        this.ingredient = ingredient;
        this.block = block;
        this.mainStack = mainStack;
        this.name = name;
    }

    /**
     * Add items that can be created in a brewing stand
     *
     * @return this
     */
    public ItemInfoBuilder brewingItems(Item... brewableItems) {
        this.brewingStacks = Arrays.stream(brewableItems).map(ItemStack::new).toArray(ItemStack[]::new);
        return this;
    }

    /**
     * Add recipes
     *
     * @param ids the ids of the recipes to be displayeed
     */
    public ItemInfoBuilder recipes(ResourceLocation... ids) {
        this.recipes = Arrays.asList(ids);
        return this;
    }

    /**
     * Add recipes
     * String ids are prefixed with vampirism
     *
     * @param vampIDs without namespace prefix
     * @return this
     */
    public ItemInfoBuilder recipes(String... vampIDs) {
        this.recipes = Arrays.stream(vampIDs).map(id -> new ResourceLocation("vampirism", id)).collect(Collectors.toList());
        return this;
    }

    /**
     * Add stacks that can be created in a brewing stand
     *
     * @return this
     */
    public ItemInfoBuilder brewingStacks(ItemStack... brewableStacks) {
        this.brewingStacks = brewableStacks;
        return this;
    }

    /**
     * Builds the entry and adds it to the given map
     */
    public void build(Map<ResourceLocation, EntryAbstract> entries) {
        String base = "guide.vampirism." + (block ? "blocks" : "items") + "." + name;
        ArrayList<IPage> pages = new ArrayList<>(PageHelper.pagesForLongText(GuideBook.translateComponent(base + ".text", formats), ingredient));
        for (ResourceLocation id : recipes) {
            pages.add(GuideHelper.getRecipePage(id));
        }
        if (brewingStacks != null) {
            for (ItemStack brew : brewingStacks) {
                BrewingRecipe r = GuideHelper.getBrewingRecipe(brew);
                if (r == null) {
                    LogManager.getLogger().error("Could not find brewing recipe for {}", brew.toString());
                } else {
                    pages.add(new PageBrewingRecipe(r));
                }
            }
        }
        pages.addAll(this.additionalPages);
        if (links != null) GuideHelper.addLinks(pages, links);
        entries.put(new ResourceLocation(base), new EntryItemStack(pages, new TranslationTextComponent(customName ? base : mainStack.getTranslationKey()), mainStack));
    }

    public ItemInfoBuilder customPages(IPage... additionalPages) {
        this.additionalPages.addAll(Arrays.asList(additionalPages));
        return this;
    }


    /**
     * Use a custom name (guide.vampirism....) instead of the translated Item/Block name
     *
     * @return
     */
    public ItemInfoBuilder customName() {
        customName = true;
        return this;
    }

    /**
     * Adds format arguments which are used when translating the description
     */
    public ItemInfoBuilder setFormats(Object... formats) {
        this.formats = formats;
        return this;
    }

    /**
     * Sets links that are added to the description pages>
     */
    public ItemInfoBuilder setLinks(Object... links) {
        this.links = links;
        return this;
    }

    /**
     * Set's the name used for unloc strings
     */
    public ItemInfoBuilder setName(String name) {
        this.name = name;
        return this;
    }
}
