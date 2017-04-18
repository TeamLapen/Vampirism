package de.teamlapen.vampirism.modcompat.guide;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.entry.EntryItemStack;
import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Utility class to build item or block info pages
 */
public class ItemInfoBuilder {
    private final ItemStack stack;
    private final boolean block;
    private String name;
    private Object[] formats = new Object[0];
    private Object[] links = null;
    private boolean ignoreMissingRecipes = false;
    private boolean customName;

    private List<Pair<ItemStack, GuideHelper.RECIPE_TYPE>> craftableStacks = null;

    /**
     * @param stack The relevant item stack. Used for display and strings.
     * @param block If this entry is a about a block or not
     */
    public ItemInfoBuilder(ItemStack stack, boolean block) {
        assert !ItemStackUtil.isEmpty(stack);
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
        if (craftableStacks != null) {

            for (Pair<ItemStack, GuideHelper.RECIPE_TYPE> craft : craftableStacks) {
                IPage p = GuideHelper.getRecipePage(craft.getLeft(), craft.getRight());
                if (p != null) {
                    pages.add(p);
                } else {
                    if (!ignoreMissingRecipes)
                        VampirismMod.log.e(GuideBook.TAG, "Failed to find %s recipe for %s", craft.getRight(), craft.getLeft());
                }
            }
        }
        if (links != null) GuideHelper.addLinks(pages, links);
        entries.put(new ResourceLocation(base), new EntryItemStack(pages, customName ? base : stack.getUnlocalizedName() + ".name", stack, true));
    }

    /**
     * Marks this as craftable
     *
     * @param type The crafting type
     */
    public ItemInfoBuilder craftable(GuideHelper.RECIPE_TYPE type) {
        this.craftableStacks = Collections.singletonList(ImmutablePair.of(stack, type));
        return this;
    }

    /**
     * Sets the craftable stacks/blocks/items whose recipes should be shown in this item entry.
     * You have to specify the recipe type for each craftable.
     * Pass Itemstack,RecipeType,ItemStack,RecipeType... or Item,RecipeType... or Block,RecipeType...
     */
    public ItemInfoBuilder craftableStacks(List<Object> list) {
        int length = list.size();
        if (length % 2 != 0) {
            VampirismMod.log.w("GuideBook", "Arguments: %s", list);
            throw new IllegalArgumentException("You have to provide a recipe type after each craftable");
        }
        this.craftableStacks = Lists.newArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            ItemStack stack;
            Object craftable = it.next();
            if (craftable instanceof ItemStack) {
                stack = (ItemStack) craftable;
            } else if (craftable instanceof Item) {
                stack = new ItemStack((Item) craftable);
            } else if (craftable instanceof Block) {
                stack = new ItemStack((Block) craftable);
            } else {
                VampirismMod.log.w("GuideBook", "Arguments: %s", list);
                throw new IllegalArgumentException("Inputs have to be items or blocks or stacks");
            }
            Object type = it.next();
            if (!(type instanceof GuideHelper.RECIPE_TYPE)) {
                VampirismMod.log.w("GuideBook", "Arguments: %s", list);
                throw new IllegalArgumentException("You have to provide a recipe type after each craftable");
            }
            craftableStacks.add(ImmutablePair.of(stack, (GuideHelper.RECIPE_TYPE) type));

        }
        return this;
    }

    /**
     * Sets the craftable stacks/blocks/items whose recipes should be shown in this item entry.
     * You have to specify the recipe type for each craftable.
     * Pass Itemstack,RecipeType,ItemStack,RecipeType... or Item,RecipeType... or Block,RecipeType...
     */
    public ItemInfoBuilder craftableStacks(Object... stacks) {
        return craftableStacks(Arrays.asList(stacks));
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
