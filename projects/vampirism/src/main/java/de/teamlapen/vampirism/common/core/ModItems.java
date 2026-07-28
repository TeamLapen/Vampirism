package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.world.items.IRefinementItem;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.util.BlockDescription;
import de.teamlapen.faction.common.world.items.consume.FactionFoodEntry;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.api.world.items.QuarrelProperties;
import de.teamlapen.vampirism.api.world.items.components.IBottleBlood;
import de.teamlapen.vampirism.common.world.blocks.CoffinBlock;
import de.teamlapen.vampirism.common.world.blocks.candle.CandleHolderBlock;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.*;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import de.teamlapen.vampirism.common.world.items.consume.AffectGarlic;
import de.teamlapen.vampirism.common.world.items.consume.ModConsumables;
import de.teamlapen.vampirism.common.world.items.consume.ModFoods;
import de.teamlapen.vampirism.common.world.items.crossbow.*;
import de.teamlapen.vampirism.common.world.items.crossbow.behavior.*;
import de.teamlapen.vampirism.common.world.items.dispenser.SyringeDispenseBehavior;
import de.teamlapen.vampirism.common.world.items.display.ItemStackWithSize;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Handles all item registrations and reference.
 */
@SuppressWarnings({"unused"})
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(REFERENCE.MODID);
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECTS = DeferredRegister.create(Registries.CONSUME_EFFECT_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<SlotDisplay.Type<?>> SLOT_DISPLAYS = DeferredRegister.create(Registries.SLOT_DISPLAY, REFERENCE.MODID);

    // Consume Effects
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<AffectGarlic>> AFFECT_GARLIC = CONSUME_EFFECTS.register("affect_garlic", () -> new ConsumeEffect.Type<>(AffectGarlic.CODEC, AffectGarlic.STREAM_CODEC));

    // slot display
    public static final DeferredHolder<SlotDisplay.Type<?>, SlotDisplay.Type<ItemStackWithSize>> ITEMSTACK_WITH_SIZE = SLOT_DISPLAYS.register("item_stack_with_size", () -> new SlotDisplay.Type<>(ItemStackWithSize.MAP_CODEC, ItemStackWithSize.STREAM_CODEC));

    // Weapons
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_NORMAL = ITEMS.registerItem("iron_heart_seeker",  props -> new HeartSeekerItem(HeartSeekerItem.IRON, IItemWithTier.Tier.NORMAL, 1.3f, props));
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_ENHANCED = ITEMS.registerItem("diamond_heart_seeker",  props -> new HeartSeekerItem(HeartSeekerItem.DIAMOND, IItemWithTier.Tier.ENHANCED, 1.4f, props));
    public static final DeferredItem<HeartSeekerItem> HEART_SEEKER_ULTIMATE = ITEMS.registerItem("netherite_heart_seeker",  props -> new HeartSeekerItem(HeartSeekerItem.NETHERITE, IItemWithTier.Tier.ULTIMATE, 1.5f, props));

    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_NORMAL = ITEMS.registerItem("iron_heart_striker", props -> new HeartStrikerItem(HeartStrikerItem.IRON, IItemWithTier.Tier.NORMAL,1.25f, props));
    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_ENHANCED = ITEMS.registerItem("diamond_heart_striker",  props -> new HeartStrikerItem(HeartStrikerItem.DIAMOND, IItemWithTier.Tier.ENHANCED, 1.3f, props));
    public static final DeferredItem<HeartStrikerItem> HEART_STRIKER_ULTIMATE = ITEMS.registerItem("netherite_heart_striker",  props -> new HeartStrikerItem(HeartStrikerItem.NETHERITE, IItemWithTier.Tier.ULTIMATE, 1.35f, props));

    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_NORMAL = ITEMS.registerItem("hunter_axe_normal", props -> new HunterAxeItem(HunterAxeItem.NORMAL, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_ENHANCED = ITEMS.registerItem("hunter_axe_enhanced",  props -> new HunterAxeItem(HunterAxeItem.ENHANCED, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterAxeItem> HUNTER_AXE_ULTIMATE = ITEMS.registerItem("hunter_axe_ultimate",  props -> new HunterAxeItem(HunterAxeItem.ULTIMATE, IItemWithTier.Tier.ULTIMATE, props));

    public static final DeferredItem<SingleCrossbowItem> BASIC_CROSSBOW = ITEMS.registerItem("basic_crossbow",  props -> new SingleCrossbowItem(props.durability(465), 1, 20, ToolMaterial.WOOD, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<DoubleCrossbowItem> BASIC_DOUBLE_CROSSBOW = ITEMS.registerItem("basic_double_crossbow",  props -> new DoubleCrossbowItem(props.durability(465), 1, 20, ToolMaterial.WOOD, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<SingleCrossbowItem> ENHANCED_CROSSBOW = ITEMS.registerItem("enhanced_crossbow",  props -> new SingleCrossbowItem(props.durability(930), 1.5F, 15, ToolMaterial.IRON, HunterSkills.WEAPON_TABLE, HunterSkills.MASTER_CRAFTSMANSHIP));
    public static final DeferredItem<DoubleCrossbowItem> ENHANCED_DOUBLE_CROSSBOW = ITEMS.registerItem("enhanced_double_crossbow",  props -> new DoubleCrossbowItem(props.durability(930), 1.5F, 15, ToolMaterial.IRON, HunterSkills.WEAPON_TABLE, HunterSkills.MASTER_CRAFTSMANSHIP));
    public static final DeferredItem<TechCrossbowItem> BASIC_TECH_CROSSBOW = ITEMS.registerItem("basic_tech_crossbow",  props -> new TechCrossbowItem(props.durability(930), 1.6F, 40, ToolMaterial.DIAMOND, HunterSkills.WEAPON_TABLE));
    public static final DeferredItem<TechCrossbowItem> ENHANCED_TECH_CROSSBOW = ITEMS.registerItem("enhanced_tech_crossbow",  props -> new TechCrossbowItem(props.durability(1860), 1.7F, 30, ToolMaterial.DIAMOND, HunterSkills.WEAPON_TABLE, HunterSkills.MASTER_CRAFTSMANSHIP));

    public static final DeferredItem<QuarrelItem> QUARREL_NORMAL = ITEMS.registerItem("quarrel_normal", props -> new QuarrelItem(new QuarrelBehavior(QuarrelProperties.of(0xFFFFFFFF).pickupBehavior(AbstractArrow.Pickup.ALLOWED).baseDamage(2).build()), props.factions$withShiftDescription()));
    public static final DeferredItem<QuarrelItem> QUARREL_HEAVY = ITEMS.registerItem("quarrel_heavy", props -> new QuarrelItem(new QuarrelBehavior(QuarrelProperties.of(0xFF9DA0A8).pickupBehavior(AbstractArrow.Pickup.ALLOWED).baseDamage(2.5f).damageMultiplier(1.6f).knockbackMultiplier(1.4f).velocityFactor(0.8f).gravityFactor(1.5f).inaccuracyFactor(0.5f).extraPierceLevel(2).chargeMultiplier(1.33f).build()), props.factions$withShiftDescription()));
    public static final DeferredItem<QuarrelItem> QUARREL_SPITFIRE = ITEMS.registerItem("quarrel_spitfire", props -> new QuarrelItem(new SpitfireBehavior(), props));
    public static final DeferredItem<QuarrelItem> QUARREL_GARLIC = ITEMS.registerItem("quarrel_garlic", props -> new QuarrelItem(new GarlicBehavior(), props));
    public static final DeferredItem<QuarrelItem> QUARREL_VAMPIRE_KILLER = ITEMS.registerItem("quarrel_vampire_killer", props -> new QuarrelItem(new VampireKillerBehavior(), props));
    public static final DeferredItem<QuarrelItem> QUARREL_TELEPORT = ITEMS.registerItem("quarrel_teleport", props -> new QuarrelItem(new TeleportBehavior(), props));
    public static final DeferredItem<QuarrelItem> QUARREL_BLEEDING = ITEMS.registerItem("quarrel_bleeding", props -> new QuarrelItem(new BleedingBehavior(), props));

    public static final DeferredItem<Item> QUARREL_CLIP = ITEMS.registerItem("quarrel_clip", props -> new Item(props.component(ModDataComponents.CONTAINED_PROJECTILES.get(), new ItemStackTemplate(QUARREL_NORMAL, 16))));
    public static final DeferredItem<Item> HEAVY_QUARREL_CLIP = ITEMS.registerItem("heavy_quarrel_clip", props -> new Item(props.component(ModDataComponents.CONTAINED_PROJECTILES.get(), new ItemStackTemplate(QUARREL_HEAVY, 8))));

    public static final DeferredItem<QuarrelPouchItem> QUARREL_POUCH = ITEMS.registerItem("quarrel_pouch", props -> new QuarrelPouchItem(props.stacksTo(1)));

    public static final DeferredItem<Item> PITCHFORK = ITEMS.registerSimpleItem("pitchfork", props -> props.sword(ToolMaterial.IRON, 6, -3));
    public static final DeferredItem<StakeItem> STAKE = ITEMS.registerItem("stake", props -> new StakeItem(props.factions$withShiftDescription()));

    public static final DeferredItem<CrucifixItem> CRUCIFIX_NORMAL = ITEMS.registerItem("crucifix_normal",  props -> new CrucifixItem(IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<CrucifixItem> CRUCIFIX_ENHANCED = ITEMS.registerItem("crucifix_enhanced",  props -> new CrucifixItem(IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<CrucifixItem> CRUCIFIX_ULTIMATE = ITEMS.registerItem("crucifix_ultimate",  props -> new CrucifixItem(IItemWithTier.Tier.ULTIMATE, props));

    // Armor
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_NORMAL = ITEMS.registerItem("armor_of_swiftness_head_normal",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.HELMET, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ENHANCED = ITEMS.registerItem("armor_of_swiftness_head_enhanced",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.HELMET, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE = ITEMS.registerItem("armor_of_swiftness_head_ultimate",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.HELMET, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_NORMAL = ITEMS.registerItem("armor_of_swiftness_chest_normal", props ->new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.CHESTPLATE, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ENHANCED = ITEMS.registerItem("armor_of_swiftness_chest_enhanced",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.CHESTPLATE, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE = ITEMS.registerItem("armor_of_swiftness_chest_ultimate",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.CHESTPLATE, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_NORMAL = ITEMS.registerItem("armor_of_swiftness_legs_normal",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.LEGGINGS, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ENHANCED = ITEMS.registerItem("armor_of_swiftness_legs_enhanced",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.LEGGINGS, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE = ITEMS.registerItem("armor_of_swiftness_legs_ultimate",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.LEGGINGS, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_NORMAL = ITEMS.registerItem("armor_of_swiftness_feet_normal",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.NORMAL_SWIFTNESS, ArmorType.BOOTS, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ENHANCED = ITEMS.registerItem("armor_of_swiftness_feet_enhanced",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ENHANCED_SWIFTNESS, ArmorType.BOOTS, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<ArmorOfSwiftnessItem> ARMOR_OF_SWIFTNESS_FEET_ULTIMATE = ITEMS.registerItem("armor_of_swiftness_feet_ultimate",  props -> new ArmorOfSwiftnessItem(ModArmorMaterials.ULTIMATE_SWIFTNESS, ArmorType.BOOTS, IItemWithTier.Tier.ULTIMATE, props));

    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_NORMAL = ITEMS.registerItem("hunter_coat_head_normal",  props -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.HELMET, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_ENHANCED = ITEMS.registerItem("hunter_coat_head_enhanced",  props -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.HELMET, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_HEAD_ULTIMATE = ITEMS.registerItem("hunter_coat_head_ultimate",  props -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.HELMET, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_NORMAL = ITEMS.registerItem("hunter_coat_chest_normal", props -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.CHESTPLATE, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_ENHANCED = ITEMS.registerItem("hunter_coat_chest_enhanced",  props -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.CHESTPLATE, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_CHEST_ULTIMATE = ITEMS.registerItem("hunter_coat_chest_ultimate",  props -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.CHESTPLATE, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_NORMAL = ITEMS.registerItem("hunter_coat_legs_normal",  props -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.LEGGINGS, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_ENHANCED = ITEMS.registerItem("hunter_coat_legs_enhanced",  props -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.LEGGINGS, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_LEGS_ULTIMATE = ITEMS.registerItem("hunter_coat_legs_ultimate",  props -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.LEGGINGS, IItemWithTier.Tier.ULTIMATE, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_NORMAL = ITEMS.registerItem("hunter_coat_feet_normal",  props -> new HunterCoatItem(ModArmorMaterials.NORMAL_HUNTER_COAT, ArmorType.BOOTS, IItemWithTier.Tier.NORMAL, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_ENHANCED = ITEMS.registerItem("hunter_coat_feet_enhanced",  props -> new HunterCoatItem(ModArmorMaterials.ENHANCED_HUNTER_COAT, ArmorType.BOOTS, IItemWithTier.Tier.ENHANCED, props));
    public static final DeferredItem<HunterCoatItem> HUNTER_COAT_FEET_ULTIMATE = ITEMS.registerItem("hunter_coat_feet_ultimate",  props -> new HunterCoatItem(ModArmorMaterials.ULTIMATE_HUNTER_COAT, ArmorType.BOOTS, IItemWithTier.Tier.ULTIMATE, props));

    public static final DeferredItem<HunterArmorItem> HUNTER_HAT_TALL = ITEMS.registerItem("hunter_hat_tall",  props -> new HunterArmorItem(ModArmorMaterials.HUNTER_HAT_TALL, ArmorType.HELMET, props));
    public static final DeferredItem<HunterArmorItem> HUNTER_HAT_BROAD = ITEMS.registerItem("hunter_hat_broad",  props -> new HunterArmorItem(ModArmorMaterials.HUNTER_HAT_BROAD, ArmorType.HELMET, props));

    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_HAT = ITEMS.registerItem("vampire_clothing_hat",  props -> new VampireClothingItem(ArmorType.HELMET, ModArmorMaterials.VAMPIRE_CLOTH_HAT, props));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_CROWN = ITEMS.registerItem("vampire_clothing_crown",  props -> new VampireClothingItem(ArmorType.HELMET, ModArmorMaterials.VAMPIRE_CLOTH_CROWN, props));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_LEGS = ITEMS.registerItem("vampire_clothing_legs",  props -> new VampireClothingItem(ArmorType.LEGGINGS, ModArmorMaterials.VAMPIRE_CLOTH_LEGS, props));
    public static final DeferredItem<VampireClothingItem> VAMPIRE_CLOTHING_BOOTS = ITEMS.registerItem("vampire_clothing_boots",  props -> new VampireClothingItem(ArmorType.BOOTS, ModArmorMaterials.VAMPIRE_CLOTH_BOOTS, props));

    public static final DeferredItem<VampireCloakItem> VAMPIRE_CLOAK_WHITE = ITEMS.registerItem("vampire_cloak_white", props -> new VampireCloakItem(DyeColor.WHITE, props));
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

    public static final DeferredItem<RefinementItem> AMULET = ITEMS.registerItem("amulet",  props -> new RefinementItem(FactionRestriction.builder(VampirismTags.Factions.IS_VAMPIRE).message(VampireClothingItem.MASSAGE_RESTRICTION_VAMPIRE_CLOTHING).apply(props), IRefinementItem.AccessorySlotType.AMULET));
    public static final DeferredItem<RefinementItem> RING = ITEMS.registerItem("ring",  props -> new RefinementItem(FactionRestriction.builder(VampirismTags.Factions.IS_VAMPIRE).message(VampireClothingItem.MASSAGE_RESTRICTION_VAMPIRE_CLOTHING).apply(props), IRefinementItem.AccessorySlotType.RING));
    public static final DeferredItem<RefinementItem> OBI_BELT = ITEMS.registerItem("obi_belt",  props -> new RefinementItem(FactionRestriction.builder(VampirismTags.Factions.IS_VAMPIRE).message(VampireClothingItem.MASSAGE_RESTRICTION_VAMPIRE_CLOTHING).apply(props), IRefinementItem.AccessorySlotType.OBI_BELT));

    // General
    public static final DeferredItem<BloodBottleItem> BLOOD_BOTTLE = ITEMS.registerItem("blood_bottle", props -> new BloodBottleItem(props.component(DataComponents.CONSUMABLE, Consumables.defaultDrink().build())));
    public static final DeferredItem<BucketItem> BLOOD_BUCKET = ITEMS.registerItem("blood_bucket",  props -> new BucketItem(ModFluids.BLOOD.get(), props.craftRemainder(Items.BUCKET).stacksTo(1).factions$withShiftDescription()));

    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_RAW_IRON = ITEMS.registerItem("blood_infused_raw_iron", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_RAW_GOLD = ITEMS.registerItem("blood_infused_raw_gold", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_IRON_INGOT = ITEMS.registerItem("blood_infused_iron_ingot", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_GOLD_INGOT = ITEMS.registerItem("blood_infused_gold_ingot", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_DIAMOND = ITEMS.registerItem("blood_infused_diamond", PureLevelItem::new);
    public static final DeferredItem<PureLevelItem> BLOOD_INFUSED_NETHERITE_INGOT = ITEMS.registerItem("blood_infused_netherite_ingot", PureLevelItem::new);

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

    public static final DeferredItem<Item> GARLIC_BREAD = ITEMS.registerItem("garlic_bread", props -> new Item(props.factions$factionFood(ModFoods.GARLIC_BREAD, ModConsumables.GARLIC)));
    public static final DeferredItem<Item> HUMAN_HEART = ITEMS.registerItem("human_heart", props -> new Item(props.factions$factionFood(ModFoods.HUMAN_HEART, ModConsumables.NASTY_NON_VAMPIRES)));
    public static final DeferredItem<Item> WEAK_HUMAN_HEART = ITEMS.registerItem("weak_human_heart",  props -> new Item(props.factions$factionFood(ModFoods.WEAK_HUMAN_HEART, ModConsumables.NASTY_NON_VAMPIRES)));

    public static final DeferredItem<SyringeItem> SYRINGE_EMPTY = ITEMS.registerItem("syringe_empty", x -> new SyringeItem(x.factions$withShiftDescription()));
    public static final DeferredItem<Item> SYRINGE_BLOOD = ITEMS.registerItem("syringe_blood", x -> new Item(x.factions$withShiftDescription()), props -> props.stacksTo(16).craftRemainder(SYRINGE_EMPTY.get()).factions$factionFood(new FactionFoodList(new FoodProperties.Builder().build(), new FactionFoodEntry(VampirismTags.Factions.IS_VAMPIRE, new FoodProperties.Builder().nutrition(BloodSyringeFluidHandler.CAPACITY / IBottleBlood.MULTIPLIER).saturationModifier(0.8F).build(), ModFoodBehaviours.VAMPIRE_FOOD)), Consumables.defaultDrink().build()));
    public static final DeferredItem<GarlicInjectionItem> INJECTION_GARLIC = ITEMS.registerItem("injection_garlic", x -> new GarlicInjectionItem(x.factions$withShiftDescription()), props -> props.stacksTo(16).craftRemainder(SYRINGE_EMPTY.get()));
    public static final DeferredItem<SanguinareInjectionItem> INJECTION_SANGUINARE = ITEMS.registerItem("injection_sanguinare", x -> new SanguinareInjectionItem(x.factions$withShiftDescription()), props -> props.stacksTo(16).craftRemainder(SYRINGE_EMPTY.get()));
    public static final DeferredItem<SerumInjectionItem> SERUM_INJECTION = ITEMS.registerItem("serum_injection", SerumInjectionItem::new, props -> props.component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).component(DataComponents.POTION_DURATION_SCALE, 0.25F).stacksTo(4).craftRemainder(SYRINGE_EMPTY.get()).useCooldown(8));

    public static final DeferredItem<AlchemicalFireItem> ALCHEMICAL_FIRE = ITEMS.registerItem("alchemical_fire", props -> new AlchemicalFireItem(props.useBlockDescriptionPrefix()));

    public static final DeferredItem<Item> PURIFIED_GARLIC = ITEMS.registerItem("purified_garlic",  Item::new, props -> props.stacksTo(16));
    public static final DeferredItem<Item> PURE_SALT = ITEMS.registerItem("pure_salt", Item::new);
    public static final DeferredItem<BlessableItem> PURE_SALT_WATER = ITEMS.registerItem("pure_salt_water",  props -> new BlessableItem(props.stacksTo(1).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true), HOLY_WATER_BOTTLE_NORMAL, HOLY_WATER_BOTTLE_ENHANCED));

    public static final DeferredItem<Item> SOUL_ORB_VAMPIRE = ITEMS.registerItem("soul_orb_vampire", Item::new);
    public static final DeferredItem<Item> MOTHER_CORE = ITEMS.registerItem("mother_core",  Item::new, props -> props.rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> VAMPIRE_BLOOD_BOTTLE = ITEMS.registerItem("vampire_blood_bottle", props -> new Item(props.factions$withShiftDescription()));
    public static final DeferredItem<VampireBookItem> VAMPIRE_BOOK = ITEMS.registerItem("vampire_book", VampireBookItem::new, props -> props.rarity(Rarity.UNCOMMON).stacksTo(1));
    public static final DeferredItem<VampireFangItem> VAMPIRE_FANG = ITEMS.registerItem("vampire_fang", VampireFangItem::new);

    public static final DeferredItem<UmbrellaItem> UMBRELLA = ITEMS.registerItem("umbrella", UmbrellaItem::new, props -> props.stacksTo(1));

    public static final DeferredItem<Item> VAMPIRE_MINION_BINDING = ITEMS.registerItem("vampire_minion_binding", Item::new);
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SIMPLE = ITEMS.registerItem("vampire_minion_upgrade_simple",  props -> new MinionUpgradeItem(1, 2, ModFactions.VAMPIRE, props));
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_ENHANCED = ITEMS.registerItem("vampire_minion_upgrade_enhanced",  props -> new MinionUpgradeItem(3, 4, ModFactions.VAMPIRE, props));
    public static final DeferredItem<MinionUpgradeItem> VAMPIRE_MINION_UPGRADE_SPECIAL = ITEMS.registerItem("vampire_minion_upgrade_special",  props -> new MinionUpgradeItem(5, 6, ModFactions.VAMPIRE, props));

    public static final DeferredItem<Item> HUNTER_MINION_EQUIPMENT = ITEMS.registerItem("hunter_minion_equipment", Item::new);
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SIMPLE = ITEMS.registerItem("hunter_minion_upgrade_simple",  props -> new MinionUpgradeItem(1, 2, ModFactions.HUNTER, props));
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_ENHANCED = ITEMS.registerItem("hunter_minion_upgrade_enhanced",  props -> new MinionUpgradeItem(3, 4, ModFactions.HUNTER, props));
    public static final DeferredItem<MinionUpgradeItem> HUNTER_MINION_UPGRADE_SPECIAL = ITEMS.registerItem("hunter_minion_upgrade_special",  props -> new MinionUpgradeItem(5, 6, ModFactions.HUNTER, props));

    public static final DeferredItem<Item> FABRIC_FILTER = ITEMS.registerItem("fabric_filter", x ->  new Item(x.factions$withShiftDescription()) ,props -> props.stacksTo(1).durability(4800));

    public static final DeferredItem<FeedingAdapterItem> FEEDING_ADAPTER = ITEMS.registerItem("feeding_adapter", FeedingAdapterItem::new, props -> props.stacksTo(1));
    public static final DeferredItem<Item> GARLIC_FINDER = ITEMS.registerItem("garlic_finder", x ->  new Item(x.factions$withShiftDescription()));

    public static final DeferredItem<OilBottleItem> OIL_BOTTLE = ITEMS.registerItem("oil_bottle",  OilBottleItem::new, props -> props.stacksTo(1));

    public static final DeferredItem<BoatItem> DARK_SPRUCE_BOAT = ITEMS.registerItem("dark_spruce_boat",  props -> new BoatItem(ModEntities.DARK_SPRUCE_BOAT.get(), props.stacksTo(1)));
    public static final DeferredItem<BoatItem> CURSED_SPRUCE_BOAT = ITEMS.registerItem("cursed_spruce_boat",  props -> new BoatItem(ModEntities.CURSED_SPRUCE_BOAT.get(), props.stacksTo(1)));
    public static final DeferredItem<BoatItem> DARK_SPRUCE_CHEST_BOAT = ITEMS.registerItem("dark_spruce_chest_boat",  props -> new BoatItem(ModEntities.DARK_SPRUCE_CHEST_BOAT.get(), props.stacksTo(1)));
    public static final DeferredItem<BoatItem> CURSED_SPRUCE_CHEST_BOAT = ITEMS.registerItem("cursed_spruce_chest_boat",  props -> new BoatItem(ModEntities.CURSED_SPRUCE_CHEST_BOAT.get(), props.stacksTo(1)));

    public static final DeferredItem<SignItem> DARK_SPRUCE_SIGN = ITEMS.registerItem("dark_spruce_sign",  props -> new SignItem(ModBlocks.DARK_SPRUCE_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_SIGN.get(), props.useBlockDescriptionPrefix().stacksTo(16)));
    public static final DeferredItem<SignItem> CURSED_SPRUCE_SIGN = ITEMS.registerItem("cursed_spruce_sign",  props -> new SignItem(ModBlocks.CURSED_SPRUCE_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_SIGN.get(), props.useBlockDescriptionPrefix().stacksTo(16)));
    public static final DeferredItem<HangingSignItem> DARK_SPRUCE_HANGING_SIGN = ITEMS.registerItem("dark_spruce_hanging_sign",  props -> new HangingSignItem(ModBlocks.DARK_SPRUCE_HANGING_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get(), props.useBlockDescriptionPrefix().stacksTo(16)));
    public static final DeferredItem<HangingSignItem> CURSED_SPRUCE_HANGING_SIGN = ITEMS.registerItem("cursed_spruce_hanging_sign",  props -> new HangingSignItem(ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get(), props.useBlockDescriptionPrefix().stacksTo(16)));

    public static final DeferredItem<TentItem> ITEM_TENT = ITEMS.registerItem("item_tent", props -> new TentItem(false, props));
    public static final DeferredItem<TentItem> ITEM_TENT_SPAWNER = ITEMS.registerItem("item_tent_spawner", props -> new TentItem(true, props));

    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK = fromCandleHolder(ModBlocks.CANDLE_STICK, ModBlocks.WALL_CANDLE_STICK);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_NORMAL = fromCandleHolder(ModBlocks.CANDLE_STICK_NORMAL, ModBlocks.WALL_CANDLE_STICK_NORMAL);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_WHITE = fromCandleHolder(ModBlocks.CANDLE_STICK_WHITE, ModBlocks.WALL_CANDLE_STICK_WHITE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_ORANGE = fromCandleHolder(ModBlocks.CANDLE_STICK_ORANGE, ModBlocks.WALL_CANDLE_STICK_ORANGE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_MAGENTA = fromCandleHolder(ModBlocks.CANDLE_STICK_MAGENTA, ModBlocks.WALL_CANDLE_STICK_MAGENTA);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_LIGHT_BLUE = fromCandleHolder(ModBlocks.CANDLE_STICK_LIGHT_BLUE, ModBlocks.WALL_CANDLE_STICK_LIGHT_BLUE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_YELLOW = fromCandleHolder(ModBlocks.CANDLE_STICK_YELLOW, ModBlocks.WALL_CANDLE_STICK_YELLOW);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_LIME = fromCandleHolder(ModBlocks.CANDLE_STICK_LIME, ModBlocks.WALL_CANDLE_STICK_LIME);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_PINK = fromCandleHolder(ModBlocks.CANDLE_STICK_PINK, ModBlocks.WALL_CANDLE_STICK_PINK);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_GRAY = fromCandleHolder(ModBlocks.CANDLE_STICK_GRAY, ModBlocks.WALL_CANDLE_STICK_GRAY);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_LIGHT_GRAY = fromCandleHolder(ModBlocks.CANDLE_STICK_LIGHT_GRAY, ModBlocks.WALL_CANDLE_STICK_LIGHT_GRAY);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_CYAN = fromCandleHolder(ModBlocks.CANDLE_STICK_CYAN, ModBlocks.WALL_CANDLE_STICK_CYAN);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_PURPLE = fromCandleHolder(ModBlocks.CANDLE_STICK_PURPLE, ModBlocks.WALL_CANDLE_STICK_PURPLE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_BLUE = fromCandleHolder(ModBlocks.CANDLE_STICK_BLUE, ModBlocks.WALL_CANDLE_STICK_BLUE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_BROWN = fromCandleHolder(ModBlocks.CANDLE_STICK_BROWN, ModBlocks.WALL_CANDLE_STICK_BROWN);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_GREEN = fromCandleHolder(ModBlocks.CANDLE_STICK_GREEN, ModBlocks.WALL_CANDLE_STICK_GREEN);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_RED = fromCandleHolder(ModBlocks.CANDLE_STICK_RED, ModBlocks.WALL_CANDLE_STICK_RED);
    public static final DeferredItem<StandingAndWallBlockItem> CANDLE_STICK_BLACK = fromCandleHolder(ModBlocks.CANDLE_STICK_BLACK, ModBlocks.WALL_CANDLE_STICK_BLACK);

    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA = fromCandleHolder(ModBlocks.CANDELABRA, ModBlocks.WALL_CANDELABRA);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_NORMAL = fromCandleHolder(ModBlocks.CANDELABRA_NORMAL, ModBlocks.WALL_CANDELABRA_NORMAL);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_WHITE = fromCandleHolder(ModBlocks.CANDELABRA_WHITE, ModBlocks.WALL_CANDELABRA_WHITE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_ORANGE = fromCandleHolder(ModBlocks.CANDELABRA_ORANGE, ModBlocks.WALL_CANDELABRA_ORANGE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_MAGENTA = fromCandleHolder(ModBlocks.CANDELABRA_MAGENTA, ModBlocks.WALL_CANDELABRA_MAGENTA);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_LIGHT_BLUE = fromCandleHolder(ModBlocks.CANDELABRA_LIGHT_BLUE, ModBlocks.WALL_CANDELABRA_LIGHT_BLUE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_YELLOW = fromCandleHolder(ModBlocks.CANDELABRA_YELLOW, ModBlocks.WALL_CANDELABRA_YELLOW);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_LIME = fromCandleHolder(ModBlocks.CANDELABRA_LIME, ModBlocks.WALL_CANDELABRA_LIME);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_PINK = fromCandleHolder(ModBlocks.CANDELABRA_PINK, ModBlocks.WALL_CANDELABRA_PINK);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_GRAY = fromCandleHolder(ModBlocks.CANDELABRA_GRAY, ModBlocks.WALL_CANDELABRA_GRAY);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_LIGHT_GRAY = fromCandleHolder(ModBlocks.CANDELABRA_LIGHT_GRAY, ModBlocks.WALL_CANDELABRA_LIGHT_GRAY);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_CYAN = fromCandleHolder(ModBlocks.CANDELABRA_CYAN, ModBlocks.WALL_CANDELABRA_CYAN);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_PURPLE = fromCandleHolder(ModBlocks.CANDELABRA_PURPLE, ModBlocks.WALL_CANDELABRA_PURPLE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_BLUE = fromCandleHolder(ModBlocks.CANDELABRA_BLUE, ModBlocks.WALL_CANDELABRA_BLUE);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_BROWN = fromCandleHolder(ModBlocks.CANDELABRA_BROWN, ModBlocks.WALL_CANDELABRA_BROWN);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_GREEN = fromCandleHolder(ModBlocks.CANDELABRA_GREEN, ModBlocks.WALL_CANDELABRA_GREEN);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_RED = fromCandleHolder(ModBlocks.CANDELABRA_RED, ModBlocks.WALL_CANDELABRA_RED);
    public static final DeferredItem<StandingAndWallBlockItem> CANDELABRA_BLACK = fromCandleHolder(ModBlocks.CANDELABRA_BLACK, ModBlocks.WALL_CANDELABRA_BLACK);

    // Spawn Eggs
    public static final DeferredItem<SpawnEggItem> VAMPIRE_SPAWN_EGG = ITEMS.registerItem("vampire_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntities.VAMPIRE.get()));
    public static final DeferredItem<SpawnEggItem> ADVANCED_VAMPIRE_SPAWN_EGG = ITEMS.registerItem("advanced_vampire_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntities.ADVANCED_VAMPIRE.get()));
    public static final DeferredItem<SpawnEggItem> VAMPIRE_BARON_SPAWN_EGG = ITEMS.registerItem("vampire_baron_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntities.VAMPIRE_BARON.get()));
    public static final DeferredItem<SpawnEggItem> TASK_MASTER_VAMPIRE_SPAWN_EGG = ITEMS.registerItem("task_master_vampire_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntities.TASK_MASTER_VAMPIRE.get()));
    public static final DeferredItem<SpawnEggItem> VAMPIRE_HUNTER_SPAWN_EGG = ITEMS.registerItem("vampire_hunter_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntities.HUNTER.get()));
    public static final DeferredItem<SpawnEggItem> ADVANCED_VAMPIRE_HUNTER_SPAWN_EGG = ITEMS.registerItem("advanced_vampire_hunter_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntities.ADVANCED_HUNTER.get()));
    public static final DeferredItem<SpawnEggItem> HUNTER_TRAINER_SPAWN_EGG = ITEMS.registerItem("hunter_trainer_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntities.HUNTER_TRAINER.get()));
    public static final DeferredItem<SpawnEggItem> TASK_MASTER_HUNTER_SPAWN_EGG = ITEMS.registerItem("task_master_hunter_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntities.TASK_MASTER_HUNTER.get()));
    public static final DeferredItem<SpawnEggItem> GHOST_SPAWN_EGG = ITEMS.registerItem("ghost_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(ModEntities.GHOST.get()));
    public static final DeferredItem<Item> RITUAL_KNIFE = ITEMS.registerItem("ritual_knife", RitualKnifeItem::new);

    public static final DeferredItem<BlockItem> GARLIC_DIFFUSER_CORE = fromBlock(ModBlocks.GARLIC_DIFFUSER_CORE, x -> x.useBlockDescriptionPrefix().factions$withShiftDescription());
    public static final DeferredItem<BlockItem> GARLIC_DIFFUSER_CORE_STRONG = fromBlock(ModBlocks.GARLIC_DIFFUSER_CORE_STRONG, x -> x.factions$withShiftDescription(Component.translatable("tooltip.vampirism.garlic_diffuser_core")));
    public static final DeferredItem<BlockItem> GARLIC_DIFFUSER_CORE_LONG = fromBlock(ModBlocks.GARLIC_DIFFUSER_CORE_LONG, x -> x.factions$withShiftDescription(Component.translatable("tooltip.vampirism.garlic_diffuser_core")));
    public static final DeferredItem<PureLevelBlockItem> BLOOD_INFUSED_IRON_BLOCK = fromBlock(ModBlocks.BLOOD_INFUSED_IRON_BLOCK, (block, itemProps) -> new PureLevelBlockItem(block, itemProps.component(ModDataComponents.PURE_LEVEL, PureLevel.LOW)));
    public static final DeferredItem<PureLevelBlockItem> BLOOD_INFUSED_ENHANCED_IRON_BLOCK = fromBlock(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK,  (block, itemProps) -> new PureLevelBlockItem(block, itemProps.component(ModDataComponents.PURE_LEVEL, new PureLevel(4))));
    public static final DeferredItem<BlockItem> ALTAR_INSPIRATION = fromBlock(ModBlocks.ALTAR_INSPIRATION);
    public static final DeferredItem<BlockItem> ALTAR_INFUSION = fromBlock(ModBlocks.ALTAR_INFUSION);
    public static final DeferredItem<BlockItem> ALTAR_PILLAR = fromBlock(ModBlocks.ALTAR_PILLAR);
    public static final DeferredItem<BlockItem> ALTAR_TIP = fromBlock(ModBlocks.ALTAR_TIP);
    public static final DeferredItem<BlockItem> BLOOD_PEDESTAL = fromBlock(ModBlocks.BLOOD_PEDESTAL);
    public static final DeferredItem<BloodContainerItem> BLOOD_CONTAINER = fromBlock(ModBlocks.BLOOD_CONTAINER, BloodContainerItem::new);
    public static final DeferredItem<BlockItem> BLOOD_GRINDER = fromBlock(ModBlocks.BLOOD_GRINDER, x -> x.factions$withShiftDescription());
    public static final DeferredItem<BlockItem> BLOOD_SIEVE = fromBlock(ModBlocks.BLOOD_SIEVE, x -> x.factions$withShiftDescription());
    public static final DeferredItem<BlockItem> INFUSER = fromBlock(ModBlocks.INFUSER);
    public static final DeferredItem<BlockItem> FOG_DIFFUSER = fromBlock(ModBlocks.FOG_DIFFUSER);
    public static final DeferredItem<BlockItem> SUNSCREEN_BEACON = fromBlock(ModBlocks.SUNSCREEN_BEACON, itemProps -> itemProps.rarity(Rarity.RARE).component(FactionDataComponents.BLOCK_DESCRIPTION, BlockDescription.INSTANCE));
    public static final DeferredItem<BlockItem> HUNTER_TABLE = fromBlock(ModBlocks.HUNTER_TABLE, x -> x.factions$withShiftDescription());
    public static final DeferredItem<BlockItem> WEAPON_TABLE = fromBlock(ModBlocks.WEAPON_TABLE);
    public static final DeferredItem<BlockItem> ALCHEMICAL_CAULDRON = fromBlock(ModBlocks.ALCHEMICAL_CAULDRON);
    public static final DeferredItem<BlockItem> VAPOR_STILL = fromBlock(ModBlocks.VAPOR_STILL, x -> x.factions$withShiftDescription());
    public static final DeferredItem<BlockItem> ALCHEMY_TABLE = fromBlock(ModBlocks.ALCHEMY_TABLE);
    public static final DeferredItem<BlockItem> INJECTION_CHAIR = fromBlock(ModBlocks.INJECTION_CHAIR, props -> props.factions$withShiftDescription());
    public static final DeferredItem<BlockItem> ALTAR_CLEANSING = fromBlock(ModBlocks.ALTAR_CLEANSING);
    public static final DeferredItem<BlockItem> GARLIC_DIFFUSER_NORMAL = fromBlock(ModBlocks.GARLIC_DIFFUSER_NORMAL, (item) -> item.factions$withShiftDescription());
    public static final DeferredItem<BlockItem> GARLIC_DIFFUSER_WEAK = fromBlock(ModBlocks.GARLIC_DIFFUSER_LONG, (item) -> item.factions$withShiftDescription());
    public static final DeferredItem<BlockItem> GARLIC_DIFFUSER_IMPROVED = fromBlock(ModBlocks.GARLIC_DIFFUSER_STRONG, (item) -> item.factions$withShiftDescription());
    public static final DeferredItem<BlockItem> VAMPIRE_BEACON = fromBlock(ModBlocks.VAMPIRE_BEACON, itemProps -> itemProps.rarity(Rarity.RARE));
    public static final DeferredItem<BlockItem> DARK_SPRUCE_LEAVES = fromBlock(ModBlocks.DARK_SPRUCE_LEAVES);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_SAPLING = fromBlock(ModBlocks.DARK_SPRUCE_SAPLING);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_SAPLING = fromBlock(ModBlocks.CURSED_SPRUCE_SAPLING);
    public static final DeferredItem<BlockItem> VAMPIRE_ORCHID = fromBlock(ModBlocks.VAMPIRE_ORCHID);
    public static final DeferredItem<BlockItem> CURSED_ROOTS = fromBlock(ModBlocks.CURSED_ROOTS);
    public static final DeferredItem<BlockItem> CURSED_HANGING_ROOTS = fromBlock(ModBlocks.CURSED_HANGING_ROOTS);
    public static final DeferredItem<BlockItem> DIRECT_CURSED_BARK = fromBlock(ModBlocks.DIRECT_CURSED_BARK);
    public static final DeferredItem<BlockItem> GARLIC = fromBlock(ModBlocks.GARLIC, GarlicItem::new);
    public static final DeferredItem<BlockItem> CURSED_GRASS = fromBlock(ModBlocks.CURSED_GRASS);
    public static final DeferredItem<BlockItem> CURSED_EARTH = fromBlock(ModBlocks.CURSED_EARTH);
    public static final DeferredItem<BlockItem> CURSED_EARTH_PATH = fromBlock(ModBlocks.CURSED_EARTH_PATH);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_LOG = fromBlock(ModBlocks.DARK_SPRUCE_LOG);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_WOOD = fromBlock(ModBlocks.DARK_SPRUCE_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_DARK_SPRUCE_LOG = fromBlock(ModBlocks.STRIPPED_DARK_SPRUCE_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_DARK_SPRUCE_WOOD = fromBlock(ModBlocks.STRIPPED_DARK_SPRUCE_WOOD);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_PLANKS = fromBlock(ModBlocks.DARK_SPRUCE_PLANKS);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_STAIRS = fromBlock(ModBlocks.DARK_SPRUCE_STAIRS);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_SLAB = fromBlock(ModBlocks.DARK_SPRUCE_SLAB);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_FENCE = fromBlock(ModBlocks.DARK_SPRUCE_FENCE);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_FENCE_GATE = fromBlock(ModBlocks.DARK_SPRUCE_FENCE_GATE);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_DOOR = fromBlock(ModBlocks.DARK_SPRUCE_DOOR);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_TRAPDOOR = fromBlock(ModBlocks.DARK_SPRUCE_TRAPDOOR);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_PRESSURE_PLACE = fromBlock(ModBlocks.DARK_SPRUCE_PRESSURE_PLACE);
    public static final DeferredItem<BlockItem> DARK_SPRUCE_BUTTON = fromBlock(ModBlocks.DARK_SPRUCE_BUTTON);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_LOG = fromBlock(ModBlocks.CURSED_SPRUCE_LOG, CursedSpruceItem::new);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_WOOD = fromBlock(ModBlocks.CURSED_SPRUCE_WOOD, CursedSpruceItem::new);
    public static final DeferredItem<BlockItem> STRIPPED_CURSED_SPRUCE_LOG = fromBlock(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_CURSED_SPRUCE_WOOD = fromBlock(ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_PLANKS = fromBlock(ModBlocks.CURSED_SPRUCE_PLANKS);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_STAIRS = fromBlock(ModBlocks.CURSED_SPRUCE_STAIRS);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_SLAB = fromBlock(ModBlocks.CURSED_SPRUCE_SLAB);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_FENCE = fromBlock(ModBlocks.CURSED_SPRUCE_FENCE);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_FENCE_GATE = fromBlock(ModBlocks.CURSED_SPRUCE_FENCE_GATE);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_DOOR = fromBlock(ModBlocks.CURSED_SPRUCE_DOOR);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_TRAPDOOR = fromBlock(ModBlocks.CURSED_SPRUCE_TRAPDOOR);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_PRESSURE_PLACE = fromBlock(ModBlocks.CURSED_SPRUCE_PRESSURE_PLACE);
    public static final DeferredItem<BlockItem> CURSED_SPRUCE_BUTTON = fromBlock(ModBlocks.CURSED_SPRUCE_BUTTON);
    public static final DeferredItem<BlockItem> DARK_STONE = fromBlock(ModBlocks.DARK_STONE);
    public static final DeferredItem<BlockItem> DARK_STONE_STAIRS = fromBlock(ModBlocks.DARK_STONE_STAIRS);
    public static final DeferredItem<BlockItem> DARK_STONE_SLAB = fromBlock(ModBlocks.DARK_STONE_SLAB);
    public static final DeferredItem<BlockItem> DARK_STONE_WALL = fromBlock(ModBlocks.DARK_STONE_WALL);
    public static final DeferredItem<BlockItem> INFESTED_DARK_STONE = fromBlock(ModBlocks.INFESTED_DARK_STONE);
    public static final DeferredItem<BlockItem> DARK_STONE_BRICKS = fromBlock(ModBlocks.DARK_STONE_BRICKS);
    public static final DeferredItem<BlockItem> DARK_STONE_BRICK_STAIRS = fromBlock(ModBlocks.DARK_STONE_BRICK_STAIRS);
    public static final DeferredItem<BlockItem> DARK_STONE_BRICK_SLAB = fromBlock(ModBlocks.DARK_STONE_BRICK_SLAB);
    public static final DeferredItem<BlockItem> DARK_STONE_BRICK_WALL = fromBlock(ModBlocks.DARK_STONE_BRICK_WALL);
    public static final DeferredItem<BlockItem> CRACKED_DARK_STONE_BRICKS = fromBlock(ModBlocks.CRACKED_DARK_STONE_BRICKS);
    public static final DeferredItem<BlockItem> CHISELED_DARK_STONE_BRICKS = fromBlock(ModBlocks.CHISELED_DARK_STONE_BRICKS);
    public static final DeferredItem<BlockItem> BLOODY_DARK_STONE_BRICKS = fromBlock(ModBlocks.BLOODY_DARK_STONE_BRICKS);
    public static final DeferredItem<BlockItem> COBBLED_DARK_STONE = fromBlock(ModBlocks.COBBLED_DARK_STONE);
    public static final DeferredItem<BlockItem> COBBLED_DARK_STONE_STAIRS = fromBlock(ModBlocks.COBBLED_DARK_STONE_STAIRS);
    public static final DeferredItem<BlockItem> COBBLED_DARK_STONE_SLAB = fromBlock(ModBlocks.COBBLED_DARK_STONE_SLAB);
    public static final DeferredItem<BlockItem> COBBLED_DARK_STONE_WALL = fromBlock(ModBlocks.COBBLED_DARK_STONE_WALL);
    public static final DeferredItem<BlockItem> POLISHED_DARK_STONE = fromBlock(ModBlocks.POLISHED_DARK_STONE);
    public static final DeferredItem<BlockItem> POLISHED_DARK_STONE_STAIRS = fromBlock(ModBlocks.POLISHED_DARK_STONE_STAIRS);
    public static final DeferredItem<BlockItem> POLISHED_DARK_STONE_SLAB = fromBlock(ModBlocks.POLISHED_DARK_STONE_SLAB);
    public static final DeferredItem<BlockItem> POLISHED_DARK_STONE_WALL = fromBlock(ModBlocks.POLISHED_DARK_STONE_WALL);
    public static final DeferredItem<BlockItem> DARK_STONE_TILES = fromBlock(ModBlocks.DARK_STONE_TILES);
    public static final DeferredItem<BlockItem> DARK_STONE_TILES_STAIRS = fromBlock(ModBlocks.DARK_STONE_TILES_STAIRS);
    public static final DeferredItem<BlockItem> DARK_STONE_TILES_SLAB = fromBlock(ModBlocks.DARK_STONE_TILES_SLAB);
    public static final DeferredItem<BlockItem> DARK_STONE_TILES_WALL = fromBlock(ModBlocks.DARK_STONE_TILES_WALL);
    public static final DeferredItem<BlockItem> CRACKED_DARK_STONE_TILES = fromBlock(ModBlocks.CRACKED_DARK_STONE_TILES);
    public static final DeferredItem<BlockItem> PURPLE_STONE_BRICKS = fromBlock(ModBlocks.PURPLE_STONE_BRICKS);
    public static final DeferredItem<BlockItem> PURPLE_STONE_BRICK_STAIRS = fromBlock(ModBlocks.PURPLE_STONE_BRICK_STAIRS);
    public static final DeferredItem<BlockItem> PURPLE_STONE_BRICK_SLAB = fromBlock(ModBlocks.PURPLE_STONE_BRICK_SLAB);
    public static final DeferredItem<BlockItem> PURPLE_STONE_BRICK_WALL = fromBlock(ModBlocks.PURPLE_STONE_BRICK_WALL);
    public static final DeferredItem<BlockItem> PURPLE_STONE_TILES = fromBlock(ModBlocks.PURPLE_STONE_TILES);
    public static final DeferredItem<BlockItem> PURPLE_STONE_TILES_STAIRS = fromBlock(ModBlocks.PURPLE_STONE_TILES_STAIRS);
    public static final DeferredItem<BlockItem> PURPLE_STONE_TILES_SLAB = fromBlock(ModBlocks.PURPLE_STONE_TILES_SLAB);
    public static final DeferredItem<BlockItem> PURPLE_STONE_TILES_WALL = fromBlock(ModBlocks.PURPLE_STONE_TILES_WALL);
    public static final DeferredItem<BlockItem> VAMPIRE_SOUL_LANTERN = fromBlock(ModBlocks.VAMPIRE_SOUL_LANTERN);
    public static final DeferredItem<BlockItem> CROSS = fromBlock(ModBlocks.CROSS);
    public static final DeferredItem<BlockItem> TOMBSTONE_SHORT = fromBlock(ModBlocks.TOMBSTONE_SHORT);
    public static final DeferredItem<BlockItem> TOMBSTONE_MEDIUM = fromBlock(ModBlocks.TOMBSTONE_MEDIUM);
    public static final DeferredItem<BlockItem> TOMBSTONE_CROSS = fromBlock(ModBlocks.TOMBSTONE_CROSS);
    public static final DeferredItem<BlockItem> GRAVE_CAGE = fromBlock(ModBlocks.GRAVE_CAGE);
    public static final DeferredItem<BlockItem> VAMPIRE_RACK = fromBlock(ModBlocks.VAMPIRE_RACK);
    public static final DeferredItem<BlockItem> THRONE = fromBlock(ModBlocks.THRONE);
    public static final DeferredItem<BlockItem> BAT_CAGE = fromBlock(ModBlocks.BAT_CAGE, BatCageItem::new);
    public static final DeferredItem<BlockItem> MOTHER_TROPHY = fromBlock(ModBlocks.MOTHER_TROPHY, itemProps -> itemProps.factions$withShiftDescription().rarity(Rarity.EPIC).stacksTo(1));
    public static final DeferredItem<CoffinItem> COFFIN_WHITE = fromCoffin(ModBlocks.COFFIN_WHITE);
    public static final DeferredItem<CoffinItem> COFFIN_ORANGE = fromCoffin(ModBlocks.COFFIN_ORANGE);
    public static final DeferredItem<CoffinItem> COFFIN_MAGENTA = fromCoffin(ModBlocks.COFFIN_MAGENTA);
    public static final DeferredItem<CoffinItem> COFFIN_YELLOW = fromCoffin(ModBlocks.COFFIN_YELLOW);
    public static final DeferredItem<CoffinItem> COFFIN_LIME = fromCoffin(ModBlocks.COFFIN_LIME);
    public static final DeferredItem<CoffinItem> COFFIN_PINK = fromCoffin(ModBlocks.COFFIN_PINK);
    public static final DeferredItem<CoffinItem> COFFIN_GRAY = fromCoffin(ModBlocks.COFFIN_GRAY);
    public static final DeferredItem<CoffinItem> COFFIN_LIGHT_GRAY = fromCoffin(ModBlocks.COFFIN_LIGHT_GRAY);
    public static final DeferredItem<CoffinItem> COFFIN_CYAN = fromCoffin(ModBlocks.COFFIN_CYAN);
    public static final DeferredItem<CoffinItem> COFFIN_LIGHT_BLUE = fromCoffin(ModBlocks.COFFIN_LIGHT_BLUE);
    public static final DeferredItem<CoffinItem> COFFIN_PURPLE = fromCoffin(ModBlocks.COFFIN_PURPLE);
    public static final DeferredItem<CoffinItem> COFFIN_BLUE = fromCoffin(ModBlocks.COFFIN_BLUE);
    public static final DeferredItem<CoffinItem> COFFIN_BROWN = fromCoffin(ModBlocks.COFFIN_BROWN);
    public static final DeferredItem<CoffinItem> COFFIN_GREEN = fromCoffin(ModBlocks.COFFIN_GREEN);
    public static final DeferredItem<CoffinItem> COFFIN_RED = fromCoffin(ModBlocks.COFFIN_RED);
    public static final DeferredItem<CoffinItem> COFFIN_BLACK = fromCoffin(ModBlocks.COFFIN_BLACK);
    public static final DeferredItem<BlockItem> CHANDELIER = fromChandelier(ModBlocks.CHANDELIER);
    public static final DeferredItem<BlockItem> CHANDELIER_NORMAL = fromChandelier(ModBlocks.CHANDELIER_NORMAL);
    public static final DeferredItem<BlockItem> CHANDELIER_WHITE = fromChandelier(ModBlocks.CHANDELIER_WHITE);
    public static final DeferredItem<BlockItem> CHANDELIER_ORANGE = fromChandelier(ModBlocks.CHANDELIER_ORANGE);
    public static final DeferredItem<BlockItem> CHANDELIER_MAGENTA = fromChandelier(ModBlocks.CHANDELIER_MAGENTA);
    public static final DeferredItem<BlockItem> CHANDELIER_LIGHT_BLUE = fromChandelier(ModBlocks.CHANDELIER_LIGHT_BLUE);
    public static final DeferredItem<BlockItem> CHANDELIER_YELLOW = fromChandelier(ModBlocks.CHANDELIER_YELLOW);
    public static final DeferredItem<BlockItem> CHANDELIER_LIME = fromChandelier(ModBlocks.CHANDELIER_LIME);
    public static final DeferredItem<BlockItem> CHANDELIER_PINK = fromChandelier(ModBlocks.CHANDELIER_PINK);
    public static final DeferredItem<BlockItem> CHANDELIER_GRAY = fromChandelier(ModBlocks.CHANDELIER_GRAY);
    public static final DeferredItem<BlockItem> CHANDELIER_LIGHT_GRAY = fromChandelier(ModBlocks.CHANDELIER_LIGHT_GRAY);
    public static final DeferredItem<BlockItem> CHANDELIER_CYAN = fromChandelier(ModBlocks.CHANDELIER_CYAN);
    public static final DeferredItem<BlockItem> CHANDELIER_PURPLE = fromChandelier(ModBlocks.CHANDELIER_PURPLE);
    public static final DeferredItem<BlockItem> CHANDELIER_BLUE = fromChandelier(ModBlocks.CHANDELIER_BLUE);
    public static final DeferredItem<BlockItem> CHANDELIER_BROWN = fromChandelier(ModBlocks.CHANDELIER_BROWN);
    public static final DeferredItem<BlockItem> CHANDELIER_GREEN = fromChandelier(ModBlocks.CHANDELIER_GREEN);
    public static final DeferredItem<BlockItem> CHANDELIER_RED = fromChandelier(ModBlocks.CHANDELIER_RED);
    public static final DeferredItem<BlockItem> CHANDELIER_BLACK = fromChandelier(ModBlocks.CHANDELIER_BLACK);
    public static final DeferredItem<BlockItem> VELMORRA_ALTAR = fromBlock(ModBlocks.VELMORRA_ALTAR);
    public static final DeferredItem<BlockItem> CHALICE = fromBlock(ModBlocks.CHALICE);


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
        DispenserBlock.registerProjectileBehavior(ModItems.QUARREL_NORMAL.get());
        DispenserBlock.registerProjectileBehavior(ModItems.QUARREL_SPITFIRE.get());
        DispenserBlock.registerProjectileBehavior(ModItems.QUARREL_TELEPORT.get());
        DispenserBlock.registerProjectileBehavior(ModItems.QUARREL_VAMPIRE_KILLER.get());
        DispenserBlock.registerProjectileBehavior(ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get());
        DispenserBlock.registerProjectileBehavior(ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get());
        DispenserBlock.registerProjectileBehavior(ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get());
    }

    private static DeferredItem<BlockItem> fromBlock(Holder<Block> block) {
        return ITEMS.registerSimpleBlockItem(block);
    }

    private static DeferredItem<BlockItem> fromBlock(Holder<Block> block, UnaryOperator<Item.Properties> properties) {
        return ITEMS.registerSimpleBlockItem(block, properties);
    }

    private static <T extends BlockItem> DeferredItem<T> fromBlock(Holder<Block> block, BiFunction<Block,Item.Properties, T> itemCreator) {
        return ITEMS.registerItem(block.unwrapKey().orElseThrow().identifier().getPath(), prop -> itemCreator.apply(block.value(), prop.useBlockDescriptionPrefix()));
    }

    private static <T extends BlockItem> DeferredItem<T> fromBlock(Holder<Block> block, Function<Item.Properties, T> itemCreator, UnaryOperator<Item.Properties> properties) {
        return ITEMS.registerItem(block.unwrapKey().orElseThrow().identifier().getPath(), itemCreator, x -> properties.apply(x).useBlockDescriptionPrefix());
    }

    private static DeferredItem<StandingAndWallBlockItem> fromCandleHolder(Holder<Block> standing, Holder<Block> wall) {
        Identifier id = standing.unwrapKey().orElseThrow().identifier();
        return ITEMS.registerItem(id.getPath(), props -> {
            CandleHolderBlock block = (CandleHolderBlock) standing.value();
            Component description = Component.translatable("tooltip." + id.getNamespace() + "." + block.getDescriptionKey());
            return new StandingAndWallBlockItem(block, wall.value(), Direction.DOWN, props.useBlockDescriptionPrefix().factions$withShiftDescription(description));
        });
    }

    private static DeferredItem<BlockItem> fromChandelier(Holder<Block> block) {
        Identifier id = block.unwrapKey().orElseThrow().identifier();
        return ITEMS.registerItem(id.getPath(), props -> {
            CandleHolderBlock candleHolder = (CandleHolderBlock) block.value();
            Component description = Component.translatable("tooltip." + id.getNamespace() + "." + candleHolder.getDescriptionKey());
            return new BlockItem(candleHolder, props.useBlockDescriptionPrefix().factions$withShiftDescription(description));
        });
    }

    private static DeferredItem<CoffinItem> fromCoffin(DeferredHolder<Block, CoffinBlock> block) {
        return fromBlock(block, (block1, itemProps) -> new CoffinItem((CoffinBlock) block1, itemProps.rarity(Rarity.RARE).stacksTo(1).useBlockDescriptionPrefix()));
    }
}
