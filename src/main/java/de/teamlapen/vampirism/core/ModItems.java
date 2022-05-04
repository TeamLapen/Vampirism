package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.player.hunter.HunterLevelingConf;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all item registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
@SuppressWarnings("unused")
public class ModItems {

    //Items
    public static final ArmorOfSwiftnessItem armor_of_swiftness_chest_enhanced = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_chest_normal = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_chest_ultimate = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_feet_enhanced = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_feet_normal = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_feet_ultimate = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_head_enhanced = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_head_normal = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_head_ultimate = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_legs_enhanced = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_legs_normal = getNull();
    public static final ArmorOfSwiftnessItem armor_of_swiftness_legs_ultimate = getNull();

    public static final SimpleCrossbowItem basic_crossbow = getNull();
    public static final DoubleCrossbowItem basic_double_crossbow = getNull();
    public static final TechCrossbowItem basic_tech_crossbow = getNull();

    public static final BloodBottleItem blood_bottle = getNull();
    public static final BucketItem blood_bucket = getNull();
    public static final VampirismItem blood_infused_iron_ingot = getNull();
    public static final VampirismItem blood_infused_enhanced_iron_ingot = getNull();

    public static final CrossbowArrowItem crossbow_arrow_normal = getNull();
    public static final CrossbowArrowItem crossbow_arrow_spitfire = getNull();
    public static final CrossbowArrowItem crossbow_arrow_vampire_killer = getNull();

    public static final SimpleCrossbowItem enhanced_crossbow = getNull();
    public static final DoubleCrossbowItem enhanced_double_crossbow = getNull();
    public static final TechCrossbowItem enhanced_tech_crossbow = getNull();

    public static final VampirismItem garlic_beacon_core = getNull();
    public static final VampirismItem garlic_beacon_core_improved = getNull();

    public static final HeartSeekerItem heart_seeker_enhanced = getNull();
    public static final HeartSeekerItem heart_seeker_normal = getNull();
    public static final HeartSeekerItem heart_seeker_ultimate = getNull();

    public static final HeartStrikerItem heart_striker_enhanced = getNull();
    public static final HeartStrikerItem heart_striker_normal = getNull();
    public static final HeartStrikerItem heart_striker_ultimate = getNull();

    public static final VampirismItem holy_salt = getNull();
    public static final VampirismItem holy_salt_water = getNull();

    public static final HolyWaterBottleItem holy_water_bottle_enhanced = getNull();
    public static final HolyWaterBottleItem holy_water_bottle_normal = getNull();
    public static final HolyWaterBottleItem holy_water_bottle_ultimate = getNull();
    public static final HolyWaterSplashBottleItem holy_water_splash_bottle_enhanced = getNull();
    public static final HolyWaterSplashBottleItem holy_water_splash_bottle_normal = getNull();
    public static final HolyWaterSplashBottleItem holy_water_splash_bottle_ultimate = getNull();

    public static final HunterAxeItem hunter_axe_enhanced = getNull();
    public static final HunterAxeItem hunter_axe_normal = getNull();
    public static final HunterAxeItem hunter_axe_ultimate = getNull();

    public static final HunterCoatItem hunter_coat_chest_enhanced = getNull();
    public static final HunterCoatItem hunter_coat_chest_normal = getNull();
    public static final HunterCoatItem hunter_coat_chest_ultimate = getNull();
    public static final HunterCoatItem hunter_coat_feet_enhanced = getNull();
    public static final HunterCoatItem hunter_coat_feet_normal = getNull();
    public static final HunterCoatItem hunter_coat_feet_ultimate = getNull();
    public static final HunterCoatItem hunter_coat_head_enhanced = getNull();
    public static final HunterCoatItem hunter_coat_head_normal = getNull();
    public static final HunterCoatItem hunter_coat_head_ultimate = getNull();
    public static final HunterCoatItem hunter_coat_legs_enhanced = getNull();
    public static final HunterCoatItem hunter_coat_legs_normal = getNull();
    public static final HunterCoatItem hunter_coat_legs_ultimate = getNull();

    public static final HunterHatItem hunter_hat_head_0 = getNull();
    public static final HunterHatItem hunter_hat_head_1 = getNull();

