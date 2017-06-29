package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Handles all item registrations and reference.
 */
@GameRegistry.ObjectHolder(REFERENCE.MODID)
public class ModItems {

    public static final ItemVampireFang vampire_fang = getNull();
    public static final ItemHumanHeart human_heart = getNull();
    public static final ItemHumanHeartWeak weak_human_heart = getNull();
    public static final ItemBloodBottle blood_bottle = getNull();
    public static final ItemTent item_tent = getNull();
    public static final ItemCoffin item_coffin = getNull();
    public static final ItemPureBlood pure_blood = getNull();
    public static final ItemHunterIntel hunter_intel = getNull();
    public static final ItemGarlic item_garlic = getNull();
    public static final ItemInjection injection = getNull();
    public static final ItemMedChair item_med_chair = getNull();
    public static final ItemPitchfork pitchfork = getNull();
    public static final ItemSimpleCrossbow basic_crossbow = getNull();
    public static final ItemDoubleCrossbow basic_double_crossbow = getNull();
    public static final ItemSimpleCrossbow enhanced_crossbow = getNull();
    public static final ItemDoubleCrossbow enhanced_double_crossbow = getNull();
    public static final ItemCrossbowArrow crossbow_arrow = getNull();
    public static final ItemStake stake = getNull();
    public static final ItemVampireBloodBottle vampire_blood_bottle = getNull();
    public static final ItemBloodPotion blood_potion = getNull();
    public static final ItemTechCrossbow basic_tech_crossbow = getNull();
    public static final ItemTechCrossbow enhanced_tech_crossbow = getNull();
    public static final VampirismItem tech_crossbow_ammo_package = getNull();
    public static final ItemVampireBook vampire_book = getNull();
    public static final ItemHolyWaterBottle holy_water_bottle = getNull();
    public static final VampirismItem holy_salt = getNull();
    public static final VampirismItem holy_salt_water = getNull();
    public static final ItemAlchemicalFire item_alchemical_fire = getNull();
    public static final VampirismItem garlic_beacon_core = getNull();
    public static final VampirismItem garlic_beacon_core_improved = getNull();
    public static final VampirismItem purified_garlic = getNull();
    public static final VampirismItem pure_salt = getNull();

    public static final ItemArmorOfSwiftness armor_of_swiftness_head = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_chest = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_legs = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_feet = getNull();

    public static final ItemHunterCoat hunter_coat_head = getNull();
    public static final ItemHunterCoat hunter_coat_chest = getNull();
    public static final ItemHunterCoat hunter_coat_legs = getNull();
    public static final ItemHunterCoat hunter_coat_feet = getNull();

    public static final ItemObsidianArmor obsidian_armor_head = getNull();
    public static final ItemObsidianArmor obsidian_armor_chest = getNull();
    public static final ItemObsidianArmor obsidian_armor_legs = getNull();
    public static final ItemObsidianArmor obsidian_armor_feet = getNull();

    public static final ItemHunterHat hunter_hat0_head = getNull();
    public static final ItemHunterHat hunter_hat1_head = getNull();

    public static final ItemHunterAxe hunter_axe = getNull();


    @SuppressWarnings("ConstantConditions")
    private static @Nonnull
    <T> T getNull() {
        return null;
    }
    
