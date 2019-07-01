package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.BloodGrinderValueLoader;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronCraftingManager;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all item registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
public class ModItems {

    public static final ItemVampireFang vampire_fang = getNull();
    public static final VampirismItemBloodFood human_heart = getNull();
    public static final VampirismItemBloodFood weak_human_heart = getNull();
    public static final ItemBloodBottle blood_bottle = getNull();
    public static final ItemTent item_tent = getNull();
    public static final ItemTent item_tent_spawner = getNull();

    public static final ItemCoffin item_coffin = getNull();
    public static final ItemPureBlood pure_blood_0 = getNull();
    public static final ItemPureBlood pure_blood_1 = getNull();
    public static final ItemPureBlood pure_blood_2 = getNull();
    public static final ItemPureBlood pure_blood_3 = getNull();
    public static final ItemPureBlood pure_blood_4 = getNull();

    public static final ItemGarlic item_garlic = getNull();
    public static final ItemInjection injection_garlic = getNull();
    public static final ItemInjection injection_sanguinare = getNull();
    public static final ItemInjection injection_empty = getNull();

    public static final ItemHunterIntel hunter_intel_0 = getNull();
    public static final ItemHunterIntel hunter_intel_1 = getNull();
    public static final ItemHunterIntel hunter_intel_2 = getNull();
    public static final ItemHunterIntel hunter_intel_3 = getNull();
    public static final ItemHunterIntel hunter_intel_4 = getNull();
    public static final ItemHunterIntel hunter_intel_5 = getNull();
    public static final ItemHunterIntel hunter_intel_6 = getNull();
    public static final ItemHunterIntel hunter_intel_7 = getNull();
    public static final ItemHunterIntel hunter_intel_8 = getNull();

    public static final ItemMedChair item_med_chair = getNull();
    public static final ItemPitchfork pitchfork = getNull();
    public static final ItemSimpleCrossbow basic_crossbow = getNull();
    public static final ItemDoubleCrossbow basic_double_crossbow = getNull();
    public static final ItemSimpleCrossbow enhanced_crossbow = getNull();
    public static final ItemDoubleCrossbow enhanced_double_crossbow = getNull();
    public static final ItemCrossbowArrow crossbow_arrow_normal = getNull();
    public static final ItemCrossbowArrow crossbow_arrow_spitfire = getNull();
    public static final ItemCrossbowArrow crossbow_arrow_vampire_killer = getNull();

    public static final ItemStake stake = getNull();
    public static final ItemVampireBloodBottle vampire_blood_bottle = getNull();
    public static final ItemBloodPotion blood_potion = getNull();
    public static final ItemTechCrossbow basic_tech_crossbow = getNull();
    public static final ItemTechCrossbow enhanced_tech_crossbow = getNull();
    public static final VampirismItem tech_crossbow_ammo_package = getNull();
    public static final ItemVampireBook vampire_book = getNull();
    public static final ItemHolyWaterBottle holy_water_bottle_normal = getNull();
    public static final ItemHolyWaterBottle holy_water_bottle_enhanced = getNull();
    public static final ItemHolyWaterBottle holy_water_bottle_ultimate = getNull();

    public static final ItemHolyWaterSplashBottle holy_water_splash_bottle_normal = getNull();
    public static final ItemHolyWaterSplashBottle holy_water_splash_bottle_enhanced = getNull();
    public static final ItemHolyWaterSplashBottle holy_water_splash_bottle_ultimate = getNull();

    public static final VampirismItem holy_salt = getNull();
    public static final VampirismItem holy_salt_water = getNull();
    public static final ItemAlchemicalFire item_alchemical_fire = getNull();
    public static final VampirismItem garlic_beacon_core = getNull();
    public static final VampirismItem garlic_beacon_core_improved = getNull();
    public static final VampirismItem purified_garlic = getNull();
    public static final VampirismItem pure_salt = getNull();

    public static final ItemArmorOfSwiftness armor_of_swiftness_head_normal = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_chest_normal = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_legs_normal = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_feet_normal = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_head_enhanced = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_chest_enhanced = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_legs_enhanced = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_feet_enhanced = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_head_ultimate = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_chest_ultimate = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_legs_ultimate = getNull();
    public static final ItemArmorOfSwiftness armor_of_swiftness_feet_ultimate = getNull();