    public static final HunterIntelItem hunter_intel_0 = getNull();
    public static final HunterIntelItem hunter_intel_1 = getNull();
    public static final HunterIntelItem hunter_intel_2 = getNull();
    public static final HunterIntelItem hunter_intel_3 = getNull();
    public static final HunterIntelItem hunter_intel_4 = getNull();
    public static final HunterIntelItem hunter_intel_5 = getNull();
    public static final HunterIntelItem hunter_intel_6 = getNull();
    public static final HunterIntelItem hunter_intel_7 = getNull();
    public static final HunterIntelItem hunter_intel_8 = getNull();
    public static final HunterIntelItem hunter_intel_9 = getNull();

    public static final VampirismItemBloodFood human_heart = getNull();

    public static final InjectionItem injection_empty = getNull();
    public static final InjectionItem injection_garlic = getNull();
    public static final InjectionItem injection_sanguinare = getNull();
    public static final InjectionItem injection_zombie_blood = getNull();

    public static final VampirismItem cure_apple = getNull();

    public static final BucketItem impure_blood_bucket = getNull();
    public static final GarlicItem item_garlic = getNull();
    public static final GarlicBreadItem garlic_bread = getNull();
    public static final AlchemicalFireItem item_alchemical_fire = getNull();

    public static final TentItem item_tent = getNull();
    public static final TentItem item_tent_spawner = getNull();

    public static final PitchforkItem pitchfork = getNull();

    public static final PureBloodItem pure_blood_0 = getNull();
    public static final PureBloodItem pure_blood_1 = getNull();
    public static final PureBloodItem pure_blood_2 = getNull();
    public static final PureBloodItem pure_blood_3 = getNull();
    public static final PureBloodItem pure_blood_4 = getNull();

    public static final VampirismItem purified_garlic = getNull();
    public static final VampirismItem pure_salt = getNull();
    public static final VampirismItem soul_orb_vampire = getNull();

    public static final StakeItem stake = getNull();
    public static final VampirismItem tech_crossbow_ammo_package = getNull();

    public static final ColoredVampireClothingItem vampire_cloak_black_blue = getNull();
    public static final ColoredVampireClothingItem vampire_cloak_black_red = getNull();
    public static final ColoredVampireClothingItem vampire_cloak_black_white = getNull();
    public static final ColoredVampireClothingItem vampire_cloak_red_black = getNull();
    public static final ColoredVampireClothingItem vampire_cloak_white_black = getNull();

    public static final VampireBloodBottleItem vampire_blood_bottle = getNull();
    public static final VampireBookItem vampire_book = getNull();
    public static final VampireFangItem vampire_fang = getNull();
    public static final VampirismItemBloodFood weak_human_heart = getNull();

    public static final SpawnEggItem vampire_spawn_egg = getNull();
    public static final SpawnEggItem vampire_hunter_spawn_egg = getNull();
    public static final SpawnEggItem advanced_vampire_spawn_egg = getNull();
    public static final SpawnEggItem advanced_vampire_hunter_spawn_egg = getNull();
    public static final SpawnEggItem vampire_baron_spawn_egg = getNull();
    public static final SpawnEggItem hunter_trainer_spawn_egg = getNull();

    public static final UmbrellaItem umbrella = getNull();
    public static final BlockItem vampire_orchid = getNull();

    public static final VampirismItem hunter_minion_equipment = getNull();
    public static final MinionUpgradeItem hunter_minion_upgrade_simple = getNull();
    public static final MinionUpgradeItem hunter_minion_upgrade_enhanced = getNull();
    public static final MinionUpgradeItem hunter_minion_upgrade_special = getNull();
    public static final FeedingAdapterItem feeding_adapter = getNull();
    public static final VampirismItem vampire_minion_binding = getNull();
    public static final MinionUpgradeItem vampire_minion_upgrade_simple = getNull();
    public static final MinionUpgradeItem vampire_minion_upgrade_enhanced = getNull();
    public static final MinionUpgradeItem vampire_minion_upgrade_special = getNull();

    public static final OblivionItem oblivion_potion = getNull();

    public static final VampireRefinementItem amulet = getNull();
    public static final VampireRefinementItem ring = getNull();
    public static final VampireRefinementItem obi_belt = getNull();