    static void registerCraftingRecipes() {
        //TODO CRAFTING
//        HunterWeaponCraftingManager weaponCraftingManager = HunterWeaponCraftingManager.getInstance();
//        AlchemicalCauldronCraftingManager cauldronCraftingManager = AlchemicalCauldronCraftingManager.getInstance();
//        if (!Configs.autoConvertGlasBottles) {
//            GameRegistry.addRecipe(new ItemStack(bloodBottle, 1, 0), "   ", "XYX", " X ", 'X', Blocks.GLASS, 'Y', Items.ROTTEN_FLESH);
//        }
//        weaponCraftingManager.addRecipe(new ItemStack(ModItems.basicCrossbow), 1, (ISkill<IHunterPlayer>) null, 1, "YXXY", " ZZ ", " ZZ ", 'X', Items.IRON_INGOT, 'Y', Items.STRING, 'Z', Blocks.PLANKS);
//        weaponCraftingManager.addRecipe(new ItemStack(ModItems.basicDoubleCrossbow), 1, HunterSkills.doubleCrossbow, 1, "YXXY", "YXXY", " ZZ ", " ZZ ", 'X', Items.IRON_INGOT, 'Y', Items.STRING, 'Z', Blocks.PLANKS);
//        weaponCraftingManager.addRecipe(new ItemStack(ModItems.enhancedCrossbow), 1, HunterSkills.enhancedCrossbow, 2, "YXXY", " XX ", " XX", 'X', Items.IRON_INGOT, 'Y', Items.STRING);
//        weaponCraftingManager.addRecipe(new ItemStack(ModItems.enhancedDoubleCrossbow), 1, new ISkill[]{HunterSkills.doubleCrossbow, HunterSkills.enhancedCrossbow}, 3, "YXXY", "YXXY", " XX ", " XX ", 'X', Items.IRON_INGOT, 'Y', Items.STRING);
//        GameRegistry.addShapedRecipe(ItemCrossbowArrow.setType(new ItemStack(ModItems.crossbowArrow, 6), ItemCrossbowArrow.EnumArrowType.NORMAL), "X", "Y", 'X', Items.IRON_INGOT, 'Y', Items.STICK);
//        weaponCraftingManager.addRecipe(ItemCrossbowArrow.setType(new ItemStack(crossbowArrow, 2), ItemCrossbowArrow.EnumArrowType.VAMPIRE_KILLER), 1, (ISkill<IHunterPlayer>) null, 1, " X  ", "XYX ", " Z  ", " W  ", 'X', itemGarlic, 'Y', Items.GOLD_INGOT, 'Z', Items.STICK, 'W', Items.FEATHER);
//        weaponCraftingManager.addRecipe(new ItemStack(techCrossbowAmmoPackage), 1, (ISkill<IHunterPlayer>) null, 1, " XZ ", "YYYY", "YYYY", "YYYY", 'X', Items.IRON_INGOT, 'Y', crossbowArrow, 'Z', Blocks.PLANKS);
//        RecipeSorter.register("vampirism:shapelessFillCrossbow", ItemTechCrossbow.ShapelessFillRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
//        GameRegistry.addRecipe(new ItemTechCrossbow.ShapelessFillRecipe(basicTechCrossbow, new ItemStack(techCrossbowAmmoPackage)));
//        GameRegistry.addRecipe(new ItemTechCrossbow.ShapelessFillRecipe(enhancedTechCrossbow, new ItemStack(techCrossbowAmmoPackage)));
//        weaponCraftingManager.addRecipe(new ItemStack(basicTechCrossbow), 1, HunterSkills.techWeapons, 5, "XYYX", "YZZY", " YY ", " YY ", 'X', Items.STRING, 'Y', Items.IRON_INGOT, 'Z', Items.DIAMOND);
//        weaponCraftingManager.addRecipe(new ItemStack(enhancedTechCrossbow), 1, HunterSkills.techWeapons, 5, "XYYX", "YZZY", "YZZY", " YY ", 'X', Items.STRING, 'Y', Items.IRON_INGOT, 'Z', Items.DIAMOND);
//        GameRegistry.addShapedRecipe(new ItemStack(stake), " X ", " Y ", " X ", 'X', Items.STICK, 'Y', Blocks.PLANKS);
//        weaponCraftingManager.addRecipe(new ItemStack(pitchfork), 1, (ISkill<IHunterPlayer>) null, 0, "X X ", "YYY ", " Y  ", " Y  ", 'X', Items.IRON_INGOT, 'Y', Items.STICK);
//        //Armor of Swiftness
//        weaponCraftingManager.addRecipe(createStack(armorOfSwiftness_helmet, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 0, "XXXX", "XYYX", "XZZX", "    ", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.POTIONITEM);
//        weaponCraftingManager.addRecipe(createStack(armorOfSwiftness_chest, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 0, "XZZX", "XXXX", "XYYX", "XXXX", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.POTIONITEM);
//        weaponCraftingManager.addRecipe(createStack(armorOfSwiftness_legs, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 0, "XXXX", "XYYX", "XZZX", "X  X", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.POTIONITEM);
//        weaponCraftingManager.addRecipe(createStack(armorOfSwiftness_boots, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 0, "    ", "XXXX", "XYYX", "XZZX", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.POTIONITEM);
//        //Armor of Swiftness Enhanced
//        weaponCraftingManager.addRecipe(createStack(armorOfSwiftness_helmet, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 3, "XXXX", "XYYX", "XZZX", "    ", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.GOLD_INGOT);
//        weaponCraftingManager.addRecipe(createStack(armorOfSwiftness_chest, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 3, "XZZX", "XXXX", "XYYX", "XXXX", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.GOLD_INGOT);
//        weaponCraftingManager.addRecipe(createStack(armorOfSwiftness_legs, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 3, "XXXX", "XYYX", "XZZX", "X  X", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.GOLD_INGOT);
//        weaponCraftingManager.addRecipe(createStack(armorOfSwiftness_boots, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 3, "    ", "XXXX", "XYYX", "XZZX", 'X', Items.LEATHER, 'Y', ModItems.itemGarlic, 'Z', Items.GOLD_INGOT);
//        //Hunter hats
//        weaponCraftingManager.addRecipe(new ItemStack(ModItems.hunterHat0), 1, (ISkill<IHunterPlayer>) null, 0, "    ", " XX ", "YYYY", "    ", 'X', new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BLACK.getMetadata()), 'Y', Items.IRON_INGOT);
//        weaponCraftingManager.addRecipe(new ItemStack(ModItems.hunterHat1), 1, (ISkill<IHunterPlayer>) null, 0, "    ", " XX ", " XX ", "YYYY", 'X', new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BLACK.getMetadata()), 'Y', Items.IRON_INGOT);
//        //Hunter Axe
//        weaponCraftingManager.addRecipe(createStack(hunterAxe, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 3, "XYX ", "XYX ", "XYX ", " Y  ", 'X', Items.IRON_INGOT, 'Y', Items.STICK);
//        weaponCraftingManager.addRecipe(createStack(hunterAxe, IItemWithTier.TIER.ENHANCED), 1, (ISkill<IHunterPlayer>) null, 5, "XZX ", "XZX ", "XYX ", " Y  ", 'X', Items.IRON_INGOT, 'Y', Items.STICK, 'Z', Items.DIAMOND);
//        //Hunter Coat
//        weaponCraftingManager.addRecipe(createStack(hunterCoat_helmet, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 2, "YXXY", "YZZY", "YZZY", "    ", 'X', Items.LEATHER, 'Y', Items.IRON_INGOT, 'Z', ModItems.itemGarlic);
//        weaponCraftingManager.addRecipe(createStack(hunterCoat_chest, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 2, "YWWY", "YZZY", "YZZY", "YXXY", 'X', Items.LEATHER, 'Y', Items.IRON_INGOT, 'Z', ModItems.itemGarlic, 'W', ModItems.vampireFang);
//        weaponCraftingManager.addRecipe(createStack(hunterCoat_legs, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 2, "YYYY", "YZZY", "YZZY", "Y  Y", 'X', Items.LEATHER, 'Y', Items.IRON_INGOT, 'Z', ModItems.itemGarlic);
//        weaponCraftingManager.addRecipe(createStack(hunterCoat_boots, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 2, "    ", "Y  Y", "YZZY", "YXXY", 'X', Items.LEATHER, 'Y', Items.IRON_INGOT, 'Z', ModItems.itemGarlic);
//        //Hunter Coat Enhanced
//        weaponCraftingManager.addRecipe(createStack(hunterCoat_helmet, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 5, "YXXY", "YZZY", "YZZY", "    ", 'X', Items.DIAMOND, 'Y', Items.IRON_INGOT, 'Z', ModItems.itemGarlic);
//        weaponCraftingManager.addRecipe(createStack(hunterCoat_chest, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 5, "YWWY", "YZZY", "YXXY", "YXXY", 'X', Items.DIAMOND, 'Y', Items.IRON_INGOT, 'Z', ModItems.itemGarlic, 'W', ModItems.vampireFang);
//        weaponCraftingManager.addRecipe(createStack(hunterCoat_legs, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 5, "YWWY", "YZZY", "YZZY", "YWWY", 'X', Items.LEATHER, 'Y', Items.IRON_INGOT, 'Z', ModItems.itemGarlic, 'W', Items.DIAMOND);
//        weaponCraftingManager.addRecipe(createStack(hunterCoat_boots, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 5, "    ", "Y  Y", "YZZY", "YXXY", 'X', Items.DIAMOND, 'Y', Items.IRON_INGOT, 'Z', ModItems.itemGarlic);
//        //Obsidian Armor
//        weaponCraftingManager.addRecipe(createStack(obsidianArmor_helmet, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 5, "XXXX", "XYYX", "XYYX", "    ", 'X', Items.IRON_INGOT, 'Y', Blocks.OBSIDIAN);
//        weaponCraftingManager.addRecipe(createStack(obsidianArmor_chest, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 5, "ZXXZ", "XYYX", "XYYX", "XYYX", 'X', Items.IRON_INGOT, 'Y', Blocks.OBSIDIAN, 'Z', Items.LEATHER);
//        weaponCraftingManager.addRecipe(createStack(obsidianArmor_legs, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 5, "XXXX", "XYYX", "XYYX", "XYYX", 'X', Items.IRON_INGOT, 'Y', Blocks.OBSIDIAN);
//        weaponCraftingManager.addRecipe(createStack(obsidianArmor_boots, IItemWithTier.TIER.NORMAL), 1, (ISkill<IHunterPlayer>) null, 5, "    ", "X  X", "XYYX", "XYYX", 'X', Items.IRON_INGOT, 'Y', Blocks.OBSIDIAN);
//        //Obsidian Armor Enhanced
//        weaponCraftingManager.addRecipe(createStack(obsidianArmor_helmet, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 5, "XDDX", "XYYX", "XYYX", "    ", 'X', Items.IRON_INGOT, 'Y', Blocks.OBSIDIAN, 'D', Items.DIAMOND);
//        weaponCraftingManager.addRecipe(createStack(obsidianArmor_chest, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 5, "ZXXZ", "DYYD", "XYYX", "DYYD", 'X', Items.IRON_INGOT, 'Y', Blocks.OBSIDIAN, 'Z', Items.LEATHER, 'D', Items.DIAMOND);
//        weaponCraftingManager.addRecipe(createStack(obsidianArmor_legs, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 5, "XDDX", "XYYX", "XYYX", "XYYX", 'X', Items.IRON_INGOT, 'Y', Blocks.OBSIDIAN, 'D', Items.DIAMOND);
//        weaponCraftingManager.addRecipe(createStack(obsidianArmor_boots, IItemWithTier.TIER.ENHANCED), 1, HunterSkills.enhancedArmor, 5, "    ", "XDDX", "XYYX", "XYYX", 'X', Items.IRON_INGOT, 'Y', Blocks.OBSIDIAN, 'D', Items.DIAMOND);
//
//        ItemHolyWaterBottle.registerSplashRecipes(holyWaterBottle, IItemWithTier.TIER.NORMAL);
//        ItemHolyWaterBottle.registerSplashRecipes(holyWaterBottle, IItemWithTier.TIER.ENHANCED);
//        ItemHolyWaterBottle.registerSplashRecipes(holyWaterBottle, IItemWithTier.TIER.ULTIMATE);
//        GameRegistry.addShapelessRecipe(new ItemStack(holySaltWater), holySalt, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER));
//        weaponCraftingManager.addShapelessRecipe(ItemCrossbowArrow.setType(new ItemStack(crossbowArrow, 2), ItemCrossbowArrow.EnumArrowType.SPITFIRE), 1, (ISkill<IHunterPlayer>) null, 2, ModItems.crossbowArrow, ModItems.itemAlchemicalFire, ModItems.crossbowArrow);
//
//        cauldronCraftingManager.registerLiquidColor(ModItems.holyWaterBottle, 0x6666FF);
//        cauldronCraftingManager.registerLiquidColor(ModItems.itemGarlic, 0xBBBBBB);
//        cauldronCraftingManager.addRecipe(new ItemStack(ModItems.itemAlchemicalFire, 4), ModItems.holyWaterBottle.getStack(IItemWithTier.TIER.NORMAL), Items.GUNPOWDER).setRequirements(1, HunterSkills.basic_alchemy);
//        cauldronCraftingManager.addRecipe(new ItemStack(ModItems.purifiedGarlic, 2), ModItems.holyWaterBottle.getStack(IItemWithTier.TIER.NORMAL), new ItemStack(ModItems.itemGarlic, 4)).setRequirements(1, HunterSkills.purifiedGarlic);
//        cauldronCraftingManager.addRecipe(new ItemStack(ModItems.garlicBeaconCore), ModItems.itemGarlic, Blocks.WOOL).setRequirements(1, HunterSkills.garlicBeacon);
//        cauldronCraftingManager.addRecipe(ModItems.garlicBeaconCoreImproved, ModItems.holyWaterBottle.getStack(IItemWithTier.TIER.ULTIMATE), ModItems.garlicBeaconCore).setRequirements(1, HunterSkills.garlicBeacon_improved).setExperience(2F);
//        cauldronCraftingManager.addRecipe(new ItemStack(ModItems.pureSalt, 4), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), null).setRequirements(1, HunterSkills.basic_alchemy).setCookingTime(20 * 60);
    }