    public static final ItemHunterCoat hunter_coat_head_normal = getNull();
    public static final ItemHunterCoat hunter_coat_chest_normal = getNull();
    public static final ItemHunterCoat hunter_coat_legs_normal = getNull();
    public static final ItemHunterCoat hunter_coat_feet_normal = getNull();
    public static final ItemHunterCoat hunter_coat_head_enhanced = getNull();
    public static final ItemHunterCoat hunter_coat_chest_enhanced = getNull();
    public static final ItemHunterCoat hunter_coat_legs_enhanced = getNull();
    public static final ItemHunterCoat hunter_coat_feet_enhanced = getNull();
    public static final ItemHunterCoat hunter_coat_head_ultimate = getNull();
    public static final ItemHunterCoat hunter_coat_chest_ultimate = getNull();
    public static final ItemHunterCoat hunter_coat_legs_ultimate = getNull();
    public static final ItemHunterCoat hunter_coat_feet_ultimate = getNull();


    public static final ItemObsidianArmor obsidian_armor_head_normal = getNull();
    public static final ItemObsidianArmor obsidian_armor_chest_normal = getNull();
    public static final ItemObsidianArmor obsidian_armor_legs_normal = getNull();
    public static final ItemObsidianArmor obsidian_armor_feet_normal = getNull();
    public static final ItemObsidianArmor obsidian_armor_head_enhanced = getNull();
    public static final ItemObsidianArmor obsidian_armor_chest_enhanced = getNull();
    public static final ItemObsidianArmor obsidian_armor_legs_enhanced = getNull();
    public static final ItemObsidianArmor obsidian_armor_feet_enhanced = getNull();
    public static final ItemObsidianArmor obsidian_armor_head_ultimate = getNull();
    public static final ItemObsidianArmor obsidian_armor_chest_ultimate = getNull();
    public static final ItemObsidianArmor obsidian_armor_legs_ultimate = getNull();
    public static final ItemObsidianArmor obsidian_armor_feet_ultimate = getNull();

    public static final ItemHunterHat hunter_hat_head_0 = getNull();
    public static final ItemHunterHat hunter_hat_head_1 = getNull();

    public static final ItemHunterAxe hunter_axe_normal = getNull();
    public static final ItemHunterAxe hunter_axe_enhanced = getNull();
    public static final ItemHunterAxe hunter_axe_ultimate = getNull();


    public static final ItemHeartSeeker heart_seeker_normal = getNull();
    public static final ItemHeartSeeker heart_seeker_enhanced = getNull();
    public static final ItemHeartSeeker heart_seeker_ultimate = getNull();
    public static final ItemHeartStriker heart_striker_normal = getNull();
    public static final ItemHeartStriker heart_striker_enhanced = getNull();
    public static final ItemHeartStriker heart_striker_ultimate = getNull();
    public static final VampirismItem blood_infused_iron_ingot = getNull();
    public static final VampirismItem blood_infused_enhanced_iron_ingot = getNull();
    public static final VampirismItem soul_orb_vampire = getNull();

    public static final ItemVampireCloak vampire_cloak_red_black = getNull();
    public static final ItemVampireCloak vampire_cloak_black_blue = getNull();
    public static final ItemVampireCloak vampire_cloak_black_red = getNull();
    public static final ItemVampireCloak vampire_cloak_black_white = getNull();
    public static final ItemVampireCloak vampire_cloak_white_black = getNull();

    public static final ItemSpawnEgg vampire_spawn_egg = getNull();
    public static final ItemSpawnEgg vampire_hunter_spawn_egg = getNull();
    public static final ItemSpawnEgg advanced_vampire_spawn_egg = getNull();
    public static final ItemSpawnEgg advanced_vampire_hunter_spawn_egg = getNull();
    public static final ItemSpawnEgg ghost_spawn_egg = getNull();
    public static final ItemSpawnEgg vampire_baron_spawn_egg = getNull();
    public static final ItemSpawnEgg hunter_trainer_spawn_egg = getNull();




