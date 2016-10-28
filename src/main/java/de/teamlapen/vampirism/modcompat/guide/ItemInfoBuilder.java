package de.teamlapen.vampirism.modcompat.guide;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.entry.EntryItemStack;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to build item or block info pages
 */
public class ItemInfoBuilder {
    private final ItemStack stack;
    private final boolean block;
    private String name;
    private Object[] formats = new Object[0];
    private Object[] links = null;
    private GuideHelper.RECIPE_TYPE recipe_type = null;
    private ItemStack[] craftableStacks = null;
    private boolean ignoreMissingRecipes = false;
    private boolean customName;

    /**
     * @param stack The relevant item stack. Used for display and strings.
     * @param block If this entry is a about a block or not
     */
    public ItemInfoBuilder(ItemStack stack, boolean block) {
        this.stack = stack;
        this.block = block;
        name = stack.getItem().getRegistryName().getResourcePath();
    }

    public ItemInfoBuilder(Item item) {
        this(new ItemStack(item), false);
    }

    public ItemInfoBuilder(Block block) {
        this(new ItemStack(block), true);
    }

    /**
     * Builds the entry and adds it to the given map
     */
    public void build(Map<ResourceLocation, EntryAbstract> entries) {
        ArrayList<IPage> pages = new ArrayList<>();
        String base = "guide.vampirism." + (block ? "blocks" : "items") + "." + name;
        pages.addAll(GuideHelper.pagesForLongText(UtilLib.translateFormatted(base + ".text", formats), stack));
        if (links != null) GuideHelper.addLinks(pages, links);
        if (recipe_type != null) {
            if (craftableStacks == null) {
                craftableStacks = new ItemStack[]{stack};
            }
            for (ItemStack s : craftableStacks) {
                IPage p = GuideHelper.getRecipePage(s, recipe_type);
                if (p != null) {
                    pages.add(p);
                } else {
                    if (!ignoreMissingRecipes)
                        VampirismMod.log.e(GuideBook.TAG, "Failed to find %s recipe for %s", recipe_type, s);
                }
            }
        }
        entries.put(new ResourceLocation(base), new EntryItemStack(pages, customName ? base : stack.getUnlocalizedName() + ".name", stack));
    }

    /**
     * Marks this as craftable
     *
     * @param type The crafting type
     */
    public ItemInfoBuilder craftable(GuideHelper.RECIPE_TYPE type) {
        this.recipe_type = type;
        return this;
    }

    /**
     * Sets the stacks that recipes should be shown.
     * You have to call {@link ItemInfoBuilder#craftable(GuideHelper.RECIPE_TYPE)} to make this have an effect
     */
    public ItemInfoBuilder craftableStacks(ItemStack... craftableStacks) {
        this.craftableStacks = craftableStacks;
        return this;
    }

    /**
     * Sets the stacks that recipes should be shown.
     * You have to call {@link ItemInfoBuilder#craftable(GuideHelper.RECIPE_TYPE)} to make this have an effect
     */
    public ItemInfoBuilder craftableStacks(List<ItemStack> craftableStacks) {
        this.craftableStacks = craftableStacks.toArray(new ItemStack[craftableStacks.size()]);
        return this;
    }

    /**
     * Sets the stacks that recipes should be shown.
     * You have to call {@link ItemInfoBuilder#craftable(GuideHelper.RECIPE_TYPE)} to make this have an effect
     */
    public ItemInfoBuilder craftableStacks(Item... craftableItems) {
        ItemStack[] stacks = new ItemStack[craftableItems.length];
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = new ItemStack(craftableItems[i]);
        }
        craftableStacks(stacks);
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
     * Don't complain about missing recipes
     */
    public ItemInfoBuilder ignoreMissingRecipes() {
        this.ignoreMissingRecipes = true;
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
     * Sets links that are added to the description pages
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
