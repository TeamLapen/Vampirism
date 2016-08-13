package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.InventoryRenderHelper;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ItemArmorOfSwiftness;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import de.teamlapen.vampirism.items.ItemInjection;
import de.teamlapen.vampirism.items.ItemPureBlood;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLStateEvent;

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
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                if (tintIndex == 0) {
                    return ((ItemArmor) stack.getItem()).getColor(stack);
                } else {
                    switch (ItemArmorOfSwiftness.getType(stack)) {
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

        //Swiftness Armor
        final ResourceLocation swiftnessArmorLoc = new ResourceLocation(REFERENCE.MODID, "item/swiftnessArmor");
        ItemMeshDefinition swiftnessArmor_meshDefinition = new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                String type = ItemArmorOfSwiftness.getType(stack).getName();
                String part = ((ItemArmor) stack.getItem()).armorType.getName();
                return new ModelResourceLocation(swiftnessArmorLoc, "type=" + part + "_" + type);
            }
        };
        ModelLoader.setCustomMeshDefinition(ModItems.armorOfSwiftness_helmet, swiftnessArmor_meshDefinition);
        ModelLoader.setCustomMeshDefinition(ModItems.armorOfSwiftness_chest, swiftnessArmor_meshDefinition);
        ModelLoader.setCustomMeshDefinition(ModItems.armorOfSwiftness_legs, swiftnessArmor_meshDefinition);
        ModelLoader.setCustomMeshDefinition(ModItems.armorOfSwiftness_boots, swiftnessArmor_meshDefinition);
        registerArmorVariants(ModItems.armorOfSwiftness_helmet, swiftnessArmorLoc, EntityEquipmentSlot.HEAD, (IStringSerializable[]) ItemArmorOfSwiftness.TYPE.values());
        registerArmorVariants(ModItems.armorOfSwiftness_chest, swiftnessArmorLoc, EntityEquipmentSlot.CHEST, (IStringSerializable[]) ItemArmorOfSwiftness.TYPE.values());
        registerArmorVariants(ModItems.armorOfSwiftness_legs, swiftnessArmorLoc, EntityEquipmentSlot.LEGS, (IStringSerializable[]) ItemArmorOfSwiftness.TYPE.values());
        registerArmorVariants(ModItems.armorOfSwiftness_boots, swiftnessArmorLoc, EntityEquipmentSlot.FEET, (IStringSerializable[]) ItemArmorOfSwiftness.TYPE.values());

        //----------------------
    }

    private static void registerArmorVariants(ItemArmor item, ResourceLocation base, EntityEquipmentSlot slot, IStringSerializable... types) {

        for (IStringSerializable s : types) {
            ModelLoader.registerItemVariants(item, new ModelResourceLocation(base, String.format("type=%s_%s", slot.getName(), s.getName())));
        }
    }
}
