package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
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

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all item registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
public class ModItems {

    //Items
    public static final VampireFangItem vampire_fang = getNull();
    public static final VampirismItemBloodFood human_heart = getNull();
    public static final VampirismItemBloodFood weak_human_heart = getNull();
    public static final BloodBottleIItem blood_bottle = getNull();
    public static final TentItem item_tent = getNull();
    public static final TentItem item_tent_spawner = getNull();

    public static final CoffinItem item_coffin = getNull();
    public static final PureBloodItem pure_blood_0 = getNull();
    public static final PureBloodItem pure_blood_1 = getNull();
    public static final PureBloodItem pure_blood_2 = getNull();
    public static final PureBloodItem pure_blood_3 = getNull();
    public static final PureBloodItem pure_blood_4 = getNull();

    public static final GarlicItem item_garlic = getNull();
    public static final InjectionItem injection_garlic = getNull();
    public static final InjectionItem injection_sanguinare = getNull();
    public static final InjectionItem injection_empty = getNull();

    public static final HunterIntelItem hunter_intel_0 = getNull();
    public static final HunterIntelItem hunter_intel_1 = getNull();
    public static final HunterIntelItem hunter_intel_2 = getNull();
    public static final HunterIntelItem hunter_intel_3 = getNull();
    public static final HunterIntelItem hunter_intel_4 = getNull();
    public static final HunterIntelItem hunter_intel_5 = getNull();
    public static final HunterIntelItem hunter_intel_6 = getNull();
    public static final HunterIntelItem hunter_intel_7 = getNull();
    public static final HunterIntelItem hunter_intel_8 = getNull();

    public static final MedChairItem item_med_chair = getNull();
    public static final PitchforkItem pitchfork = getNull();
    public static final SimpleCrossbowItem basic_crossbow = getNull();
    public static final DoubleCrossbowItem basic_double_crossbow = getNull();
    public static final SimpleCrossbowItem enhanced_crossbow = getNull();
    public static final DoubleCrossbowItem enhanced_double_crossbow = getNull();
    public static final CrossbowArrowItem crossbow_arrow_normal = getNull();
    public static final CrossbowArrowItem crossbow_arrow_spitfire = getNull();
    public static final CrossbowArrowItem crossbow_arrow_vampire_killer = getNull();

    public static final StakeItem stake = getNull();
    public static final VampireBloodBottleItem vampire_blood_bottle = getNull();
    public static final BloodPotionItem blood_potion = getNull();
    public static final TechCrossbowItem basic_tech_crossbow = getNull();
    public static final TechCrossbowItem enhanced_tech_crossbow = getNull();
    public static final VampirismItem tech_crossbow_ammo_package = getNull();
    public static final VampireBookItem vampire_book = getNull();
    public static final HolyWaterBottleItem holy_water_bottle_normal = getNull();
    public static final HolyWaterBottleItem holy_water_bottle_enhanced = getNull();
    public static final HolyWaterBottleItem holy_water_bottle_ultimate = getNull();

    public static final HolyWaterSplashBottleItem holy_water_splash_bottle_normal = getNull();
    public static final HolyWaterSplashBottleItem holy_water_splash_bottle_enhanced = getNull();
    public static final HolyWaterSplashBottleItem holy_water_splash_bottle_ultimate = getNull();

    public static final VampirismItem holy_salt = getNull();
    public static final VampirismItem holy_salt_water = getNull();
    public static final AlchemicalFireItem item_alchemical_fire = getNull();
    public static final VampirismItem garlic_beacon_core = getNull();
    public static final VampirismItem garlic_beacon_core_improved = getNull();
    public static final VampirismItem purified_garlic = getNull();
    public static final VampirismItem pure_salt = getNull();

    public static final ArmorOfSwiftnessItem armor_of_swiftness_head_normal = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_chest_normal = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_legs_normal = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_feet_normal = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_head_enhanced = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_chest_enhanced = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_legs_enhanced = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_feet_enhanced = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_head_ultimate = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_chest_ultimate = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_legs_ultimate = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_feet_ultimate = getNull();


