package de.teamlapen.lib.lib.util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Helper for item/block model registration.
 * Inspired by @TehNut https://github.com/WayofTime/BloodMagic/blob/1.8/src/main/java/WayofTime/bloodmagic/util/helper/InventoryRenderHelperV2.java
 *
 * For blocks that only have the "normal" type, you can use the block methods to declare the item properties in the same file as the block one using "inventory".
 * For blocks that have multiple types, you have to use the item methods using Item.getItemForBlock and specifiy the item models in blockstates/item
 */
@SideOnly(Side.CLIENT)
public class InventoryRenderHelper {
    private final String domain;

    public InventoryRenderHelper(String domain) {
        this.domain = domain;
    }

    /**
     * Register a CustomModelResourceLocation using {@link ModelLoader#setCustomModelResourceLocation(Item, int, ModelResourceLocation)}
     *
     * @param item
     * @param meta
     * @param name
     * @param variant
     */
    public void registerRender(Item item, int meta, String name, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(domain, "item/" + name), "type=" + variant));
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Item, int, String, String)} with registry name
     */
    public void registerRender(Item item, int meta, String variant) {
        registerRender(item, meta, item.getRegistryName().getResourcePath(), variant);
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Item, int, String, String)} with meta 0
     */
    public void registerRender(Item item, String name, String variant) {
        registerRender(item, 0, name, variant);
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Item, int, String, String)} with meta 0 and registry name
     */
    public void registerRender(Item item, String variant) {
        registerRender(item, item.getRegistryName().getResourcePath(), variant);
    }

    /**
     * Register a CustomModelResourceLocation using {@link ModelLoader#setCustomModelResourceLocation(Item, int, ModelResourceLocation)} for the block's item
     */
    public void registerRender(Block block, int meta, String name) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(new ResourceLocation(domain, name), "inventory"));
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Block, int, String)} with registry name
     */
    public void registerRender(Block block, int meta) {
        registerRender(block, meta, block.getRegistryName().getResourcePath());
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Block, int, String)} with meta 0
     */
    public void registerRender(Block block, String name) {
        registerRender(block, 0, name);
    }

    /**
     * Register a item for a block which only has the normal property.
     * Specify the model in the same file as the block under "inventory"
     * @param block
     */
    public void registerRender(Block block){
        registerRender(block, 0, block.getRegistryName().getResourcePath());
    }


    /**
     * {@link InventoryRenderHelper#registerRender(Item, int, String, String)} with registry name for every meta.
     * variant is "meta_"+i
     */
    public void registerRenderAllMeta(Item item, int meta_count) {
        for (int i = 0; i < meta_count; i++) {
            registerRender(item, i, "meta_" + i);
        }
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Item, int, String, String)} with registry name for every variant.
     * each variant represents one meta value
     */
    public void registerRenderAllMeta(Item item, String[] variants) {
        for (int i = 0; i < variants.length; i++) {
            registerRender(item, i, variants[i]);
        }
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Item, int, String, String)} with registry name for every variant.
     * All metas use the same state
     */
    public void registerRenderAllMeta(Item item, int meta_count, String state) {
        for (int i = 0; i < meta_count; i++) {
            registerRender(item, i, state);
        }
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Item, int, String, String)} with registry name for every variant.
     * each variant represents one meta value
     */
    public void registerRenderAllMeta(Item item, IStringSerializable[] variants){
        for (int i=0;i<variants.length;i++){
            registerRender(item,i,variants[i].getName());
        }
    }


}