    static void registerCraftingRecipes() {

        // TODO CRAFTING
        //TODO 1.13 probably have to modifiy alchemical cauldron system with item groups /ingridients
        //TODO 1.13 (rewrite alchemical cauldron recipes to register recipes through json)
        AlchemicalCauldronCraftingManager cauldronCraftingManager = AlchemicalCauldronCraftingManager.getInstance();

        // ItemHolyWaterBottle.registerSplashRecipes(holy_water_bottle,
        // IItemWithTier.TIER.NORMAL);
        // ItemHolyWaterBottle.registerSplashRecipes(holy_water_bottle,
        // IItemWithTier.TIER.ENHANCED);
        // ItemHolyWaterBottle.registerSplashRecipes(holy_water_bottle,
        // IItemWithTier.TIER.ULTIMATE);
        //
        cauldronCraftingManager.registerLiquidColor(ModItems.holy_water_bottle_normal, 0x6666FF);
        cauldronCraftingManager.registerLiquidColor(ModItems.holy_water_bottle_enhanced, 0x6666FF);
        cauldronCraftingManager.registerLiquidColor(ModItems.holy_water_bottle_ultimate, 0x6666FF);

        cauldronCraftingManager.registerLiquidColor(ModItems.item_garlic, 0xBBBBBB);
        cauldronCraftingManager
                .addRecipe(new ItemStack(ModItems.item_alchemical_fire, 4),
                        ModItems.holy_water_bottle_normal, Items.GUNPOWDER)
                .setRequirements(1, HunterSkills.basic_alchemy);
        cauldronCraftingManager.addRecipe(new ItemStack(ModItems.purified_garlic, 2),
                ModItems.holy_water_bottle_normal, new ItemStack(ModItems.item_garlic, 4))
                .setRequirements(1, HunterSkills.purified_garlic);
        cauldronCraftingManager.addRecipe(new ItemStack(ModItems.garlic_beacon_core), ModItems.item_garlic, Blocks.BLACK_WOOL)
                .setRequirements(1, HunterSkills.garlic_beacon);
        cauldronCraftingManager
                .addRecipe(ModItems.garlic_beacon_core_improved,
                        ModItems.holy_water_bottle_ultimate, ModItems.garlic_beacon_core)
                .setRequirements(1, HunterSkills.garlic_beacon_improved).setExperience(2F);
        //cauldronCraftingManager.addRecipe(new ItemStack(ModItems.pure_salt, 4),new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), null).setRequirements(1, HunterSkills.basic_alchemy).setCookingTime(20 * 60); TODO 1.14 fluid

        // Brewing
        BrewingRecipeRegistry.addRecipe(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.WATER), Ingredient.fromStacks(new ItemStack(holy_salt)), new ItemStack(holy_salt_water));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new ItemStack(holy_water_bottle_normal), Ingredient.fromItems(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_normal)) {


            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return holy_water_bottle_normal.equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new ItemStack(holy_water_bottle_enhanced), Ingredient.fromItems(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_enhanced)) {

            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return holy_water_bottle_enhanced.equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                new ItemStack(holy_water_bottle_ultimate), Ingredient.fromItems(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_ultimate)) {

            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return holy_water_bottle_ultimate.equals(stack.getItem());
            }
        });
    }


    static void registerItems(IForgeRegistry<Item> registry) {
        registry.register(new ItemVampireFang());
        registry.register(new VampirismItemBloodFood("human_heart", 20, 1.2F, new Item.Properties().group(VampirismMod.creativeTab)));
        registry.register(new VampirismItemBloodFood("weak_human_heart", 10, 0.9F, new Item.Properties().group(VampirismMod.creativeTab)));
        registry.register(new ItemBloodBottle());
        registry.register(new ItemTent(true));
        registry.register(new ItemTent(false));

        registry.register(new ItemCoffin());
        registry.register(new ItemPureBlood(0));
        registry.register(new ItemPureBlood(1));
        registry.register(new ItemPureBlood(2));
        registry.register(new ItemPureBlood(3));
        registry.register(new ItemPureBlood(4));

        for (int i = 0; i < HunterLevelingConf.instance().HUNTER_INTEL_COUNT; i++) {
            registry.register(new ItemHunterIntel(i));
        }
        registry.register(new ItemGarlic());
        registry.register(new ItemMedChair());
        registry.register(new ItemInjection(ItemInjection.TYPE.GARLIC));
        registry.register(new ItemInjection(ItemInjection.TYPE.SANGUINARE));
        registry.register(new ItemInjection(ItemInjection.TYPE.EMPTY));

        registry.register(new ItemPitchfork());
        ItemSimpleCrossbow basic_crossbow = new ItemSimpleCrossbow("basic_crossbow", 1, 20, 300);
        basic_crossbow.setEnchantability(ItemTier.WOOD);
        registry.register(basic_crossbow);
        ItemDoubleCrossbow basic_double_crossbow = new ItemDoubleCrossbow("basic_double_crossbow", 1, 20, 300);
        basic_double_crossbow.setEnchantability(ItemTier.WOOD);
        registry.register(basic_double_crossbow);
        ItemSimpleCrossbow enhanced_crossbow = new ItemSimpleCrossbow("enhanced_crossbow", 1.5F, 15, 350);
        enhanced_crossbow.setEnchantability(ItemTier.IRON);
        registry.register(enhanced_crossbow);
        ItemDoubleCrossbow enhanced_double_crossbow = new ItemDoubleCrossbow("enhanced_double_crossbow", 1.5F, 15, 350);
        enhanced_double_crossbow.setEnchantability(ItemTier.IRON);
        registry.register(enhanced_double_crossbow);
        registry.register(new ItemCrossbowArrow(ItemCrossbowArrow.EnumArrowType.VAMPIRE_KILLER));
        registry.register(new ItemCrossbowArrow(ItemCrossbowArrow.EnumArrowType.NORMAL));
        registry.register(new ItemCrossbowArrow(ItemCrossbowArrow.EnumArrowType.SPITFIRE));

        registry.register(new ItemStake());
        registry.register(new ItemVampireBloodBottle());
        registry.register(new ItemBloodPotion());
        ItemTechCrossbow basic_tech_crossbow = new ItemTechCrossbow("basic_tech_crossbow", 1.6F, 6, 300);
        basic_tech_crossbow.setEnchantability(ItemTier.DIAMOND);
        registry.register(basic_tech_crossbow);
        ItemTechCrossbow enhanced_tech_crossbow = new ItemTechCrossbow("enhanced_tech_crossbow", 1.7F, 4, 450);
        enhanced_tech_crossbow.setEnchantability(ItemTier.DIAMOND);
        registry.register(enhanced_tech_crossbow);
        registry.register(new VampirismItem("tech_crossbow_ammo_package", new Item.Properties().group(VampirismMod.creativeTab)) {

            @OnlyIn(Dist.CLIENT)
            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                tooltip.add(UtilLib.translated("item.vampirism." + regName + ".tooltip", UtilLib.translated(basic_tech_crossbow.getTranslationKey())));
            }

        });
        registry.register(new ItemVampireBook());
        registry.register(new ItemHolyWaterBottle(IItemWithTier.TIER.NORMAL));
        registry.register(new ItemHolyWaterBottle(IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemHolyWaterBottle(IItemWithTier.TIER.ULTIMATE));


        registry.register(new ItemHolyWaterSplashBottle(IItemWithTier.TIER.NORMAL));
        registry.register(new ItemHolyWaterSplashBottle(IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemHolyWaterSplashBottle(IItemWithTier.TIER.ULTIMATE));

        registry.register(new VampirismItem("holy_salt", new Item.Properties().group(VampirismMod.creativeTab)) {

            @Override
            public boolean hasEffect(ItemStack stack) {

                return true;
            }
        });
        registry.register(new VampirismItem("pure_salt", new Item.Properties().group(VampirismMod.creativeTab)));

        registry.register(new VampirismItem("holy_salt_water", new Item.Properties().maxStackSize(1)) {

            @Override
            public boolean hasEffect(ItemStack stack) {

                return true;
            }
        });
        registry.register(new ItemAlchemicalFire());
        registry.register(new VampirismItem("garlic_beacon_core", new Item.Properties().group(VampirismMod.creativeTab)));
        registry.register(new VampirismItem("garlic_beacon_core_improved", new Item.Properties().group(VampirismMod.creativeTab)));
        registry.register(new VampirismItem("purified_garlic", new Item.Properties().group(VampirismMod.creativeTab)));

        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.HEAD, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.CHEST, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.LEGS, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.FEET, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.HEAD, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.CHEST, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.LEGS, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.FEET, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.HEAD, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.CHEST, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.LEGS, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemArmorOfSwiftness(EntityEquipmentSlot.FEET, IItemWithTier.TIER.ULTIMATE));

        registry.register(new ItemHunterHat(0));
        registry.register(new ItemHunterHat(1));

        registry.register(new ItemHunterAxe(IItemWithTier.TIER.NORMAL));
        registry.register(new ItemHunterAxe(IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemHunterAxe(IItemWithTier.TIER.ULTIMATE));


        registry.register(new ItemHunterCoat(EntityEquipmentSlot.HEAD, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.CHEST, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.LEGS, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.FEET, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.HEAD, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.CHEST, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.LEGS, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.FEET, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.HEAD, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.CHEST, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.LEGS, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemHunterCoat(EntityEquipmentSlot.FEET, IItemWithTier.TIER.ULTIMATE));

        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.HEAD, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.CHEST, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.LEGS, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.FEET, IItemWithTier.TIER.NORMAL));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.HEAD, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.CHEST, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.LEGS, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.FEET, IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.HEAD, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.CHEST, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.LEGS, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemObsidianArmor(EntityEquipmentSlot.FEET, IItemWithTier.TIER.ULTIMATE));

        registry.register(new ItemHeartSeeker(IItemWithTier.TIER.NORMAL));
        registry.register(new ItemHeartStriker(IItemWithTier.TIER.NORMAL));
        registry.register(new ItemHeartSeeker(IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemHeartStriker(IItemWithTier.TIER.ENHANCED));
        registry.register(new ItemHeartSeeker(IItemWithTier.TIER.ULTIMATE));
        registry.register(new ItemHeartStriker(IItemWithTier.TIER.ULTIMATE));

        registry.register(new VampirismItem("blood_infused_iron_ingot", new Item.Properties()));
        registry.register(new VampirismItem("blood_infused_enhanced_iron_ingot", new Item.Properties()));
        registry.register(new VampirismItem("soul_orb_vampire", new Item.Properties()));

        registry.register(new ItemVampireCloak(ItemVampireCloak.EnumCloakColor.REDBLACK));
        registry.register(new ItemVampireCloak(ItemVampireCloak.EnumCloakColor.BLACKBLUE));
        registry.register(new ItemVampireCloak(ItemVampireCloak.EnumCloakColor.BLACKRED));
        registry.register(new ItemVampireCloak(ItemVampireCloak.EnumCloakColor.BLACKWHITE));
        registry.register(new ItemVampireCloak(ItemVampireCloak.EnumCloakColor.WHITEBLACK));

        registry.register(new VampirismSpawnEgg(ModEntities.vampire, "vampire_spawn_egg"));
        registry.register(new VampirismSpawnEgg(ModEntities.vampire_hunter, "vampire_hunter_spawn_egg"));
        registry.register(new VampirismSpawnEgg(ModEntities.advanced_vampire, "advanced_vampire_spawn_egg"));
        registry.register(new VampirismSpawnEgg(ModEntities.advanced_hunter, "advanced_vampire_hunter_spawn_egg"));
        registry.register(new VampirismSpawnEgg(ModEntities.vampire_baron, "vampire_baron_spawn_egg"));
        registry.register(new VampirismSpawnEgg(ModEntities.ghost, "ghost_spawn_egg"));
        registry.register(new VampirismSpawnEgg(ModEntities.hunter_trainer, "hunter_trainer_spawn_egg"));
    }

    static void registerBloodConversionRates() {

        Map<ResourceLocation, Integer> valuesIn = BloodGrinderValueLoader.getBloodGrinderValues();
        for (ResourceLocation e : valuesIn.keySet()) {
            BloodConversionRegistry.registerItem(e, valuesIn.get(e) * VReference.FOOD_TO_FLUID_BLOOD);
        }
    }
}
