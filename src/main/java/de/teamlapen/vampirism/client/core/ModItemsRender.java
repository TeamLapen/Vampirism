package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.InventoryRenderHelper;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import de.teamlapen.vampirism.items.ItemCrossbowArrow;
import de.teamlapen.vampirism.items.ItemInjection;
import de.teamlapen.vampirism.items.ItemPureBlood;
import de.teamlapen.vampirism.items.ItemVampireCloak.EnumCloakColor;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.util.REFERENCE;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles item render registration
 */
@SideOnly(Side.CLIENT)
public class ModItemsRender {

	public static void register() {
		registerRenderers();
	}

	static void registerColors() {

		// Swiftness armor
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			if (tintIndex == 0) {
				return ((ItemArmor) stack.getItem()).getColor(stack);
			} else {
				switch (ModItems.armor_of_swiftness_feet.getTier(stack)) {
					case ENHANCED:
						return 0x007CFF;
					case ULTIMATE:
						return 0x07F8FF;
					default:
						return 0xFFF100;
				}
			}
		}, ModItems.armor_of_swiftness_feet, ModItems.armor_of_swiftness_chest, ModItems.armor_of_swiftness_head, ModItems.armor_of_swiftness_legs);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			if (tintIndex == 1) {
				return ItemCrossbowArrow.getType(stack).color;
			}
			return 0xFFFFFF;
		}, ModItems.crossbow_arrow);
	}

	private static void registerRenderers() {
		VampirismMod.log.d("ModItemsRender", "Registering renderer");
		InventoryRenderHelper renderHelper = new InventoryRenderHelper(REFERENCE.MODID);
		renderHelper.registerRender(ModItems.vampire_fang, "normal");
		renderHelper.registerRender(ModItems.human_heart, "normal");
		renderHelper.registerRender(ModItems.weak_human_heart, "normal");
		renderHelper.registerRender(ModItems.item_tent, "normal");
		renderHelper.registerRenderAllMeta(ModItems.blood_bottle, ItemBloodBottle.AMOUNT + 1);
		renderHelper.registerRender(ModItems.item_coffin, "normal");
		renderHelper.registerRenderAllMeta(ModItems.pure_blood, ItemPureBlood.COUNT);
		renderHelper.registerRenderAllMeta(ModItems.hunter_intel, HunterLevelingConf.instance().HUNTER_INTEL_COUNT, "normal");
		renderHelper.registerRender(ModItems.item_garlic, "normal");
		renderHelper.registerRenderAllMeta(ModItems.injection, ItemInjection.META_COUNT);
		renderHelper.registerRender(ModItems.item_med_chair, "normal");
		renderHelper.registerRender(ModItems.pitchfork, "normal");
		renderHelper.registerRender(ModItems.basic_crossbow, "normal");
		renderHelper.registerRender(ModItems.crossbow_arrow, "normal");
		renderHelper.registerRender(ModItems.basic_double_crossbow, "normal");
		renderHelper.registerRender(ModItems.enhanced_crossbow, "normal");
		renderHelper.registerRender(ModItems.enhanced_double_crossbow, "normal");
		renderHelper.registerRender(ModItems.stake, "normal");
		renderHelper.registerRender(ModItems.vampire_blood_bottle, "normal");
		renderHelper.registerRender(ModItems.blood_potion, "normal");
		renderHelper.registerRender(ModItems.basic_tech_crossbow, "normal");
		renderHelper.registerRender(ModItems.enhanced_tech_crossbow, "normal");
		renderHelper.registerRender(ModItems.tech_crossbow_ammo_package, "normal");
		renderHelper.registerRender(ModItems.vampire_book, "normal");
		renderHelper.registerRender(ModItems.hunter_hat0_head, "normal");
		renderHelper.registerRender(ModItems.hunter_hat1_head, "normal");
		registerSimpleItemWithTier(ModItems.holy_water_bottle);
		renderHelper.registerRender(ModItems.holy_salt, "normal");
		renderHelper.registerRender(ModItems.pure_salt, "normal");
		renderHelper.registerRender(ModItems.holy_salt_water, "normal");
		renderHelper.registerRender(ModItems.item_alchemical_fire, "normal");
		renderHelper.registerRender(ModItems.garlic_beacon_core, "normal");
		renderHelper.registerRender(ModItems.garlic_beacon_core_improved, "normal");
		renderHelper.registerRender(ModItems.purified_garlic, "normal");

		registerSimpleItemWithTier(ModItems.holy_water_bottle);
		registerSimpleItemWithTier(ModItems.holy_water_splash_bottle);

		registerSimpleItemWithTier(ModItems.hunter_axe);

		registerArmorItemWithTier(ModItems.armor_of_swiftness_head, "swiftness_armor");
		registerArmorItemWithTier(ModItems.armor_of_swiftness_chest, "swiftness_armor");
		registerArmorItemWithTier(ModItems.armor_of_swiftness_legs, "swiftness_armor");
		registerArmorItemWithTier(ModItems.armor_of_swiftness_feet, "swiftness_armor");

		registerArmorItemWithTier(ModItems.hunter_coat_head, "hunter_coat");
		registerArmorItemWithTier(ModItems.hunter_coat_chest, "hunter_coat");
		registerArmorItemWithTier(ModItems.hunter_coat_legs, "hunter_coat");
		registerArmorItemWithTier(ModItems.hunter_coat_feet, "hunter_coat");

		registerArmorItemWithTier(ModItems.obsidian_armor_head, "obsidian_armor");
		registerArmorItemWithTier(ModItems.obsidian_armor_chest, "obsidian_armor");
		registerArmorItemWithTier(ModItems.obsidian_armor_legs, "obsidian_armor");
		registerArmorItemWithTier(ModItems.obsidian_armor_feet, "obsidian_armor");

		registerSimpleItemWithTier(ModItems.heart_seeker);
		registerSimpleItemWithTier(ModItems.heart_striker);
		renderHelper.registerRender(ModItems.blood_infused_iron_ingot, "normal");
		renderHelper.registerRender(ModItems.blood_infused_enhanced_iron_ingot, "normal");
		renderHelper.registerRender(ModItems.soul_orb_vampire, "normal");

        registerVampireCloakWithColor(ModItems.vampire_cloak, "vampire_cloak");

		// ----------------------
	}

	/**
	 * Registers all variants of an {@link IItemWithTier} as well as the custom mesh
	 * definition Only works with items that only have variants based on tier
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
	 * Registers all variants of an {@link IItemWithTier} ItemArmor as well as the
	 * custom mesh definition Only works with items that only have variants based on
	 * tier
	 */
	private static void registerArmorItemWithTier(IItemWithTier armorWithTier, String baseName) {
		ItemArmor item = (ItemArmor) armorWithTier;
		ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "item/" + baseName);
		ModelLoader.setCustomMeshDefinition(item, new IItemWithTier.ArmorMeshDefinition(loc));
		for (IStringSerializable s : IItemWithTier.TIER.values()) {
			ModelLoader.registerItemVariants(item, new ModelResourceLocation(loc, "part=" + item.armorType.getName() + "_" + s.getName()));
		}
	}

	/**
     * Register all variants of an Item based on {@link EnumCloakColor} Only works
     * with vampirecloaks
     */
    private static void registerVampireCloakWithColor(Item item, String baseName) {
        for (EnumCloakColor e : EnumCloakColor.values()) {
			ModelLoader.setCustomModelResourceLocation(item, e.getMetadata(), new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, "item/" + baseName), "color=" + e.getDyeColorName()));
		}
	}

}