    public static ItemStack createStack(IItemWithTier item, IItemWithTier.TIER tier) {
        return item.setTier(new ItemStack((Item) item), tier);
    }

    static void registerItems(IForgeRegistry<Item> registry) {
        registry.register(new ItemVampireFang());
        registry.register(new ItemHumanHeart());
        registry.register(new ItemHumanHeartWeak());
        registry.register(new ItemBloodBottle());
        registry.register(new ItemTent());
        registry.register(new ItemCoffin());
        registry.register(new ItemPureBlood());
        registry.register(new ItemHunterIntel());
        registry.register(new ItemGarlic());
        registry.register(new ItemMedChair());
        registry.register(new ItemInjection());
        registry.register(new ItemPitchfork());
        ItemSimpleCrossbow basic_crossbow = new ItemSimpleCrossbow("basic_crossbow", 1, 20, 300);
        basic_crossbow.setEnchantability(Item.ToolMaterial.WOOD);
        registry.register(basic_crossbow);
        ItemDoubleCrossbow basic_double_crossbow = new ItemDoubleCrossbow("basic_double_crossbow", 1, 20, 300);
        basic_double_crossbow.setEnchantability(Item.ToolMaterial.WOOD);
        registry.register(basic_double_crossbow);
        ItemSimpleCrossbow enhanced_crossbow = new ItemSimpleCrossbow("enhanced_crossbow", 1.5F, 15, 350);
        enhanced_crossbow.setEnchantability(Item.ToolMaterial.IRON);
        registry.register(enhanced_crossbow);
        ItemDoubleCrossbow enhanced_double_crossbow = new ItemDoubleCrossbow("enhanced_double_crossbow", 1.5F, 15, 350);
        enhanced_double_crossbow.setEnchantability(Item.ToolMaterial.IRON);
        registry.register(enhanced_double_crossbow);
        registry.register(new ItemCrossbowArrow());
        registry.register(new ItemStake());
        registry.register(new ItemVampireBloodBottle());
        registry.register(new ItemBloodPotion());
        ItemTechCrossbow basic_tech_crossbow = new ItemTechCrossbow("basic_tech_crossbow", 1.6F, 6, 300);
        basic_tech_crossbow.setEnchantability(Item.ToolMaterial.DIAMOND);
        registry.register(basic_tech_crossbow);
        ItemTechCrossbow enhanced_tech_crossbow = new ItemTechCrossbow("enhanced_tech_crossbow", 1.7F, 4, 450);
        enhanced_tech_crossbow.setEnchantability(Item.ToolMaterial.DIAMOND);
        registry.register(enhanced_tech_crossbow);
        registry.register(new VampirismItem("tech_crossbow_ammo_package") {
            @Override
            public void addInformation(ItemStack stack, @Nullable EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
                tooltip.add(UtilLib.translateFormatted("item.vampirism." + regName + ".tooltip", basic_tech_crossbow.getLocalizedName()));
            }
        });
        registry.register(new ItemVampireBook());
        registry.register(new ItemHolyWaterBottle());
        registry.register(new VampirismItem("holy_salt") {
            @Override
            public boolean hasEffect(ItemStack stack) {
                return true;
            }
        });
        registry.register(new VampirismItem("pure_salt"));

        registry.register(new VampirismItem("holy_salt_water") {
            @Override
            public boolean hasEffect(ItemStack stack) {
                return true;
            }
        });
        registry.register(new ItemAlchemicalFire());
        registry.register(new VampirismItem("garlic_beacon_core"));
        registry.register(new VampirismItem("garlic_beacon_core_improved"));
        registry.register(new VampirismItem("purified_garlic"));

        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.HEAD));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.CHEST));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.LEGS));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.FEET));

        registry.register(new ItemHunterHat(0));
        registry.register(new ItemHunterHat(1));

        registry.register(new ItemHunterAxe());

        registry.register(new ItemHunterCoat(EntityEquipmentSlot.HEAD));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.CHEST));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.LEGS));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.FEET));

        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.HEAD));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.CHEST));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.LEGS));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.FEET));
    }



    /**
     * Fix item mappings
     */
    static boolean fixMapping(RegistryEvent.MissingMappings.Mapping<Item> mapping) {

        //Removed battle Axe
        if ("battleaxe".equals(mapping.key.getResourcePath())) {
            mapping.ignore();
            return true;
        }
        //Check for mappings changed for 1.11 CamelCase to lower underscore
        String old = mapping.key.getResourcePath();
        boolean r = checkMapping(mapping, old, armor_of_swiftness_feet, armor_of_swiftness_chest, armor_of_swiftness_head, armor_of_swiftness_legs, basic_crossbow, basic_double_crossbow, basic_tech_crossbow, blood_bottle, blood_potion, crossbow_arrow, enhanced_crossbow, enhanced_double_crossbow);
        if (!r)
            r = checkMapping(mapping, old, enhanced_tech_crossbow, human_heart, weak_human_heart, hunter_axe, hunter_coat_feet, hunter_coat_chest, hunter_coat_head, hunter_coat_legs, hunter_hat0_head, hunter_hat1_head, hunter_intel, injection, item_alchemical_fire, item_coffin, item_garlic, item_med_chair);
        if (!r)
            r = checkMapping(mapping, old, item_tent, obsidian_armor_feet, obsidian_armor_chest, obsidian_armor_head, obsidian_armor_legs, pitchfork, pure_blood, tech_crossbow_ammo_package, vampire_blood_bottle, vampire_book, vampire_fang);
        return r;
    }

    private static boolean checkMapping(RegistryEvent.MissingMappings.Mapping<Item> mapping, String name, Item... items) {
        for (Item i : items) {
            String oldRegisteredName;
            if (i instanceof VampirismHunterArmor) {
                oldRegisteredName = ((VampirismHunterArmor) i).getOldRegisteredName();
            } else {
                String newRegisteredName = i instanceof VampirismItem ? ((VampirismItem) i).getRegisteredName() : (i instanceof VampirismItemBloodFood ? ((VampirismItemBloodFood) i).getRegisteredName() : null);
                if (newRegisteredName == null) {
                    VampirismMod.log.w("ModItems", "Unknown item class. Unable to determine new registered name during mapping fix", i.getClass());
                    continue;
                }
                oldRegisteredName = newRegisteredName.replaceAll("_", "");
            }

            if (oldRegisteredName.equals(name)) {
                mapping.remap(i);
                return true;
            }
        }
        return false;
    }
}