    public static final HunterCoatItem hunter_coat_head_normal = getNull();
    public static final HunterCoatItem hunter_coat_chest_normal = getNull();
    public static final HunterCoatItem hunter_coat_legs_normal = getNull();
    public static final HunterCoatItem hunter_coat_feet_normal = getNull();
    public static final HunterCoatItem hunter_coat_head_enhanced = getNull();
    public static final HunterCoatItem hunter_coat_chest_enhanced = getNull();
    public static final HunterCoatItem hunter_coat_legs_enhanced = getNull();
    public static final HunterCoatItem hunter_coat_feet_enhanced = getNull();
    public static final HunterCoatItem hunter_coat_head_ultimate = getNull();
    public static final HunterCoatItem hunter_coat_chest_ultimate = getNull();
    public static final HunterCoatItem hunter_coat_legs_ultimate = getNull();
    public static final HunterCoatItem hunter_coat_feet_ultimate = getNull();


    public static final ObsidianArmorItem obsidian_armor_head_normal = getNull();
    public static final ObsidianArmorItem obsidian_armor_chest_normal = getNull();
    public static final ObsidianArmorItem obsidian_armor_legs_normal = getNull();
    public static final ObsidianArmorItem obsidian_armor_feet_normal = getNull();
    public static final ObsidianArmorItem obsidian_armor_head_enhanced = getNull();
    public static final ObsidianArmorItem obsidian_armor_chest_enhanced = getNull();
    public static final ObsidianArmorItem obsidian_armor_legs_enhanced = getNull();
    public static final ObsidianArmorItem obsidian_armor_feet_enhanced = getNull();
    public static final ObsidianArmorItem obsidian_armor_head_ultimate = getNull();
    public static final ObsidianArmorItem obsidian_armor_chest_ultimate = getNull();
    public static final ObsidianArmorItem obsidian_armor_legs_ultimate = getNull();
    public static final ObsidianArmorItem obsidian_armor_feet_ultimate = getNull();

    public static final HunterHatItem hunter_hat_head_0 = getNull();
    public static final HunterHatItem hunter_hat_head_1 = getNull();

    public static final HunterAxeItem hunter_axe_normal = getNull();
    public static final HunterAxeItem hunter_axe_enhanced = getNull();
    public static final HunterAxeItem hunter_axe_ultimate = getNull();


    public static final HeartSeekerItem heart_seeker_normal = getNull();
    public static final HeartSeekerItem heart_seeker_enhanced = getNull();
    public static final HeartSeekerItem heart_seeker_ultimate = getNull();
    public static final HeartStrikerItem heart_striker_normal = getNull();
    public static final HeartStrikerItem heart_striker_enhanced = getNull();
    public static final HeartStrikerItem heart_striker_ultimate = getNull();
    public static final VampirismItem blood_infused_iron_ingot = getNull();
    public static final VampirismItem blood_infused_enhanced_iron_ingot = getNull();
    public static final VampirismItem soul_orb_vampire = getNull();

    public static final VampireCloakItem vampire_cloak_red_black = getNull();
    public static final VampireCloakItem vampire_cloak_black_blue = getNull();
    public static final VampireCloakItem vampire_cloak_black_red = getNull();
    public static final VampireCloakItem vampire_cloak_black_white = getNull();
    public static final VampireCloakItem vampire_cloak_white_black = getNull();

    public static final SpawnEggItem vampire_spawn_egg = getNull();
    public static final SpawnEggItem vampire_hunter_spawn_egg = getNull();
    public static final SpawnEggItem advanced_vampire_spawn_egg = getNull();
    public static final SpawnEggItem advanced_vampire_hunter_spawn_egg = getNull();
    public static final SpawnEggItem ghost_spawn_egg = getNull();
    public static final SpawnEggItem vampire_baron_spawn_egg = getNull();
    public static final SpawnEggItem hunter_trainer_spawn_egg = getNull();


