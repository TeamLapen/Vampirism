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

    private static final int VERY_DURABLE_COUNT = 12;
    private static final int VERY_DURABLE_COUNT_EFFICIENT = 6;
    private static final int VERY_STRONG_COUNT = 16;
    private static final int VERY_STRONG_COUNT_EFFICIENT = 8;

    //Hunter
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOW_FALLING = POTIONS.register("very_long_slow_falling", () -> new HunterPotion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOW_FALLING_EXTENDED = POTIONS.register("very_long_slow_falling_extended", () -> new HunterPotion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, 72000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_LUCK = POTIONS.register("long_luck", () -> new HunterPotion("luck", new MobEffectInstance(MobEffects.LUCK, 60000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_LUCK_EXTENDED = POTIONS.register("long_luck_extended", () -> new HunterPotion("luck", new MobEffectInstance(MobEffects.LUCK, 90000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WEAKNESS = POTIONS.register("very_long_weakness", () -> new HunterPotion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WEAKNESS_EXTENDED = POTIONS.register("very_long_weakness_extended", () -> new HunterPotion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, 72000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_STRENGTH = POTIONS.register("very_long_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.STRENGTH, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_STRENGTH_EXTENDED = POTIONS.register("very_long_strength_extended", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.STRENGTH, 144000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_STRENGTH = POTIONS.register("long_strong_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.STRENGTH, 4800, 1)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_STRENGTH_EXTENDED = POTIONS.register("long_strong_strength_extended", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.STRENGTH, 7200, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_REGENERATION = POTIONS.register("very_strong_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 450, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_REGENERATION_EXTENDED = POTIONS.register("very_strong_regeneration_extended", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 675, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_REGENERATION = POTIONS.register("very_long_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 18000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_REGENERATION_EXTENDED = POTIONS.register("very_long_regeneration_extended", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 27000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_REGENERATION = POTIONS.register("long_strong_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 1200, 1)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_REGENERATION_EXTENDED = POTIONS.register("long_strong_regeneration_extended", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 1800, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_POISON = POTIONS.register("very_strong_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 432, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_POISON_EXTENDED = POTIONS.register("very_strong_poison_extended", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 648, 2)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_POISON = POTIONS.register("long_strong_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 1200, 1)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_POISON_EXTENDED = POTIONS.register("long_strong_poison_extended", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 1800, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_POISON = POTIONS.register("very_long_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 18000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_POISON_EXTENDED = POTIONS.register("very_long_poison_extended", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 27000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALING = POTIONS.register("very_strong_healing", () -> new HunterPotion("healing", new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALING_EXTENDED = POTIONS.register("very_strong_healing_extended", () -> new HunterPotion("healing", new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 3)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WATER_BREATHING = POTIONS.register("very_long_water_breathing", () -> new HunterPotion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WATER_BREATHING_EXTENDED = POTIONS.register("very_long_water_breathing_extended", () -> new HunterPotion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, 144000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SLOWNESS = POTIONS.register("very_strong_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, 400, 5)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SLOWNESS_EXTENDED = POTIONS.register("very_strong_slowness_extended", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, 600, 5)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOWNESS = POTIONS.register("very_long_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOWNESS_EXTENDED = POTIONS.register("very_long_slowness_extended", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, 72000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SLOWNESS = POTIONS.register("long_strong_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, 4800, 3)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SLOWNESS_EXTENDED = POTIONS.register("long_strong_slowness_extended", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, 7200, 3)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SWIFTNESS = POTIONS.register("very_strong_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.SPEED, 1800, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SWIFTNESS_EXTENDED = POTIONS.register("very_strong_swiftness_extended", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.SPEED, 2700, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SWIFTNESS = POTIONS.register("very_long_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.SPEED, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SWIFTNESS_EXTENDED = POTIONS.register("very_long_swiftness_extended", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.SPEED, 72000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SWIFTNESS = POTIONS.register("long_strong_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.SPEED, 4800, 1)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SWIFTNESS_EXTENDED = POTIONS.register("long_strong_swiftness_extended", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.SPEED, 7200, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_FIRE_RESISTANCE = POTIONS.register("very_long_fire_resistance", () -> new HunterPotion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_FIRE_RESISTANCE_EXTENDED = POTIONS.register("very_long_fire_resistance_extended", () -> new HunterPotion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 144000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_LEAPING = POTIONS.register("very_strong_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, 1800, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_LEAPING_EXTENDED = POTIONS.register("very_strong_leaping_extended", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, 2700, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_LEAPING = POTIONS.register("very_long_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_LEAPING_EXTENDED = POTIONS.register("very_long_leaping_extended", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, 144000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_LEAPING = POTIONS.register("long_strong_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, 9600, 1)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_LEAPING_EXTENDED = POTIONS.register("long_strong_leaping_extended", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, 14400, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_INVISIBILITY = POTIONS.register("very_long_invisibility", () -> new HunterPotion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_INVISIBILITY_EXTENDED = POTIONS.register("very_long_invisibility_extended", () -> new HunterPotion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 144000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NIGHT_VISION = POTIONS.register("very_long_night_vision", () -> new HunterPotion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NIGHT_VISION_EXTENDED = POTIONS.register("very_long_night_vision_extended", () -> new HunterPotion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, 144000)));
    public static final DeferredHolder<Potion, HunterPotion> NAUSEA = POTIONS.register("nausea", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.NAUSEA, 1200)));
    public static final DeferredHolder<Potion, HunterPotion> NAUSEA_EXTENDED = POTIONS.register("nausea_extended", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.NAUSEA, 1800)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_NAUSEA = POTIONS.register("long_nausea", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.NAUSEA, 2400)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_NAUSEA_EXTENDED = POTIONS.register("long_nausea_extended", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.NAUSEA, 3600)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NAUSEA = POTIONS.register("very_long_nausea", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.NAUSEA, 24000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NAUSEA_EXTENDED = POTIONS.register("very_long_nausea_extended", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.NAUSEA, 36000)));
    public static final DeferredHolder<Potion, HunterPotion> BLINDNESS = POTIONS.register("blindness", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 1200)));
    public static final DeferredHolder<Potion, HunterPotion> BLINDNESS_EXTENDED = POTIONS.register("blindness_extended", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 1800)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_BLINDNESS = POTIONS.register("long_blindness", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 4800)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_BLINDNESS_EXTENDED = POTIONS.register("long_blindness_extended", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 7200)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_BLINDNESS = POTIONS.register("very_long_blindness", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 24000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_BLINDNESS_EXTENDED = POTIONS.register("very_long_blindness_extended", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 36000)));
    public static final DeferredHolder<Potion, HunterPotion> HEALTH_BOOST = POTIONS.register("health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200)));
    public static final DeferredHolder<Potion, HunterPotion> HEALTH_BOOST_EXTENDED = POTIONS.register("health_boost_extended", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 1800)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_HEALTH_BOOST = POTIONS.register("long_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 4800)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_HEALTH_BOOST_EXTENDED = POTIONS.register("long_health_boost_extended", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 7200)));
    public static final DeferredHolder<Potion, HunterPotion> STRONG_HEALTH_BOOST = POTIONS.register("strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 1)));
    public static final DeferredHolder<Potion, HunterPotion> STRONG_HEALTH_BOOST_EXTENDED = POTIONS.register("strong_health_boost_extended", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 600, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_HEALTH_BOOST = POTIONS.register("very_long_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_HEALTH_BOOST_EXTENDED = POTIONS.register("very_long_health_boost_extended", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 72000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALTH_BOOST = POTIONS.register("very_strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALTH_BOOST_EXTENDED = POTIONS.register("very_strong_health_boost_extended", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 600, 2)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_HEALTH_BOOST = POTIONS.register("long_strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 1)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_HEALTH_BOOST_EXTENDED = POTIONS.register("long_strong_health_boost_extended", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 1800, 1)));
    public static final DeferredHolder<Potion, Potion> GARLIC = POTIONS.register("garlic", () -> new Potion("garlic", new MobEffectInstance(ModEffects.GARLIC, 1200)));

    //Vampire
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
        veryDurable(Potions.LONG_STRENGTH, VERY_LONG_STRENGTH, VERY_LONG_STRENGTH_EXTENDED);
        veryStrong(VERY_LONG_STRENGTH, LONG_STRONG_STRENGTH, LONG_STRONG_STRENGTH_EXTENDED);
        veryStrongDracula(VERY_LONG_STRENGTH_EXTENDED, LONG_STRONG_STRENGTH_EXTENDED);
        veryDurable(Potions.LONG_REGENERATION, VERY_LONG_REGENERATION, VERY_LONG_REGENERATION_EXTENDED);
        veryStrong(Potions.STRONG_REGENERATION, VERY_STRONG_REGENERATION, VERY_STRONG_REGENERATION_EXTENDED);
        veryDurable(VERY_STRONG_REGENERATION, LONG_STRONG_REGENERATION, LONG_STRONG_REGENERATION_EXTENDED);
        veryDurableDracula(VERY_STRONG_REGENERATION_EXTENDED, LONG_STRONG_REGENERATION_EXTENDED);
        veryStrong(VERY_LONG_REGENERATION, LONG_STRONG_REGENERATION, LONG_STRONG_REGENERATION_EXTENDED);
        veryStrongDracula(VERY_LONG_REGENERATION_EXTENDED, LONG_STRONG_REGENERATION_EXTENDED);
        veryDurable(Potions.LONG_POISON, VERY_LONG_POISON, VERY_LONG_POISON_EXTENDED);
        veryStrong(Potions.STRONG_POISON, VERY_STRONG_POISON, VERY_STRONG_POISON_EXTENDED);
        veryDurable(VERY_STRONG_POISON, LONG_STRONG_POISON, LONG_STRONG_POISON_EXTENDED);
        veryDurableDracula(VERY_STRONG_POISON_EXTENDED, LONG_STRONG_POISON_EXTENDED);
        veryStrong(VERY_LONG_POISON, LONG_STRONG_POISON, LONG_STRONG_POISON_EXTENDED);
        veryStrongDracula(VERY_LONG_POISON_EXTENDED, LONG_STRONG_POISON_EXTENDED);
        veryStrong(Potions.STRONG_HEALING, VERY_STRONG_HEALING, VERY_STRONG_HEALING_EXTENDED);
        veryDurable(Potions.LONG_WATER_BREATHING, VERY_LONG_WATER_BREATHING, VERY_LONG_WATER_BREATHING_EXTENDED);
        veryDurable(Potions.LONG_SLOWNESS, VERY_LONG_SLOWNESS, VERY_LONG_SLOWNESS_EXTENDED);
        veryStrong(Potions.STRONG_SLOWNESS, VERY_STRONG_SLOWNESS, VERY_STRONG_SLOWNESS_EXTENDED);
        veryDurable(VERY_STRONG_SLOWNESS, LONG_STRONG_SLOWNESS, LONG_STRONG_SLOWNESS_EXTENDED);
        veryDurableDracula(VERY_STRONG_SLOWNESS_EXTENDED, LONG_STRONG_SLOWNESS_EXTENDED);
        veryStrong(VERY_LONG_SLOWNESS, LONG_STRONG_SLOWNESS, LONG_STRONG_SLOWNESS_EXTENDED);
        veryStrongDracula(VERY_LONG_SLOWNESS_EXTENDED, LONG_STRONG_SLOWNESS_EXTENDED);
        veryDurable(Potions.LONG_SWIFTNESS, VERY_LONG_SWIFTNESS, VERY_LONG_SWIFTNESS_EXTENDED);
        veryStrong(Potions.STRONG_SWIFTNESS, VERY_STRONG_SWIFTNESS, VERY_STRONG_SWIFTNESS_EXTENDED);
        veryDurable(VERY_STRONG_SWIFTNESS, LONG_STRONG_SWIFTNESS, LONG_STRONG_SWIFTNESS_EXTENDED);
        veryDurableDracula(VERY_STRONG_SWIFTNESS_EXTENDED, LONG_STRONG_SWIFTNESS_EXTENDED);
        veryStrong(VERY_LONG_SWIFTNESS, LONG_STRONG_SWIFTNESS, LONG_STRONG_SWIFTNESS_EXTENDED);
        veryStrongDracula(VERY_LONG_SWIFTNESS_EXTENDED, LONG_STRONG_SWIFTNESS_EXTENDED);
        veryDurable(Potions.LONG_FIRE_RESISTANCE, VERY_LONG_FIRE_RESISTANCE, VERY_LONG_FIRE_RESISTANCE_EXTENDED);
        veryStrong(Potions.STRONG_LEAPING, VERY_STRONG_LEAPING, VERY_STRONG_LEAPING_EXTENDED);
        veryDurable(Potions.LONG_LEAPING, VERY_LONG_LEAPING, VERY_LONG_LEAPING_EXTENDED);
        veryDurable(VERY_STRONG_LEAPING, LONG_STRONG_LEAPING, LONG_STRONG_LEAPING_EXTENDED);
        veryDurableDracula(VERY_STRONG_LEAPING_EXTENDED, LONG_STRONG_LEAPING_EXTENDED);
        veryStrong(VERY_LONG_LEAPING, LONG_STRONG_LEAPING, LONG_STRONG_LEAPING_EXTENDED);
        veryStrongDracula(VERY_LONG_LEAPING_EXTENDED, LONG_STRONG_LEAPING_EXTENDED);
        veryDurable(Potions.LONG_INVISIBILITY, VERY_LONG_INVISIBILITY, VERY_LONG_INVISIBILITY_EXTENDED);
        veryDurable(Potions.LONG_NIGHT_VISION, VERY_LONG_NIGHT_VISION, VERY_LONG_NIGHT_VISION_EXTENDED);
        master(NAUSEA, () -> Ingredient.of(registryAccess.lookupOrThrow(Registries.ITEM).getOrThrow(Tags.Items.MUSHROOMS)), 8, 4);
        masterSovereign(NAUSEA_EXTENDED, () -> Ingredient.of(registryAccess.lookupOrThrow(Registries.ITEM).getOrThrow(Tags.Items.MUSHROOMS)), 8, 4);
        durable(NAUSEA, LONG_NAUSEA, LONG_NAUSEA_EXTENDED);
        veryDurable(LONG_NAUSEA, VERY_LONG_NAUSEA, VERY_LONG_NAUSEA_EXTENDED);
        veryDurableDracula(LONG_NAUSEA_EXTENDED, VERY_LONG_NAUSEA_EXTENDED);
        master(BLINDNESS, () -> Ingredient.of(Items.INK_SAC), 12, 6);
        masterSovereign(BLINDNESS_EXTENDED, () -> Ingredient.of(Items.INK_SAC), 12, 6);
        durable(BLINDNESS, LONG_BLINDNESS, LONG_BLINDNESS_EXTENDED);
        veryDurable(LONG_BLINDNESS, VERY_LONG_BLINDNESS, VERY_LONG_BLINDNESS_EXTENDED);
        veryDurableDracula(LONG_BLINDNESS_EXTENDED, VERY_LONG_BLINDNESS_EXTENDED);
        master(HEALTH_BOOST, () -> Ingredient.of(Items.APPLE), 16, 8);
        masterSovereign(HEALTH_BOOST_EXTENDED, () -> Ingredient.of(Items.APPLE), 16, 8);
        durable(HEALTH_BOOST, LONG_HEALTH_BOOST, LONG_HEALTH_BOOST_EXTENDED);
        strong(HEALTH_BOOST, STRONG_HEALTH_BOOST, STRONG_HEALTH_BOOST_EXTENDED);
        veryDurable(LONG_HEALTH_BOOST, VERY_LONG_HEALTH_BOOST, VERY_LONG_HEALTH_BOOST_EXTENDED);
        veryDurableDracula(LONG_HEALTH_BOOST_EXTENDED, VERY_LONG_HEALTH_BOOST_EXTENDED);
        veryStrong(STRONG_HEALTH_BOOST, VERY_STRONG_HEALTH_BOOST, VERY_STRONG_HEALTH_BOOST_EXTENDED);
        veryStrongDracula(STRONG_HEALTH_BOOST_EXTENDED, VERY_STRONG_HEALTH_BOOST_EXTENDED);
        veryDurable(VERY_STRONG_HEALTH_BOOST, LONG_STRONG_HEALTH_BOOST, LONG_STRONG_HEALTH_BOOST_EXTENDED);
        veryDurableDracula(VERY_STRONG_HEALTH_BOOST_EXTENDED, LONG_STRONG_HEALTH_BOOST_EXTENDED);
        veryStrong(VERY_LONG_HEALTH_BOOST, LONG_STRONG_HEALTH_BOOST, LONG_STRONG_HEALTH_BOOST_EXTENDED);
        veryStrongDracula(VERY_LONG_HEALTH_BOOST_EXTENDED, LONG_STRONG_HEALTH_BOOST_EXTENDED);
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
