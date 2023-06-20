package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.entity.IVampirismBoat;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.items.crossbow.ArrowContainer;
import de.teamlapen.vampirism.items.crossbow.DoubleCrossbowItem;
import de.teamlapen.vampirism.items.crossbow.SingleCrossbowItem;
import de.teamlapen.vampirism.items.crossbow.TechCrossbowItem;
import de.teamlapen.vampirism.misc.VampirismCreativeTab;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Handles all item registrations and reference.
 */
@SuppressWarnings("unused")
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, REFERENCE.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, REFERENCE.MODID);

    private static final Set<RegistryObject<? extends Item>> VAMPIRISM_TAB_ITEMS = new HashSet<>();
    private static final Map<ResourceKey<CreativeModeTab>, Set<RegistryObject<? extends Item>>> CREATIVE_TAB_ITEMS = new HashMap<>();

    public static final ResourceKey<CreativeModeTab> VAMPIRISM_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(REFERENCE.MODID, "default"));
    public static final RegistryObject<CreativeModeTab> VAMPIRISM_TAB = CREATIVE_TABS.register(VAMPIRISM_TAB_KEY.location().getPath(), () -> VampirismCreativeTab.builder(VAMPIRISM_TAB_ITEMS.stream().map(RegistryObject::get).collect(Collectors.toSet())).build());

    //Items
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_NORMAL = register("armor_of_swiftness_chest_normal", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.CHESTPLATE, ArmorOfSwiftnessItem.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ENHANCED = register("armor_of_swiftness_chest_enhanced", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.CHESTPLATE, ArmorOfSwiftnessItem.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE = register("armor_of_swiftness_chest_ultimate", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.CHESTPLATE, ArmorOfSwiftnessItem.ULTIMATE));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_NORMAL = register("armor_of_swiftness_feet_normal", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.BOOTS, ArmorOfSwiftnessItem.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ENHANCED = register("armor_of_swiftness_feet_enhanced", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.BOOTS, ArmorOfSwiftnessItem.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ULTIMATE = register("armor_of_swiftness_feet_ultimate", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.BOOTS, ArmorOfSwiftnessItem.ULTIMATE));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_NORMAL = register("armor_of_swiftness_head_normal", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.HELMET, ArmorOfSwiftnessItem.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ENHANCED = register("armor_of_swiftness_head_enhanced", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.HELMET, ArmorOfSwiftnessItem.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE = register("armor_of_swiftness_head_ultimate", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.HELMET, ArmorOfSwiftnessItem.ULTIMATE));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_NORMAL = register("armor_of_swiftness_legs_normal", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.LEGGINGS, ArmorOfSwiftnessItem.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ENHANCED = register("armor_of_swiftness_legs_enhanced", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.LEGGINGS, ArmorOfSwiftnessItem.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE = register("armor_of_swiftness_legs_ultimate", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.LEGGINGS, ArmorOfSwiftnessItem.ULTIMATE));

    public static final RegistryObject<SingleCrossbowItem> BASIC_CROSSBOW = register("basic_crossbow", () -> new SingleCrossbowItem(props().defaultDurability(465), 1, 20, Tiers.WOOD));
    public static final RegistryObject<DoubleCrossbowItem> BASIC_DOUBLE_CROSSBOW = register("basic_double_crossbow", () -> new DoubleCrossbowItem(props().durability(465), 1, 20, Tiers.WOOD));
    public static final RegistryObject<TechCrossbowItem> BASIC_TECH_CROSSBOW = register("basic_tech_crossbow", () -> new TechCrossbowItem(props().durability(930), 1.6F, 40, Tiers.DIAMOND));

    public static final RegistryObject<BloodBottleItem> BLOOD_BOTTLE = ITEMS.register("blood_bottle", BloodBottleItem::new);
    public static final RegistryObject<BucketItem> BLOOD_BUCKET = register("blood_bucket", CreativeModeTabs.TOOLS_AND_UTILITIES, () -> new BucketItem(ModFluids.BLOOD, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> BLOOD_INFUSED_IRON_INGOT = register("blood_infused_iron_ingot", () -> new Item(props()));
    public static final RegistryObject<Item> BLOOD_INFUSED_ENHANCED_IRON_INGOT = register("blood_infused_enhanced_iron_ingot", () -> new Item(props()));

    public static final RegistryObject<CrossbowArrowItem> CROSSBOW_ARROW_NORMAL = register("crossbow_arrow_normal", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.NORMAL));
    public static final RegistryObject<CrossbowArrowItem> CROSSBOW_ARROW_SPITFIRE = register("crossbow_arrow_spitfire", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.SPITFIRE));
    public static final RegistryObject<CrossbowArrowItem> CROSSBOW_ARROW_VAMPIRE_KILLER = register("crossbow_arrow_vampire_killer", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.VAMPIRE_KILLER));
    public static final RegistryObject<CrossbowArrowItem> CROSSBOW_ARROW_TELEPORT = register("crossbow_arrow_teleport", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.TELEPORT));

    public static final RegistryObject<SingleCrossbowItem> ENHANCED_CROSSBOW = register("enhanced_crossbow", () -> new SingleCrossbowItem(props().durability(930), 1.5F, 15, Tiers.IRON));
    public static final RegistryObject<DoubleCrossbowItem> ENHANCED_DOUBLE_CROSSBOW = register("enhanced_double_crossbow", () -> new DoubleCrossbowItem(props().durability(930),1.5F, 15, Tiers.IRON));
    public static final RegistryObject<TechCrossbowItem> ENHANCED_TECH_CROSSBOW = register("enhanced_tech_crossbow", () -> new TechCrossbowItem(props().durability(1860), 1.7F, 30, Tiers.DIAMOND));

    public static final RegistryObject<Item> GARLIC_DIFFUSER_CORE = register("garlic_diffuser_core", () -> new Item(props()));
    public static final RegistryObject<Item> GARLIC_DIFFUSER_CORE_IMPROVED = register("garlic_diffuser_core_improved", () -> new Item(props()));

    public static final RegistryObject<HeartSeekerItem> HEART_SEEKER_NORMAL = register("heart_seeker_normal", () -> new HeartSeekerItem(HeartSeekerItem.NORMAL));
    public static final RegistryObject<HeartSeekerItem> HEART_SEEKER_ENHANCED = register("heart_seeker_enhanced", () -> new HeartSeekerItem(HeartSeekerItem.ENHANCED));
    public static final RegistryObject<HeartSeekerItem> HEART_SEEKER_ULTIMATE = register("heart_seeker_ultimate", () -> new HeartSeekerItem(HeartSeekerItem.ULTIMATE));

    public static final RegistryObject<HeartStrikerItem> HEART_STRIKER_NORMAL = register("heart_striker_normal", () -> new HeartStrikerItem(HeartStrikerItem.NORMAL));
    public static final RegistryObject<HeartStrikerItem> HEART_STRIKER_ENHANCED = register("heart_striker_enhanced", () -> new HeartStrikerItem(HeartStrikerItem.ENHANCED));
    public static final RegistryObject<HeartStrikerItem> HEART_STRIKER_ULTIMATE = register("heart_striker_ultimate", () -> new HeartStrikerItem(HeartStrikerItem.ULTIMATE));

    public static final RegistryObject<HolyWaterBottleItem> HOLY_WATER_BOTTLE_NORMAL = register("holy_water_bottle_normal", () -> new HolyWaterBottleItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ENHANCED = register("holy_water_bottle_enhanced", () -> new HolyWaterBottleItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ULTIMATE = register("holy_water_bottle_ultimate", () -> new HolyWaterBottleItem(IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_NORMAL = register("holy_water_splash_bottle_normal", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ENHANCED = register("holy_water_splash_bottle_enhanced", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ULTIMATE = register("holy_water_splash_bottle_ultimate", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<BlessableItem> PURE_SALT_WATER = register("pure_salt_water", () -> new BlessableItem(new Item.Properties().stacksTo(1), HOLY_WATER_BOTTLE_NORMAL::get, HOLY_WATER_BOTTLE_ENHANCED::get) {
        @Override
        public boolean isFoil(ItemStack stack) {
            return true;
        }
    });

    public static final RegistryObject<HunterAxeItem> HUNTER_AXE_NORMAL = register("hunter_axe_normal", () -> new HunterAxeItem(HunterAxeItem.NORMAL));
    public static final RegistryObject<HunterAxeItem> HUNTER_AXE_ENHANCED = register("hunter_axe_enhanced", () -> new HunterAxeItem(HunterAxeItem.ENHANCED));
    public static final RegistryObject<HunterAxeItem> HUNTER_AXE_ULTIMATE = register("hunter_axe_ultimate", () -> new HunterAxeItem(HunterAxeItem.ULTIMATE));

    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_CHEST_NORMAL = register("hunter_coat_chest_normal", () -> new HunterCoatItem(ArmorItem.Type.CHESTPLATE, HunterCoatItem.NORMAL));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_CHEST_ENHANCED = register("hunter_coat_chest_enhanced", () -> new HunterCoatItem(ArmorItem.Type.CHESTPLATE, HunterCoatItem.ENHANCED));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_CHEST_ULTIMATE = register("hunter_coat_chest_ultimate", () -> new HunterCoatItem(ArmorItem.Type.CHESTPLATE, HunterCoatItem.ULTIMATE));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_FEET_NORMAL = register("hunter_coat_feet_normal", () -> new HunterCoatItem(ArmorItem.Type.BOOTS, HunterCoatItem.NORMAL));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_FEET_ENHANCED = register("hunter_coat_feet_enhanced", () -> new HunterCoatItem(ArmorItem.Type.BOOTS, HunterCoatItem.ENHANCED));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_FEET_ULTIMATE = register("hunter_coat_feet_ultimate", () -> new HunterCoatItem(ArmorItem.Type.BOOTS, HunterCoatItem.ULTIMATE));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_HEAD_NORMAL = register("hunter_coat_head_normal", () -> new HunterCoatItem(ArmorItem.Type.HELMET, HunterCoatItem.NORMAL));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_HEAD_ENHANCED = register("hunter_coat_head_enhanced", () -> new HunterCoatItem(ArmorItem.Type.HELMET, HunterCoatItem.ENHANCED));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_HEAD_ULTIMATE = register("hunter_coat_head_ultimate", () -> new HunterCoatItem(ArmorItem.Type.HELMET, HunterCoatItem.ULTIMATE));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_LEGS_NORMAL = register("hunter_coat_legs_normal", () -> new HunterCoatItem(ArmorItem.Type.LEGGINGS, HunterCoatItem.NORMAL));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_LEGS_ENHANCED = register("hunter_coat_legs_enhanced", () -> new HunterCoatItem(ArmorItem.Type.LEGGINGS, HunterCoatItem.ENHANCED));
    public static final RegistryObject<HunterCoatItem> HUNTER_COAT_LEGS_ULTIMATE = register("hunter_coat_legs_ultimate", () -> new HunterCoatItem(ArmorItem.Type.LEGGINGS, HunterCoatItem.ULTIMATE));

    public static final RegistryObject<HunterHatItem> HUNTER_HAT_HEAD_0 = register("hunter_hat_head_0", () -> new HunterHatItem(0));
    public static final RegistryObject<HunterHatItem> HUNTER_HAT_HEAD_1 = register("hunter_hat_head_1", () -> new HunterHatItem(1));

    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_0 = register("hunter_intel_0", () -> new HunterIntelItem(0));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_1 = register("hunter_intel_1", () -> new HunterIntelItem(1));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_2 = register("hunter_intel_2", () -> new HunterIntelItem(2));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_3 = register("hunter_intel_3", () -> new HunterIntelItem(3));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_4 = register("hunter_intel_4", () -> new HunterIntelItem(4));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_5 = register("hunter_intel_5", () -> new HunterIntelItem(5));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_6 = register("hunter_intel_6", () -> new HunterIntelItem(6));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_7 = register("hunter_intel_7", () -> new HunterIntelItem(7));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_8 = register("hunter_intel_8", () -> new HunterIntelItem(8));
    public static final RegistryObject<HunterIntelItem> HUNTER_INTEL_9 = register("hunter_intel_9", () -> new HunterIntelItem(9));

    public static final RegistryObject<VampirismItemBloodFoodItem> HUMAN_HEART = register("human_heart", () -> new VampirismItemBloodFoodItem((new FoodProperties.Builder()).nutrition(20).saturationMod(1.5F).build(), new FoodProperties.Builder().nutrition(5).saturationMod(1f).build()));

    public static final RegistryObject<InjectionItem> INJECTION_EMPTY = register("injection_empty", () -> new InjectionItem(InjectionItem.TYPE.EMPTY));
    public static final RegistryObject<InjectionItem> INJECTION_GARLIC = register("injection_garlic", () -> new InjectionItem(InjectionItem.TYPE.GARLIC));
    public static final RegistryObject<InjectionItem> INJECTION_SANGUINARE = register("injection_sanguinare", () -> new InjectionItem(InjectionItem.TYPE.SANGUINARE));

    public static final RegistryObject<BucketItem> IMPURE_BLOOD_BUCKET = register("impure_blood_bucket", CreativeModeTabs.TOOLS_AND_UTILITIES, () -> new BucketItem(ModFluids.IMPURE_BLOOD, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<GarlicItem> ITEM_GARLIC = register("item_garlic", GarlicItem::new);
    public static final RegistryObject<GarlicBreadItem> GARLIC_BREAD = register("garlic_bread", GarlicBreadItem::new);
    public static final RegistryObject<AlchemicalFireItem> ITEM_ALCHEMICAL_FIRE = register("item_alchemical_fire", AlchemicalFireItem::new);

    public static final RegistryObject<TentItem> ITEM_TENT = register("item_tent", () -> new TentItem(false));
    public static final RegistryObject<TentItem> ITEM_TENT_SPAWNER = register("item_tent_spawner", () -> new TentItem(true));

    public static final RegistryObject<PitchforkItem> PITCHFORK = register("pitchfork", PitchforkItem::new);

    public static final RegistryObject<PureBloodItem> PURE_BLOOD_0 = register("pure_blood_0", () -> new PureBloodItem(0));
    public static final RegistryObject<PureBloodItem> PURE_BLOOD_1 = register("pure_blood_1", () -> new PureBloodItem(1));
    public static final RegistryObject<PureBloodItem> PURE_BLOOD_2 = register("pure_blood_2", () -> new PureBloodItem(2));
    public static final RegistryObject<PureBloodItem> PURE_BLOOD_3 = register("pure_blood_3", () -> new PureBloodItem(3));
    public static final RegistryObject<PureBloodItem> PURE_BLOOD_4 = register("pure_blood_4", () -> new PureBloodItem(4));

    public static final RegistryObject<Item> PURIFIED_GARLIC = register("purified_garlic", () -> new Item(props()));
    public static final RegistryObject<Item> PURE_SALT = register("pure_salt", () -> new Item(props()));
    public static final RegistryObject<Item> SOUL_ORB_VAMPIRE = register("soul_orb_vampire", () -> new Item(props()));

    public static final RegistryObject<StakeItem> STAKE = register("stake", StakeItem::new);
    public static final RegistryObject<Item> ARROW_CLIP = register("tech_crossbow_ammo_package", () -> new ArrowContainer(props(), 12, (stack) -> stack.is(CROSSBOW_ARROW_NORMAL.get())) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> texts, @NotNull TooltipFlag flag) {
            texts.add(Component.translatable("item.vampirism.tech_crossbow_ammo_package.tooltip", Component.translatable(BASIC_TECH_CROSSBOW.get().getDescriptionId())).withStyle(ChatFormatting.GRAY));
            texts.add(Component.empty());
            super.appendHoverText(stack, level, texts, flag);
        }

    });

    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_BLACK_BLUE = register("vampire_cloak_black_blue", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKBLUE));
    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_BLACK_RED = register("vampire_cloak_black_red", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKRED));
    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_BLACK_WHITE = register("vampire_cloak_black_white", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKWHITE));
    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_RED_BLACK = register("vampire_cloak_red_black", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.REDBLACK));
    public static final RegistryObject<ColoredVampireClothingItem> VAMPIRE_CLOAK_WHITE_BLACK = register("vampire_cloak_white_black", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.WHITEBLACK));

    public static final RegistryObject<VampireBloodBottleItem> VAMPIRE_BLOOD_BOTTLE = register("vampire_blood_bottle", VampireBloodBottleItem::new);
    public static final RegistryObject<VampireBookItem> VAMPIRE_BOOK = register("vampire_book", VampireBookItem::new);
    public static final RegistryObject<VampireFangItem> VAMPIRE_FANG = register("vampire_fang", VampireFangItem::new);
    public static final RegistryObject<VampirismItemBloodFoodItem> WEAK_HUMAN_HEART = register("weak_human_heart", () -> new VampirismItemBloodFoodItem((new FoodProperties.Builder()).nutrition(10).saturationMod(0.9F).build(), new FoodProperties.Builder().nutrition(3).saturationMod(1f).build()));

    public static final RegistryObject<SpawnEggItem> VAMPIRE_SPAWN_EGG = register("vampire_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new ForgeSpawnEggItem(ModEntities.VAMPIRE, 0x8B15A3, 0xa735e3, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> VAMPIRE_HUNTER_SPAWN_EGG = register("vampire_hunter_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new ForgeSpawnEggItem(ModEntities.HUNTER, 0x2d05f2, 0x2600e0, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> ADVANCED_VAMPIRE_SPAWN_EGG = register("advanced_vampire_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new ForgeSpawnEggItem(ModEntities.ADVANCED_VAMPIRE, 0x8B15A3, 0x560a7e, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG = register("advanced_vampire_hunter_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new ForgeSpawnEggItem(ModEntities.ADVANCED_HUNTER, 0x2d05f2, 0x1a028c, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> VAMPIRE_BARON_SPAWN_EGG = register("vampire_baron_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new ForgeSpawnEggItem(ModEntities.VAMPIRE_BARON, 0x8B15A3, 0x15acda, new Item.Properties()));
    public static final RegistryObject<SpawnEggItem> HUNTER_TRAINER_SPAWN_EGG = register("hunter_trainer_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new ForgeSpawnEggItem(ModEntities.HUNTER_TRAINER, 0x2d05f2, 0x1cdb49, new Item.Properties()));

    public static final RegistryObject<UmbrellaItem> UMBRELLA = register("umbrella", UmbrellaItem::new);

    public static final RegistryObject<Item> HUNTER_MINION_EQUIPMENT = register("hunter_minion_equipment", () -> new Item(props()));
    public static final RegistryObject<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SIMPLE = register("hunter_minion_upgrade_simple", () -> new MinionUpgradeItem(1, 2, VReference.HUNTER_FACTION));
    public static final RegistryObject<MinionUpgradeItem> HUNTER_MINION_UPGRADE_ENHANCED = register("hunter_minion_upgrade_enhanced", () -> new MinionUpgradeItem(3, 4, VReference.HUNTER_FACTION));
    public static final RegistryObject<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SPECIAL = register("hunter_minion_upgrade_special", () -> new MinionUpgradeItem(5, 6, VReference.HUNTER_FACTION));
    public static final RegistryObject<FeedingAdapterItem> FEEDING_ADAPTER = register("feeding_adapter", FeedingAdapterItem::new);
    public static final RegistryObject<Item> VAMPIRE_MINION_BINDING = register("vampire_minion_binding", () -> new Item(props()));
    public static final RegistryObject<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SIMPLE = register("vampire_minion_upgrade_simple", () -> new MinionUpgradeItem(1, 2, VReference.VAMPIRE_FACTION));
    public static final RegistryObject<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_ENHANCED = register("vampire_minion_upgrade_enhanced", () -> new MinionUpgradeItem(3, 4, VReference.VAMPIRE_FACTION));
    public static final RegistryObject<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SPECIAL = register("vampire_minion_upgrade_special", () -> new MinionUpgradeItem(5, 6, VReference.VAMPIRE_FACTION));

    public static final RegistryObject<OblivionItem> OBLIVION_POTION = register("oblivion_potion", () -> new OblivionItem(props()));

    public static final RegistryObject<RefinementItem> AMULET = register("amulet", () -> new VampireRefinementItem(props(), IRefinementItem.AccessorySlotType.AMULET));
    public static final RegistryObject<RefinementItem> RING = register("ring", () -> new VampireRefinementItem(props(), IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<RefinementItem> OBI_BELT = register("obi_belt", () -> new VampireRefinementItem(props(), IRefinementItem.AccessorySlotType.OBI_BELT));

    public static final RegistryObject<VampireClothingItem> VAMPIRE_CLOTHING_CROWN = register("vampire_clothing_crown", () -> new VampireClothingItem(ArmorItem.Type.HELMET));
    public static final RegistryObject<VampireClothingItem> VAMPIRE_CLOTHING_LEGS = register("vampire_clothing_legs", () -> new VampireClothingItem(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<VampireClothingItem> VAMPIRE_CLOTHING_BOOTS = register("vampire_clothing_boots", () -> new VampireClothingItem(ArmorItem.Type.BOOTS));
    public static final RegistryObject<VampireClothingItem> VAMPIRE_CLOTHING_HAT = register("vampire_clothing_hat", () -> new VampireClothingItem(ArmorItem.Type.HELMET));

    public static final RegistryObject<Item> GARLIC_FINDER = register("garlic_finder", () -> new Item(props().rarity(Rarity.RARE)));

    public static final RegistryObject<StandingAndWallBlockItem> ITEM_CANDELABRA = register("item_candelabra", () -> new StandingAndWallBlockItem(ModBlocks.CANDELABRA.get(), ModBlocks.CANDELABRA_WALL.get(), new Item.Properties(), Direction.DOWN));
    public static final RegistryObject<SignItem> DARK_SPRUCE_SIGN = register("dark_spruce_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ModBlocks.DARK_SPRUCE_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_SIGN.get()));
    public static final RegistryObject<SignItem> CURSED_SPRUCE_SIGN = register("cursed_spruce_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ModBlocks.CURSED_SPRUCE_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_SIGN.get()));

    public static final RegistryObject<CrucifixItem> CRUCIFIX_NORMAL = register("crucifix_normal", () -> new CrucifixItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<CrucifixItem> CRUCIFIX_ENHANCED = register("crucifix_enhanced", () -> new CrucifixItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<CrucifixItem> CRUCIFIX_ULTIMATE = register("crucifix_ultimate", () -> new CrucifixItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<VampirismBoatItem> DARK_SPRUCE_BOAT = register("dark_spruce_boat", () -> new VampirismBoatItem(IVampirismBoat.BoatType.DARK_SPRUCE, false, props().stacksTo(1)));
    public static final RegistryObject<VampirismBoatItem> CURSED_SPRUCE_BOAT = register("cursed_spruce_boat", () -> new VampirismBoatItem(IVampirismBoat.BoatType.CURSED_SPRUCE, false, props().stacksTo(1)));
    public static final RegistryObject<VampirismBoatItem> DARK_SPRUCE_CHEST_BOAT = register("dark_spruce_chest_boat", () -> new VampirismBoatItem(IVampirismBoat.BoatType.DARK_SPRUCE, true, props().stacksTo(1)));
    public static final RegistryObject<VampirismBoatItem> CURSED_SPRUCE_CHEST_BOAT = register("cursed_spruce_chest_boat", () -> new VampirismBoatItem(IVampirismBoat.BoatType.CURSED_SPRUCE, true, props().stacksTo(1)));

    public static final RegistryObject<OilBottleItem> OIL_BOTTLE = register("oil_bottle", () -> new OilBottleItem(props().stacksTo(1)));

    static void registerCraftingRecipes() {
        // Brewing
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), Ingredient.of(new ItemStack(PURE_SALT.get())), new ItemStack(PURE_SALT_WATER.get()));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(HOLY_WATER_BOTTLE_NORMAL.get()), Ingredient.of(Items.GUNPOWDER), new ItemStack(HOLY_WATER_SPLASH_BOTTLE_NORMAL.get())) {
            @Override
            public boolean isInput(@NotNull ItemStack stack) {

                return HOLY_WATER_BOTTLE_NORMAL.get().equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(HOLY_WATER_BOTTLE_ENHANCED.get()), Ingredient.of(Items.GUNPOWDER), new ItemStack(HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get())) {
            @Override
            public boolean isInput(@NotNull ItemStack stack) {

                return HOLY_WATER_BOTTLE_ENHANCED.get().equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(HOLY_WATER_BOTTLE_ULTIMATE.get()), Ingredient.of(Items.GUNPOWDER), new ItemStack(HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get())) {
            @Override
            public boolean isInput(@NotNull ItemStack stack) {
                return HOLY_WATER_BOTTLE_ULTIMATE.get().equals(stack.getItem());
            }
        });
    }

    static <I extends Item> RegistryObject<I> register(final String name, ResourceKey<CreativeModeTab> tab, final Supplier<? extends I> sup) {
        RegistryObject<I> item = ITEMS.register(name, sup);
        if (tab == VAMPIRISM_TAB_KEY) {
            VAMPIRISM_TAB_ITEMS.add(item);
        } else {
            CREATIVE_TAB_ITEMS.computeIfAbsent(tab, (a) -> new HashSet<>()).add(item);
        }
        return item;
    }

    static <I extends Item> RegistryObject<I> register(final String name, final Supplier<? extends I> sup) {
        return register(name, VAMPIRISM_TAB_KEY, sup);
    }


    public static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
        ITEMS.register(bus);
        if (VampirismMod.inDataGen) {
            DeferredRegister<Item> DUMMY_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "guideapi_vp");
            RegistryObject<DummyItem> dummy_item = DUMMY_ITEMS.register("vampirism-guidebook", DummyItem::new);
            DUMMY_ITEMS.register(bus);
            VAMPIRISM_TAB_ITEMS.add(dummy_item);
        }
    }

    private static Item.@NotNull Properties props() {
        return new Item.Properties();
    }

    @ApiStatus.Internal
    public static void registerOtherCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        CREATIVE_TAB_ITEMS.forEach((tab, items) -> {
            if (event.getTabKey() == tab) {
                items.forEach(item -> event.accept(item.get()));
            }
        });
    }

    public static void fixMappings(@NotNull MissingMappingsEvent event) {
        event.getAllMappings(ForgeRegistries.Keys.ITEMS).forEach(missingMapping -> {
            switch (missingMapping.getKey().toString()) {
                case "vampirism:blood_potion", "vampirism:blood_potion_table" -> missingMapping.ignore();
                case "vampirism:vampire_clothing_head" -> missingMapping.remap(VAMPIRE_CLOTHING_CROWN.get());
                case "vampirism:vampire_clothing_feet" -> missingMapping.remap(VAMPIRE_CLOTHING_BOOTS.get());
                case "vampirism:garlic_beacon_core" -> missingMapping.remap(GARLIC_DIFFUSER_CORE.get());
                case "vampirism:garlic_beacon_core_improved" -> missingMapping.remap(GARLIC_DIFFUSER_CORE_IMPROVED.get());
                case "vampirism:garlic_beacon_normal" -> missingMapping.remap(ModBlocks.GARLIC_DIFFUSER_NORMAL.get().asItem());
                case "vampirism:garlic_beacon_weak" -> missingMapping.remap(ModBlocks.GARLIC_DIFFUSER_WEAK.get().asItem());
                case "vampirism:garlic_beacon_improved" -> missingMapping.remap(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get().asItem());
                case "vampirism:church_altar" -> missingMapping.remap(ModBlocks.ALTAR_CLEANSING.get().asItem());
                case "vampirism:item_med_chair" -> missingMapping.remap(ModBlocks.MED_CHAIR.get().asItem());
                case "vampirism:bloody_spruce_log" -> missingMapping.remap(ModBlocks.CURSED_SPRUCE_LOG.get().asItem());
                case "vampirism:bloody_spruce_leaves" -> missingMapping.remap(ModBlocks.DARK_SPRUCE_LEAVES.get().asItem());
                case "vampirism:coffin" -> missingMapping.remap(ModBlocks.COFFIN_RED.get().asItem());
                case "vampirism:holy_salt_water" -> missingMapping.remap(PURE_SALT_WATER.get());
                case "vampirism:holy_salt" -> missingMapping.remap(PURE_SALT.get());
                case "vampirism:injection_zombie_blood" -> missingMapping.remap(Items.APPLE);
                case "vampirism:cure_apple" -> missingMapping.remap(Items.GOLDEN_APPLE);
            }
            if (missingMapping.getKey().toString().startsWith("vampirism:obsidian_armor")) {
                Item hunterArmorReplacement = (Item) event.getRegistry().getValue(new ResourceLocation(missingMapping.getKey().toString().replace("obsidian_armor", "hunter_coat")));
                if (hunterArmorReplacement != null) {
                    missingMapping.remap(hunterArmorReplacement);
                } else {
                    LogManager.getLogger().warn("Could not find hunter armor replacement for {}", missingMapping.getKey().toString());
                    missingMapping.ignore();
                }
            }
        });
    }
}
