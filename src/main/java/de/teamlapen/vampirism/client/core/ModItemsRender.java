package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.InventoryRenderHelper;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import javax.annotation.Nonnull;

/**
 * Handles item render registration
 */
public class ModItemsRender {

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerRenderers();
                break;
            case INIT:
                registerColors();
                break;
        }

    }

    private static void registerColors() {

        //Swiftness armor
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
                if (tintIndex == 0) {
                    return ((ItemArmor) stack.getItem()).getColor(stack);
                } else {
                    switch (ModItems.armorOfSwiftness_boots.getTier(stack)) {
                        case ENHANCED:
                            return 0x007CFF;
                        case ULTIMATE:
                            return 0x07F8FF;
                        default:
                            return 0xFFF100;
                    }
                }
            }
        }, ModItems.armorOfSwiftness_boots, ModItems.armorOfSwiftness_chest, ModItems.armorOfSwiftness_helmet, ModItems.armorOfSwiftness_legs);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
                if (tintIndex == 1) {
                    return ItemCrossbowArrow.getType(stack).color;
                }
                return 0xFFFFFF;
            }
        }, ModItems.crossbowArrow);
    }

    private static void registerRenderers() {
        VampirismMod.log.d("ModItemsRender", "Registering renderer");
        InventoryRenderHelper renderHelper = new InventoryRenderHelper(REFERENCE.MODID);
        renderHelper.registerRender(ModItems.vampireFang, "normal");
        renderHelper.registerRender(ModItems.humanHeart, "normal");
        renderHelper.registerRender(ModItems.humanHeartWeak, "normal");
        renderHelper.registerRender(ModItems.itemTent, "normal");
        renderHelper.registerRenderAllMeta(ModItems.bloodBottle, ItemBloodBottle.AMOUNT + 1);
        renderHelper.registerRender(ModItems.battleAxe, "normal");
        renderHelper.registerRender(ModItems.itemCoffin, "normal");
        renderHelper.registerRenderAllMeta(ModItems.pureBlood, ItemPureBlood.COUNT);
        renderHelper.registerRenderAllMeta(ModItems.hunterIntel, HunterLevelingConf.instance().HUNTER_INTEL_COUNT, "normal");
        renderHelper.registerRender(ModItems.itemGarlic, "normal");
        renderHelper.registerRenderAllMeta(ModItems.injection, ItemInjection.META_COUNT);
        renderHelper.registerRender(ModItems.itemMedChair, "normal");
        renderHelper.registerRender(ModItems.pitchfork, "normal");
        renderHelper.registerRender(ModItems.basicCrossbow, "normal");
        renderHelper.registerRender(ModItems.crossbowArrow, "normal");
        renderHelper.registerRender(ModItems.basicDoubleCrossbow, "normal");
        renderHelper.registerRender(ModItems.enhancedCrossbow, "normal");
        renderHelper.registerRender(ModItems.enhancedDoubleCrossbow, "normal");
        renderHelper.registerRender(ModItems.stake, "normal");
        renderHelper.registerRender(ModItems.vampireBlood, "normal");
        renderHelper.registerRender(ModItems.bloodPotion, "normal");
        renderHelper.registerRender(ModItems.basicTechCrossbow, "normal");
        renderHelper.registerRender(ModItems.enhancedTechCrossbow, "normal");
        renderHelper.registerRender(ModItems.techCrossbowAmmoPackage, "normal");
        renderHelper.registerRender(ModItems.vampireBook, "normal");
        renderHelper.registerRender(ModItems.hunterHat0, "normal");
        renderHelper.registerRender(ModItems.hunterHat1, "normal");
        registerSimpleItemWithTier(ModItems.holyWaterBottle);
        renderHelper.registerRender(ModItems.holySalt, "normal");
        renderHelper.registerRender(ModItems.pureSalt, "normal");
        renderHelper.registerRender(ModItems.holySaltWater, "normal");
        renderHelper.registerRender(ModItems.itemAlchemicalFire, "normal");
        renderHelper.registerRender(ModItems.garlicBeaconCore, "normal");
        renderHelper.registerRender(ModItems.garlicBeaconCoreImproved, "normal");
        renderHelper.registerRender(ModItems.purifiedGarlic, "normal");

        final ResourceLocation holyWaterSplash = new ResourceLocation(REFERENCE.MODID, "item/" + ModItems.holyWaterBottle.getRegistryName().getResourcePath());
        ModelLoader.setCustomMeshDefinition(ModItems.holyWaterBottle, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(holyWaterSplash, "tier=" + ((IItemWithTier) stack.getItem()).getTier(stack) + (((ItemHolyWaterBottle) stack.getItem()).isSplash(stack) ? ",splash" : ""));
            }
        });
        for (IStringSerializable s : IItemWithTier.TIER.values()) {
            ModelLoader.registerItemVariants(ModItems.holyWaterBottle, new ModelResourceLocation(holyWaterSplash, "tier=" + s.getName() + ",splash"));
            ModelLoader.registerItemVariants(ModItems.holyWaterBottle, new ModelResourceLocation(holyWaterSplash, "tier=" + s.getName()));

        }

        registerSimpleItemWithTier(ModItems.hunterAxe);

        registerArmorItemWithTier(ModItems.armorOfSwiftness_helmet, "swiftnessArmor");
        registerArmorItemWithTier(ModItems.armorOfSwiftness_chest, "swiftnessArmor");
        registerArmorItemWithTier(ModItems.armorOfSwiftness_legs, "swiftnessArmor");
        registerArmorItemWithTier(ModItems.armorOfSwiftness_boots, "swiftnessArmor");

        registerArmorItemWithTier(ModItems.hunterCoat_helmet, "hunterCoat");
        registerArmorItemWithTier(ModItems.hunterCoat_chest, "hunterCoat");
        registerArmorItemWithTier(ModItems.hunterCoat_legs, "hunterCoat");
        registerArmorItemWithTier(ModItems.hunterCoat_boots, "hunterCoat");

        registerArmorItemWithTier(ModItems.obsidianArmor_helmet, "obsidianArmor");
        registerArmorItemWithTier(ModItems.obsidianArmor_chest, "obsidianArmor");
        registerArmorItemWithTier(ModItems.obsidianArmor_legs, "obsidianArmor");
        registerArmorItemWithTier(ModItems.obsidianArmor_boots, "obsidianArmor");


        //----------------------
    }

    /**
     * Registers all variants of an {@link IItemWithTier} as well as the custom mesh definition
     * Only works with items that only have variants based on tier
     */
    private static void registerSimpleItemWithTier(IItemWithTier itemWithTier) {
        Item item = (Item) itemWithTier;
        ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "item/" + item.getRegistryName().getResourcePath());
        ModelLoader.setCustomMeshDefinition(item, new IItemWithTier.SimpleMeshDefinition(loc));
        for (IStringSerializable s : IItemWithTier.TIER.values()) {
            ModelLoader.registerItemVariants(item, new ModelResourceLocation(loc, "tier=" + s.getName()));
        }
    }

    /**
     * Registers all variants of an {@link IItemWithTier} ItemArmor as well as the custom mesh definition
     * Only works with items that only have variants based on tier
     */
    private static void registerArmorItemWithTier(IItemWithTier armorWithTier, String baseName) {
        ItemArmor item = (ItemArmor) armorWithTier;
        ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "item/" + baseName);
        ModelLoader.setCustomMeshDefinition(item, new IItemWithTier.ArmorMeshDefinition(loc));
        for (IStringSerializable s : IItemWithTier.TIER.values()) {
            ModelLoader.registerItemVariants(item, new ModelResourceLocation(loc, "part=" + item.armorType.getName() + "_" + s.getName()));
        }
    }


}
