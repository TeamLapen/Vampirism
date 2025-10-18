package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.common.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.items.*;
import de.teamlapen.vampirism.common.items.component.FactionRestriction;
import de.teamlapen.vampirism.common.items.consume.*;
import de.teamlapen.vampirism.common.items.crossbow.ArrowContainer;
import de.teamlapen.vampirism.common.items.crossbow.DoubleCrossbowItem;
import de.teamlapen.vampirism.common.items.crossbow.SingleCrossbowItem;
import de.teamlapen.vampirism.common.items.crossbow.TechCrossbowItem;
import de.teamlapen.vampirism.common.items.crossbow.arrow.*;
import de.teamlapen.vampirism.common.items.dispenser.SyringeDispenseBehavior;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.util.DescriptionUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handles all item registrations and reference.
 */
@SuppressWarnings("unused")
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(REFERENCE.MODID);
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECTS = DeferredRegister.create(Registries.CONSUME_EFFECT_TYPE, REFERENCE.MODID);

    // Consume Effects
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<OblivionEffect>> OBLIVION = CONSUME_EFFECTS.register("oblivious", () -> new ConsumeEffect.Type<>(OblivionEffect.CODEC, OblivionEffect.STREAM_CODEC));
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<FactionBasedConsumeEffect>> FACTION_BASED = CONSUME_EFFECTS.register("faction_based", () -> new ConsumeEffect.Type<>(FactionBasedConsumeEffect.CODEC, FactionBasedConsumeEffect.STREAM_CODEC));
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<BloodConsume>> CONSUME_BLOOD_EFFECT = CONSUME_EFFECTS.register("blood_consume", () -> new ConsumeEffect.Type<>(BloodConsume.CODEC, BloodConsume.STREAM_CODEC));
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<AffectGarlic>> AFFECT_GARLIC = CONSUME_EFFECTS.register("affect_garlic", () -> new ConsumeEffect.Type<>(AffectGarlic.CODEC, AffectGarlic.STREAM_CODEC));

    // Weapons
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_NORMAL = ITEMS.registerItem("iron_heart_seeker",  props -> new HeartSeekerItem(HeartSeekerItem.IRON, IItemWithTier.Tier.NORMAL, 1.3f, props));
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_ENHANCED = ITEMS.registerItem("diamond_heart_seeker",  props -> new HeartSeekerItem(HeartSeekerItem.DIAMOND, IItemWithTier.Tier.ENHANCED, 1.4f, props));
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_ULTIMATE = ITEMS.registerItem("netherite_heart_seeker",  props -> new HeartSeekerItem(HeartSeekerItem.NETHERITE, IItemWithTier.Tier.ULTIMATE, 1.5f, props));

    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_NORMAL = ITEMS.registerItem("iron_heart_striker", props -> new HeartStrikerItem(HeartStrikerItem.IRON, IItemWithTier.Tier.NORMAL,1.25f, props));
    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_ENHANCED = ITEMS.registerItem("diamond_heart_striker",  props -> new HeartStrikerItem(HeartStrikerItem.DIAMOND, IItemWithTier.Tier.ENHANCED, 1.3f, props));
    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_ULTIMATE = ITEMS.registerItem("netherite_heart_striker",  props -> new HeartStrikerItem(HeartStrikerItem.NETHERITE, IItemWithTier.Tier.ULTIMATE, 1.35f, props));

    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_NORMAL = ITEMS.registerItem("hunter_axe_normal",  props -> new HunterAxeItem(HunterAxeItem.NORMAL, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_ENHANCED = ITEMS.registerItem("hunter_axe_enhanced",  props -> new HunterAxeItem(HunterAxeItem.ENHANCED, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_ULTIMATE = ITEMS.registerItem("hunter_axe_ultimate",  props -> new HunterAxeItem(HunterAxeItem.ULTIMATE, IItemWithTier.Tier.ULTIMATE, props));

    public static final DeferredItem<SingleCrossbowItem> BASIC_CROSSBOW = ITEMS.registerItem("basic_crossbow",  props -> new SingleCrossbowItem(props.durability(465), 1, 20, ToolMaterial.WOOD, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<DoubleCrossbowItem> BASIC_DOUBLE_CROSSBOW = ITEMS.registerItem("basic_double_crossbow",  props -> new DoubleCrossbowItem(props.durability(465), 1, 20, ToolMaterial.WOOD, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<SingleCrossbowItem> ENHANCED_CROSSBOW = ITEMS.registerItem("enhanced_crossbow",  props -> new SingleCrossbowItem(props.durability(930), 1.5F, 15, ToolMaterial.IRON, HunterSkills.MASTER_CRAFTSMANSHIP));
    public static final DeferredItem<DoubleCrossbowItem> ENHANCED_DOUBLE_CROSSBOW = ITEMS.registerItem("enhanced_double_crossbow",  props -> new DoubleCrossbowItem(props.durability(930), 1.5F, 15, ToolMaterial.IRON, HunterSkills.MASTER_CRAFTSMANSHIP));
    public static final DeferredItem<TechCrossbowItem> BASIC_TECH_CROSSBOW = ITEMS.registerItem("basic_tech_crossbow",  props -> new TechCrossbowItem(props.durability(930), 1.6F, 40, ToolMaterial.DIAMOND, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<TechCrossbowItem> ENHANCED_TECH_CROSSBOW = ITEMS.registerItem("enhanced_tech_crossbow",  props -> new TechCrossbowItem(props.durability(1860), 1.7F, 30, ToolMaterial.DIAMOND, HunterSkills.MASTER_CRAFTSMANSHIP));

    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_NORMAL = ITEMS.registerItem("crossbow_arrow_normal", props -> new CrossbowArrowItem(new NormalBehavior(), props));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_SPITFIRE = ITEMS.registerItem("crossbow_arrow_spitfire",  props -> new CrossbowArrowItem(new SpitfireBehavior(), props));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_GARLIC = ITEMS.registerItem("crossbow_arrow_garlic",  props -> new CrossbowArrowItem(new GarlicBehavior(), props));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_VAMPIRE_KILLER = ITEMS.registerItem("crossbow_arrow_vampire_killer",  props -> new CrossbowArrowItem(new VampireKillerBehavior(), props));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_TELEPORT = ITEMS.registerItem("crossbow_arrow_teleport",  props -> new CrossbowArrowItem(new TeleportBehavior(), props));
    public static final DeferredItem<CrossbowArrowItem> CROSSBOW_ARROW_BLEEDING = ITEMS.registerItem("crossbow_arrow_bleeding",  props -> new CrossbowArrowItem(new BleedingBehavior(), props));

    public static final DeferredItem<ArrowContainer> ARROW_CLIP = ITEMS.registerItem("tech_crossbow_ammo_package",  props -> new ArrowContainer(props.stacksTo(1), 12, (stack) -> stack.is(CROSSBOW_ARROW_NORMAL.get())));
    public static final DeferredItem<Item> QUARREL_POUCH = ITEMS.registerItem("quarrel_pouch",  props -> new QuarrelPouch(props.stacksTo(1)));

    public static final DeferredItem<SwordItem> PITCHFORK = ITEMS.registerItem("pitchfork", props -> new SwordItem(ToolMaterial.IRON, 6, -3, props));
    public static final DeferredItem<StakeItem> STAKE = ITEMS.registerItem("stake", StakeItem::new);

    public static final DeferredItem<CrucifixItem> CRUCIFIX_NORMAL = ITEMS.registerItem("crucifix_normal",  props -> new CrucifixItem(IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<CrucifixItem> CRUCIFIX_ENHANCED = ITEMS.registerItem("crucifix_enhanced",  props -> new CrucifixItem(IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<CrucifixItem> CRUCIFIX_ULTIMATE = ITEMS.registerItem("crucifix_ultimate",  props -> new CrucifixItem(IItemWithTier.Tier.ULTIMATE, props));

    // Armor
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_NORMAL = ITEMS.registerItem("armor_of_swiftness_chest_normal",  props ->new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.CHESTPLATE, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ENHANCED = ITEMS.registerItem("armor_of_swiftness_chest_enhanced",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.CHESTPLATE, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE = ITEMS.registerItem("armor_of_swiftness_chest_ultimate",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.CHESTPLATE, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_NORMAL = ITEMS.registerItem("armor_of_swiftness_feet_normal",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.BOOTS, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ENHANCED = ITEMS.registerItem("armor_of_swiftness_feet_enhanced",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.BOOTS, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ULTIMATE = ITEMS.registerItem("armor_of_swiftness_feet_ultimate",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.BOOTS, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_NORMAL = ITEMS.registerItem("armor_of_swiftness_head_normal",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.HELMET, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ENHANCED = ITEMS.registerItem("armor_of_swiftness_head_enhanced",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.HELMET, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE = ITEMS.registerItem("armor_of_swiftness_head_ultimate",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.HELMET, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_NORMAL = ITEMS.registerItem("armor_of_swiftness_legs_normal",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.LEGGINGS, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ENHANCED = ITEMS.registerItem("armor_of_swiftness_legs_enhanced",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.LEGGINGS, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE = ITEMS.registerItem("armor_of_swiftness_legs_ultimate",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.LEGGINGS, IItemWithTier.Tier.ULTIMATE, props));

    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_NORMAL = ITEMS.registerItem("hunter_coat_chest_normal", props -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.CHESTPLATE, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_ENHANCED = ITEMS.registerItem("hunter_coat_chest_enhanced",  props -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.CHESTPLATE, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_ULTIMATE = ITEMS.registerItem("hunter_coat_chest_ultimate",  props -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.CHESTPLATE, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_NORMAL = ITEMS.registerItem("hunter_coat_feet_normal",  props -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.BOOTS, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_ENHANCED = ITEMS.registerItem("hunter_coat_feet_enhanced",  props -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.BOOTS, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_ULTIMATE = ITEMS.registerItem("hunter_coat_feet_ultimate",  props -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.BOOTS, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_NORMAL = ITEMS.registerItem("hunter_coat_head_normal",  props -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.HELMET, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_ENHANCED = ITEMS.registerItem("hunter_coat_head_enhanced",  props -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.HELMET, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_ULTIMATE = ITEMS.registerItem("hunter_coat_head_ultimate",  props -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.HELMET, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_NORMAL = ITEMS.registerItem("hunter_coat_legs_normal",  props -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.LEGGINGS, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_ENHANCED = ITEMS.registerItem("hunter_coat_legs_enhanced",  props -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.LEGGINGS, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_ULTIMATE = ITEMS.registerItem("hunter_coat_legs_ultimate",  props -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.LEGGINGS, IItemWithTier.Tier.ULTIMATE, props));

    public static final DeferredItem<HunterArmorItem> HUNTER_HAT_TALL = ITEMS.registerItem("hunter_hat_tall",  props -> new HunterArmorItem(ModArmorMaterials.HUNTER_HAT_TALL, ArmorType.HELMET, props));
    public static final DeferredItem<HunterArmorItem> HUNTER_HAT_BROAD = ITEMS.registerItem("hunter_hat_broad",  props -> new HunterArmorItem(ModArmorMaterials.HUNTER_HAT_BROAD, ArmorType.HELMET, props));

    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_CROWN = ITEMS.registerItem("vampire_clothing_crown",  props -> new VampireClothingItem(ArmorType.HELMET, ModArmorMaterials.VAMPIRE_CLOTH_CROWN, props));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_LEGS = ITEMS.registerItem("vampire_clothing_legs",  props -> new VampireClothingItem(ArmorType.LEGGINGS, ModArmorMaterials.VAMPIRE_CLOTH_LEGS, props));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_BOOTS = ITEMS.registerItem("vampire_clothing_boots",  props -> new VampireClothingItem(ArmorType.BOOTS, ModArmorMaterials.VAMPIRE_CLOTH_BOOTS, props));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_HAT = ITEMS.registerItem("vampire_clothing_hat",  props -> new VampireClothingItem(ArmorType.HELMET, ModArmorMaterials.VAMPIRE_CLOTH_HAT, props));

    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_WHITE = ITEMS.registerItem("vampire_cloak_white",  props -> new VampireCloakItem(DyeColor.WHITE, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_ORANGE = ITEMS.registerItem("vampire_cloak_orange",  props -> new VampireCloakItem(DyeColor.ORANGE, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_MAGENTA = ITEMS.registerItem("vampire_cloak_magenta",  props -> new VampireCloakItem(DyeColor.MAGENTA, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_LIGHT_BLUE = ITEMS.registerItem("vampire_cloak_light_blue",  props -> new VampireCloakItem(DyeColor.LIGHT_BLUE, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_YELLOW = ITEMS.registerItem("vampire_cloak_yellow",  props -> new VampireCloakItem(DyeColor.YELLOW, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_LIME = ITEMS.registerItem("vampire_cloak_lime",  props -> new VampireCloakItem(DyeColor.LIME, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_PINK = ITEMS.registerItem("vampire_cloak_pink",  props -> new VampireCloakItem(DyeColor.PINK, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_GRAY = ITEMS.registerItem("vampire_cloak_gray",  props -> new VampireCloakItem(DyeColor.GRAY, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_LIGHT_GRAY = ITEMS.registerItem("vampire_cloak_light_gray",  props -> new VampireCloakItem(DyeColor.LIGHT_GRAY, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_CYAN = ITEMS.registerItem("vampire_cloak_cyan",  props -> new VampireCloakItem(DyeColor.CYAN, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_PURPLE = ITEMS.registerItem("vampire_cloak_purple",  props -> new VampireCloakItem(DyeColor.PURPLE, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_BLUE = ITEMS.registerItem("vampire_cloak_blue",  props -> new VampireCloakItem(DyeColor.BLUE, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_BROWN = ITEMS.registerItem("vampire_cloak_brown",  props -> new VampireCloakItem(DyeColor.BROWN, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_GREEN = ITEMS.registerItem("vampire_cloak_green",  props -> new VampireCloakItem(DyeColor.GREEN, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_RED = ITEMS.registerItem("vampire_cloak_red",  props -> new VampireCloakItem(DyeColor.RED, props));
    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_BLACK = ITEMS.registerItem("vampire_cloak_black",  props -> new VampireCloakItem(DyeColor.BLACK, props));

    public static final DeferredItem<RefinementItem> AMULET = ITEMS.registerItem("amulet",  props -> new RefinementItem(FactionRestriction.builder(ModFactionTags.IS_VAMPIRE).apply(props), IRefinementItem.AccessorySlotType.AMULET));
    public static final DeferredItem<RefinementItem> RING = ITEMS.registerItem("ring",  props -> new RefinementItem(FactionRestriction.builder(ModFactionTags.IS_VAMPIRE).apply(props), IRefinementItem.AccessorySlotType.RING));
    public static final DeferredItem<RefinementItem> OBI_BELT = ITEMS.registerItem("obi_belt",  props -> new RefinementItem(FactionRestriction.builder(ModFactionTags.IS_VAMPIRE).apply(props), IRefinementItem.AccessorySlotType.OBI_BELT));

    // General
    public static final DeferredItem<BloodBottleItem> BLOOD_BOTTLE = ITEMS.registerItem("blood_bottle", props -> new BloodBottleItem(props.component(DataComponents.CONSUMABLE, Consumables.defaultDrink().build())));
    public static final DeferredItem<BucketItem> BLOOD_BUCKET = ITEMS.registerItem("blood_bucket",  props -> new BucketItem(ModFluids.BLOOD.get(), props.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_RAW_IRON = ITEMS.registerItem("blood_infused_raw_iron", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_RAW_GOLD = ITEMS.registerItem("blood_infused_raw_gold", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_IRON_INGOT = ITEMS.registerItem("blood_infused_iron_ingot", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_GOLD_INGOT = ITEMS.registerItem("blood_infused_gold_ingot", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_DIAMOND = ITEMS.registerItem("blood_infused_diamond", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_NETHERITE_INGOT = ITEMS.registerItem("blood_infused_netherite_ingot", PureLevelItem::new);

    public static final DeferredItem<Item> GARLIC_DIFFUSER_CORE = ITEMS.registerItem("garlic_diffuser_core", Item::new);
    public static final DeferredItem<Item> GARLIC_DIFFUSER_CORE_IMPROVED = ITEMS.registerItem("garlic_diffuser_core_improved", Item::new);

    public static final DeferredItem<HolyWaterBottleItem> HOLY_WATER_BOTTLE_NORMAL = ITEMS.registerItem("holy_water_bottle_normal",  props -> new HolyWaterBottleItem(IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ENHANCED = ITEMS.registerItem("holy_water_bottle_enhanced",  props -> new HolyWaterBottleItem(IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HolyWaterBottleItem> HOLY_WATER_BOTTLE_ULTIMATE = ITEMS.registerItem("holy_water_bottle_ultimate",  props -> new HolyWaterBottleItem(IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_NORMAL = ITEMS.registerItem("holy_water_splash_bottle_normal", props -> new HolyWaterSplashBottleItem(IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ENHANCED = ITEMS.registerItem("holy_water_splash_bottle_enhanced",  props -> new HolyWaterSplashBottleItem(IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HolyWaterSplashBottleItem> HOLY_WATER_SPLASH_BOTTLE_ULTIMATE = ITEMS.registerItem("holy_water_splash_bottle_ultimate",  props -> new HolyWaterSplashBottleItem(IItemWithTier.Tier.ULTIMATE, props));

    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_0 = ITEMS.registerItem("hunter_intel_0",  props -> new HunterIntelItem(0, props));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_1 = ITEMS.registerItem("hunter_intel_1",  props -> new HunterIntelItem(1, props));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_2 = ITEMS.registerItem("hunter_intel_2",  props -> new HunterIntelItem(2, props));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_3 = ITEMS.registerItem("hunter_intel_3",  props -> new HunterIntelItem(3, props));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_4 = ITEMS.registerItem("hunter_intel_4",  props -> new HunterIntelItem(4, props));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_5 = ITEMS.registerItem("hunter_intel_5",  props -> new HunterIntelItem(5, props));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_6 = ITEMS.registerItem("hunter_intel_6",  props -> new HunterIntelItem(6, props));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_7 = ITEMS.registerItem("hunter_intel_7",  props -> new HunterIntelItem(7, props));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_8 = ITEMS.registerItem("hunter_intel_8",  props -> new HunterIntelItem(8, props));
    public static final DeferredItem<HunterIntelItem> HUNTER_INTEL_9 = ITEMS.registerItem("hunter_intel_9",  props -> new HunterIntelItem(9, props));

    public static final DeferredItem<PureBloodItem> PURE_BLOOD_0 = ITEMS.registerItem("pure_blood_0",  props -> new PureBloodItem(0, props));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_1 = ITEMS.registerItem("pure_blood_1",  props -> new PureBloodItem(1, props));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_2 = ITEMS.registerItem("pure_blood_2",  props -> new PureBloodItem(2, props));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_3 = ITEMS.registerItem("pure_blood_3",  props -> new PureBloodItem(3, props));
    public static final DeferredItem<PureBloodItem> PURE_BLOOD_4 = ITEMS.registerItem("pure_blood_4",  props -> new PureBloodItem(4, props));

    public static final DeferredItem<Item> GARLIC_BREAD = ITEMS.registerItem("garlic_bread", props -> new Item(props.food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.7F).build()).component(DataComponents.CONSUMABLE, ModConsumables.GARLIC)));
    public static final DeferredItem<Item> HUMAN_HEART = ITEMS.registerItem("human_heart",  props -> new Item(props.component(DataComponents.FOOD, new FoodProperties.Builder().nutrition(5).saturationModifier(1f).build()).component(ModDataComponents.VAMPIRE_FOOD, new BloodFoodProperties.Builder().blood(20).saturationModifier(1.5F).build()).component(DataComponents.CONSUMABLE, Consumables.defaultFood().onConsume(new FactionBasedConsumeEffect(new NotHolderSet<>(ModRegistries.FACTIONS, HolderSet.direct((Holder<IFaction<?>>) (Object) ModFactions.VAMPIRE)), new ApplyStatusEffectsConsumeEffect(List.of(new MobEffectInstance(MobEffects.CONFUSION, 20*20))))).build())));
    public static final DeferredItem<VampirismItemBloodFoodItem> WEAK_HUMAN_HEART = ITEMS.registerItem("weak_human_heart",  props -> new VampirismItemBloodFoodItem(props.food(new FoodProperties.Builder().nutrition(3).saturationModifier(1f).build()), new BloodFoodProperties.Builder().blood(10).saturationModifier(0.9F).build()));

    public static final DeferredItem<Item> SYRINGE_EMPTY = ITEMS.registerItem("syringe_empty", Item::new);
    public static final DeferredItem<Item> SYRINGE_BLOOD = ITEMS.registerItem("syringe_blood", props -> new Item(props.stacksTo(16).craftRemainder(SYRINGE_EMPTY.get())));
    public static final DeferredItem<GarlicInjectionItem> INJECTION_GARLIC = ITEMS.registerItem("injection_garlic", props -> new GarlicInjectionItem(props.stacksTo(16).craftRemainder(SYRINGE_EMPTY.get())));
    public static final DeferredItem<SanguinareInjectionItem> INJECTION_SANGUINARE = ITEMS.registerItem("injection_sanguinare", props -> new SanguinareInjectionItem(props.stacksTo(16).craftRemainder(SYRINGE_EMPTY.get())));

    public static final DeferredItem<AlchemicalFireItem> ITEM_ALCHEMICAL_FIRE = ITEMS.registerItem("item_alchemical_fire", AlchemicalFireItem::new);

    public static final DeferredItem<Item> PURIFIED_GARLIC = ITEMS.registerItem("purified_garlic",  props -> new Item(props.stacksTo(16)));
    public static final DeferredItem<Item> PURE_SALT = ITEMS.registerItem("pure_salt", Item::new);
    public static final DeferredItem<BlessableItem> PURE_SALT_WATER = ITEMS.registerItem("pure_salt_water",  props -> new BlessableItem(props.stacksTo(1), HOLY_WATER_BOTTLE_NORMAL::get, HOLY_WATER_BOTTLE_ENHANCED::get) {
        @Override
        public boolean isFoil(@NotNull ItemStack stack) {
            return true;
        }
    });

    public static final DeferredItem<Item> SOUL_ORB_VAMPIRE = ITEMS.registerItem("soul_orb_vampire", Item::new);
    public static final DeferredItem<Item> MOTHER_CORE = ITEMS.registerItem("mother_core",  props -> new Item(props.rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> VAMPIRE_BLOOD_BOTTLE = ITEMS.registerItem("vampire_blood_bottle", Item::new);
    public static final DeferredItem<VampireBookItem> VAMPIRE_BOOK = ITEMS.registerItem("vampire_book",  props -> new VampireBookItem(props.rarity(Rarity.UNCOMMON).stacksTo(1)));
    public static final DeferredItem<VampireFangItem> VAMPIRE_FANG = ITEMS.registerItem("vampire_fang", VampireFangItem::new);

    public static final DeferredItem<UmbrellaItem> UMBRELLA = ITEMS.registerItem("umbrella", props -> new UmbrellaItem(props.stacksTo(1)));

    public static final DeferredItem<Item> HUNTER_MINION_EQUIPMENT = ITEMS.registerItem("hunter_minion_equipment", Item::new);
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SIMPLE = ITEMS.registerItem("hunter_minion_upgrade_simple",  props -> new MinionUpgradeItem(1, 2, ModFactions.HUNTER, props));
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_ENHANCED = ITEMS.registerItem("hunter_minion_upgrade_enhanced",  props -> new MinionUpgradeItem(3, 4, ModFactions.HUNTER, props));
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SPECIAL = ITEMS.registerItem("hunter_minion_upgrade_special",  props -> new MinionUpgradeItem(5, 6, ModFactions.HUNTER, props));

    public static final DeferredItem<Item> VAMPIRE_MINION_BINDING = ITEMS.registerItem("vampire_minion_binding", Item::new);
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SIMPLE = ITEMS.registerItem("vampire_minion_upgrade_simple",  props -> new MinionUpgradeItem(1, 2, ModFactions.VAMPIRE, props));
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_ENHANCED = ITEMS.registerItem("vampire_minion_upgrade_enhanced",  props -> new MinionUpgradeItem(3, 4, ModFactions.VAMPIRE, props));
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SPECIAL = ITEMS.registerItem("vampire_minion_upgrade_special",  props -> new MinionUpgradeItem(5, 6, ModFactions.VAMPIRE, props));

    public static final DeferredItem<Item> FABRIC_FILTER = ITEMS.registerItem("fabric_filter", props -> new Item(props.stacksTo(1).durability(4800)));

    public static final DeferredItem<FeedingAdapterItem> FEEDING_ADAPTER = ITEMS.registerItem("feeding_adapter", props -> new FeedingAdapterItem(props.stacksTo(1)));
    public static final DeferredItem<OblivionPotionItem> OBLIVION_POTION = ITEMS.registerItem("oblivion_potion", props -> new OblivionPotionItem(props.stacksTo(1).rarity(Rarity.UNCOMMON).component(DataComponents.CONSUMABLE, Consumables.defaultDrink().onConsume(new OblivionEffect()).build())));
    public static final DeferredItem<Item> GARLIC_FINDER = ITEMS.registerItem("garlic_finder",  props -> new Item(props.rarity(Rarity.RARE)));

    public static final DeferredItem<OilBottleItem> OIL_BOTTLE = ITEMS.registerItem("oil_bottle",  props -> new OilBottleItem(props.stacksTo(1)));

    public static final DeferredItem<BoatItem> DARK_SPRUCE_BOAT = ITEMS.registerItem("dark_spruce_boat",  props -> new BoatItem(ModEntities.DARK_SPRUCE_BOAT.get(), props.stacksTo(1)));
    public static final DeferredItem<BoatItem> CURSED_SPRUCE_BOAT = ITEMS.registerItem("cursed_spruce_boat",  props -> new BoatItem(ModEntities.CURSED_SPRUCE_BOAT.get(), props.stacksTo(1)));
    public static final DeferredItem<BoatItem> DARK_SPRUCE_CHEST_BOAT = ITEMS.registerItem("dark_spruce_chest_boat",  props -> new BoatItem(ModEntities.DARK_SPRUCE_CHEST_BOAT.get(), props.stacksTo(1)));
    public static final DeferredItem<BoatItem> CURSED_SPRUCE_CHEST_BOAT = ITEMS.registerItem("cursed_spruce_chest_boat",  props -> new BoatItem(ModEntities.CURSED_SPRUCE_CHEST_BOAT.get(), props.stacksTo(1)));

    public static final DeferredItem<SignItem> DARK_SPRUCE_SIGN = ITEMS.registerItem("dark_spruce_sign",  props -> new SignItem(ModBlocks.DARK_SPRUCE_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_SIGN.get(), props.useBlockDescriptionPrefix().stacksTo(16)));
    public static final DeferredItem<SignItem> CURSED_SPRUCE_SIGN = ITEMS.registerItem("cursed_spruce_sign",  props -> new SignItem(ModBlocks.CURSED_SPRUCE_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_SIGN.get(), props.useBlockDescriptionPrefix().stacksTo(16)));
    public static final DeferredItem<HangingSignItem> DARK_SPRUCE_HANGING_SIGN = ITEMS.registerItem("dark_spruce_hanging_sign",  props -> new HangingSignItem(ModBlocks.DARK_SPRUCE_HANGING_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get(), props.useBlockDescriptionPrefix().stacksTo(16)));
    public static final DeferredItem<HangingSignItem> CURSED_SPRUCE_HANGING_SIGN = ITEMS.registerItem("cursed_spruce_hanging_sign",  props -> new HangingSignItem(ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get(), props.useBlockDescriptionPrefix().stacksTo(16)));

    public static final DeferredItem<TentItem> ITEM_TENT = ITEMS.registerItem("item_tent",  props -> new TentItem(false, props));
    public static final DeferredItem<TentItem> ITEM_TENT_SPAWNER = ITEMS.registerItem("item_tent_spawner",  props -> new TentItem(true, props));

    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK = ITEMS.registerItem("candle_stick",  props -> new StandingAndWallBlockItem(ModBlocks.CANDLE_STICK.get(), ModBlocks.WALL_CANDLE_STICK.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_NORMAL = ITEMS.registerItem("candle_stick_normal",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_NORMAL.get(), ModBlocks.WALL_CANDLE_STICK_NORMAL.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_WHITE = ITEMS.registerItem("candle_stick_white",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_WHITE.get(), ModBlocks.WALL_CANDLE_STICK_WHITE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_ORANGE = ITEMS.registerItem("candle_stick_orange",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_ORANGE.get(), ModBlocks.WALL_CANDLE_STICK_ORANGE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_MAGENTA = ITEMS.registerItem("candle_stick_magenta",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_MAGENTA.get(), ModBlocks.WALL_CANDLE_STICK_MAGENTA.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_LIGHT_BLUE = ITEMS.registerItem("candle_stick_light_blue",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_LIGHT_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_BLUE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_YELLOW = ITEMS.registerItem("candle_stick_yellow",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_YELLOW.get(), ModBlocks.WALL_CANDLE_STICK_YELLOW.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_LIME = ITEMS.registerItem("candle_stick_lime",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_LIME.get(), ModBlocks.WALL_CANDLE_STICK_LIME.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_PINK = ITEMS.registerItem("candle_stick_pink",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_PINK.get(), ModBlocks.WALL_CANDLE_STICK_PINK.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_GRAY = ITEMS.registerItem("candle_stick_gray",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_GRAY.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_LIGHT_GRAY = ITEMS.registerItem("candle_stick_light_gray",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_LIGHT_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_GRAY.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_CYAN = ITEMS.registerItem("candle_stick_cyan",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_CYAN.get(), ModBlocks.WALL_CANDLE_STICK_CYAN.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_PURPLE = ITEMS.registerItem("candle_stick_purple",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_PURPLE.get(), ModBlocks.WALL_CANDLE_STICK_PURPLE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_BLUE = ITEMS.registerItem("candle_stick_blue",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_BLUE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_BROWN = ITEMS.registerItem("candle_stick_brown",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_BROWN.get(), ModBlocks.WALL_CANDLE_STICK_BROWN.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_GREEN = ITEMS.registerItem("candle_stick_green",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_GREEN.get(), ModBlocks.WALL_CANDLE_STICK_GREEN.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_RED = ITEMS.registerItem("candle_stick_red",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_RED.get(), ModBlocks.WALL_CANDLE_STICK_RED.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_BLACK = ITEMS.registerItem("candle_stick_black",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDLE_STICK_BLACK.get(), ModBlocks.WALL_CANDLE_STICK_BLACK.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));

    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA = ITEMS.registerItem("candelabra",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA.get(), ModBlocks.WALL_CANDELABRA.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_NORMAL = ITEMS.registerItem("candelabra_normal",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_NORMAL.get(), ModBlocks.WALL_CANDELABRA_NORMAL.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_WHITE = ITEMS.registerItem("candelabra_white",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_WHITE.get(), ModBlocks.WALL_CANDELABRA_WHITE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_ORANGE = ITEMS.registerItem("candelabra_orange",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_ORANGE.get(), ModBlocks.WALL_CANDELABRA_ORANGE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_MAGENTA = ITEMS.registerItem("candelabra_magenta",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_MAGENTA.get(), ModBlocks.WALL_CANDELABRA_MAGENTA.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_LIGHT_BLUE = ITEMS.registerItem("candelabra_light_blue",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_LIGHT_BLUE.get(), ModBlocks.WALL_CANDELABRA_LIGHT_BLUE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_YELLOW = ITEMS.registerItem("candelabra_yellow",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_YELLOW.get(), ModBlocks.WALL_CANDELABRA_YELLOW.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_LIME = ITEMS.registerItem("candelabra_lime",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_LIME.get(), ModBlocks.WALL_CANDELABRA_LIME.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_PINK = ITEMS.registerItem("candelabra_pink",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_PINK.get(), ModBlocks.WALL_CANDELABRA_PINK.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_GRAY = ITEMS.registerItem("candelabra_gray",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_GRAY.get(), ModBlocks.WALL_CANDELABRA_GRAY.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_LIGHT_GRAY = ITEMS.registerItem("candelabra_light_gray",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_LIGHT_GRAY.get(), ModBlocks.WALL_CANDELABRA_LIGHT_GRAY.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_CYAN = ITEMS.registerItem("candelabra_cyan",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_CYAN.get(), ModBlocks.WALL_CANDELABRA_CYAN.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_PURPLE = ITEMS.registerItem("candelabra_purple",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_PURPLE.get(), ModBlocks.WALL_CANDELABRA_PURPLE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_BLUE = ITEMS.registerItem("candelabra_blue",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_BLUE.get(), ModBlocks.WALL_CANDELABRA_BLUE.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_BROWN = ITEMS.registerItem("candelabra_brown",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_BROWN.get(), ModBlocks.WALL_CANDELABRA_BROWN.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_GREEN = ITEMS.registerItem("candelabra_green",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_GREEN.get(), ModBlocks.WALL_CANDELABRA_GREEN.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_RED = ITEMS.registerItem("candelabra_red",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_RED.get(), ModBlocks.WALL_CANDELABRA_RED.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_BLACK = ITEMS.registerItem("candelabra_black",  props -> new  StandingAndWallBlockItem(ModBlocks.CANDELABRA_BLACK.get(), ModBlocks.WALL_CANDELABRA_BLACK.get(), Direction.DOWN, props.useBlockDescriptionPrefix()));

    // Spawn Eggs
    public static final DeferredItem<SpawnEggItem> VAMPIRE_SPAWN_EGG = ITEMS.registerItem("vampire_spawn_egg",  props -> new SpawnEggItem(ModEntities.VAMPIRE.get(), props));
    public static final DeferredItem<SpawnEggItem> ADVANCED_VAMPIRE_SPAWN_EGG = ITEMS.registerItem("advanced_vampire_spawn_egg",  props -> new SpawnEggItem(ModEntities.ADVANCED_VAMPIRE.get(), props));
    public static final DeferredItem<SpawnEggItem> VAMPIRE_BARON_SPAWN_EGG = ITEMS.registerItem("vampire_baron_spawn_egg",  props -> new SpawnEggItem(ModEntities.VAMPIRE_BARON.get(), props));
    public static final DeferredItem<SpawnEggItem> TASK_MASTER_VAMPIRE_SPAWN_EGG = ITEMS.registerItem("task_master_vampire_spawn_egg",  props -> new SpawnEggItem(ModEntities.TASK_MASTER_VAMPIRE.get(), props));

    public static final DeferredItem<SpawnEggItem> VAMPIRE_HUNTER_SPAWN_EGG = ITEMS.registerItem("vampire_hunter_spawn_egg",  props -> new SpawnEggItem(ModEntities.HUNTER.get(), props));
    public static final DeferredItem<SpawnEggItem> ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG = ITEMS.registerItem("advanced_vampire_hunter_spawn_egg",  props -> new SpawnEggItem(ModEntities.ADVANCED_HUNTER.get(), props));
    public static final DeferredItem<SpawnEggItem> HUNTER_TRAINER_SPAWN_EGG = ITEMS.registerItem("hunter_trainer_spawn_egg",  props -> new SpawnEggItem(ModEntities.HUNTER_TRAINER.get(), props));
    public static final DeferredItem<SpawnEggItem> TASK_MASTER_HUNTER_SPAWN_EGG = ITEMS.registerItem("task_master_hunter_spawn_egg",  props -> new SpawnEggItem(ModEntities.TASK_MASTER_HUNTER.get(), props));

    public static final DeferredItem<SpawnEggItem> GHOST_SPAWN_EGG = ITEMS.registerItem("ghost_spawn_egg",  props -> new SpawnEggItem(ModEntities.GHOST.get(), props));


    @SuppressWarnings("unchecked")
    public static Stream<Holder<Item>> listElements() {
        return ((Collection<Holder<Item>>) (Object) ITEMS.getEntries()).stream();
    }

    static void register(IEventBus bus) {
        ITEMS.register(bus);

        if (VampirismMod.inDataGen) {
            DeferredRegister.Items GUIDEAPI_ITEMS = DeferredRegister.createItems(REFERENCE.GUIDEAPI_MODID);
            DeferredItem<DummyItem> guidebook = GUIDEAPI_ITEMS.registerItem(REFERENCE.GUIDEBOOK_ID, DummyItem::new);
            GUIDEAPI_ITEMS.register(bus);
        }
    }

    public static void registerDispenserBehaviour() {
        DispenserBlock.registerBehavior(ModItems.SYRINGE_EMPTY.get(), new SyringeDispenseBehavior());
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

    public static void registerShiftTooltips(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltipComponents = event.getToolTip();

        Stream<ItemLike> descriptionItems = Stream.of(
                ModBlocks.HUNTER_TABLE,
                ModBlocks.MED_CHAIR,
                ModBlocks.MOTHER_TROPHY,
                ModBlocks.BLOOD_GRINDER,
                ModBlocks.BLOOD_SIEVE,
                FABRIC_FILTER,
                BLOOD_BUCKET,
                SYRINGE_EMPTY,
                SYRINGE_BLOOD,
                INJECTION_GARLIC,
                INJECTION_SANGUINARE
        );

        if (descriptionItems.anyMatch(item -> stack.is(item.asItem()))) {
            DescriptionUtil.addDescriptionTooltip(event.getItemStack().getItem(), event.getToolTip());
        }
    }
}