    public static final VampireClothingItem vampire_clothing_crown = getNull();
    public static final VampireClothingItem vampire_clothing_legs = getNull();
    public static final VampireClothingItem vampire_clothing_boots = getNull();
    public static final VampireClothingItem vampire_clothing_hat = getNull();

    public static final VampirismItem garlic_finder = getNull();

    public static final WallOrFloorItem item_candelabra = getNull();
    public static final BlockItem cursed_spruce_log = getNull();
    public static final BlockItem dark_spruce_leaves = getNull();
    public static final SignItem dark_spruce_sign = getNull();
    public static final SignItem cursed_spruce_sign = getNull();

    public static final CrucifixItem crucifix_normal = getNull();
    public static final CrucifixItem crucifix_enhanced = getNull();
    public static final CrucifixItem crucifix_ultimate = getNull();

    public static final OilBottle oil_bottle = getNull();

    static void registerCraftingRecipes() {
        // Brewing
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), Ingredient.of(new ItemStack(holy_salt)), new ItemStack(holy_salt_water));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(holy_water_bottle_normal), Ingredient.of(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_normal)) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return holy_water_bottle_normal.equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(holy_water_bottle_enhanced), Ingredient.of(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_enhanced)) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return holy_water_bottle_enhanced.equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(holy_water_bottle_ultimate), Ingredient.of(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_ultimate)) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {
                return holy_water_bottle_ultimate.equals(stack.getItem());
            }
        });
    }


    static void registerItems(IForgeRegistry<Item> registry) {
        registry.register(new VampireFangItem());
        registry.register(new VampirismItemBloodFood("human_heart", (new Food.Builder()).nutrition(20).saturationMod(1.5F).build(), new Food.Builder().nutrition(5).saturationMod(1f).build()));
        registry.register(new VampirismItemBloodFood("weak_human_heart", (new Food.Builder()).nutrition(10).saturationMod(0.9F).build(), new Food.Builder().nutrition(3).saturationMod(1f).build()));
        registry.register(new BloodBottleItem());
        registry.register(new TentItem(true));
        registry.register(new TentItem(false));

        registry.register(new PureBloodItem(0));
        registry.register(new PureBloodItem(1));
        registry.register(new PureBloodItem(2));
        registry.register(new PureBloodItem(3));
        registry.register(new PureBloodItem(4));

        for (int i = 0; i < HunterLevelingConf.instance().HUNTER_INTEL_COUNT; i++) {
            registry.register(new HunterIntelItem(i));
        }
        registry.register(new GarlicItem());
        registry.register(new InjectionItem(InjectionItem.TYPE.GARLIC));
        registry.register(new InjectionItem(InjectionItem.TYPE.SANGUINARE));
        registry.register(new InjectionItem(InjectionItem.TYPE.EMPTY));
        registry.register(new InjectionItem(InjectionItem.TYPE.ZOMBIE_BLOOD));

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
        TechCrossbowItem basic_tech_crossbow = new TechCrossbowItem("basic_tech_crossbow", 1.6F, 6, 300);
        basic_tech_crossbow.setEnchantability(ItemTier.DIAMOND);
        registry.register(basic_tech_crossbow);
        TechCrossbowItem enhanced_tech_crossbow = new TechCrossbowItem("enhanced_tech_crossbow", 1.7F, 4, 450);
        enhanced_tech_crossbow.setEnchantability(ItemTier.DIAMOND);
        registry.register(enhanced_tech_crossbow);
        registry.register(new VampirismItem("tech_crossbow_ammo_package", new Item.Properties().tab(VampirismMod.creativeTab)) {

            @OnlyIn(Dist.CLIENT)
            @Override
            public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                tooltip.add(new TranslationTextComponent("item.vampirism." + regName + ".tooltip", new TranslationTextComponent(basic_tech_crossbow.getDescriptionId())).withStyle(TextFormatting.GRAY));
            }

        });
        registry.register(new VampireBookItem());
        registry.register(new HolyWaterBottleItem(IItemWithTier.TIER.NORMAL));
        registry.register(new HolyWaterBottleItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new HolyWaterBottleItem(IItemWithTier.TIER.ULTIMATE));


        registry.register(new HolyWaterSplashBottleItem(IItemWithTier.TIER.NORMAL));
        registry.register(new HolyWaterSplashBottleItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new HolyWaterSplashBottleItem(IItemWithTier.TIER.ULTIMATE));

        registry.register(new VampirismItem("holy_salt", creativeTabProps()) {

            @Override
            public boolean isFoil(ItemStack stack) {

                return true;
            }
        });
        registry.register(new VampirismItem("pure_salt", creativeTabProps()));

        registry.register(new VampirismItem("holy_salt_water", new Item.Properties().stacksTo(1)) {

            @Override
            public boolean isFoil(ItemStack stack) {

                return true;
            }
        });
        registry.register(new AlchemicalFireItem());
        registry.register(new VampirismItem("garlic_beacon_core", creativeTabProps()));
        registry.register(new VampirismItem("garlic_beacon_core_improved", creativeTabProps()));
        registry.register(new VampirismItem("purified_garlic", creativeTabProps()));

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

        registry.register(new HeartSeekerItem(IItemWithTier.TIER.NORMAL));
        registry.register(new HeartStrikerItem(IItemWithTier.TIER.NORMAL));
        registry.register(new HeartSeekerItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new HeartStrikerItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new HeartSeekerItem(IItemWithTier.TIER.ULTIMATE));
        registry.register(new HeartStrikerItem(IItemWithTier.TIER.ULTIMATE));

        registry.register(new VampirismItem("blood_infused_iron_ingot", creativeTabProps()));
        registry.register(new VampirismItem("blood_infused_enhanced_iron_ingot", creativeTabProps()));
        registry.register(new VampirismItem("soul_orb_vampire", creativeTabProps()));

        registry.register(new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.REDBLACK));
        registry.register(new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKBLUE));
        registry.register(new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKRED));
        registry.register(new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKWHITE));
        registry.register(new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.WHITEBLACK));

        registry.register(new SpawnEggItem(ModEntities.vampire, 0x8B15A3, 0xa735e3, new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(REFERENCE.MODID, "vampire_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.hunter, 0x2d05f2, 0x2600e0, new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(REFERENCE.MODID, "vampire_hunter_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.advanced_vampire, 0x8B15A3, 0x560a7e, new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(REFERENCE.MODID, "advanced_vampire_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.advanced_hunter, 0x2d05f2, 0x1a028c, new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(REFERENCE.MODID, "advanced_vampire_hunter_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.vampire_baron, 0x8B15A3, 0x15acda, new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(REFERENCE.MODID, "vampire_baron_spawn_egg"));
        registry.register(new SpawnEggItem(ModEntities.hunter_trainer, 0x2d05f2, 0x1cdb49, new Item.Properties().tab(ItemGroup.TAB_MISC)).setRegistryName(REFERENCE.MODID, "hunter_trainer_spawn_egg"));

        registry.register(new BucketItem(ModFluids.blood, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ItemGroup.TAB_MISC)).setRegistryName(REFERENCE.MODID, "blood_bucket"));
        registry.register(new BucketItem(ModFluids.impure_blood, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ItemGroup.TAB_MISC)).setRegistryName(REFERENCE.MODID, "impure_blood_bucket"));

        registry.register(new GarlicBreadItem());
        registry.register(new UmbrellaItem());

        registry.register(new VampirismItem("hunter_minion_equipment", creativeTabProps()));
        registry.register(new MinionUpgradeItem("hunter_minion_upgrade_simple", 1, 2, VReference.HUNTER_FACTION));
        registry.register(new MinionUpgradeItem("hunter_minion_upgrade_enhanced", 3, 4, VReference.HUNTER_FACTION));
        registry.register(new MinionUpgradeItem("hunter_minion_upgrade_special", 5, 6, VReference.HUNTER_FACTION));

        registry.register(new FeedingAdapterItem());
        registry.register(new VampirismItem("vampire_minion_binding", creativeTabProps()));
        registry.register(new MinionUpgradeItem("vampire_minion_upgrade_simple", 1, 2, VReference.VAMPIRE_FACTION));
        registry.register(new MinionUpgradeItem("vampire_minion_upgrade_enhanced", 3, 4, VReference.VAMPIRE_FACTION));
        registry.register(new MinionUpgradeItem("vampire_minion_upgrade_special", 5, 6, VReference.VAMPIRE_FACTION));

        registry.register(new OblivionItem("oblivion_potion", creativeTabProps()));

        registry.register(new VampireRefinementItem(creativeTabProps(), IRefinementItem.AccessorySlotType.AMULET).setRegistryName(REFERENCE.MODID, "amulet"));
        registry.register(new VampireRefinementItem(creativeTabProps(), IRefinementItem.AccessorySlotType.RING).setRegistryName(REFERENCE.MODID, "ring"));
        registry.register(new VampireRefinementItem(creativeTabProps(), IRefinementItem.AccessorySlotType.OBI_BELT).setRegistryName(REFERENCE.MODID, "obi_belt"));

        registry.register(new VampireClothingItem(EquipmentSlotType.HEAD, "vampire_clothing_crown"));
        registry.register(new VampireClothingItem(EquipmentSlotType.LEGS, "vampire_clothing_legs"));
        registry.register(new VampireClothingItem(EquipmentSlotType.FEET, "vampire_clothing_boots"));
        registry.register(new VampireClothingItem(EquipmentSlotType.HEAD, "vampire_clothing_hat"));

        registry.register(new VampirismItem("cure_apple", creativeTabProps().rarity(Rarity.RARE)));
        registry.register(new VampirismItem("garlic_finder", creativeTabProps().rarity(Rarity.RARE)));

        registry.register(new WallOrFloorItem(ModBlocks.candelabra, ModBlocks.candelabra_wall, new Item.Properties().tab(VampirismMod.creativeTab)).setRegistryName(REFERENCE.MODID, "item_candelabra"));

        registry.register(new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_DECORATIONS), ModBlocks.dark_spruce_sign, ModBlocks.dark_spruce_wall_sign).setRegistryName(REFERENCE.MODID, "dark_spruce_sign"));
        registry.register(new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_DECORATIONS), ModBlocks.cursed_spruce_sign, ModBlocks.cursed_spruce_wall_sign).setRegistryName(REFERENCE.MODID, "cursed_spruce_sign"));

        registry.register(new CrucifixItem(IItemWithTier.TIER.NORMAL));
        registry.register(new CrucifixItem(IItemWithTier.TIER.ENHANCED));
        registry.register(new CrucifixItem(IItemWithTier.TIER.ULTIMATE));

        registry.register(new OilBottle(creativeTabProps().stacksTo(1)).setRegistryName(REFERENCE.MODID, "oil_bottle"));

        if (VampirismMod.inDataGen) {
            registry.register(new DummyItem().setRegistryName("guideapi-vp", "vampirism-guidebook"));
        }
    }

    private static Item.Properties creativeTabProps() {
        return new Item.Properties().tab(VampirismMod.creativeTab);
    }

    public static void fixMappings(RegistryEvent.MissingMappings<Item> event) {
        event.getAllMappings().forEach(missingMapping -> {
            switch (missingMapping.key.toString()) {
                case "vampirism:blood_potion":
                case "vampirism:blood_potion_table":
                    missingMapping.ignore();
                    break;
                case "vampirism:vampire_clothing_head":
                    missingMapping.remap(vampire_clothing_crown);
                    break;
                case "vampirism:vampire_clothing_feet":
                    missingMapping.remap(vampire_clothing_boots);
                    break;
                case "vampirism:item_med_chair":
                    missingMapping.remap(Item.byBlock(ModBlocks.med_chair));
                    break;
                case "vampirism:bloody_spruce_log":
                    missingMapping.remap(cursed_spruce_log);
                    break;
                case "vampirism:bloody_spruce_leaves":
                    missingMapping.remap(dark_spruce_leaves);
                    break;
            }
            if(missingMapping.key.toString().startsWith("vampirism:obsidian_armor")){
                Item hunterArmorReplacement = event.getRegistry().getValue(new ResourceLocation(missingMapping.key.toString().replace("obsidian_armor","hunter_coat")));
                if(hunterArmorReplacement != null){
                    missingMapping.remap(hunterArmorReplacement);
                }
                else{
                    LogManager.getLogger().warn("Could not find hunter armor replacement for {}", missingMapping.key.toString());
                    missingMapping.ignore();
                }
            }
        });
    }
}
