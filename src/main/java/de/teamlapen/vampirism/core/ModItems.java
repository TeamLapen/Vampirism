package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.items.*;
import de.teamlapen.vampirism.items.component.FactionRestriction;
import de.teamlapen.vampirism.items.consume.*;
import de.teamlapen.vampirism.items.crossbow.ArrowContainer;
import de.teamlapen.vampirism.items.crossbow.DoubleCrossbowItem;
import de.teamlapen.vampirism.items.crossbow.SingleCrossbowItem;
import de.teamlapen.vampirism.items.crossbow.TechCrossbowItem;
import de.teamlapen.vampirism.items.crossbow.arrow.*;
import de.teamlapen.vampirism.misc.VampirismCreativeTab;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles all item registrations and reference.
 */
@SuppressWarnings("unused")
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(REFERENCE.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, REFERENCE.MODID);
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECTS = DeferredRegister.create(Registries.CONSUME_EFFECT_TYPE, REFERENCE.MODID);

    private static final Set<DeferredHolder<Item, ? extends Item>> VAMPIRISM_TAB_ITEMS = new HashSet<>();
    private static final Map<ResourceKey<CreativeModeTab>, Set<DeferredHolder<Item, ? extends Item>>> CREATIVE_TAB_ITEMS = new HashMap<>();

    // creative mode tab
    public static final ResourceKey<CreativeModeTab> VAMPIRISM_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, VResourceLocation.mod("default"));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VAMPIRISM_TAB = CREATIVE_TABS.register(VAMPIRISM_TAB_KEY.location().getPath(), () -> VampirismCreativeTab.builder(VAMPIRISM_TAB_ITEMS.stream().map(DeferredHolder::get).collect(Collectors.toSet())).build());

    // consume effects
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<OblivionEffect>> OBLIVION = CONSUME_EFFECTS.register("oblivious", () -> new ConsumeEffect.Type<>(OblivionEffect.CODEC, OblivionEffect.STREAM_CODEC));
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<FactionBasedConsumeEffect>> FACTION_BASED = CONSUME_EFFECTS.register("faction_based", () -> new ConsumeEffect.Type<>(FactionBasedConsumeEffect.CODEC, FactionBasedConsumeEffect.STREAM_CODEC));
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<BloodConsume>> CONSUME_BLOOD_EFFECT = CONSUME_EFFECTS.register("blood_consume", () -> new ConsumeEffect.Type<>(BloodConsume.CODEC, BloodConsume.STREAM_CODEC));
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<AffectGarlic>> AFFECT_GARLIC = CONSUME_EFFECTS.register("affect_garlic", () -> new ConsumeEffect.Type<>(AffectGarlic.CODEC, AffectGarlic.STREAM_CODEC));
    // items
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_NORMAL = register("armor_of_swiftness_chest_normal", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.CHESTPLATE, IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ENHANCED = register("armor_of_swiftness_chest_enhanced", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.CHESTPLATE, IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE = register("armor_of_swiftness_chest_ultimate", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.CHESTPLATE, IItemWithTier.TIER.ULTIMATE, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_NORMAL = register("armor_of_swiftness_feet_normal", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.BOOTS, IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ENHANCED = register("armor_of_swiftness_feet_enhanced", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.BOOTS, IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ULTIMATE = register("armor_of_swiftness_feet_ultimate", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.BOOTS, IItemWithTier.TIER.ULTIMATE, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_NORMAL = register("armor_of_swiftness_head_normal", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.HELMET, IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ENHANCED = register("armor_of_swiftness_head_enhanced", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.HELMET, IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE = register("armor_of_swiftness_head_ultimate", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.HELMET, IItemWithTier.TIER.ULTIMATE, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_NORMAL = register("armor_of_swiftness_legs_normal", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.LEGGINGS, IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ENHANCED = register("armor_of_swiftness_legs_enhanced", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.LEGGINGS, IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE = register("armor_of_swiftness_legs_ultimate", (prop) -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.LEGGINGS, IItemWithTier.TIER.ULTIMATE, prop));

    public static final DeferredItem<SingleCrossbowItem> BASIC_CROSSBOW = register("basic_crossbow", (prop) -> new SingleCrossbowItem(prop.durability(465), 1, 20, ToolMaterial.WOOD, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<DoubleCrossbowItem> BASIC_DOUBLE_CROSSBOW = register("basic_double_crossbow", (prop) -> new DoubleCrossbowItem(prop.durability(465), 1, 20, ToolMaterial.WOOD, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<TechCrossbowItem> BASIC_TECH_CROSSBOW = register("basic_tech_crossbow", (prop) -> new TechCrossbowItem(prop.durability(930), 1.6F, 40, ToolMaterial.DIAMOND, HunterSkills.WEAPON_TABLE));

    public static final DeferredItem<BloodBottleItem> BLOOD_BOTTLE = ITEMS.registerItem("blood_bottle", (prop) -> new BloodBottleItem(prop.stacksTo(1).component(DataComponents.CONSUMABLE, Consumables.defaultDrink().build())));
    public static final DeferredItem<BucketItem> BLOOD_BUCKET = register("blood_bucket", null, (prop) -> new BucketItem(ModFluids.BLOOD.get(), prop.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_RAW_IRON = register("blood_infused_raw_iron", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_RAW_GOLD = register("blood_infused_raw_gold", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_IRON_INGOT = register("blood_infused_iron_ingot", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_GOLD_INGOT = register("blood_infused_gold_ingot", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_DIAMOND = register("blood_infused_diamond", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_NETHERITE_INGOT = register("blood_infused_netherite_ingot", PureLevelItem::new);

    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_NORMAL = register("crossbow_arrow_normal", (prop) -> new CrossbowArrowItem(new NormalBehavior(), prop));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_SPITFIRE = register("crossbow_arrow_spitfire", (prop) -> new CrossbowArrowItem(new SpitfireBehavior(), prop));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_VAMPIRE_KILLER = register("crossbow_arrow_vampire_killer", (prop) -> new CrossbowArrowItem(new VampireKillerBehavior(), prop));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_TELEPORT = register("crossbow_arrow_teleport", (prop) -> new CrossbowArrowItem(new TeleportBehavior(), prop));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_BLEEDING = register("crossbow_arrow_bleeding", (prop) -> new CrossbowArrowItem(new BleedingBehavior(), prop));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_GARLIC = register("crossbow_arrow_garlic", (prop) -> new CrossbowArrowItem(new GarlicBehavior(), prop));

    public static final DeferredItem<SingleCrossbowItem> ENHANCED_CROSSBOW = register("enhanced_crossbow", (prop) -> new SingleCrossbowItem(prop.durability(930), 1.5F, 15, ToolMaterial.IRON, HunterSkills.MASTER_CRAFTSMANSHIP));
    public static final DeferredItem<DoubleCrossbowItem> ENHANCED_DOUBLE_CROSSBOW = register("enhanced_double_crossbow", (prop) -> new DoubleCrossbowItem(prop.durability(930), 1.5F, 15, ToolMaterial.IRON, HunterSkills.MASTER_CRAFTSMANSHIP));
    public static final DeferredItem<TechCrossbowItem> ENHANCED_TECH_CROSSBOW = register("enhanced_tech_crossbow", (prop) -> new TechCrossbowItem(prop.durability(1860), 1.7F, 30, ToolMaterial.DIAMOND, HunterSkills.MASTER_CRAFTSMANSHIP));

    public static final DeferredItem<Item> GARLIC_DIFFUSER_CORE = register("garlic_diffuser_core", Item::new);
    public static final DeferredItem<Item> GARLIC_DIFFUSER_CORE_IMPROVED = register("garlic_diffuser_core_improved", Item::new);

    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_NORMAL = register("iron_heart_seeker", (prop) -> new HeartSeekerItem(HeartSeekerItem.IRON, IItemWithTier.TIER.NORMAL, 1.3f, prop));
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_ENHANCED = register("diamond_heart_seeker", (prop) -> new HeartSeekerItem(HeartSeekerItem.DIAMOND, IItemWithTier.TIER.ENHANCED, 1.4f, prop));
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_ULTIMATE = register("netherite_heart_seeker", (prop) -> new HeartSeekerItem(HeartSeekerItem.NETHERITE, IItemWithTier.TIER.ULTIMATE, 1.5f, prop));

    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_NORMAL = register("iron_heart_striker", (prop) -> new HeartStrikerItem(HeartStrikerItem.IRON, IItemWithTier.TIER.NORMAL,1.25f, prop));
    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_ENHANCED = register("diamond_heart_striker", (prop) -> new HeartStrikerItem(HeartStrikerItem.DIAMOND, IItemWithTier.TIER.ENHANCED, 1.3f, prop));
    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_ULTIMATE = register("netherite_heart_striker", (prop) -> new HeartStrikerItem(HeartStrikerItem.NETHERITE, IItemWithTier.TIER.ULTIMATE, 1.35f, prop));

    public static final DeferredItem<HolyWaterBottleItem> HOLY_WATER_BOTTLE_NORMAL = register("holy_water_bottle_normal", (prop) -> new HolyWaterBottleItem(IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ENHANCED = register("holy_water_bottle_enhanced", (prop) -> new HolyWaterBottleItem(IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ULTIMATE = register("holy_water_bottle_ultimate", (prop) -> new HolyWaterBottleItem(IItemWithTier.TIER.ULTIMATE, prop));
    public static final DeferredItem<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_NORMAL = register("holy_water_splash_bottle_normal", (prop) -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ENHANCED = register("holy_water_splash_bottle_enhanced", (prop) -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ULTIMATE = register("holy_water_splash_bottle_ultimate", (prop) -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ULTIMATE, prop));

    public static final DeferredItem<BlessableItem> PURE_SALT_WATER = register("pure_salt_water", (prop) -> new BlessableItem(prop.stacksTo(1), HOLY_WATER_BOTTLE_NORMAL::get, HOLY_WATER_BOTTLE_ENHANCED::get) {
        @Override
        public boolean isFoil(@NotNull ItemStack stack) {
            return true;
        }
    });

    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_NORMAL = register("hunter_axe_normal", (prop) -> new HunterAxeItem(HunterAxeItem.NORMAL, IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_ENHANCED = register("hunter_axe_enhanced", (prop) -> new HunterAxeItem(HunterAxeItem.ENHANCED, IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_ULTIMATE = register("hunter_axe_ultimate", (prop) -> new HunterAxeItem(HunterAxeItem.ULTIMATE, IItemWithTier.TIER.ULTIMATE, prop));

    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_NORMAL = register("hunter_coat_chest_normal", (prop) -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.CHESTPLATE, IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_ENHANCED = register("hunter_coat_chest_enhanced", (prop) -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.CHESTPLATE, IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_ULTIMATE = register("hunter_coat_chest_ultimate", (prop) -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.CHESTPLATE, IItemWithTier.TIER.ULTIMATE, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_NORMAL = register("hunter_coat_feet_normal", (prop) -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.BOOTS, IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_ENHANCED = register("hunter_coat_feet_enhanced", (prop) -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.BOOTS, IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_ULTIMATE = register("hunter_coat_feet_ultimate", (prop) -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.BOOTS, IItemWithTier.TIER.ULTIMATE, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_NORMAL = register("hunter_coat_head_normal", (prop) -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.HELMET, IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_ENHANCED = register("hunter_coat_head_enhanced", (prop) -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.HELMET, IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_ULTIMATE = register("hunter_coat_head_ultimate", (prop) -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.HELMET, IItemWithTier.TIER.ULTIMATE, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_NORMAL = register("hunter_coat_legs_normal", (prop) -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.LEGGINGS, IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_ENHANCED = register("hunter_coat_legs_enhanced", (prop) -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.LEGGINGS, IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_ULTIMATE = register("hunter_coat_legs_ultimate", (prop) -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.LEGGINGS, IItemWithTier.TIER.ULTIMATE, prop));

    public static final DeferredItem<HunterHatItem> HUNTER_HAT_TALL = register("hunter_hat_tall", (prop) -> new HunterHatItem(ModArmorMaterials.HUNTER_HAT_TALL, prop));
    public static final DeferredItem<HunterHatItem> HUNTER_HAT_BROAD = register("hunter_hat_broad", (prop) -> new HunterHatItem(ModArmorMaterials.HUNTER_HAT_BROAD, prop));

    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_0 = register("hunter_intel_0", (prop) -> new HunterIntelItem(0, prop));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_1 = register("hunter_intel_1", (prop) -> new HunterIntelItem(1, prop));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_2 = register("hunter_intel_2", (prop) -> new HunterIntelItem(2, prop));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_3 = register("hunter_intel_3", (prop) -> new HunterIntelItem(3, prop));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_4 = register("hunter_intel_4", (prop) -> new HunterIntelItem(4, prop));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_5 = register("hunter_intel_5", (prop) -> new HunterIntelItem(5, prop));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_6 = register("hunter_intel_6", (prop) -> new HunterIntelItem(6, prop));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_7 = register("hunter_intel_7", (prop) -> new HunterIntelItem(7, prop));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_8 = register("hunter_intel_8", (prop) -> new HunterIntelItem(8, prop));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_9 = register("hunter_intel_9", (prop) -> new HunterIntelItem(9, prop));

    public static final DeferredItem<Item> HUMAN_HEART = register("human_heart", (prop) -> new Item(prop.component(DataComponents.FOOD, new FoodProperties.Builder().nutrition(5).saturationModifier(1f).build()).component(ModDataComponents.VAMPIRE_FOOD, new BloodFoodProperties.Builder().blood(20).saturationModifier(1.5F).build()).component(DataComponents.CONSUMABLE, Consumables.defaultFood().onConsume(new FactionBasedConsumeEffect(new NotHolderSet<>(ModRegistries.FACTIONS, HolderSet.direct((Holder<IFaction<?>>) (Object) ModFactions.VAMPIRE)), new ApplyStatusEffectsConsumeEffect(List.of(new MobEffectInstance(MobEffects.CONFUSION, 20*20))))).build())));

    public static final DeferredItem<InjectionItem> INJECTION_EMPTY = register("injection_empty", (prop) -> new InjectionItem(InjectionItem.TYPE.EMPTY, prop));
    public static final DeferredItem<InjectionItem> INJECTION_GARLIC = register("injection_garlic", (prop) -> new InjectionItem(InjectionItem.TYPE.GARLIC, prop));
    public static final DeferredItem<InjectionItem> INJECTION_SANGUINARE = register("injection_sanguinare", (prop) -> new InjectionItem(InjectionItem.TYPE.SANGUINARE, prop));

    public static final DeferredItem<BucketItem> IMPURE_BLOOD_BUCKET = register("impure_blood_bucket", null, (prop) -> new BucketItem(ModFluids.IMPURE_BLOOD.get(), prop.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredItem<Item> GARLIC_BREAD = register("garlic_bread", props -> new Item(props.food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.7F).build()).component(DataComponents.CONSUMABLE, ModConsumables.GARLIC)));
    public static final DeferredItem<AlchemicalFireItem> ITEM_ALCHEMICAL_FIRE = register("item_alchemical_fire", AlchemicalFireItem::new);

    public static final DeferredItem<TentItem> ITEM_TENT = register("item_tent", (prop) -> new TentItem(false, prop));
    public static final DeferredItem<TentItem> ITEM_TENT_SPAWNER = register("item_tent_spawner", (prop) -> new TentItem(true, prop));

    public static final DeferredItem<SwordItem> PITCHFORK = register("pitchfork", props -> new SwordItem(ToolMaterial.IRON, 6, -3, props));

    public static final DeferredItem<PureBloodItem> PURE_BLOOD_0 = register("pure_blood_0", (prop) -> new PureBloodItem(0, prop));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_1 = register("pure_blood_1", (prop) -> new PureBloodItem(1, prop));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_2 = register("pure_blood_2", (prop) -> new PureBloodItem(2, prop));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_3 = register("pure_blood_3", (prop) -> new PureBloodItem(3, prop));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_4 = register("pure_blood_4", (prop) -> new PureBloodItem(4, prop));

    public static final DeferredItem<Item> PURIFIED_GARLIC = register("purified_garlic", (prop) -> new Item(prop.stacksTo(16)));
    public static final DeferredItem<Item> PURE_SALT = register("pure_salt", Item::new);
    public static final DeferredItem<Item> SOUL_ORB_VAMPIRE = register("soul_orb_vampire", Item::new);

    public static final DeferredItem<StakeItem> STAKE = register("stake", StakeItem::new);
    public static final DeferredItem<ArrowContainer> ARROW_CLIP = register("tech_crossbow_ammo_package", (prop) -> new ArrowContainer(prop.stacksTo(1), 12, (stack) -> stack.is(CROSSBOW_ARROW_NORMAL.get())));

    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_WHITE = register("vampire_cloak_white", (prop) -> new VampireCloakItem(DyeColor.WHITE, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_ORANGE = register("vampire_cloak_orange", (prop) -> new VampireCloakItem(DyeColor.ORANGE, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_MAGENTA = register("vampire_cloak_magenta", (prop) -> new VampireCloakItem(DyeColor.MAGENTA, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_LIGHT_BLUE = register("vampire_cloak_light_blue", (prop) -> new VampireCloakItem(DyeColor.LIGHT_BLUE, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_YELLOW = register("vampire_cloak_yellow", (prop) -> new VampireCloakItem(DyeColor.YELLOW, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_LIME = register("vampire_cloak_lime", (prop) -> new VampireCloakItem(DyeColor.LIME, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_PINK = register("vampire_cloak_pink", (prop) -> new VampireCloakItem(DyeColor.PINK, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_GRAY = register("vampire_cloak_gray", (prop) -> new VampireCloakItem(DyeColor.GRAY, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_LIGHT_GRAY = register("vampire_cloak_light_gray", (prop) -> new VampireCloakItem(DyeColor.LIGHT_GRAY, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_CYAN = register("vampire_cloak_cyan", (prop) -> new VampireCloakItem(DyeColor.CYAN, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_PURPLE = register("vampire_cloak_purple", (prop) -> new VampireCloakItem(DyeColor.PURPLE, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_BLUE = register("vampire_cloak_blue", (prop) -> new VampireCloakItem(DyeColor.BLUE, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_BROWN = register("vampire_cloak_brown", (prop) -> new VampireCloakItem(DyeColor.BROWN, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_GREEN = register("vampire_cloak_green", (prop) -> new VampireCloakItem(DyeColor.GREEN, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_RED = register("vampire_cloak_red", (prop) -> new VampireCloakItem(DyeColor.RED, prop));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_BLACK = register("vampire_cloak_black", (prop) -> new VampireCloakItem(DyeColor.BLACK, prop));

    public static final DeferredItem<Item> VAMPIRE_BLOOD_BOTTLE = register("vampire_blood_bottle", Item::new);
    public static final DeferredItem<VampireBookItem> VAMPIRE_BOOK = register("vampire_book", (prop) -> new VampireBookItem(prop.rarity(Rarity.UNCOMMON).stacksTo(1)));
    public static final DeferredItem<VampireFangItem> VAMPIRE_FANG = register("vampire_fang", VampireFangItem::new);
    public static final DeferredItem<VampirismItemBloodFoodItem> WEAK_HUMAN_HEART = register("weak_human_heart", (prop) -> new VampirismItemBloodFoodItem(prop.food(new FoodProperties.Builder().nutrition(3).saturationModifier(1f).build()), new BloodFoodProperties.Builder().blood(10).saturationModifier(0.9F).build()));

    public static final DeferredItem<SpawnEggItem> VAMPIRE_SPAWN_EGG = register("vampire_spawn_egg", null, (prop) -> new SpawnEggItem(ModEntities.VAMPIRE.get(), prop));
    public static final DeferredItem<SpawnEggItem> ADVANCED_VAMPIRE_SPAWN_EGG = register("advanced_vampire_spawn_egg", null, (prop) -> new SpawnEggItem(ModEntities.ADVANCED_VAMPIRE.get(), prop));
    public static final DeferredItem<SpawnEggItem> VAMPIRE_BARON_SPAWN_EGG = register("vampire_baron_spawn_egg", null, (prop) -> new SpawnEggItem(ModEntities.VAMPIRE_BARON.get(), prop));
    public static final DeferredItem<SpawnEggItem> TASK_MASTER_VAMPIRE_SPAWN_EGG = register("task_master_vampire_spawn_egg", null, (prop) -> new SpawnEggItem(ModEntities.TASK_MASTER_VAMPIRE.get(), prop));

    public static final DeferredItem<SpawnEggItem> VAMPIRE_HUNTER_SPAWN_EGG = register("vampire_hunter_spawn_egg", null, (prop) -> new SpawnEggItem(ModEntities.HUNTER.get(), prop));
    public static final DeferredItem<SpawnEggItem> ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG = register("advanced_vampire_hunter_spawn_egg", null, (prop) -> new SpawnEggItem(ModEntities.ADVANCED_HUNTER.get(), prop));
    public static final DeferredItem<SpawnEggItem> HUNTER_TRAINER_SPAWN_EGG = register("hunter_trainer_spawn_egg", null, (prop) -> new SpawnEggItem(ModEntities.HUNTER_TRAINER.get(), prop));
    public static final DeferredItem<SpawnEggItem> TASK_MASTER_HUNTER_SPAWN_EGG = register("task_master_hunter_spawn_egg", null, (prop) -> new SpawnEggItem(ModEntities.TASK_MASTER_HUNTER.get(), prop));

    public static final DeferredItem<SpawnEggItem> GHOST_SPAWN_EGG = register("ghost_spawn_egg", null, (prop) -> new SpawnEggItem(ModEntities.GHOST.get(), prop));

    public static final DeferredItem<UmbrellaItem> UMBRELLA = register("umbrella", UmbrellaItem::new);

    public static final DeferredItem<Item> HUNTER_MINION_EQUIPMENT = register("hunter_minion_equipment", Item::new);
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SIMPLE = register("hunter_minion_upgrade_simple", (prop) -> new MinionUpgradeItem(1, 2, ModFactions.HUNTER, prop));
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_ENHANCED = register("hunter_minion_upgrade_enhanced", (prop) -> new MinionUpgradeItem(3, 4, ModFactions.HUNTER, prop));
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SPECIAL = register("hunter_minion_upgrade_special", (prop) -> new MinionUpgradeItem(5, 6, ModFactions.HUNTER, prop));
    public static final DeferredItem<FeedingAdapterItem> FEEDING_ADAPTER = register("feeding_adapter", FeedingAdapterItem::new);
    public static final DeferredItem<Item> VAMPIRE_MINION_BINDING = register("vampire_minion_binding", Item::new);
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SIMPLE = register("vampire_minion_upgrade_simple", (prop) -> new MinionUpgradeItem(1, 2, ModFactions.VAMPIRE, prop));
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_ENHANCED = register("vampire_minion_upgrade_enhanced", (prop) -> new MinionUpgradeItem(3, 4, ModFactions.VAMPIRE, prop));
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SPECIAL = register("vampire_minion_upgrade_special", (prop) -> new MinionUpgradeItem(5, 6, ModFactions.VAMPIRE, prop));

    public static final DeferredItem<OblivionItem> OBLIVION_POTION = register("oblivion_potion", OblivionItem::new);

    public static final DeferredItem<RefinementItem> AMULET = register("amulet", (prop) -> new RefinementItem(FactionRestriction.builder(ModFactionTags.IS_VAMPIRE).apply(prop), IRefinementItem.AccessorySlotType.AMULET));
    public static final DeferredItem<RefinementItem> RING = register("ring", (prop) -> new RefinementItem(FactionRestriction.builder(ModFactionTags.IS_VAMPIRE).apply(prop), IRefinementItem.AccessorySlotType.RING));
    public static final DeferredItem<RefinementItem> OBI_BELT = register("obi_belt", (prop) -> new RefinementItem(FactionRestriction.builder(ModFactionTags.IS_VAMPIRE).apply(prop), IRefinementItem.AccessorySlotType.OBI_BELT));

    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_CROWN = register("vampire_clothing_crown", (prop) -> new VampireClothingItem(ArmorType.HELMET, ModArmorMaterials.VAMPIRE_CLOTH_CROWN, prop));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_LEGS = register("vampire_clothing_legs", (prop) -> new VampireClothingItem(ArmorType.LEGGINGS, ModArmorMaterials.VAMPIRE_CLOTH_LEGS, prop));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_BOOTS = register("vampire_clothing_boots", (prop) -> new VampireClothingItem(ArmorType.BOOTS, ModArmorMaterials.VAMPIRE_CLOTH_BOOTS, prop));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_HAT = register("vampire_clothing_hat", (prop) -> new VampireClothingItem(ArmorType.HELMET, ModArmorMaterials.VAMPIRE_CLOTH_HAT, prop));

    public static final DeferredItem<Item> GARLIC_FINDER = register("garlic_finder", (prop) -> new Item(prop.rarity(Rarity.RARE)));

    public static final DeferredItem<SignItem> DARK_SPRUCE_SIGN = register("dark_spruce_sign", (prop) -> new SignItem(ModBlocks.DARK_SPRUCE_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_SIGN.get(), prop.useBlockDescriptionPrefix().stacksTo(16)));
    public static final DeferredItem<SignItem> CURSED_SPRUCE_SIGN = register("cursed_spruce_sign", (prop) -> new SignItem(ModBlocks.CURSED_SPRUCE_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_SIGN.get(), prop.useBlockDescriptionPrefix().stacksTo(16)));

    public static final DeferredItem<CrucifixItem> CRUCIFIX_NORMAL = register("crucifix_normal", (prop) -> new CrucifixItem(IItemWithTier.TIER.NORMAL, prop));
    public static final DeferredItem<CrucifixItem> CRUCIFIX_ENHANCED = register("crucifix_enhanced", (prop) -> new CrucifixItem(IItemWithTier.TIER.ENHANCED, prop));
    public static final DeferredItem<CrucifixItem> CRUCIFIX_ULTIMATE = register("crucifix_ultimate", (prop) -> new CrucifixItem(IItemWithTier.TIER.ULTIMATE, prop));

    public static final DeferredItem<BoatItem> DARK_SPRUCE_BOAT = register("dark_spruce_boat", (prop) -> new BoatItem(ModEntities.DARK_SPRUCE_BOAT.get(), prop.stacksTo(1)));
    public static final DeferredItem<BoatItem> CURSED_SPRUCE_BOAT = register("cursed_spruce_boat", (prop) -> new BoatItem(ModEntities.CURSED_SPRUCE_BOAT.get(), prop.stacksTo(1)));
    public static final DeferredItem<BoatItem> DARK_SPRUCE_CHEST_BOAT = register("dark_spruce_chest_boat", (prop) -> new BoatItem(ModEntities.DARK_SPRUCE_CHEST_BOAT.get(), prop.stacksTo(1)));
    public static final DeferredItem<BoatItem> CURSED_SPRUCE_CHEST_BOAT = register("cursed_spruce_chest_boat", (prop) -> new BoatItem(ModEntities.CURSED_SPRUCE_CHEST_BOAT.get(), prop.stacksTo(1)));

    public static final DeferredItem<OilBottleItem> OIL_BOTTLE = register("oil_bottle", (prop) -> new OilBottleItem(prop.stacksTo(1)));
    public static final DeferredItem<HangingSignItem> DARK_SPRUCE_HANGING_SIGN = register("dark_spruce_hanging_sign", (prop) -> new HangingSignItem(ModBlocks.DARK_SPRUCE_HANGING_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get(), prop.useBlockDescriptionPrefix().stacksTo(16)));
    public static final DeferredItem<HangingSignItem> CURSED_SPRUCE_HANGING_SIGN = register("cursed_spruce_hanging_sign", (prop) -> new HangingSignItem(ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get(), prop.useBlockDescriptionPrefix().stacksTo(16)));

    public static final DeferredItem<Item> MOTHER_CORE = register("mother_core", (prop) -> new Item(prop.rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK = register("candle_stick", (prop) -> new StandingAndWallBlockItem(ModBlocks.CANDLE_STICK.get(), ModBlocks.WALL_CANDLE_STICK.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_NORMAL = register("candle_stick_normal", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_NORMAL.get(), ModBlocks.WALL_CANDLE_STICK_NORMAL.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_WHITE = register("candle_stick_white", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_WHITE.get(), ModBlocks.WALL_CANDLE_STICK_WHITE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_ORANGE = register("candle_stick_orange", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_ORANGE.get(), ModBlocks.WALL_CANDLE_STICK_ORANGE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_MAGENTA = register("candle_stick_magenta", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_MAGENTA.get(), ModBlocks.WALL_CANDLE_STICK_MAGENTA.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_LIGHT_BLUE = register("candle_stick_light_blue", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_LIGHT_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_BLUE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_YELLOW = register("candle_stick_yellow", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_YELLOW.get(), ModBlocks.WALL_CANDLE_STICK_YELLOW.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_LIME = register("candle_stick_lime", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_LIME.get(), ModBlocks.WALL_CANDLE_STICK_LIME.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_PINK = register("candle_stick_pink", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_PINK.get(), ModBlocks.WALL_CANDLE_STICK_PINK.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_GRAY = register("candle_stick_gray", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_GRAY.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_LIGHT_GRAY = register("candle_stick_light_gray", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_LIGHT_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_GRAY.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_CYAN = register("candle_stick_cyan", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_CYAN.get(), ModBlocks.WALL_CANDLE_STICK_CYAN.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_PURPLE = register("candle_stick_purple", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_PURPLE.get(), ModBlocks.WALL_CANDLE_STICK_PURPLE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_BLUE = register("candle_stick_blue", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_BLUE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_BROWN = register("candle_stick_brown", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_BROWN.get(), ModBlocks.WALL_CANDLE_STICK_BROWN.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_GREEN = register("candle_stick_green", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_GREEN.get(), ModBlocks.WALL_CANDLE_STICK_GREEN.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_RED = register("candle_stick_red", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_RED.get(), ModBlocks.WALL_CANDLE_STICK_RED.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_BLACK = register("candle_stick_black", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_BLACK.get(), ModBlocks.WALL_CANDLE_STICK_BLACK.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));

    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA = register("candelabra", (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA.get(), ModBlocks.WALL_CANDELABRA.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_NORMAL = register("candelabra_normal", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_NORMAL.get(), ModBlocks.WALL_CANDELABRA_NORMAL.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_WHITE = register("candelabra_white", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_WHITE.get(), ModBlocks.WALL_CANDELABRA_WHITE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_ORANGE = register("candelabra_orange", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_ORANGE.get(), ModBlocks.WALL_CANDELABRA_ORANGE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_MAGENTA = register("candelabra_magenta", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_MAGENTA.get(), ModBlocks.WALL_CANDELABRA_MAGENTA.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_LIGHT_BLUE = register("candelabra_light_blue", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_LIGHT_BLUE.get(), ModBlocks.WALL_CANDELABRA_LIGHT_BLUE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_YELLOW = register("candelabra_yellow", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_YELLOW.get(), ModBlocks.WALL_CANDELABRA_YELLOW.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_LIME = register("candelabra_lime", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_LIME.get(), ModBlocks.WALL_CANDELABRA_LIME.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_PINK = register("candelabra_pink", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_PINK.get(), ModBlocks.WALL_CANDELABRA_PINK.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_GRAY = register("candelabra_gray", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_GRAY.get(), ModBlocks.WALL_CANDELABRA_GRAY.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_LIGHT_GRAY = register("candelabra_light_gray", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_LIGHT_GRAY.get(), ModBlocks.WALL_CANDELABRA_LIGHT_GRAY.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_CYAN = register("candelabra_cyan", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_CYAN.get(), ModBlocks.WALL_CANDELABRA_CYAN.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_PURPLE = register("candelabra_purple", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_PURPLE.get(), ModBlocks.WALL_CANDELABRA_PURPLE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_BLUE = register("candelabra_blue", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_BLUE.get(), ModBlocks.WALL_CANDELABRA_BLUE.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_BROWN = register("candelabra_brown", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_BROWN.get(), ModBlocks.WALL_CANDELABRA_BROWN.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_GREEN = register("candelabra_green", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_GREEN.get(), ModBlocks.WALL_CANDELABRA_GREEN.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_RED = register("candelabra_red", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_RED.get(), ModBlocks.WALL_CANDELABRA_RED.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_BLACK = register("candelabra_black", null, (prop) -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_BLACK.get(), ModBlocks.WALL_CANDELABRA_BLACK.get(), Direction.DOWN, prop.useBlockDescriptionPrefix()));

    public static final DeferredItem<BlockItem> CHANDELIER = register("chandelier", (prop) -> new BlockItem(ModBlocks.CHANDELIER.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_NORMAL = register("chandelier_normal", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_NORMAL.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_WHITE = register("chandelier_white", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_WHITE.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_ORANGE = register("chandelier_orange", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_ORANGE.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_MAGENTA = register("chandelier_magenta", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_MAGENTA.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_LIGHT_BLUE = register("chandelier_light_blue", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_LIGHT_BLUE.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_YELLOW = register("chandelier_yellow", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_YELLOW.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_LIME = register("chandelier_lime", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_LIME.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_PINK = register("chandelier_pink", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_PINK.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_GRAY = register("chandelier_gray", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_GRAY.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_LIGHT_GRAY = register("chandelier_light_gray", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_LIGHT_GRAY.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_CYAN = register("chandelier_cyan", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_CYAN.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_PURPLE = register("chandelier_purple", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_PURPLE.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_BLUE = register("chandelier_blue", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_BLUE.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_BROWN = register("chandelier_brown", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_BROWN.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_GREEN = register("chandelier_green", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_GREEN.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_RED = register("chandelier_red", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_RED.get(), prop.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> CHANDELIER_BLACK = register("chandelier_black", null, (prop) -> new BlockItem(ModBlocks.CHANDELIER_BLACK.get(), prop.useBlockDescriptionPrefix()));

    static <I extends Item> DeferredItem<I> register(final String name, ResourceKey<CreativeModeTab> tab, final Function<Item.Properties, ? extends I> sup) {
        DeferredItem<I> item = ITEMS.registerItem(name, sup);
        if (tab == VAMPIRISM_TAB_KEY) {
            VAMPIRISM_TAB_ITEMS.add(item);
        } else {
            CREATIVE_TAB_ITEMS.computeIfAbsent(tab, (a) -> new HashSet<>()).add(item);
        }
        return item;
    }

    static <I extends Item> DeferredItem<I> register(final String name, final Function<Item.Properties, ? extends I> sup) {
        return register(name, VAMPIRISM_TAB_KEY, sup);
    }


    static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
        ITEMS.register(bus);
        if (VampirismMod.inDataGen) {
            DeferredRegister.Items DUMMY_ITEMS = DeferredRegister.createItems("guideapi_vp");
            DeferredItem<DummyItem> dummy_item = DUMMY_ITEMS.registerItem("vampirism-guidebook", DummyItem::new);
            DUMMY_ITEMS.register(bus);
            VAMPIRISM_TAB_ITEMS.add(dummy_item);
        }
    }

    @SuppressWarnings("unchecked")
    public static Stream<Holder<Item>> listElements() {
        return ((Collection<Holder<Item>>) (Object) ITEMS.getEntries()).stream();
    }

    private static Item.@NotNull Properties props() {
        return new Item.Properties();
    }

    static void registerOtherCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        CREATIVE_TAB_ITEMS.forEach((tab, items) -> {
            if (event.getTabKey() == tab) {
                items.forEach(item -> event.accept(item.get()));
            }
        });
    }

    public static void registerDispenserBehaviourUnsafe() {
        DispenserBlock.registerBehavior(ModItems.DARK_SPRUCE_BOAT.get(), new BoatDispenseItemBehavior(ModEntities.DARK_SPRUCE_BOAT.get()));
        DispenserBlock.registerBehavior(ModItems.CURSED_SPRUCE_BOAT.get(), new BoatDispenseItemBehavior(ModEntities.CURSED_SPRUCE_BOAT.get()));
        DispenserBlock.registerBehavior(ModItems.DARK_SPRUCE_CHEST_BOAT.get(), new BoatDispenseItemBehavior(ModEntities.DARK_SPRUCE_CHEST_BOAT.get()));
        DispenserBlock.registerBehavior(ModItems.CURSED_SPRUCE_CHEST_BOAT.get(), new BoatDispenseItemBehavior(ModEntities.CURSED_SPRUCE_CHEST_BOAT.get()));
        DispenserBlock.registerProjectileBehavior(ModItems.CROSSBOW_ARROW_NORMAL.get());
        DispenserBlock.registerProjectileBehavior(ModItems.CROSSBOW_ARROW_SPITFIRE.get());
        DispenserBlock.registerProjectileBehavior(ModItems.CROSSBOW_ARROW_TELEPORT.get());
        DispenserBlock.registerProjectileBehavior(ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get());
        DispenserBlock.registerProjectileBehavior(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get());
        DispenserBlock.registerProjectileBehavior(ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get());
        DispenserBlock.registerProjectileBehavior(ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get());
    }
}
