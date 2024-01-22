package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.entity.IVampirismBoat;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.items.crossbow.ArrowContainer;
import de.teamlapen.vampirism.items.crossbow.DoubleCrossbowItem;
import de.teamlapen.vampirism.items.crossbow.SingleCrossbowItem;
import de.teamlapen.vampirism.items.crossbow.TechCrossbowItem;
import de.teamlapen.vampirism.misc.VampirismCreativeTab;
import de.teamlapen.vampirism.misc.VampirismDispenseBoatBehavior;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
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
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(REFERENCE.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, REFERENCE.MODID);

    private static final Set<DeferredHolder<Item, ? extends Item>> VAMPIRISM_TAB_ITEMS = new HashSet<>();
    private static final Map<ResourceKey<CreativeModeTab>, Set<DeferredHolder<Item, ? extends Item>>> CREATIVE_TAB_ITEMS = new HashMap<>();

    public static final ResourceKey<CreativeModeTab> VAMPIRISM_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(REFERENCE.MODID, "default"));
    public static final DeferredHolder<CreativeModeTab,CreativeModeTab> VAMPIRISM_TAB = CREATIVE_TABS.register(VAMPIRISM_TAB_KEY.location().getPath(), () -> VampirismCreativeTab.builder(VAMPIRISM_TAB_ITEMS.stream().map(DeferredHolder::get).collect(Collectors.toSet())).build());
    public static final EffectCure GARLIC_CURE = EffectCure.get("vampirism:garlic");

    //Items
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_NORMAL = register("armor_of_swiftness_chest_normal", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.CHESTPLATE, ArmorOfSwiftnessItem.NORMAL));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ENHANCED = register("armor_of_swiftness_chest_enhanced", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.CHESTPLATE, ArmorOfSwiftnessItem.ENHANCED));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE = register("armor_of_swiftness_chest_ultimate", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.CHESTPLATE, ArmorOfSwiftnessItem.ULTIMATE));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_NORMAL = register("armor_of_swiftness_feet_normal", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.BOOTS, ArmorOfSwiftnessItem.NORMAL));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ENHANCED = register("armor_of_swiftness_feet_enhanced", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.BOOTS, ArmorOfSwiftnessItem.ENHANCED));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ULTIMATE = register("armor_of_swiftness_feet_ultimate", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.BOOTS, ArmorOfSwiftnessItem.ULTIMATE));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_NORMAL = register("armor_of_swiftness_head_normal", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.HELMET, ArmorOfSwiftnessItem.NORMAL));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ENHANCED = register("armor_of_swiftness_head_enhanced", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.HELMET, ArmorOfSwiftnessItem.ENHANCED));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE = register("armor_of_swiftness_head_ultimate", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.HELMET, ArmorOfSwiftnessItem.ULTIMATE));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_NORMAL = register("armor_of_swiftness_legs_normal", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.LEGGINGS, ArmorOfSwiftnessItem.NORMAL));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ENHANCED = register("armor_of_swiftness_legs_enhanced", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.LEGGINGS, ArmorOfSwiftnessItem.ENHANCED));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE = register("armor_of_swiftness_legs_ultimate", () -> new ArmorOfSwiftnessItem(ArmorItem.Type.LEGGINGS, ArmorOfSwiftnessItem.ULTIMATE));

    public static final DeferredItem<SingleCrossbowItem> BASIC_CROSSBOW = register("basic_crossbow", () -> new SingleCrossbowItem(props().defaultDurability(465), 1, 20, Tiers.WOOD, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<DoubleCrossbowItem> BASIC_DOUBLE_CROSSBOW = register("basic_double_crossbow", () -> new DoubleCrossbowItem(props().durability(465), 1, 20, Tiers.WOOD, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<TechCrossbowItem> BASIC_TECH_CROSSBOW = register("basic_tech_crossbow", () -> new TechCrossbowItem(props().durability(930), 1.6F, 40, Tiers.DIAMOND, HunterSkills.WEAPON_TABLE));

    public static final DeferredItem<BloodBottleItem> BLOOD_BOTTLE = ITEMS.register("blood_bottle", BloodBottleItem::new);
    public static final DeferredItem<BucketItem> BLOOD_BUCKET = register("blood_bucket", CreativeModeTabs.TOOLS_AND_UTILITIES, () -> new BucketItem(ModFluids.BLOOD, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> BLOOD_INFUSED_IRON_INGOT = register("blood_infused_iron_ingot", () -> new Item(props()));
    public static final DeferredItem<Item> BLOOD_INFUSED_ENHANCED_IRON_INGOT = register("blood_infused_enhanced_iron_ingot", () -> new Item(props()));

    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_NORMAL = register("crossbow_arrow_normal", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.NORMAL));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_SPITFIRE = register("crossbow_arrow_spitfire", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.SPITFIRE));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_VAMPIRE_KILLER = register("crossbow_arrow_vampire_killer", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.VAMPIRE_KILLER));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_TELEPORT = register("crossbow_arrow_teleport", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.TELEPORT));

    public static final DeferredItem<SingleCrossbowItem> ENHANCED_CROSSBOW = register("enhanced_crossbow", () -> new SingleCrossbowItem(props().durability(930), 1.5F, 15, Tiers.IRON, HunterSkills.MASTER_CRAFTSMANSHIP));
    public static final DeferredItem<DoubleCrossbowItem> ENHANCED_DOUBLE_CROSSBOW = register("enhanced_double_crossbow", () -> new DoubleCrossbowItem(props().durability(930),1.5F, 15, Tiers.IRON, HunterSkills.MASTER_CRAFTSMANSHIP));
    public static final DeferredItem<TechCrossbowItem> ENHANCED_TECH_CROSSBOW = register("enhanced_tech_crossbow", () -> new TechCrossbowItem(props().durability(1860), 1.7F, 30, Tiers.DIAMOND, HunterSkills.MASTER_CRAFTSMANSHIP));

    public static final DeferredItem<Item> GARLIC_DIFFUSER_CORE = register("garlic_diffuser_core", () -> new Item(props()));
    public static final DeferredItem<Item> GARLIC_DIFFUSER_CORE_IMPROVED = register("garlic_diffuser_core_improved", () -> new Item(props()));

    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_NORMAL = register("heart_seeker_normal", () -> new HeartSeekerItem(HeartSeekerItem.NORMAL));
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_ENHANCED = register("heart_seeker_enhanced", () -> new HeartSeekerItem(HeartSeekerItem.ENHANCED));
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_ULTIMATE = register("heart_seeker_ultimate", () -> new HeartSeekerItem(HeartSeekerItem.ULTIMATE));

    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_NORMAL = register("heart_striker_normal", () -> new HeartStrikerItem(HeartStrikerItem.NORMAL));
    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_ENHANCED = register("heart_striker_enhanced", () -> new HeartStrikerItem(HeartStrikerItem.ENHANCED));
    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_ULTIMATE = register("heart_striker_ultimate", () -> new HeartStrikerItem(HeartStrikerItem.ULTIMATE));

    public static final DeferredItem<HolyWaterBottleItem> HOLY_WATER_BOTTLE_NORMAL = register("holy_water_bottle_normal", () -> new HolyWaterBottleItem(IItemWithTier.TIER.NORMAL));
    public static final DeferredItem<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ENHANCED = register("holy_water_bottle_enhanced", () -> new HolyWaterBottleItem(IItemWithTier.TIER.ENHANCED));
    public static final DeferredItem<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ULTIMATE = register("holy_water_bottle_ultimate", () -> new HolyWaterBottleItem(IItemWithTier.TIER.ULTIMATE));
    public static final DeferredItem<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_NORMAL = register("holy_water_splash_bottle_normal", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.NORMAL));
    public static final DeferredItem<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ENHANCED = register("holy_water_splash_bottle_enhanced", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ENHANCED));
    public static final DeferredItem<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ULTIMATE = register("holy_water_splash_bottle_ultimate", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ULTIMATE));

    public static final DeferredItem<BlessableItem> PURE_SALT_WATER = register("pure_salt_water", () -> new BlessableItem(new Item.Properties().stacksTo(1), HOLY_WATER_BOTTLE_NORMAL::get, HOLY_WATER_BOTTLE_ENHANCED::get) {
        @Override
        public boolean isFoil(ItemStack stack) {
            return true;
        }
    });

    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_NORMAL = register("hunter_axe_normal", () -> new HunterAxeItem(HunterAxeItem.NORMAL));
    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_ENHANCED = register("hunter_axe_enhanced", () -> new HunterAxeItem(HunterAxeItem.ENHANCED));
    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_ULTIMATE = register("hunter_axe_ultimate", () -> new HunterAxeItem(HunterAxeItem.ULTIMATE));

    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_NORMAL = register("hunter_coat_chest_normal", () -> new HunterCoatItem(ArmorItem.Type.CHESTPLATE, HunterCoatItem.NORMAL));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_ENHANCED = register("hunter_coat_chest_enhanced", () -> new HunterCoatItem(ArmorItem.Type.CHESTPLATE, HunterCoatItem.ENHANCED));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_ULTIMATE = register("hunter_coat_chest_ultimate", () -> new HunterCoatItem(ArmorItem.Type.CHESTPLATE, HunterCoatItem.ULTIMATE));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_NORMAL = register("hunter_coat_feet_normal", () -> new HunterCoatItem(ArmorItem.Type.BOOTS, HunterCoatItem.NORMAL));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_ENHANCED = register("hunter_coat_feet_enhanced", () -> new HunterCoatItem(ArmorItem.Type.BOOTS, HunterCoatItem.ENHANCED));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_ULTIMATE = register("hunter_coat_feet_ultimate", () -> new HunterCoatItem(ArmorItem.Type.BOOTS, HunterCoatItem.ULTIMATE));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_NORMAL = register("hunter_coat_head_normal", () -> new HunterCoatItem(ArmorItem.Type.HELMET, HunterCoatItem.NORMAL));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_ENHANCED = register("hunter_coat_head_enhanced", () -> new HunterCoatItem(ArmorItem.Type.HELMET, HunterCoatItem.ENHANCED));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_ULTIMATE = register("hunter_coat_head_ultimate", () -> new HunterCoatItem(ArmorItem.Type.HELMET, HunterCoatItem.ULTIMATE));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_NORMAL = register("hunter_coat_legs_normal", () -> new HunterCoatItem(ArmorItem.Type.LEGGINGS, HunterCoatItem.NORMAL));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_ENHANCED = register("hunter_coat_legs_enhanced", () -> new HunterCoatItem(ArmorItem.Type.LEGGINGS, HunterCoatItem.ENHANCED));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_ULTIMATE = register("hunter_coat_legs_ultimate", () -> new HunterCoatItem(ArmorItem.Type.LEGGINGS, HunterCoatItem.ULTIMATE));

    public static final DeferredItem<HunterHatItem> HUNTER_HAT_HEAD_0 = register("hunter_hat_head_0", () -> new HunterHatItem(HunterHatItem.HatType.TYPE_1));
    public static final DeferredItem<HunterHatItem> HUNTER_HAT_HEAD_1 = register("hunter_hat_head_1", () -> new HunterHatItem(HunterHatItem.HatType.TYPE_2));

    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_0 = register("hunter_intel_0", () -> new HunterIntelItem(0));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_1 = register("hunter_intel_1", () -> new HunterIntelItem(1));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_2 = register("hunter_intel_2", () -> new HunterIntelItem(2));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_3 = register("hunter_intel_3", () -> new HunterIntelItem(3));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_4 = register("hunter_intel_4", () -> new HunterIntelItem(4));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_5 = register("hunter_intel_5", () -> new HunterIntelItem(5));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_6 = register("hunter_intel_6", () -> new HunterIntelItem(6));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_7 = register("hunter_intel_7", () -> new HunterIntelItem(7));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_8 = register("hunter_intel_8", () -> new HunterIntelItem(8));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_9 = register("hunter_intel_9", () -> new HunterIntelItem(9));

    public static final DeferredItem<VampirismItemBloodFoodItem> HUMAN_HEART = register("human_heart", () -> new VampirismItemBloodFoodItem((new FoodProperties.Builder()).nutrition(20).saturationMod(1.5F).build(), new FoodProperties.Builder().nutrition(5).saturationMod(1f).build()));

    public static final DeferredItem<InjectionItem> INJECTION_EMPTY = register("injection_empty", () -> new InjectionItem(InjectionItem.TYPE.EMPTY));
    public static final DeferredItem<InjectionItem> INJECTION_GARLIC = register("injection_garlic", () -> new InjectionItem(InjectionItem.TYPE.GARLIC));
    public static final DeferredItem<InjectionItem> INJECTION_SANGUINARE = register("injection_sanguinare", () -> new InjectionItem(InjectionItem.TYPE.SANGUINARE));

    public static final DeferredItem<BucketItem> IMPURE_BLOOD_BUCKET = register("impure_blood_bucket", CreativeModeTabs.TOOLS_AND_UTILITIES, () -> new BucketItem(ModFluids.IMPURE_BLOOD, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<GarlicItem> ITEM_GARLIC = register("item_garlic", GarlicItem::new);
    public static final DeferredItem<GarlicBreadItem> GARLIC_BREAD = register("garlic_bread", GarlicBreadItem::new);
    public static final DeferredItem<AlchemicalFireItem> ITEM_ALCHEMICAL_FIRE = register("item_alchemical_fire", AlchemicalFireItem::new);

    public static final DeferredItem<TentItem> ITEM_TENT = register("item_tent", () -> new TentItem(false));
    public static final DeferredItem<TentItem> ITEM_TENT_SPAWNER = register("item_tent_spawner", () -> new TentItem(true));

    public static final DeferredItem<PitchforkItem> PITCHFORK = register("pitchfork", PitchforkItem::new);

    public static final DeferredItem<PureBloodItem> PURE_BLOOD_0 = register("pure_blood_0", () -> new PureBloodItem(0));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_1 = register("pure_blood_1", () -> new PureBloodItem(1));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_2 = register("pure_blood_2", () -> new PureBloodItem(2));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_3 = register("pure_blood_3", () -> new PureBloodItem(3));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_4 = register("pure_blood_4", () -> new PureBloodItem(4));

    public static final DeferredItem<Item> PURIFIED_GARLIC = register("purified_garlic", () -> new Item(props().stacksTo(16)));
    public static final DeferredItem<Item> PURE_SALT = register("pure_salt", () -> new Item(props()));
    public static final DeferredItem<Item> SOUL_ORB_VAMPIRE = register("soul_orb_vampire", () -> new Item(props()));

    public static final DeferredItem<StakeItem> STAKE = register("stake", StakeItem::new);
    public static final DeferredItem<ArrowContainer> ARROW_CLIP = register("tech_crossbow_ammo_package", () -> new ArrowContainer(props().stacksTo(1), 12, (stack) -> stack.is(CROSSBOW_ARROW_NORMAL.get())) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> texts, @NotNull TooltipFlag flag) {
            texts.add(Component.translatable("item.vampirism.tech_crossbow_ammo_package.tooltip", Component.translatable(BASIC_TECH_CROSSBOW.get().getDescriptionId())).withStyle(ChatFormatting.GRAY));
            texts.add(Component.empty());
            super.appendHoverText(stack, level, texts, flag);
        }

    });

    public static final DeferredItem<ColoredVampireClothingItem> VAMPIRE_CLOAK_BLACK_BLUE = register("vampire_cloak_black_blue", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKBLUE));
    public static final DeferredItem<ColoredVampireClothingItem> VAMPIRE_CLOAK_BLACK_RED = register("vampire_cloak_black_red", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKRED));
    public static final DeferredItem<ColoredVampireClothingItem> VAMPIRE_CLOAK_BLACK_WHITE = register("vampire_cloak_black_white", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKWHITE));
    public static final DeferredItem<ColoredVampireClothingItem> VAMPIRE_CLOAK_RED_BLACK = register("vampire_cloak_red_black", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.REDBLACK));
    public static final DeferredItem<ColoredVampireClothingItem> VAMPIRE_CLOAK_WHITE_BLACK = register("vampire_cloak_white_black", () -> new ColoredVampireClothingItem(ArmorItem.Type.CHESTPLATE, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.WHITEBLACK));

    public static final DeferredItem<VampireBloodBottleItem> VAMPIRE_BLOOD_BOTTLE = register("vampire_blood_bottle", VampireBloodBottleItem::new);
    public static final DeferredItem<VampireBookItem> VAMPIRE_BOOK = register("vampire_book", VampireBookItem::new);
    public static final DeferredItem<VampireFangItem> VAMPIRE_FANG = register("vampire_fang", VampireFangItem::new);
    public static final DeferredItem<VampirismItemBloodFoodItem> WEAK_HUMAN_HEART = register("weak_human_heart", () -> new VampirismItemBloodFoodItem((new FoodProperties.Builder()).nutrition(10).saturationMod(0.9F).build(), new FoodProperties.Builder().nutrition(3).saturationMod(1f).build()));

    public static final DeferredItem<SpawnEggItem> VAMPIRE_SPAWN_EGG = register("vampire_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new DeferredSpawnEggItem(ModEntities.VAMPIRE, 0x8B15A3, 0xa735e3, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> VAMPIRE_HUNTER_SPAWN_EGG = register("vampire_hunter_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new DeferredSpawnEggItem(ModEntities.HUNTER, 0x2d05f2, 0x2600e0, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> ADVANCED_VAMPIRE_SPAWN_EGG = register("advanced_vampire_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new DeferredSpawnEggItem(ModEntities.ADVANCED_VAMPIRE, 0x8B15A3, 0x560a7e, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG = register("advanced_vampire_hunter_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new DeferredSpawnEggItem(ModEntities.ADVANCED_HUNTER, 0x2d05f2, 0x1a028c, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> VAMPIRE_BARON_SPAWN_EGG = register("vampire_baron_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new DeferredSpawnEggItem(ModEntities.VAMPIRE_BARON, 0x8B15A3, 0x15acda, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> HUNTER_TRAINER_SPAWN_EGG = register("hunter_trainer_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new DeferredSpawnEggItem(ModEntities.HUNTER_TRAINER, 0x2d05f2, 0x1cdb49, new Item.Properties()));

    public static final DeferredItem<UmbrellaItem> UMBRELLA = register("umbrella", UmbrellaItem::new);

    public static final DeferredItem<Item> HUNTER_MINION_EQUIPMENT = register("hunter_minion_equipment", () -> new Item(props()));
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SIMPLE = register("hunter_minion_upgrade_simple", () -> new MinionUpgradeItem(1, 2, VReference.HUNTER_FACTION));
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_ENHANCED = register("hunter_minion_upgrade_enhanced", () -> new MinionUpgradeItem(3, 4, VReference.HUNTER_FACTION));
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SPECIAL = register("hunter_minion_upgrade_special", () -> new MinionUpgradeItem(5, 6, VReference.HUNTER_FACTION));
    public static final DeferredItem<FeedingAdapterItem> FEEDING_ADAPTER = register("feeding_adapter", FeedingAdapterItem::new);
    public static final DeferredItem<Item> VAMPIRE_MINION_BINDING = register("vampire_minion_binding", () -> new Item(props()));
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SIMPLE = register("vampire_minion_upgrade_simple", () -> new MinionUpgradeItem(1, 2, VReference.VAMPIRE_FACTION));
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_ENHANCED = register("vampire_minion_upgrade_enhanced", () -> new MinionUpgradeItem(3, 4, VReference.VAMPIRE_FACTION));
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SPECIAL = register("vampire_minion_upgrade_special", () -> new MinionUpgradeItem(5, 6, VReference.VAMPIRE_FACTION));

    public static final DeferredItem<OblivionItem> OBLIVION_POTION = register("oblivion_potion", () -> new OblivionItem(props()));

    public static final DeferredItem<RefinementItem> AMULET = register("amulet", () -> new VampireRefinementItem(props(), IRefinementItem.AccessorySlotType.AMULET));
    public static final DeferredItem<RefinementItem> RING = register("ring", () -> new VampireRefinementItem(props(), IRefinementItem.AccessorySlotType.RING));
    public static final DeferredItem<RefinementItem> OBI_BELT = register("obi_belt", () -> new VampireRefinementItem(props(), IRefinementItem.AccessorySlotType.OBI_BELT));

    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_CROWN = register("vampire_clothing_crown", () -> new VampireClothingItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_LEGS = register("vampire_clothing_legs", () -> new VampireClothingItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_BOOTS = register("vampire_clothing_boots", () -> new VampireClothingItem(ArmorItem.Type.BOOTS));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_HAT = register("vampire_clothing_hat", () -> new VampireClothingItem(ArmorItem.Type.HELMET));

    public static final DeferredItem<Item> GARLIC_FINDER = register("garlic_finder", () -> new Item(props().rarity(Rarity.RARE)));

    public static final DeferredItem<StandingAndWallBlockItem> ITEM_CANDELABRA = register("item_candelabra", () -> new StandingAndWallBlockItem(ModBlocks.CANDELABRA.get(), ModBlocks.CANDELABRA_WALL.get(), new Item.Properties(), Direction.DOWN));
    public static final DeferredItem<SignItem> DARK_SPRUCE_SIGN = register("dark_spruce_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ModBlocks.DARK_SPRUCE_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_SIGN.get()));
    public static final DeferredItem<SignItem> CURSED_SPRUCE_SIGN = register("cursed_spruce_sign", () -> new SignItem((new Item.Properties()).stacksTo(16), ModBlocks.CURSED_SPRUCE_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_SIGN.get()));

    public static final DeferredItem<CrucifixItem> CRUCIFIX_NORMAL = register("crucifix_normal", () -> new CrucifixItem(IItemWithTier.TIER.NORMAL));
    public static final DeferredItem<CrucifixItem> CRUCIFIX_ENHANCED = register("crucifix_enhanced", () -> new CrucifixItem(IItemWithTier.TIER.ENHANCED));
    public static final DeferredItem<CrucifixItem> CRUCIFIX_ULTIMATE = register("crucifix_ultimate", () -> new CrucifixItem(IItemWithTier.TIER.ULTIMATE));

    public static final DeferredItem<VampirismBoatItem> DARK_SPRUCE_BOAT = register("dark_spruce_boat", () -> new VampirismBoatItem(IVampirismBoat.BoatType.DARK_SPRUCE, false, props().stacksTo(1)));
    public static final DeferredItem<VampirismBoatItem> CURSED_SPRUCE_BOAT = register("cursed_spruce_boat", () -> new VampirismBoatItem(IVampirismBoat.BoatType.CURSED_SPRUCE, false, props().stacksTo(1)));
    public static final DeferredItem<VampirismBoatItem> DARK_SPRUCE_CHEST_BOAT = register("dark_spruce_chest_boat", () -> new VampirismBoatItem(IVampirismBoat.BoatType.DARK_SPRUCE, true, props().stacksTo(1)));
    public static final DeferredItem<VampirismBoatItem> CURSED_SPRUCE_CHEST_BOAT = register("cursed_spruce_chest_boat", () -> new VampirismBoatItem(IVampirismBoat.BoatType.CURSED_SPRUCE, true, props().stacksTo(1)));

    public static final DeferredItem<OilBottleItem> OIL_BOTTLE = register("oil_bottle", () -> new OilBottleItem(props().stacksTo(1)));
    public static final DeferredItem<HangingSignItem> DARK_SPRUCE_HANGING_SIGN = register("dark_spruce_hanging_sign", () -> new HangingSignItem(ModBlocks.DARK_SPRUCE_HANGING_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));
    public static final DeferredItem<HangingSignItem> CURSED_SPRUCE_HANGING_SIGN = register("cursed_spruce_hanging_sign", () -> new HangingSignItem(ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> MOTHER_CORE = register("mother_core", () -> new Item(props().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK = register("candle_stick", () -> new StandingAndWallBlockItem(ModBlocks.CANDLE_STICK.get(), ModBlocks.WALL_CANDLE_STICK.get(), new Item.Properties(), Direction.DOWN));

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

    static <I extends Item> DeferredItem<I> register(final String name, ResourceKey<CreativeModeTab> tab, final Supplier<? extends I> sup) {
        DeferredItem<I> item = ITEMS.register(name, sup);
        if (tab == VAMPIRISM_TAB_KEY) {
            VAMPIRISM_TAB_ITEMS.add(item);
        } else {
            CREATIVE_TAB_ITEMS.computeIfAbsent(tab, (a) -> new HashSet<>()).add(item);
        }
        return item;
    }

    static <I extends Item> DeferredItem<I> register(final String name, final Supplier<? extends I> sup) {
        return register(name, VAMPIRISM_TAB_KEY, sup);
    }


    public static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
        ITEMS.register(bus);
        if (VampirismMod.inDataGen) {
            DeferredRegister.Items DUMMY_ITEMS = DeferredRegister.createItems("guideapi_vp");
            DeferredItem<DummyItem> dummy_item = DUMMY_ITEMS.register("vampirism-guidebook", DummyItem::new);
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

    public static void registerDispenserBehaviourUnsafe() {
        DispenserBlock.registerBehavior(ModItems.DARK_SPRUCE_BOAT.get(), new VampirismDispenseBoatBehavior(IVampirismBoat.BoatType.DARK_SPRUCE));
        DispenserBlock.registerBehavior(ModItems.CURSED_SPRUCE_BOAT.get(), new VampirismDispenseBoatBehavior(IVampirismBoat.BoatType.CURSED_SPRUCE));
        AbstractProjectileDispenseBehavior crossbowArrowBehaviour = new AbstractProjectileDispenseBehavior() {
            protected @NotNull Projectile getProjectile(@NotNull Level level, @NotNull Position position, @NotNull ItemStack stack) {
                return ((CrossbowArrowItem) stack.getItem()).createArrow(level, stack, position);
            }
        };
        DispenserBlock.registerBehavior(ModItems.CROSSBOW_ARROW_NORMAL.get(), crossbowArrowBehaviour);
        DispenserBlock.registerBehavior(ModItems.CROSSBOW_ARROW_SPITFIRE.get(), crossbowArrowBehaviour);
        DispenserBlock.registerBehavior(ModItems.CROSSBOW_ARROW_TELEPORT.get(), crossbowArrowBehaviour);
        DispenserBlock.registerBehavior(ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), crossbowArrowBehaviour);
    }
}
