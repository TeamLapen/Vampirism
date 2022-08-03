package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.items.crossbow.DoubleCrossbowItem;
import de.teamlapen.vampirism.items.crossbow.TechCrossbowItem;
import de.teamlapen.vampirism.items.crossbow.*;
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
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Handles all item registrations and reference.
 */
@SuppressWarnings("unused")
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, REFERENCE.MODID);

    //Items
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ENHANCED = ITEMS.register("armor_of_swiftness_chest_enhanced", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_NORMAL = ITEMS.register("armor_of_swiftness_chest_normal", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE = ITEMS.register("armor_of_swiftness_chest_ultimate", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ENHANCED = ITEMS.register("armor_of_swiftness_feet_enhanced", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_NORMAL = ITEMS.register("armor_of_swiftness_feet_normal", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.FEET, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ULTIMATE = ITEMS.register("armor_of_swiftness_feet_ultimate", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ENHANCED = ITEMS.register("armor_of_swiftness_head_enhanced", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_NORMAL = ITEMS.register("armor_of_swiftness_head_normal", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE = ITEMS.register("armor_of_swiftness_head_ultimate", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ENHANCED = ITEMS.register("armor_of_swiftness_legs_enhanced", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_NORMAL = ITEMS.register("armor_of_swiftness_legs_normal", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE = ITEMS.register("armor_of_swiftness_legs_ultimate", () -> new ArmorOfSwiftnessItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<VampirismCrossbowItem> BASIC_CROSSBOW = ITEMS.register("basic_crossbow", () -> new SingleCrossbowItem(creativeTabProps().defaultDurability(300), 1,20,ItemTier.WOOD));
    public static final RegistryObject<DoubleCrossbowItem> BASIC_DOUBLE_CROSSBOW = ITEMS.register("basic_double_crossbow", () -> new DoubleCrossbowItem(creativeTabProps().durability(300),1,20,ItemTier.WOOD));
    public static final RegistryObject<TechCrossbowItem> BASIC_TECH_CROSSBOW = ITEMS.register("basic_tech_crossbow", () -> new TechCrossbowItem(creativeTabProps().durability(300),1.6F, 10, ItemTier.DIAMOND));

    public static final RegistryObject<BloodBottleItem> BLOOD_BOTTLE = ITEMS.register("blood_bottle", BloodBottleItem::new);
    public static final RegistryObject<BucketItem> BLOOD_BUCKET = ITEMS.register("blood_bucket", () -> new BucketItem(ModFluids.BLOOD, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> BLOOD_INFUSED_IRON_INGOT = ITEMS.register("blood_infused_iron_ingot", () -> new Item(creativeTabProps()));
    public static final RegistryObject<Item> BLOOD_INFUSED_ENHANCED_IRON_INGOT = ITEMS.register("blood_infused_enhanced_iron_ingot", () -> new Item(creativeTabProps()));

    public static final RegistryObject<CrossbowArrowItem> CROSSBOW_ARROW_NORMAL = ITEMS.register("crossbow_arrow_normal", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.NORMAL));
    public static final RegistryObject<CrossbowArrowItem> CROSSBOW_ARROW_SPITFIRE = ITEMS.register("crossbow_arrow_spitfire", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.SPITFIRE));
    public static final RegistryObject<CrossbowArrowItem> CROSSBOW_ARROW_VAMPIRE_KILLER = ITEMS.register("crossbow_arrow_vampire_killer", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.VAMPIRE_KILLER));
    public static final RegistryObject<CrossbowArrowItem> CROSSBOW_ARROW_TELEPORT = ITEMS.register("crossbow_arrow_teleport", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.TELEPORT));

    public static final RegistryObject<SingleCrossbowItem> ENHANCED_CROSSBOW = ITEMS.register("enhanced_crossbow", () -> new SingleCrossbowItem( creativeTabProps().durability(250), 1.5F, 15, ItemTier.IRON));
    public static final RegistryObject<DoubleCrossbowItem> ENHANCED_DOUBLE_CROSSBOW = ITEMS.register("enhanced_double_crossbow", () -> new DoubleCrossbowItem(creativeTabProps().durability(350),1.5F, 15, ItemTier.IRON));
    public static final RegistryObject<TechCrossbowItem> ENHANCED_TECH_CROSSBOW = ITEMS.register("enhanced_tech_crossbow", () -> new TechCrossbowItem(creativeTabProps().durability(450), 1.7F, 10, ItemTier.DIAMOND));

    public static final RegistryObject<Item> GARLIC_BEACON_CORE = ITEMS.register("garlic_beacon_core", () -> new Item(creativeTabProps()));
    public static final RegistryObject<Item> GARLIC_BEACON_CORE_IMPROVED = ITEMS.register("garlic_beacon_core_improved", () -> new Item(creativeTabProps()));

    public static final RegistryObject<HeartSeekerItem> HEART_SEEKER_ENHANCED = ITEMS.register("heart_seeker_enhanced", () -> new HeartSeekerItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HeartSeekerItem> HEART_SEEKER_NORMAL = ITEMS.register("heart_seeker_normal", () -> new HeartSeekerItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HeartSeekerItem> HEART_SEEKER_ULTIMATE = ITEMS.register("heart_seeker_ultimate", () -> new HeartSeekerItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<HeartStrikerItem> HEART_STRIKER_ENHANCED = ITEMS.register("heart_striker_enhanced", () -> new HeartStrikerItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HeartStrikerItem> HEART_STRIKER_NORMAL = ITEMS.register("heart_striker_normal", () -> new HeartStrikerItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HeartStrikerItem> HEART_STRIKER_ULTIMATE = ITEMS.register("heart_striker_ultimate", () -> new HeartStrikerItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ENHANCED = ITEMS.register("holy_water_bottle_enhanced", () -> new HolyWaterBottleItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HolyWaterBottleItem> HOLY_WATER_BOTTLE_NORMAL = ITEMS.register("holy_water_bottle_normal", () -> new HolyWaterBottleItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ULTIMATE = ITEMS.register("holy_water_bottle_ultimate", () -> new HolyWaterBottleItem(IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ENHANCED = ITEMS.register("holy_water_splash_bottle_enhanced", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_NORMAL = ITEMS.register("holy_water_splash_bottle_normal", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ULTIMATE = ITEMS.register("holy_water_splash_bottle_ultimate", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<BlessableItem> PURE_SALT_WATER = ITEMS.register("pure_salt_water", () -> new BlessableItem(new Item.Properties().stacksTo(1), HOLY_WATER_BOTTLE_NORMAL::get, HOLY_WATER_BOTTLE_ENHANCED::get) {
        @Override
        public boolean isFoil(ItemStack stack) {
            return true;
        }
    });

    public static final RegistryObject<HunterAxeItem> HUNTER_AXE_ENHANCED = ITEMS.register("hunter_axe_enhanced", () -> new HunterAxeItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterAxeItem> HUNTER_AXE_NORMAL = ITEMS.register("hunter_axe_normal", () -> new HunterAxeItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterAxeItem> HUNTER_AXE_ULTIMATE = ITEMS.register("hunter_axe_ultimate", () -> new HunterAxeItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_CHEST_ENHANCED = ITEMS.register("hunter_coat_chest_enhanced", () -> new HunterCoatItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_CHEST_NORMAL = ITEMS.register("hunter_coat_chest_normal", () -> new HunterCoatItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_CHEST_ULTIMATE = ITEMS.register("hunter_coat_chest_ultimate", () -> new HunterCoatItem(EquipmentSlotType.CHEST, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_FEET_ENHANCED = ITEMS.register("hunter_coat_feet_enhanced", () -> new HunterCoatItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_FEET_NORMAL = ITEMS.register("hunter_coat_feet_normal", () -> new HunterCoatItem(EquipmentSlotType.FEET, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_FEET_ULTIMATE = ITEMS.register("hunter_coat_feet_ultimate", () -> new HunterCoatItem(EquipmentSlotType.FEET, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_HEAD_ENHANCED = ITEMS.register("hunter_coat_head_enhanced", () -> new HunterCoatItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_HEAD_NORMAL = ITEMS.register("hunter_coat_head_normal", () -> new HunterCoatItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_HEAD_ULTIMATE = ITEMS.register("hunter_coat_head_ultimate", () -> new HunterCoatItem(EquipmentSlotType.HEAD, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_LEGS_ENHANCED = ITEMS.register("hunter_coat_legs_enhanced", () -> new HunterCoatItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_LEGS_NORMAL = ITEMS.register("hunter_coat_legs_normal", () -> new HunterCoatItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_LEGS_ULTIMATE = ITEMS.register("hunter_coat_legs_ultimate", () -> new HunterCoatItem(EquipmentSlotType.LEGS, IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<HunterHatItem> HUNTER_HAT_HEAD_0 = ITEMS.register("hunter_hat_head_0", () -> new HunterHatItem(0));
    public static final RegistryObject<HunterHatItem> HUNTER_HAT_HEAD_1 = ITEMS.register("hunter_hat_head_1", () -> new HunterHatItem(1));

    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_0 = ITEMS.register("hunter_intel_0", () -> new HunterIntelItem(0));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_1 = ITEMS.register("hunter_intel_1", () -> new HunterIntelItem(1));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_2 = ITEMS.register("hunter_intel_2", () -> new HunterIntelItem(2));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_3 = ITEMS.register("hunter_intel_3", () -> new HunterIntelItem(3));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_4 = ITEMS.register("hunter_intel_4", () -> new HunterIntelItem(4));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_5 = ITEMS.register("hunter_intel_5", () -> new HunterIntelItem(5));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_6 = ITEMS.register("hunter_intel_6", () -> new HunterIntelItem(6));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_7 = ITEMS.register("hunter_intel_7", () -> new HunterIntelItem(7));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_8 = ITEMS.register("hunter_intel_8", () -> new HunterIntelItem(8));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_9 = ITEMS.register("hunter_intel_9", () -> new HunterIntelItem(9));

    public static final RegistryObject<VampirismItemBloodFood> HUMAN_HEART = ITEMS.register("human_heart", () -> new VampirismItemBloodFood((new Food.Builder()).nutrition(20).saturationMod(1.5F).build(), new Food.Builder().nutrition(5).saturationMod(1f).build()));

    public static final RegistryObject<InjectionItem> INJECTION_EMPTY = ITEMS.register("injection_empty", () -> new InjectionItem(InjectionItem.TYPE.EMPTY));
    public static final RegistryObject<InjectionItem> INJECTION_GARLIC = ITEMS.register("injection_garlic", () -> new InjectionItem(InjectionItem.TYPE.GARLIC));
    public static final RegistryObject<InjectionItem> INJECTION_SANGUINARE = ITEMS.register("injection_sanguinare", () -> new InjectionItem(InjectionItem.TYPE.SANGUINARE));
    public static final RegistryObject<InjectionItem> INJECTION_ZOMBIE_BLOOD = ITEMS.register("injection_zombie_blood", () -> new InjectionItem(InjectionItem.TYPE.ZOMBIE_BLOOD));

    public static final RegistryObject<Item> CURE_APPLE = ITEMS.register("cure_apple", () -> new Item(creativeTabProps().rarity(Rarity.RARE)));

    public static final RegistryObject<BucketItem> IMPURE_BLOOD_BUCKET = ITEMS.register("impure_blood_bucket", () -> new BucketItem(ModFluids.IMPURE_BLOOD, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<GarlicItem> ITEM_GARLIC = ITEMS.register("item_garlic", GarlicItem::new);
    public static final RegistryObject<GarlicBreadItem> GARLIC_BREAD = ITEMS.register("garlic_bread", GarlicBreadItem::new);
    public static final RegistryObject<AlchemicalFireItem> ITEM_ALCHEMICAL_FIRE = ITEMS.register("item_alchemical_fire", AlchemicalFireItem::new);

    public static final RegistryObject<TentItem> ITEM_TENT = ITEMS.register("item_tent", () -> new TentItem(false));
    public static final RegistryObject<TentItem> ITEM_TENT_SPAWNER = ITEMS.register("item_tent_spawner", () -> new TentItem(true));

    public static final RegistryObject<PitchforkItem> PITCHFORK = ITEMS.register("pitchfork", PitchforkItem::new);

    public static final RegistryObject<PureBloodItem> PURE_BLOOD_0 = ITEMS.register("pure_blood_0", () -> new PureBloodItem(0));
    public static final RegistryObject<PureBloodItem> PURE_BLOOD_1 = ITEMS.register("pure_blood_1", () -> new PureBloodItem(1));
    public static final RegistryObject<PureBloodItem> PURE_BLOOD_2 = ITEMS.register("pure_blood_2", () -> new PureBloodItem(2));
    public static final RegistryObject<PureBloodItem> PURE_BLOOD_3 = ITEMS.register("pure_blood_3", () -> new PureBloodItem(3));
    public static final RegistryObject<PureBloodItem> PURE_BLOOD_4 = ITEMS.register("pure_blood_4", () -> new PureBloodItem(4));

    public static final RegistryObject<Item> PURIFIED_GARLIC = ITEMS.register("purified_garlic", () -> new Item(creativeTabProps()));
    public static final RegistryObject<Item> PURE_SALT = ITEMS.register("pure_salt", () -> new Item(creativeTabProps()));
    public static final RegistryObject<Item> SOUL_ORB_VAMPIRE = ITEMS.register("soul_orb_vampire", () -> new Item(creativeTabProps()));

    public static final RegistryObject<StakeItem> STAKE = ITEMS.register("stake", StakeItem::new);
    public static final RegistryObject<Item> TECH_CROSSBOW_AMMO_PACKAGE = ITEMS.register("tech_crossbow_ammo_package", () -> new ArrowContainer(new Item.Properties().tab(VampirismMod.creativeTab), CROSSBOW_ARROW_NORMAL,12) {

                @OnlyIn(Dist.CLIENT)
                @Override
                public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    tooltip.add(new TranslationTextComponent("item.vampirism.tech_crossbow_ammo_package.tooltip", new TranslationTextComponent(BASIC_TECH_CROSSBOW.get().getDescriptionId())).withStyle(TextFormatting.GRAY));
                }

            });

    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_BLACK_BLUE = ITEMS.register("vampire_cloak_black_blue", () -> new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKBLUE));
    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_BLACK_RED = ITEMS.register("vampire_cloak_black_red", () -> new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKRED));
    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_BLACK_WHITE = ITEMS.register("vampire_cloak_black_white", () -> new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKWHITE));
    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_RED_BLACK = ITEMS.register("vampire_cloak_red_black", () -> new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.REDBLACK));
    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_WHITE_BLACK = ITEMS.register("vampire_cloak_white_black", () -> new ColoredVampireClothingItem(EquipmentSlotType.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.WHITEBLACK));

    public static final RegistryObject<VampireBloodBottleItem> VAMPIRE_BLOOD_BOTTLE = ITEMS.register("vampire_blood_bottle", VampireBloodBottleItem::new);
    public static final RegistryObject<VampireBookItem> VAMPIRE_BOOK = ITEMS.register("vampire_book", VampireBookItem::new);
    public static final RegistryObject<VampireFangItem> VAMPIRE_FANG = ITEMS.register("vampire_fang", VampireFangItem::new);
    public static final RegistryObject<VampirismItemBloodFood> WEAK_HUMAN_HEART = ITEMS.register("weak_human_heart", () -> new VampirismItemBloodFood((new Food.Builder()).nutrition(10).saturationMod(0.9F).build(), new Food.Builder().nutrition(3).saturationMod(1f).build()));


    public static final RegistryObject<SpawnEggItem> VAMPIRE_SPAWN_EGG = ITEMS.register("vampire_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.VAMPIRE, 0x8B15A3, 0xa735e3, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> VAMPIRE_HUNTER_SPAWN_EGG = ITEMS.register("vampire_hunter_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.HUNTER, 0x2d05f2, 0x2600e0, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> ADVANCED_VAMPIRE_SPAWN_EGG = ITEMS.register("advanced_vampire_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.ADVANCED_VAMPIRE, 0x8B15A3, 0x560a7e, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG = ITEMS.register("advanced_vampire_hunter_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.ADVANCED_HUNTER, 0x2d05f2, 0x1a028c, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> VAMPIRE_BARON_SPAWN_EGG = ITEMS.register("vampire_baron_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.VAMPIRE_BARON, 0x8B15A3, 0x15acda, new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> HUNTER_TRAINER_SPAWN_EGG = ITEMS.register("hunter_trainer_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.HUNTER_TRAINER, 0x2d05f2, 0x1cdb49, new Item.Properties().tab(ItemGroup.TAB_MISC)));


    public static final RegistryObject<UmbrellaItem> UMBRELLA = ITEMS.register("umbrella", UmbrellaItem::new);

    public static final RegistryObject<Item> HUNTER_MINION_EQUIPMENT = ITEMS.register("hunter_minion_equipment", () -> new Item(creativeTabProps()));
    public static final RegistryObject<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SIMPLE = ITEMS.register("hunter_minion_upgrade_simple", () -> new MinionUpgradeItem(1,2, VReference.HUNTER_FACTION));
    public static final RegistryObject<MinionUpgradeItem> HUNTER_MINION_UPGRADE_ENHANCED = ITEMS.register("hunter_minion_upgrade_enhanced", () -> new MinionUpgradeItem(3,4, VReference.HUNTER_FACTION));
    public static final RegistryObject<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SPECIAL = ITEMS.register("hunter_minion_upgrade_special", () -> new MinionUpgradeItem(5,6, VReference.HUNTER_FACTION));
    public static final RegistryObject<FeedingAdapterItem> FEEDING_ADAPTER = ITEMS.register("feeding_adapter", FeedingAdapterItem::new);
    public static final RegistryObject<Item> VAMPIRE_MINION_BINDING = ITEMS.register("vampire_minion_binding", () -> new Item(creativeTabProps()));
    public static final RegistryObject<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SIMPLE = ITEMS.register("vampire_minion_upgrade_simple", () -> new MinionUpgradeItem(1,2, VReference.VAMPIRE_FACTION));
    public static final RegistryObject<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_ENHANCED = ITEMS.register("vampire_minion_upgrade_enhanced", () -> new MinionUpgradeItem(3,4, VReference.VAMPIRE_FACTION));
    public static final RegistryObject<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SPECIAL = ITEMS.register("vampire_minion_upgrade_special", () -> new MinionUpgradeItem(5,6, VReference.VAMPIRE_FACTION));

    public static final RegistryObject<OblivionItem> OBLIVION_POTION = ITEMS.register("oblivion_potion", () -> new OblivionItem(creativeTabProps()));

    public static final RegistryObject<VampireRefinementItem> AMULET = ITEMS.register("amulet", () -> new VampireRefinementItem(creativeTabProps(), IRefinementItem.AccessorySlotType.AMULET));
    public static final RegistryObject<VampireRefinementItem> RING = ITEMS.register("ring", () -> new VampireRefinementItem(creativeTabProps(), IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<VampireRefinementItem> OBI_BELT = ITEMS.register("obi_belt", () -> new VampireRefinementItem(creativeTabProps(), IRefinementItem.AccessorySlotType.OBI_BELT));

    public static final RegistryObject<VampireClothingItem> VAMPIRE_CLOTHING_CROWN = ITEMS.register("vampire_clothing_crown", () -> new VampireClothingItem(EquipmentSlotType.HEAD));
    public static final RegistryObject<VampireClothingItem> VAMPIRE_CLOTHING_LEGS = ITEMS.register("vampire_clothing_legs", () -> new VampireClothingItem(EquipmentSlotType.LEGS));
    public static final RegistryObject<VampireClothingItem> VAMPIRE_CLOTHING_BOOTS = ITEMS.register("vampire_clothing_boots", () -> new VampireClothingItem(EquipmentSlotType.FEET));
    public static final RegistryObject<VampireClothingItem> VAMPIRE_CLOTHING_HAT = ITEMS.register("vampire_clothing_hat", () -> new VampireClothingItem(EquipmentSlotType.HEAD));

    public static final RegistryObject<Item> GARLIC_FINDER = ITEMS.register("garlic_finder", () -> new Item(creativeTabProps().rarity(Rarity.RARE)));

    public static final RegistryObject<WallOrFloorItem> ITEM_CANDELABRA = ITEMS.register("item_candelabra", () -> new WallOrFloorItem(ModBlocks.CANDELABRA.get(), ModBlocks.CANDELABRA_WALL.get(), new Item.Properties().tab(VampirismMod.creativeTab)));
    public static final RegistryObject<SignItem> DARK_SPRUCE_SIGN = ITEMS.register("dark_spruce_sign", () -> new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_DECORATIONS), ModBlocks.DARK_SPRUCE_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_SIGN.get()));
    public static final RegistryObject<SignItem> CURSED_SPRUCE_SIGN = ITEMS.register("cursed_spruce_sign", () -> new SignItem((new Item.Properties()).stacksTo(16).tab(ItemGroup.TAB_DECORATIONS), ModBlocks.CURSED_SPRUCE_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_SIGN.get()));

    public static final RegistryObject<CrucifixItem> CRUCIFIX_NORMAL = ITEMS.register("crucifix_normal", () -> new CrucifixItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<CrucifixItem> CRUCIFIX_ENHANCED = ITEMS.register("crucifix_enhanced", () -> new CrucifixItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<CrucifixItem> CRUCIFIX_ULTIMATE = ITEMS.register("crucifix_ultimate", () -> new CrucifixItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<VampirismBoatItem> DARK_SPRUCE_BOAT = ITEMS.register("dark_spruce_boat", () -> new VampirismBoatItem(VampirismBoatItem.BoatType.DARK_SPRUCE, creativeTabProps().stacksTo(1)));
    public static final RegistryObject<VampirismBoatItem> CURSED_SPRUCE_BOAT = ITEMS.register("cursed_spruce_boat", () -> new VampirismBoatItem(VampirismBoatItem.BoatType.CURSED_SPRUCE, creativeTabProps().stacksTo(1)));

    public static final RegistryObject<OilBottle> OIL_BOTTLE = ITEMS.register("oil_bottle", () -> new OilBottle(creativeTabProps().stacksTo(1)));
    static void registerCraftingRecipes() {
        // Brewing
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), Ingredient.of(new ItemStack(PURE_SALT.get())), new ItemStack(PURE_SALT_WATER.get()));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(HOLY_WATER_BOTTLE_NORMAL.get()), Ingredient.of(Items.GUNPOWDER), new ItemStack(HOLY_WATER_SPLASH_BOTTLE_NORMAL.get())) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return HOLY_WATER_BOTTLE_NORMAL.get().equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(HOLY_WATER_BOTTLE_ENHANCED.get()), Ingredient.of(Items.GUNPOWDER), new ItemStack(HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get())) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return HOLY_WATER_BOTTLE_ENHANCED.get().equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(HOLY_WATER_BOTTLE_ULTIMATE.get()), Ingredient.of(Items.GUNPOWDER), new ItemStack(HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get())) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {
                return HOLY_WATER_BOTTLE_ULTIMATE.get().equals(stack.getItem());
            }
        });
    }


    static void registerItems(IEventBus bus) {
        ITEMS.register(bus);
        if (VampirismMod.inDataGen) {
            DeferredRegister<Item> DUMMY_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "guideapi_vp");
            DUMMY_ITEMS.register("vampirism-guidebook", DummyItem::new);
            DUMMY_ITEMS.register(bus);
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
                    missingMapping.remap(VAMPIRE_CLOTHING_CROWN.get());
                    break;
                case "vampirism:vampire_clothing_feet":
                    missingMapping.remap(VAMPIRE_CLOTHING_BOOTS.get());
                    break;
                case "vampirism:item_med_chair":
                    missingMapping.remap(ModBlocks.MED_CHAIR.get().asItem());
                    break;
                case "vampirism:bloody_spruce_log":
                    missingMapping.remap(ModBlocks.CURSED_SPRUCE_LOG.get().asItem());
                    break;
                case "vampirism:bloody_spruce_leaves":
                    missingMapping.remap(ModBlocks.DARK_SPRUCE_LEAVES.get().asItem());
                    break;
                case "vampirism:coffin":
                    missingMapping.remap(ModBlocks.COFFIN_RED.get().asItem());
                case "vampirism:holy_salt_water":
                    missingMapping.remap(PURE_SALT_WATER.get());
                    break;
                case "vampirism:holy_salt":
                    missingMapping.remap(PURE_SALT.get());
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
