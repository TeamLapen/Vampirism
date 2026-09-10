package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.api.world.items.ExtendedPotionMix;
import de.teamlapen.vampirism.common.util.ItemDataUtils;
import de.teamlapen.vampirism.common.world.potions.BasePotion;
import de.teamlapen.vampirism.common.world.potions.BasePotion.HunterPotion;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, REFERENCE.MODID);

    private static final double DURABLE_STEP = 4.0;
    private static final double SOVEREIGN_FACTOR = 2.0;

    private static final int VERY_DURABLE_COUNT = 2;
    private static final int VERY_DURABLE_COUNT_EFFICIENT = 1;
    private static final int VERY_STRONG_COUNT = 4;
    private static final int VERY_STRONG_COUNT_EFFICIENT = 2;

    private static final int[][] ROUNDING = {
            { 1200, 1 },
            { 6000, 300 },
            { 18000, 1200 },
            { 36000, 6000 },
            { 72000, 12000 },
            { Integer.MAX_VALUE, 18000 }
    };

    private static final class Families {
        private static final Family SLOW_FALLING = new Family("slow_falling", MobEffects.SLOW_FALLING, 1800, 4800, 0, 0, 1.0, 0, false);
        private static final Family LUCK = new Family("luck", MobEffects.LUCK, 6000, 16000, 0, 0, 1.0, 0, false);
        private static final Family WEAKNESS = new Family("weakness", MobEffects.WEAKNESS, 1800, 4800, 0, 0, 1.0, 0, false);
        private static final Family STRENGTH = new Family("strength", MobEffects.STRENGTH, 3600, 9600, 1800, 1, 1.0, 1, false);
        private static final Family REGENERATION = new Family("regeneration", MobEffects.REGENERATION, 900, 1800, 450, 1, 1.0, 1, false);
        private static final Family POISON = new Family("poison", MobEffects.POISON, 900, 1800, 432, 1, 1.0, 1, false);
        private static final Family WATER_BREATHING = new Family("water_breathing", MobEffects.WATER_BREATHING, 3600, 9600, 0, 0, 1.0, 0, false);
        private static final Family SLOWNESS = new Family("slowness", MobEffects.SLOWNESS, 1800, 4800, 400, 3, 1.0, 2, false);
        private static final Family SWIFTNESS = new Family("swiftness", MobEffects.SPEED, 3600, 9600, 1800, 1, 1.0, 1, false);
        private static final Family FIRE_RESISTANCE = new Family("fire_resistance", MobEffects.FIRE_RESISTANCE, 3600, 9600, 0, 0, 1.0, 0, false);
        private static final Family LEAPING = new Family("leaping", MobEffects.JUMP_BOOST, 3600, 9600, 1800, 1, 1.0, 1, false);
        private static final Family INVISIBILITY = new Family("invisibility", MobEffects.INVISIBILITY, 3600, 9600, 0, 0, 1.0, 0, false);
        private static final Family NIGHT_VISION = new Family("night_vision", MobEffects.NIGHT_VISION, 3600, 9600, 0, 0, 1.0, 0, false);
        private static final Family HEALING = new Family("healing", MobEffects.INSTANT_HEALTH, 1, 0, 1, 1, 1.0, 1, true);
        private static final Family NAUSEA = new Family("nausea", MobEffects.NAUSEA, 1200, 2400, 0, 0, 1.0, 0, false);
        private static final Family BLINDNESS = new Family("blindness", MobEffects.BLINDNESS, 1200, 4800, 0, 0, 1.0, 0, false);
        private static final Family HEALTH_BOOST = new Family("health_boost", MobEffects.HEALTH_BOOST, 1200, 4800, 400, 1, 1.0, 1, false);
    }

    private record Family(String name, Holder<MobEffect> effect, int base, int longDuration, int strongDuration, int strongAmplifier, double strongStep, int amplifierStep, boolean instant) {}

    // Hunter
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOW_FALLING = potion("very_long_slow_falling", Families.SLOW_FALLING, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOW_FALLING_EXTENDED = extended("very_long_slow_falling_extended", Families.SLOW_FALLING, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_LUCK = potion("long_luck", Families.LUCK, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_LUCK_EXTENDED = extended("long_luck_extended", Families.LUCK, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WEAKNESS = potion("very_long_weakness", Families.WEAKNESS, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WEAKNESS_EXTENDED = extended("very_long_weakness_extended", Families.WEAKNESS, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_STRENGTH = potion("very_long_strength", Families.STRENGTH, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_STRENGTH_EXTENDED = extended("very_long_strength_extended", Families.STRENGTH, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_STRENGTH = potion("long_strong_strength", Families.STRENGTH, 2, 1);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_STRENGTH_EXTENDED = extended("long_strong_strength_extended", Families.STRENGTH, 2, 1);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_REGENERATION = potion("very_strong_regeneration", Families.REGENERATION, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_REGENERATION_EXTENDED = extended("very_strong_regeneration_extended", Families.REGENERATION, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_REGENERATION = potion("very_long_regeneration", Families.REGENERATION, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_REGENERATION_EXTENDED = extended("very_long_regeneration_extended", Families.REGENERATION, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_REGENERATION = potion("long_strong_regeneration", Families.REGENERATION, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_REGENERATION_EXTENDED = extended("long_strong_regeneration_extended", Families.REGENERATION, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_POISON = potion("very_strong_poison", Families.POISON, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_POISON_EXTENDED = extended("very_strong_poison_extended", Families.POISON, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_POISON = potion("long_strong_poison", Families.POISON, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_POISON_EXTENDED = extended("long_strong_poison_extended", Families.POISON, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_POISON = potion("very_long_poison", Families.POISON, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_POISON_EXTENDED = extended("very_long_poison_extended", Families.POISON, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALING = potion("very_strong_healing", Families.HEALING, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALING_EXTENDED = extended("very_strong_healing_extended", Families.HEALING, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WATER_BREATHING = potion("very_long_water_breathing", Families.WATER_BREATHING, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WATER_BREATHING_EXTENDED = extended("very_long_water_breathing_extended", Families.WATER_BREATHING, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SLOWNESS = potion("very_strong_slowness", Families.SLOWNESS, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SLOWNESS_EXTENDED = extended("very_strong_slowness_extended", Families.SLOWNESS, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOWNESS = potion("very_long_slowness", Families.SLOWNESS, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOWNESS_EXTENDED = extended("very_long_slowness_extended", Families.SLOWNESS, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SLOWNESS = potion("long_strong_slowness", Families.SLOWNESS, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SLOWNESS_EXTENDED = extended("long_strong_slowness_extended", Families.SLOWNESS, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SWIFTNESS = potion("very_strong_swiftness", Families.SWIFTNESS, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SWIFTNESS_EXTENDED = extended("very_strong_swiftness_extended", Families.SWIFTNESS, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SWIFTNESS = potion("very_long_swiftness", Families.SWIFTNESS, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SWIFTNESS_EXTENDED = extended("very_long_swiftness_extended", Families.SWIFTNESS, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SWIFTNESS = potion("long_strong_swiftness", Families.SWIFTNESS, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SWIFTNESS_EXTENDED = extended("long_strong_swiftness_extended", Families.SWIFTNESS, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_FIRE_RESISTANCE = potion("very_long_fire_resistance", Families.FIRE_RESISTANCE, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_FIRE_RESISTANCE_EXTENDED = extended("very_long_fire_resistance_extended", Families.FIRE_RESISTANCE, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_LEAPING = potion("very_strong_leaping", Families.LEAPING, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_LEAPING_EXTENDED = extended("very_strong_leaping_extended", Families.LEAPING, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_LEAPING = potion("very_long_leaping", Families.LEAPING, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_LEAPING_EXTENDED = extended("very_long_leaping_extended", Families.LEAPING, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_LEAPING = potion("long_strong_leaping", Families.LEAPING, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_LEAPING_EXTENDED = extended("long_strong_leaping_extended", Families.LEAPING, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_INVISIBILITY = potion("very_long_invisibility", Families.INVISIBILITY, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_INVISIBILITY_EXTENDED = extended("very_long_invisibility_extended", Families.INVISIBILITY, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NIGHT_VISION = potion("very_long_night_vision", Families.NIGHT_VISION, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NIGHT_VISION_EXTENDED = extended("very_long_night_vision_extended", Families.NIGHT_VISION, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> NAUSEA = potion("nausea", Families.NAUSEA, 0, 0);
    public static final DeferredHolder<Potion, HunterPotion> NAUSEA_EXTENDED = extended("nausea_extended", Families.NAUSEA, 0, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_NAUSEA = potion("long_nausea", Families.NAUSEA, 1, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_NAUSEA_EXTENDED = extended("long_nausea_extended", Families.NAUSEA, 1, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NAUSEA = potion("very_long_nausea", Families.NAUSEA, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NAUSEA_EXTENDED = extended("very_long_nausea_extended", Families.NAUSEA, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> BLINDNESS = potion("blindness", Families.BLINDNESS, 0, 0);
    public static final DeferredHolder<Potion, HunterPotion> BLINDNESS_EXTENDED = extended("blindness_extended", Families.BLINDNESS, 0, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_BLINDNESS = potion("long_blindness", Families.BLINDNESS, 1, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_BLINDNESS_EXTENDED = extended("long_blindness_extended", Families.BLINDNESS, 1, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_BLINDNESS = potion("very_long_blindness", Families.BLINDNESS, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_BLINDNESS_EXTENDED = extended("very_long_blindness_extended", Families.BLINDNESS, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> HEALTH_BOOST = potion("health_boost", Families.HEALTH_BOOST, 0, 0);
    public static final DeferredHolder<Potion, HunterPotion> HEALTH_BOOST_EXTENDED = extended("health_boost_extended", Families.HEALTH_BOOST, 0, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_HEALTH_BOOST = potion("long_health_boost", Families.HEALTH_BOOST, 1, 0);
    public static final DeferredHolder<Potion, HunterPotion> LONG_HEALTH_BOOST_EXTENDED = extended("long_health_boost_extended", Families.HEALTH_BOOST, 1, 0);
    public static final DeferredHolder<Potion, HunterPotion> STRONG_HEALTH_BOOST = potion("strong_health_boost", Families.HEALTH_BOOST, 0, 1);
    public static final DeferredHolder<Potion, HunterPotion> STRONG_HEALTH_BOOST_EXTENDED = extended("strong_health_boost_extended", Families.HEALTH_BOOST, 0, 1);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_HEALTH_BOOST = potion("very_long_health_boost", Families.HEALTH_BOOST, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_HEALTH_BOOST_EXTENDED = extended("very_long_health_boost_extended", Families.HEALTH_BOOST, 2, 0);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALTH_BOOST = potion("very_strong_health_boost", Families.HEALTH_BOOST, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALTH_BOOST_EXTENDED = extended("very_strong_health_boost_extended", Families.HEALTH_BOOST, 0, 2);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_HEALTH_BOOST = potion("long_strong_health_boost", Families.HEALTH_BOOST, 2, 2);
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_HEALTH_BOOST_EXTENDED = extended("long_strong_health_boost_extended", Families.HEALTH_BOOST, 2, 2);
    public static final DeferredHolder<Potion, Potion> GARLIC = POTIONS.register("garlic", () -> new Potion("garlic", new MobEffectInstance(ModEffects.GARLIC, 1200)));

    // Vampire
    public static final DeferredHolder<Potion, BasePotion> VAMPIRE_FIRE_RESISTANCE = POTIONS.register("vampire_fire_resistance", () -> new BasePotion("vampire_fire_resistance", new MobEffectInstance(ModEffects.FIRE_PROTECTION, 3600, 5)));
    public static final DeferredHolder<Potion, BasePotion> LONG_VAMPIRE_FIRE_RESISTANCE = POTIONS.register("long_vampire_fire_resistance", () -> new BasePotion("vampire_fire_resistance", new MobEffectInstance(ModEffects.FIRE_PROTECTION, 9600, 5)));

    static void register(IEventBus bus) {
        POTIONS.register(bus);
    }

    static void registerPotionMixes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        registerPotionMixes(event.getRegistryAccess());

        builder.addMix(Potions.WATER, ModBlocks.GARLIC.get().asItem(), GARLIC);

        builder.addRecipe(DataComponentIngredient.of(true, ItemDataUtils.createPotion(Potions.WATER)), Ingredient.of(ModItems.PURE_SALT), new ItemStack(ModItems.PURE_SALT_WATER.get()));
        splashItemBottle(ModItems.HOLY_WATER_BOTTLE_NORMAL.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_NORMAL.get(), builder);
        splashItemBottle(ModItems.HOLY_WATER_BOTTLE_ENHANCED.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ENHANCED.get(), builder);
        splashItemBottle(ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get(), ModItems.HOLY_WATER_SPLASH_BOTTLE_ULTIMATE.get(), builder);
    }

    private static void registerPotionMixes(RegistryAccess registryAccess) {
        veryDurable(Potions.LUCK, LONG_LUCK, LONG_LUCK_EXTENDED);
        veryDurable(Potions.LONG_SLOW_FALLING, VERY_LONG_SLOW_FALLING, VERY_LONG_SLOW_FALLING_EXTENDED);
        veryDurable(Potions.LONG_WEAKNESS, VERY_LONG_WEAKNESS, VERY_LONG_WEAKNESS_EXTENDED);
        veryDurable(Potions.LONG_WATER_BREATHING, VERY_LONG_WATER_BREATHING, VERY_LONG_WATER_BREATHING_EXTENDED);
        veryDurable(Potions.LONG_FIRE_RESISTANCE, VERY_LONG_FIRE_RESISTANCE, VERY_LONG_FIRE_RESISTANCE_EXTENDED);
        veryDurable(Potions.LONG_INVISIBILITY, VERY_LONG_INVISIBILITY, VERY_LONG_INVISIBILITY_EXTENDED);
        veryDurable(Potions.LONG_NIGHT_VISION, VERY_LONG_NIGHT_VISION, VERY_LONG_NIGHT_VISION_EXTENDED);
        veryStrong(Potions.STRONG_HEALING, VERY_STRONG_HEALING, VERY_STRONG_HEALING_EXTENDED);

        veryDurable(Potions.LONG_STRENGTH, VERY_LONG_STRENGTH, VERY_LONG_STRENGTH_EXTENDED);
        veryStrong(VERY_LONG_STRENGTH, LONG_STRONG_STRENGTH, LONG_STRONG_STRENGTH_EXTENDED);
        veryStrongDracula(VERY_LONG_STRENGTH_EXTENDED, LONG_STRONG_STRENGTH_EXTENDED);

        extendedFamily(Potions.LONG_REGENERATION, Potions.STRONG_REGENERATION, VERY_LONG_REGENERATION, VERY_LONG_REGENERATION_EXTENDED, VERY_STRONG_REGENERATION, VERY_STRONG_REGENERATION_EXTENDED, LONG_STRONG_REGENERATION, LONG_STRONG_REGENERATION_EXTENDED);
        extendedFamily(Potions.LONG_POISON, Potions.STRONG_POISON, VERY_LONG_POISON, VERY_LONG_POISON_EXTENDED, VERY_STRONG_POISON, VERY_STRONG_POISON_EXTENDED, LONG_STRONG_POISON, LONG_STRONG_POISON_EXTENDED);
        extendedFamily(Potions.LONG_SLOWNESS, Potions.STRONG_SLOWNESS, VERY_LONG_SLOWNESS, VERY_LONG_SLOWNESS_EXTENDED, VERY_STRONG_SLOWNESS, VERY_STRONG_SLOWNESS_EXTENDED, LONG_STRONG_SLOWNESS, LONG_STRONG_SLOWNESS_EXTENDED);
        extendedFamily(Potions.LONG_SWIFTNESS, Potions.STRONG_SWIFTNESS, VERY_LONG_SWIFTNESS, VERY_LONG_SWIFTNESS_EXTENDED, VERY_STRONG_SWIFTNESS, VERY_STRONG_SWIFTNESS_EXTENDED, LONG_STRONG_SWIFTNESS, LONG_STRONG_SWIFTNESS_EXTENDED);
        extendedFamily(Potions.LONG_LEAPING, Potions.STRONG_LEAPING, VERY_LONG_LEAPING, VERY_LONG_LEAPING_EXTENDED, VERY_STRONG_LEAPING, VERY_STRONG_LEAPING_EXTENDED, LONG_STRONG_LEAPING, LONG_STRONG_LEAPING_EXTENDED);

        masterFamily(() -> Ingredient.of(registryAccess.lookupOrThrow(Registries.ITEM).getOrThrow(Tags.Items.MUSHROOMS)), 8, 4, NAUSEA, NAUSEA_EXTENDED, LONG_NAUSEA, LONG_NAUSEA_EXTENDED, VERY_LONG_NAUSEA, VERY_LONG_NAUSEA_EXTENDED);
        masterFamily(() -> Ingredient.of(Items.INK_SAC), 12, 6, BLINDNESS, BLINDNESS_EXTENDED, LONG_BLINDNESS, LONG_BLINDNESS_EXTENDED, VERY_LONG_BLINDNESS, VERY_LONG_BLINDNESS_EXTENDED);

        master(HEALTH_BOOST, () -> Ingredient.of(Items.APPLE), 16, 8);
        masterSovereign(HEALTH_BOOST_EXTENDED, () -> Ingredient.of(Items.APPLE), 16, 8);
        durable(HEALTH_BOOST, LONG_HEALTH_BOOST, LONG_HEALTH_BOOST_EXTENDED);
        strong(HEALTH_BOOST, STRONG_HEALTH_BOOST, STRONG_HEALTH_BOOST_EXTENDED);
        extendedFamily(LONG_HEALTH_BOOST, STRONG_HEALTH_BOOST, VERY_LONG_HEALTH_BOOST, VERY_LONG_HEALTH_BOOST_EXTENDED, VERY_STRONG_HEALTH_BOOST, VERY_STRONG_HEALTH_BOOST_EXTENDED, LONG_STRONG_HEALTH_BOOST, LONG_STRONG_HEALTH_BOOST_EXTENDED);
        veryDurableDracula(LONG_HEALTH_BOOST_EXTENDED, VERY_LONG_HEALTH_BOOST_EXTENDED);
        veryStrongDracula(STRONG_HEALTH_BOOST_EXTENDED, VERY_STRONG_HEALTH_BOOST_EXTENDED);
    }

    private static void extendedFamily(Holder<Potion> vanillaLong, Holder<Potion> vanillaStrong, Holder<Potion> longCell, Holder<Potion> longCellExtended, Holder<Potion> strongCell, Holder<Potion> strongCellExtended, Holder<Potion> capstone, Holder<Potion> capstoneExtended) {
        veryDurable(vanillaLong, longCell, longCellExtended);
        veryStrong(vanillaStrong, strongCell, strongCellExtended);
        veryStrong(longCell, capstone, capstoneExtended);
        veryStrongDracula(longCellExtended, capstoneExtended);
        veryDurable(strongCell, capstone, capstoneExtended);
        veryDurableDracula(strongCellExtended, capstoneExtended);
    }

    private static void masterFamily(Supplier<Ingredient> ingredient, int count, int countReduced, Holder<Potion> base, Holder<Potion> baseExtended, Holder<Potion> longCell, Holder<Potion> longCellExtended, Holder<Potion> veryLongCell, Holder<Potion> veryLongCellExtended) {
        master(base, ingredient, count, countReduced);
        masterSovereign(baseExtended, ingredient, count, countReduced);
        durable(base, longCell, longCellExtended);
        veryDurable(longCell, veryLongCell, veryLongCellExtended);
        veryDurableDracula(longCellExtended, veryLongCellExtended);
    }

    private static DeferredHolder<Potion, HunterPotion> potion(String name, Family family, int durableSteps, int strongSteps) {
        return POTIONS.register(name, () -> new HunterPotion(family.name(), new MobEffectInstance(family.effect(), duration(family, durableSteps, strongSteps, false), amplifier(family, strongSteps, false))));
    }

    private static DeferredHolder<Potion, HunterPotion> extended(String name, Family family, int durableSteps, int strongSteps) {
        return POTIONS.register(name, () -> new HunterPotion(family.name(), new MobEffectInstance(family.effect(), duration(family, durableSteps, strongSteps, true), amplifier(family, strongSteps, true))));
    }

    private static int duration(Family family, int durableSteps, int strongSteps, boolean sovereign) {
        if (family.instant()) return 1;

        double duration = family.base();
        if (durableSteps > 0) duration = duration * family.longDuration() / family.base();
        if (durableSteps > 1) duration *= DURABLE_STEP;
        if (strongSteps > 0) duration = duration * family.strongDuration() / family.base();
        if (strongSteps > 1) duration *= family.strongStep();

        return snap(duration, sovereign ? SOVEREIGN_FACTOR : 1.0);
    }

    private static int snap(double duration, double factor) {
        for (int[] step : ROUNDING) {
            if (duration < step[0]) {
                long granularity = step[1];
                return (int) Math.max(1, Math.round(Math.round(duration / granularity) * granularity * factor));
            }
        }

        return Math.max(1, (int) Math.round(duration * factor));
    }

    private static int amplifier(Family family, int strongSteps, boolean sovereign) {
        int amplifier = strongSteps == 0 ? 0 : strongSteps == 1 ? family.strongAmplifier() : family.strongAmplifier() + family.amplifierStep();
        return family.instant() && sovereign ? amplifier + 1 : amplifier;
    }

    private static void durable(Holder<Potion> in, Holder<Potion> out) {
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.REDSTONE), 1).blood().build());
    }

    private static void durable(Holder<Potion> in, Holder<Potion> out, Holder<Potion> outExtended) {
        durable(in, out);
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, outExtended).ingredient(() -> Ingredient.of(Items.REDSTONE), 1).sovereignBlood().build());
    }

    private static void strong(Holder<Potion> in, Holder<Potion> out) {
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.GLOWSTONE_DUST), 1).blood().build());
    }

    private static void strong(Holder<Potion> in, Holder<Potion> out, Holder<Potion> outExtended) {
        strong(in, out);
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, outExtended).ingredient(() -> Ingredient.of(Items.GLOWSTONE_DUST), 1).sovereignBlood().build());
    }

    private static void veryDurable(Holder<Potion> in, Holder<Potion> out) {
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.REDSTONE_BLOCK), VERY_DURABLE_COUNT, VERY_DURABLE_COUNT_EFFICIENT).blood().durable().build());
    }

    private static void veryDurableDracula(Holder<Potion> in, Holder<Potion> out) {
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.REDSTONE_BLOCK), VERY_DURABLE_COUNT, VERY_DURABLE_COUNT_EFFICIENT).sovereignBlood().durable().build());
    }

    private static void veryDurable(Holder<Potion> in, Holder<Potion> out, Holder<Potion> outExtended) {
        veryDurable(in, out);
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, outExtended).ingredient(() -> Ingredient.of(Items.REDSTONE_BLOCK), VERY_DURABLE_COUNT, VERY_DURABLE_COUNT_EFFICIENT).sovereignBlood().durable().build());
    }

    private static void veryStrong(Holder<Potion> in, Holder<Potion> out) {
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.GLOWSTONE), VERY_STRONG_COUNT, VERY_STRONG_COUNT_EFFICIENT).blood().concentrated().build());
    }

    private static void veryStrongDracula(Holder<Potion> in, Holder<Potion> out) {
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.GLOWSTONE), VERY_STRONG_COUNT, VERY_STRONG_COUNT_EFFICIENT).sovereignBlood().concentrated().build());
    }

    private static void veryStrong(Holder<Potion> in, Holder<Potion> out, Holder<Potion> outExtended) {
        veryStrong(in, out);
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, outExtended).ingredient(() -> Ingredient.of(Items.GLOWSTONE), VERY_STRONG_COUNT, VERY_STRONG_COUNT_EFFICIENT).sovereignBlood().concentrated().build());
    }

    private static void master(Holder<Potion> out, Supplier<Ingredient> in, int count, int countReduced) {
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(Potions.AWKWARD, out).master().ingredient(in, count, countReduced).blood().build());
    }

    private static void masterSovereign(Holder<Potion> out, Supplier<Ingredient> in, int count, int countReduced) {
        VampirismApi.services().extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(Potions.AWKWARD, out).master().ingredient(in, count, countReduced).sovereignBlood().build());
    }

    private static void splashItemBottle(Item item, Item resultSplashItem, PotionBrewing.Builder builder) {
        builder.addRecipe(new BrewingRecipe(Ingredient.of(item), Ingredient.of(Items.GUNPOWDER), new ItemStack(resultSplashItem)) {
            @Override
            public boolean isInput(ItemStack stack) {
                return item.equals(stack.getItem());
            }
        });
    }
}