    static void registerCraftingRecipes() {
        // Brewing
        BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER)), Ingredient.fromStacks(new ItemStack(holy_salt)), new ItemStack(holy_salt_water));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromItems(holy_water_bottle_normal), Ingredient.fromItems(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_normal)) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return holy_water_bottle_normal.equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromItems(holy_water_bottle_enhanced), Ingredient.fromItems(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_enhanced)) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return holy_water_bottle_enhanced.equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.fromItems(holy_water_bottle_ultimate), Ingredient.fromItems(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_ultimate)) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {
                return holy_water_bottle_ultimate.equals(stack.getItem());
            }
        });
    }


    static void registerItems(IForgeRegistry<Item> registry) {
        registry.register(new VampireFangItem());
        registry.register(new VampirismItemBloodFood("human_heart", (new Food.Builder()).hunger(20).saturation(1.5F).build()));
        registry.register(new VampirismItemBloodFood("weak_human_heart", (new Food.Builder()).hunger(10).saturation(0.9F).build()));
        registry.register(new BloodBottleIItem());
        registry.register(new TentItem(true));
        registry.register(new TentItem(false));

        registry.register(new CoffinItem());
        registry.register(new PureBloodItem(0));
        registry.register(new PureBloodItem(1));
        registry.register(new PureBloodItem(2));
        registry.register(new PureBloodItem(3));
        registry.register(new PureBloodItem(4));

        for (int i = 0; i < HunterLevelingConf.instance().HUNTER_INTEL_COUNT; i++) {
            registry.register(new HunterIntelItem(i));
        }
        registry.register(new GarlicItem());
        registry.register(new MedChairItem());
        registry.register(new InjectionItem(InjectionItem.TYPE.GARLIC));
        registry.register(new InjectionItem(InjectionItem.TYPE.SANGUINARE));
        registry.register(new InjectionItem(InjectionItem.TYPE.EMPTY));

        registry.register(new PitchforkItem());
        SimpleCrossbowItem basic_crossbow = new SimpleCrossbowItem("basic_crossbow", 1, 20, 300);
        basic_crossbow.setEnchantability(ItemTier.WOOD);
        registry.register(basic_crossbow);
        DoubleCrossbowItem basic_double_crossbow = new DoubleCrossbowItem("basic_double_crossbow", 1, 20, 300);
        basic_double_crossbow.setEnchantability(ItemTier.WOOD);
        registry.register(basic_double_crossbow);
        SimpleCrossbowItem enhanced_crossbow = new SimpleCrossbowItem("enhanced_crossbow", 1.5F, 15, 350);
        enhanced_crossbow.setEnchantability(ItemTier.IRON);
        registry.register(enhanced_crossbow);
        DoubleCrossbowItem enhanced_double_crossbow = new DoubleCrossbowItem("enhanced_double_crossbow", 1.5F, 15, 350);
        enhanced_double_crossbow.setEnchantability(ItemTier.IRON);
        registry.register(enhanced_double_crossbow);
        registry.register(new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.VAMPIRE_KILLER));
        registry.register(new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.NORMAL));
        registry.register(new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.SPITFIRE));

        registry.register(new StakeItem());
        registry.register(new VampireBloodBottleItem());
        registry.register(new BloodPotionItem());
        TechCrossbowItem basic_tech_crossbow = new TechCrossbowItem("basic_tech_crossbow", 1.6F, 6, 300);
        basic_tech_crossbow.setEnchantability(ItemTier.DIAMOND);
        registry.register(basic_tech_crossbow);
        TechCrossbowItem enhanced_tech_crossbow = new TechCrossbowItem("enhanced_tech_crossbow", 1.7F, 4, 450);
        enhanced_tech_crossbow.setEnchantability(ItemTier.DIAMOND);
        registry.register(enhanced_tech_crossbow);
        registry.register(new VampirismItem("tech_crossbow_ammo_package", new Item.Properties().group(VampirismMod.creativeTab)) {

            @OnlyIn(Dist.CLIENT)
            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                tooltip.add(UtilLib.translated("item.vampirism." + regName + ".tooltip", UtilLib.translated(basic_tech_crossbow.getTranslationKey())));
            }

        });
        registry.register(new VampireBookItem());
        registry.register(new HolyWaterBottleItem(IItemWithTier.TIER.NORMAL));
        registry.register(new HolyWaterBottleItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new HolyWaterBottleItem(IItemWithTier.TIER.ULTIMATE));


        registry.register(new HolyWaterSplashBottleItem(IItemWithTier.TIER.NORMAL));
        registry.register(new HolyWaterSplashBottleItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new HolyWaterSplashBottleItem(IItemWithTier.TIER.ULTIMATE));

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
        registry.register(new AlchemicalFireItem());
        registry.register(new VampirismItem("garlic_beacon_core", new Item.Properties().group(VampirismMod.creativeTab)));
        registry.register(new VampirismItem("garlic_beacon_core_improved", new Item.Properties().group(VampirismMod.creativeTab)));
        registry.register(new VampirismItem("purified_garlic", new Item.Properties().group(VampirismMod.creativeTab)));

        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.NORMAL));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.NORMAL));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.NORMAL));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.FEET, IItemWithTier.TIER.NORMAL));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ENHANCED));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ENHANCED));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ENHANCED));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ENHANCED));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ArmorOfSwiftnessItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ULTIMATE));

        registry.register(new HunterHatItem(0));
        registry.register(new HunterHatItem(1));

        registry.register(new HunterAxeItem(IItemWithTier.TIER.NORMAL));
        registry.register(new HunterAxeItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new HunterAxeItem(IItemWithTier.TIER.ULTIMATE));


        registry.register(new HunterCoatItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.NORMAL));
        registry.register(new HunterCoatItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.NORMAL));
        registry.register(new HunterCoatItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.NORMAL));
        registry.register(new HunterCoatItem(EquipmentSlotType.FEET, IItemWithTier.TIER.NORMAL));
        registry.register(new HunterCoatItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ENHANCED));
        registry.register(new HunterCoatItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ENHANCED));
        registry.register(new HunterCoatItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ENHANCED));
        registry.register(new HunterCoatItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ENHANCED));
        registry.register(new HunterCoatItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ULTIMATE));
        registry.register(new HunterCoatItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ULTIMATE));
        registry.register(new HunterCoatItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ULTIMATE));
        registry.register(new HunterCoatItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ULTIMATE));

        registry.register(new ObsidianArmorItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.NORMAL));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.NORMAL));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.NORMAL));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.FEET, IItemWithTier.TIER.NORMAL));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ENHANCED));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ENHANCED));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ENHANCED));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ENHANCED));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ULTIMATE));
        registry.register(new ObsidianArmorItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ULTIMATE));

        registry.register(new HeartSeekerItem(IItemWithTier.TIER.NORMAL));
        registry.register(new HeartStrikerItem(IItemWithTier.TIER.NORMAL));
        registry.register(new HeartSeekerItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new HeartStrikerItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new HeartSeekerItem(IItemWithTier.TIER.ULTIMATE));
        registry.register(new HeartStrikerItem(IItemWithTier.TIER.ULTIMATE));

        registry.register(new VampirismItem("blood_infused_iron_ingot", new Item.Properties().group(VampirismMod.creativeTab)));
        registry.register(new VampirismItem("blood_infused_enhanced_iron_ingot", new Item.Properties().group(VampirismMod.creativeTab)));
        registry.register(new VampirismItem("soul_orb_vampire", new Item.Properties().group(VampirismMod.creativeTab)));

        registry.register(new VampireCloakItem(VampireCloakItem.EnumCloakColor.REDBLACK));
        registry.register(new VampireCloakItem(VampireCloakItem.EnumCloakColor.BLACKBLUE));
        registry.register(new VampireCloakItem(VampireCloakItem.EnumCloakColor.BLACKRED));
        registry.register(new VampireCloakItem(VampireCloakItem.EnumCloakColor.BLACKWHITE));
        registry.register(new VampireCloakItem(VampireCloakItem.EnumCloakColor.WHITEBLACK));

        registry.register(new SpawnEggItem(ModEntities.vampire, 0x8B15A3, 0xa735e3, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(REFERENCE.MODID, "vampire_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.vampire_hunter, 0x2d05f2, 0x2600e0, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(REFERENCE.MODID, "vampire_hunter_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.advanced_vampire, 0x8B15A3, 0x560a7e, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(REFERENCE.MODID, "advanced_vampire_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.advanced_hunter, 0x2d05f2, 0x1a028c, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(REFERENCE.MODID, "advanced_vampire_hunter_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.vampire_baron, 0x8B15A3, 0x15acda, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(REFERENCE.MODID, "vampire_baron_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.ghost, 0x626262, 0xbcbcbc, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(REFERENCE.MODID, "ghost_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.hunter_trainer, 0x2d05f2, 0x1cdb49, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(REFERENCE.MODID, "hunter_trainer_spawn_egg"));
    }
}
