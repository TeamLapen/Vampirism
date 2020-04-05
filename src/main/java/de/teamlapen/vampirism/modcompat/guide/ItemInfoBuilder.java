package de.teamlapen.vampirism.modcompat.guide;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.entry.EntryItemStack;
import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class to build item or block info pages
 */
public class ItemInfoBuilder {
    private final ItemStack stack;
    private final boolean block;
    private String name;
    private Object[] formats = new Object[0];
    private Object[] links = null;
    private boolean customName;

    @Nonnull
    private List<ResourceLocation> recipes = Collections.emptyList();

    /**
     * @param stack The relevant item stack. Used for display and strings.
     * @param block If this entry is a about a block or not
     */
    public ItemInfoBuilder(ItemStack stack, boolean block) {
        assert !stack.isEmpty();
        this.stack = stack;
        this.block = block;
        name = stack.getItem().getRegistryName().getPath();
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
        pages.addAll(GuideHelper.pagesForLongText(UtilLib.translate(base + ".text", formats), stack));
        for (ResourceLocation id : recipes) {
            pages.add(GuideHelper.getRecipePage(id));
        }
        if (links != null) GuideHelper.addLinks(pages, links);
        entries.put(new ResourceLocation(base), new EntryItemStack(pages, customName ? base : stack.getTranslationKey(), stack));
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
