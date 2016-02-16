package de.teamlapen.lib.lib.util;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Helper for item/block model registration.
 * Inspired by @TehNut https://github.com/WayofTime/BloodMagic/blob/1.8/src/main/java/WayofTime/bloodmagic/util/helper/InventoryRenderHelperV2.java
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
        registerRender(item, meta, item.getRegistryName().split(":")[1], variant);
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
     * {@link InventoryRenderHelper#registerRender(Item, int, String, String)} with meta 0
     */
    public void registerRender(Item item, String name, String variant) {
        registerRender(item, 0, name, variant);
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Item, int, String, String)} with meta 0 and registry name
     */
    public void registerRender(Item item, String variant) {
        registerRender(item, item.getRegistryName().split(":")[1], variant);
    }

    /**
     * Register a CustomModelResourceLocation using {@link ModelLoader#setCustomModelResourceLocation(Item, int, ModelResourceLocation)} for the block's item
     */
    public void registerRender(Block block, int meta, String name, String variant) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(new ResourceLocation(domain, name), variant));
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Block, int, String, String)} with registry name
     */
    public void registerRender(Block block, int meta, String variant) {
        registerRender(block, meta, block.getRegistryName().split(":")[1], variant);
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Block, int, String, String)} with meta 0
     */
    public void registerRender(Block block, String name, String variant) {
        registerRender(block, 0, name, variant);
    }

    /**
     * {@link InventoryRenderHelper#registerRender(Block, int, String, String)} with registry name and meta 0
     */
    public void registerRender(Block block, String variant) {
        registerRender(block, block.getRegistryName().split(":")[1], variant);
    }

}
