package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.items.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_chest_enhanced =
            ITEMS.register("armor_of_swiftness_chest_enhanced", () -> new ArmorOfSwiftnessItem(EquipmentSlot.CHEST, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_chest_normal =
            ITEMS.register("armor_of_swiftness_chest_normal", () -> new ArmorOfSwiftnessItem(EquipmentSlot.CHEST, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_chest_ultimate =
            ITEMS.register("armor_of_swiftness_chest_ultimate", () -> new ArmorOfSwiftnessItem(EquipmentSlot.CHEST, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_feet_enhanced =
            ITEMS.register("armor_of_swiftness_feet_enhanced", () -> new ArmorOfSwiftnessItem(EquipmentSlot.FEET, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_feet_normal =
            ITEMS.register("armor_of_swiftness_feet_normal", () -> new ArmorOfSwiftnessItem(EquipmentSlot.FEET, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_feet_ultimate =
            ITEMS.register("armor_of_swiftness_feet_ultimate", () -> new ArmorOfSwiftnessItem(EquipmentSlot.FEET, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_head_enhanced =
            ITEMS.register("armor_of_swiftness_head_enhanced", () -> new ArmorOfSwiftnessItem(EquipmentSlot.HEAD, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_head_normal =
            ITEMS.register("armor_of_swiftness_head_normal", () -> new ArmorOfSwiftnessItem(EquipmentSlot.HEAD, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_head_ultimate =
            ITEMS.register("armor_of_swiftness_head_ultimate", () -> new ArmorOfSwiftnessItem(EquipmentSlot.HEAD, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_legs_enhanced =
            ITEMS.register("armor_of_swiftness_legs_enhanced", () -> new ArmorOfSwiftnessItem(EquipmentSlot.LEGS, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_legs_normal =
            ITEMS.register("armor_of_swiftness_legs_normal", () -> new ArmorOfSwiftnessItem(EquipmentSlot.LEGS, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ArmorOfSwiftnessItem> armor_of_swiftness_legs_ultimate =
            ITEMS.register("armor_of_swiftness_legs_ultimate", () -> new ArmorOfSwiftnessItem(EquipmentSlot.LEGS, IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<SimpleCrossbowItem> basic_crossbow =
            ITEMS.register("basic_crossbow", () -> new SimpleCrossbowItem(1, 20, 300, Tiers.WOOD));
    public static final RegistryObject<DoubleCrossbowItem> basic_double_crossbow =
            ITEMS.register("basic_double_crossbow", () -> new DoubleCrossbowItem(1, 20, 300, Tiers.WOOD));
    public static final RegistryObject<TechCrossbowItem> basic_tech_crossbow =
            ITEMS.register("basic_tech_crossbow", () -> new TechCrossbowItem(1.6F, 6, 300, Tiers.DIAMOND));

    public static final RegistryObject<BloodBottleItem> blood_bottle =
            ITEMS.register("blood_bottle", BloodBottleItem::new);
    public static final RegistryObject<BucketItem> blood_bucket =
            ITEMS.register("blood_bucket", () -> new BucketItem(ModFluids.blood, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<VampirismItem> blood_infused_iron_ingot =
            ITEMS.register("blood_infused_iron_ingot", () -> new VampirismItem(creativeTabProps()));
    public static final RegistryObject<VampirismItem> blood_infused_enhanced_iron_ingot =
            ITEMS.register("blood_infused_enhanced_iron_ingot", () -> new VampirismItem(creativeTabProps()));

    public static final RegistryObject<CrossbowArrowItem> crossbow_arrow_normal =
            ITEMS.register("crossbow_arrow_normal", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.NORMAL));
    public static final RegistryObject<CrossbowArrowItem> crossbow_arrow_spitfire =
            ITEMS.register("crossbow_arrow_spitfire", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.SPITFIRE));
    public static final RegistryObject<CrossbowArrowItem> crossbow_arrow_vampire_killer =
            ITEMS.register("crossbow_arrow_vampire_killer", () -> new CrossbowArrowItem(CrossbowArrowItem.EnumArrowType.VAMPIRE_KILLER));

    public static final RegistryObject<SimpleCrossbowItem> enhanced_crossbow =
            ITEMS.register("enhanced_crossbow", () -> new SimpleCrossbowItem(1.5F, 15, 350, Tiers.IRON));
    public static final RegistryObject<DoubleCrossbowItem> enhanced_double_crossbow =
            ITEMS.register("enhanced_double_crossbow", () -> new DoubleCrossbowItem(1.5F, 15, 350, Tiers.IRON));
    public static final RegistryObject<TechCrossbowItem> enhanced_tech_crossbow =
            ITEMS.register("enhanced_tech_crossbow", () -> new TechCrossbowItem(1.7F, 4, 450, Tiers.DIAMOND));

    public static final RegistryObject<VampirismItem> garlic_diffuser_core =
            ITEMS.register("garlic_diffuser_core", () -> new VampirismItem(creativeTabProps()));
    public static final RegistryObject<VampirismItem> garlic_diffuser_core_improved =
            ITEMS.register("garlic_diffuser_core_improved", () -> new VampirismItem(creativeTabProps()));

    public static final RegistryObject<HeartSeekerItem> heart_seeker_enhanced =
            ITEMS.register("heart_seeker_enhanced", () -> new HeartSeekerItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HeartSeekerItem> heart_seeker_normal =
            ITEMS.register("heart_seeker_normal", () -> new HeartSeekerItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HeartSeekerItem> heart_seeker_ultimate =
            ITEMS.register("heart_seeker_ultimate", () -> new HeartSeekerItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<HeartStrikerItem> heart_striker_enhanced =
            ITEMS.register("heart_striker_enhanced", () -> new HeartStrikerItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HeartStrikerItem> heart_striker_normal =
            ITEMS.register("heart_striker_normal", () -> new HeartStrikerItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HeartStrikerItem> heart_striker_ultimate =
            ITEMS.register("heart_striker_ultimate", () -> new HeartStrikerItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<VampirismItem> holy_salt =
            ITEMS.register("holy_salt", () -> new VampirismItem(creativeTabProps()) {
            @Override
            public boolean isFoil(@Nonnull ItemStack stack) {

                return true;
            }
        });
    public static final RegistryObject<VampirismItem> holy_salt_water =
            ITEMS.register("holy_salt_water", () -> new VampirismItem(creativeTabProps()) {
            @Override
            public boolean isFoil(@Nonnull ItemStack stack) {

                return true;
            }
        });

    public static final RegistryObject<HolyWaterBottleItem> holy_water_bottle_enhanced =
            ITEMS.register("holy_water_bottle_enhanced", () -> new HolyWaterBottleItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HolyWaterBottleItem> holy_water_bottle_normal =
            ITEMS.register("holy_water_bottle_normal", () -> new HolyWaterBottleItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HolyWaterBottleItem> holy_water_bottle_ultimate =
            ITEMS.register("holy_water_bottle_ultimate", () -> new HolyWaterBottleItem(IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<HolyWaterSplashBottleItem> holy_water_splash_bottle_enhanced =
            ITEMS.register("holy_water_splash_bottle_enhanced", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HolyWaterSplashBottleItem> holy_water_splash_bottle_normal =
            ITEMS.register("holy_water_splash_bottle_normal", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HolyWaterSplashBottleItem> holy_water_splash_bottle_ultimate =
            ITEMS.register("holy_water_splash_bottle_ultimate", () -> new HolyWaterSplashBottleItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<HunterAxeItem> hunter_axe_enhanced =
            ITEMS.register("hunter_axe_enhanced", () -> new HunterAxeItem(IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterAxeItem> hunter_axe_normal =
            ITEMS.register("hunter_axe_normal", () -> new HunterAxeItem(IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterAxeItem> hunter_axe_ultimate =
            ITEMS.register("hunter_axe_ultimate", () -> new HunterAxeItem(IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<HunterCoatItem> hunter_coat_chest_enhanced =
            ITEMS.register("hunter_coat_chest_enhanced", () -> new HunterCoatItem(EquipmentSlot.CHEST, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterCoatItem> hunter_coat_chest_normal =
            ITEMS.register("hunter_coat_chest_normal", () -> new HunterCoatItem(EquipmentSlot.CHEST, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterCoatItem> hunter_coat_chest_ultimate =
            ITEMS.register("hunter_coat_chest_ultimate", () -> new HunterCoatItem(EquipmentSlot.CHEST, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<HunterCoatItem> hunter_coat_feet_enhanced =
            ITEMS.register("hunter_coat_feet_enhanced", () -> new HunterCoatItem(EquipmentSlot.FEET, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterCoatItem> hunter_coat_feet_normal =
            ITEMS.register("hunter_coat_feet_normal", () -> new HunterCoatItem(EquipmentSlot.FEET, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterCoatItem> hunter_coat_feet_ultimate =
            ITEMS.register("hunter_coat_feet_ultimate", () -> new HunterCoatItem(EquipmentSlot.FEET, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<HunterCoatItem> hunter_coat_head_enhanced =
            ITEMS.register("hunter_coat_head_enhanced", () -> new HunterCoatItem(EquipmentSlot.HEAD, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterCoatItem> hunter_coat_head_normal =
            ITEMS.register("hunter_coat_head_normal", () -> new HunterCoatItem(EquipmentSlot.HEAD, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterCoatItem> hunter_coat_head_ultimate =
            ITEMS.register("hunter_coat_head_ultimate", () -> new HunterCoatItem(EquipmentSlot.HEAD, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<HunterCoatItem> hunter_coat_legs_enhanced =
            ITEMS.register("hunter_coat_legs_enhanced", () -> new HunterCoatItem(EquipmentSlot.LEGS, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<HunterCoatItem> hunter_coat_legs_normal =
            ITEMS.register("hunter_coat_legs_normal", () -> new HunterCoatItem(EquipmentSlot.LEGS, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<HunterCoatItem> hunter_coat_legs_ultimate =
            ITEMS.register("hunter_coat_legs_ultimate", () -> new HunterCoatItem(EquipmentSlot.LEGS, IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<HunterHatItem> hunter_hat_head_0 =
            ITEMS.register("hunter_hat_head_0", () -> new HunterHatItem(0));
    public static final RegistryObject<HunterHatItem> hunter_hat_head_1 =
            ITEMS.register("hunter_hat_head_1", () -> new HunterHatItem(1));

    public static final RegistryObject<HunterIntelItem> hunter_intel_0 =
            ITEMS.register("hunter_intel_0", () -> new HunterIntelItem(0));
    public static final RegistryObject<HunterIntelItem> hunter_intel_1 =
            ITEMS.register("hunter_intel_1", () -> new HunterIntelItem(1));
    public static final RegistryObject<HunterIntelItem> hunter_intel_2 =
            ITEMS.register("hunter_intel_2", () -> new HunterIntelItem(2));
    public static final RegistryObject<HunterIntelItem> hunter_intel_3 =
            ITEMS.register("hunter_intel_3", () -> new HunterIntelItem(3));
    public static final RegistryObject<HunterIntelItem> hunter_intel_4 =
            ITEMS.register("hunter_intel_4", () -> new HunterIntelItem(4));
    public static final RegistryObject<HunterIntelItem> hunter_intel_5 =
            ITEMS.register("hunter_intel_5", () -> new HunterIntelItem(5));
    public static final RegistryObject<HunterIntelItem> hunter_intel_6 =
            ITEMS.register("hunter_intel_6", () -> new HunterIntelItem(6));
    public static final RegistryObject<HunterIntelItem> hunter_intel_7 =
            ITEMS.register("hunter_intel_7", () -> new HunterIntelItem(7));
    public static final RegistryObject<HunterIntelItem> hunter_intel_8 =
            ITEMS.register("hunter_intel_8", () -> new HunterIntelItem(8));
    public static final RegistryObject<HunterIntelItem> hunter_intel_9 =
            ITEMS.register("hunter_intel_9", () -> new HunterIntelItem(9));

    public static final RegistryObject<VampirismItemBloodFood> human_heart =
            ITEMS.register("human_heart", () -> new VampirismItemBloodFood((new FoodProperties.Builder()).nutrition(20).saturationMod(1.5F).build(), new FoodProperties.Builder().nutrition(5).saturationMod(1f).build()));

    public static final RegistryObject<InjectionItem> injection_empty =
            ITEMS.register("injection_empty", () -> new InjectionItem(InjectionItem.TYPE.EMPTY));
    public static final RegistryObject<InjectionItem> injection_garlic =
            ITEMS.register("injection_garlic", () -> new InjectionItem(InjectionItem.TYPE.GARLIC));
    public static final RegistryObject<InjectionItem> injection_sanguinare =
            ITEMS.register("injection_sanguinare", () -> new InjectionItem(InjectionItem.TYPE.SANGUINARE));
    public static final RegistryObject<InjectionItem> injection_zombie_blood =
            ITEMS.register("injection_zombie_blood", () -> new InjectionItem(InjectionItem.TYPE.ZOMBIE_BLOOD));

    public static final RegistryObject<VampirismItem> cure_apple =
            ITEMS.register("cure_apple", () -> new VampirismItem(creativeTabProps().rarity(Rarity.RARE)));

    public static final RegistryObject<BucketItem> impure_blood_bucket =
            ITEMS.register("impure_blood_bucket", () -> new BucketItem(ModFluids.impure_blood, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<GarlicItem> item_garlic =
            ITEMS.register("item_garlic", GarlicItem::new);
    public static final RegistryObject<MedChairItem> item_med_chair =
            ITEMS.register("item_med_chair", MedChairItem::new);
    public static final RegistryObject<GarlicBreadItem> garlic_bread =
            ITEMS.register("garlic_bread", GarlicBreadItem::new);
    public static final RegistryObject<AlchemicalFireItem> item_alchemical_fire =
            ITEMS.register("item_alchemical_fire", AlchemicalFireItem::new);

    public static final RegistryObject<TentItem> item_tent =
            ITEMS.register("item_tent", () -> new TentItem(false));
    public static final RegistryObject<TentItem> item_tent_spawner =
            ITEMS.register("item_tent_spawner", () -> new TentItem(true));

    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_chest_enhanced =
            ITEMS.register("obsidian_armor_chest_enhanced", () -> new ObsidianArmorItem(EquipmentSlot.CHEST, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_chest_normal =
            ITEMS.register("obsidian_armor_chest_normal", () -> new ObsidianArmorItem(EquipmentSlot.CHEST, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_chest_ultimate =
            ITEMS.register("obsidian_armor_chest_ultimate", () -> new ObsidianArmorItem(EquipmentSlot.CHEST, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_feet_enhanced =
            ITEMS.register("obsidian_armor_feet_enhanced", () -> new ObsidianArmorItem(EquipmentSlot.FEET, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_feet_normal =
            ITEMS.register("obsidian_armor_feet_normal", () -> new ObsidianArmorItem(EquipmentSlot.FEET, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_feet_ultimate =
            ITEMS.register("obsidian_armor_feet_ultimate", () -> new ObsidianArmorItem(EquipmentSlot.FEET, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_head_enhanced =
            ITEMS.register("obsidian_armor_head_enhanced", () -> new ObsidianArmorItem(EquipmentSlot.HEAD, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_head_normal =
            ITEMS.register("obsidian_armor_head_normal", () -> new ObsidianArmorItem(EquipmentSlot.HEAD, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_head_ultimate =
            ITEMS.register("obsidian_armor_head_ultimate", () -> new ObsidianArmorItem(EquipmentSlot.HEAD, IItemWithTier.TIER.ULTIMATE));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_legs_enhanced =
            ITEMS.register("obsidian_armor_legs_enhanced", () -> new ObsidianArmorItem(EquipmentSlot.LEGS, IItemWithTier.TIER.ENHANCED));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_legs_normal =
            ITEMS.register("obsidian_armor_legs_normal", () -> new ObsidianArmorItem(EquipmentSlot.LEGS, IItemWithTier.TIER.NORMAL));
    public static final RegistryObject<ObsidianArmorItem> obsidian_armor_legs_ultimate =
            ITEMS.register("obsidian_armor_legs_ultimate", () -> new ObsidianArmorItem(EquipmentSlot.LEGS, IItemWithTier.TIER.ULTIMATE));

    public static final RegistryObject<PitchforkItem> pitchfork =
            ITEMS.register("pitchfork", PitchforkItem::new);

    public static final RegistryObject<PureBloodItem> pure_blood_0 =
            ITEMS.register("pure_blood_0", () -> new PureBloodItem(0));
    public static final RegistryObject<PureBloodItem> pure_blood_1 =
            ITEMS.register("pure_blood_1", () -> new PureBloodItem(1));
    public static final RegistryObject<PureBloodItem> pure_blood_2 =
            ITEMS.register("pure_blood_2", () -> new PureBloodItem(2));
    public static final RegistryObject<PureBloodItem> pure_blood_3 =
            ITEMS.register("pure_blood_3", () -> new PureBloodItem(3));
    public static final RegistryObject<PureBloodItem> pure_blood_4 =
            ITEMS.register("pure_blood_4", () -> new PureBloodItem(4));

    public static final RegistryObject<VampirismItem> purified_garlic =
            ITEMS.register("purified_garlic", () -> new VampirismItem(creativeTabProps()));
    public static final RegistryObject<VampirismItem> pure_salt =
            ITEMS.register("pure_salt", () -> new VampirismItem(creativeTabProps()));
    public static final RegistryObject<VampirismItem> soul_orb_vampire =
            ITEMS.register("soul_orb_vampire", () -> new VampirismItem(creativeTabProps()));

    public static final RegistryObject<StakeItem> stake =
            ITEMS.register("stake", StakeItem::new);
    public static final RegistryObject<VampirismItem> tech_crossbow_ammo_package =
            ITEMS.register("tech_crossbow_ammo_package", () -> new VampirismItem(new Item.Properties().tab(VampirismMod.creativeTab)) {
            @Override
            public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
                tooltip.add(new TranslatableComponent("item.vampirism.tech_crossbow_ammo_package.tooltip", new TranslatableComponent(basic_tech_crossbow.get().getDescriptionId())).withStyle(ChatFormatting.GRAY));
            }

        });

    public static final RegistryObject<ColoredVampireClothingItem> vampire_cloak_black_blue =
            ITEMS.register("vampire_cloak_black_blue", () -> new ColoredVampireClothingItem(EquipmentSlot.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKBLUE));
    public static final RegistryObject<ColoredVampireClothingItem> vampire_cloak_black_red =
            ITEMS.register("vampire_cloak_black_red", () -> new ColoredVampireClothingItem(EquipmentSlot.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKRED));
    public static final RegistryObject<ColoredVampireClothingItem> vampire_cloak_black_white =
            ITEMS.register("vampire_cloak_black_white", () -> new ColoredVampireClothingItem(EquipmentSlot.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.BLACKWHITE));
    public static final RegistryObject<ColoredVampireClothingItem> vampire_cloak_red_black =
            ITEMS.register("vampire_cloak_red_black", () -> new ColoredVampireClothingItem(EquipmentSlot.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.REDBLACK));
    public static final RegistryObject<ColoredVampireClothingItem> vampire_cloak_white_black =
            ITEMS.register("vampire_cloak_white_black", () -> new ColoredVampireClothingItem(EquipmentSlot.CHEST, ColoredVampireClothingItem.EnumModel.CLOAK, "vampire_cloak", ColoredVampireClothingItem.EnumClothingColor.WHITEBLACK));

    public static final RegistryObject<VampireBloodBottleItem> vampire_blood_bottle =
            ITEMS.register("vampire_blood_bottle", VampireBloodBottleItem::new);
    public static final RegistryObject<VampireBookItem> vampire_book =
            ITEMS.register("vampire_book", VampireBookItem::new);
    public static final RegistryObject<VampireFangItem> vampire_fang =
            ITEMS.register("vampire_fang", VampireFangItem::new);
    public static final RegistryObject<VampirismItemBloodFood> weak_human_heart =
            ITEMS.register("weak_human_heart", () -> new VampirismItemBloodFood((new FoodProperties.Builder()).nutrition(10).saturationMod(0.9F).build(), new FoodProperties.Builder().nutrition(3).saturationMod(1f).build()));

    public static final RegistryObject<SpawnEggItem> vampire_spawn_egg =
            ITEMS.register("vampire_spawn_egg", () -> new SpawnEggItem(ModEntities.vampire.get(), 0x8B15A3, 0xa735e3, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> vampire_hunter_spawn_egg =
            ITEMS.register("vampire_hunter_spawn_egg", () -> new SpawnEggItem(ModEntities.hunter.get(), 0x2d05f2, 0x2600e0, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> advanced_vampire_spawn_egg =
            ITEMS.register("advanced_vampire_spawn_egg", () -> new SpawnEggItem(ModEntities.advanced_vampire.get(), 0x8B15A3, 0x560a7e, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> advanced_vampire_hunter_spawn_egg =
            ITEMS.register("advanced_vampire_hunter_spawn_egg", () -> new SpawnEggItem(ModEntities.advanced_hunter.get(), 0x2d05f2, 0x1a028c, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> vampire_baron_spawn_egg =
            ITEMS.register("vampire_baron_spawn_egg", () -> new SpawnEggItem(ModEntities.vampire_baron.get(), 0x8B15A3, 0x15acda, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<SpawnEggItem> hunter_trainer_spawn_egg =
            ITEMS.register("hunter_trainer_spawn_egg", () -> new SpawnEggItem(ModEntities.hunter_trainer.get(), 0x2d05f2, 0x1cdb49, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<UmbrellaItem> umbrella =
            ITEMS.register("umbrella", UmbrellaItem::new);

    public static final RegistryObject<VampirismItem> hunter_minion_equipment =
            ITEMS.register("hunter_minion_equipment", () -> new VampirismItem(creativeTabProps()));
    public static final RegistryObject<MinionUpgradeItem> hunter_minion_upgrade_simple =
            ITEMS.register("hunter_minion_upgrade_simple", () -> new MinionUpgradeItem(1, 2, VReference.HUNTER_FACTION));
    public static final RegistryObject<MinionUpgradeItem> hunter_minion_upgrade_enhanced =
            ITEMS.register("hunter_minion_upgrade_enhanced", () -> new MinionUpgradeItem(3, 4, VReference.HUNTER_FACTION));
    public static final RegistryObject<MinionUpgradeItem> hunter_minion_upgrade_special =
            ITEMS.register("hunter_minion_upgrade_special", () -> new MinionUpgradeItem(5, 6, VReference.HUNTER_FACTION));
    public static final RegistryObject<FeedingAdapterItem> feeding_adapter =
            ITEMS.register("feeding_adapter", FeedingAdapterItem::new);
    public static final RegistryObject<VampirismItem> vampire_minion_binding =
            ITEMS.register("vampire_minion_binding", () -> new VampirismItem(creativeTabProps()));
    public static final RegistryObject<MinionUpgradeItem> vampire_minion_upgrade_simple =
            ITEMS.register("vampire_minion_upgrade_simple", () -> new MinionUpgradeItem(1, 2, VReference.VAMPIRE_FACTION));
    public static final RegistryObject<MinionUpgradeItem> vampire_minion_upgrade_enhanced =
            ITEMS.register("vampire_minion_upgrade_enhanced", () -> new MinionUpgradeItem(3, 4, VReference.VAMPIRE_FACTION));
    public static final RegistryObject<MinionUpgradeItem> vampire_minion_upgrade_special =
            ITEMS.register("vampire_minion_upgrade_special", () -> new MinionUpgradeItem(6, 6, VReference.VAMPIRE_FACTION));

    public static final RegistryObject<OblivionItem> oblivion_potion =
            ITEMS.register("oblivion_potion", () -> new OblivionItem(creativeTabProps()));

    public static final RegistryObject<RefinementItem> amulet =
            ITEMS.register("amulet", () -> new VampireRefinementItem(creativeTabProps(), IRefinementItem.AccessorySlotType.AMULET));
    public static final RegistryObject<RefinementItem> ring =
            ITEMS.register("ring", () -> new VampireRefinementItem(creativeTabProps(), IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<RefinementItem> obi_belt =
            ITEMS.register("obi_belt", () -> new VampireRefinementItem(creativeTabProps(), IRefinementItem.AccessorySlotType.OBI_BELT));

    public static final RegistryObject<VampireClothingItem> vampire_clothing_crown =
            ITEMS.register("vampire_clothing_crown", () -> new VampireClothingItem(EquipmentSlot.HEAD));
    public static final RegistryObject<VampireClothingItem> vampire_clothing_legs =
            ITEMS.register("vampire_clothing_legs", () -> new VampireClothingItem(EquipmentSlot.LEGS));
    public static final RegistryObject<VampireClothingItem> vampire_clothing_boots =
            ITEMS.register("vampire_clothing_boots", () -> new VampireClothingItem(EquipmentSlot.FEET));
    public static final RegistryObject<VampireClothingItem> vampire_clothing_hat =
            ITEMS.register("vampire_clothing_hat", () -> new VampireClothingItem(EquipmentSlot.HEAD));

    public static final RegistryObject<VampirismItem> garlic_finder =
            ITEMS.register("garlic_finder", () -> new VampirismItem(creativeTabProps().rarity(Rarity.RARE)));

    public static final RegistryObject<StandingAndWallBlockItem> item_candelabra =
            ITEMS.register("item_candelabra", () -> new StandingAndWallBlockItem(ModBlocks.candelabra.get(), ModBlocks.candelabra_wall.get(), new Item.Properties().tab(VampirismMod.creativeTab)));


    static void registerCraftingRecipes() {
        // Brewing
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), Ingredient.of(new ItemStack(holy_salt.get())), new ItemStack(holy_salt_water.get()));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(holy_water_bottle_normal.get()), Ingredient.of(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_normal.get())) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return holy_water_bottle_normal.get().equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(holy_water_bottle_enhanced.get()), Ingredient.of(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_enhanced.get())) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {

                return holy_water_bottle_enhanced.get().equals(stack.getItem());
            }
        });
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(holy_water_bottle_ultimate.get()), Ingredient.of(Items.GUNPOWDER), new ItemStack(holy_water_splash_bottle_ultimate.get())) {
            @Override
            public boolean isInput(@Nonnull ItemStack stack) {
                return holy_water_bottle_ultimate.get().equals(stack.getItem());
            }
        });

    }


    public static void registerItems(IEventBus bus) {
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
                case "vampirism:blood_potion", "vampirism:blood_potion_table" -> missingMapping.ignore();
                case "vampirism:vampire_clothing_head" -> missingMapping.remap(vampire_clothing_crown.get());
                case "vampirism:vampire_clothing_feet" -> missingMapping.remap(vampire_clothing_boots.get());
                case "vampirism:garlic_beacon_core" -> missingMapping.remap(garlic_diffuser_core.get());
                case "vampirism:garlic_beacon_core_improved" -> missingMapping.remap(garlic_diffuser_core_improved.get());
                case "vampirism:garlic_beacon_normal" -> missingMapping.remap(ModBlocks.garlic_diffuser_normal.get().asItem());
                case "vampirism:garlic_beacon_weak" -> missingMapping.remap(ModBlocks.garlic_diffuser_weak.get().asItem());
                case "vampirism:garlic_beacon_improved" -> missingMapping.remap(ModBlocks.garlic_diffuser_improved.get().asItem());
                case "vampirism:church_altar" -> missingMapping.remap(ModBlocks.altar_cleansing.get().asItem());
            }
        });
    }
}
