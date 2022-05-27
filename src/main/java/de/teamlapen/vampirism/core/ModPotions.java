package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.effects.VampirismPotion.HunterPotion;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, REFERENCE.MODID);

    //Hunter

    public static final RegistryObject<HunterPotion> VERY_LONG_SLOW_FALLING = POTIONS.register("very_long_slow_falling", () -> new HunterPotion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, 48000)));
    public static final RegistryObject<HunterPotion> LONG_LUCK = POTIONS.register("long_luck", () -> new HunterPotion("luck", new MobEffectInstance(MobEffects.LUCK, 60000)));
    public static final RegistryObject<HunterPotion> VERY_LONG_WEAKNESS = POTIONS.register("very_long_weakness", () -> new HunterPotion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, 48000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_STRENGTH = POTIONS.register("very_strong_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_STRENGTH = POTIONS.register("very_long_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 96000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_STRENGTH = POTIONS.register("long_strong_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 4800, 1)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_REGENERATION = POTIONS.register("very_strong_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 450, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_REGENERATION = POTIONS.register("very_long_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 18000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_REGENERATION = POTIONS.register("long_strong_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 1200, 1)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_POISON = POTIONS.register("very_strong_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 432, 2)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_POISON = POTIONS.register("long_strong_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 1200, 1)));
    public static final RegistryObject<HunterPotion> VERY_LONG_POISON = POTIONS.register("very_long_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 18000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_HARMING = POTIONS.register("very_strong_harming", () -> new HunterPotion("harming", new MobEffectInstance(MobEffects.HARM, 1, 2)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_HEALING = POTIONS.register("very_strong_healing", () -> new HunterPotion("healing", new MobEffectInstance(MobEffects.HEAL, 1, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_WATER_BREATHING = POTIONS.register("very_long_water_breathing", () -> new HunterPotion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, 96000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_SLOWNESS = POTIONS.register("very_strong_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 5)));
    public static final RegistryObject<HunterPotion> VERY_LONG_SLOWNESS = POTIONS.register("very_long_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 48000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_SLOWNESS = POTIONS.register("long_strong_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 4800, 3)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_SWIFTNESS = POTIONS.register("very_strong_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_SWIFTNESS = POTIONS.register("very_long_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 48000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_SWIFTNESS = POTIONS.register("long_strong_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 4800, 1)));
    public static final RegistryObject<HunterPotion> VERY_LONG_FIRE_RESISTANCE = POTIONS.register("very_long_fire_resistance", () -> new HunterPotion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 96000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_LEAPING = POTIONS.register("very_strong_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP, 1800, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_LEAPING = POTIONS.register("very_long_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP, 96000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_LEAPING = POTIONS.register("long_strong_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP, 9600, 1)));
    public static final RegistryObject<HunterPotion> VERY_LONG_INVISIBILITY = POTIONS.register("very_long_invisibility", () -> new HunterPotion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 96000)));
    public static final RegistryObject<HunterPotion> VERY_LONG_NIGHT_VISION = POTIONS.register("very_long_night_vision", () -> new HunterPotion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, 96000)));
    public static final RegistryObject<HunterPotion> NAUSEA = POTIONS.register("nausea", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.CONFUSION, 1200)));
    public static final RegistryObject<HunterPotion> LONG_NAUSEA = POTIONS.register("long_nausea", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.CONFUSION, 2400)));
    public static final RegistryObject<HunterPotion> VERY_LONG_NAUSEA = POTIONS.register("very_long_nausea", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.CONFUSION, 24000)));
    public static final RegistryObject<HunterPotion> THIRST = POTIONS.register("thirst", () -> new HunterPotion(null, new MobEffectInstance(ModEffects.THIRST.get(), 1200)));
    public static final RegistryObject<HunterPotion> LONG_THIRST = POTIONS.register("long_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.THIRST.get(), 4800)));
    public static final RegistryObject<HunterPotion> STRONG_THIRST = POTIONS.register("strong_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.THIRST.get(), 400, 1)));
    public static final RegistryObject<HunterPotion> VERY_LONG_THIRST = POTIONS.register("very_long_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.THIRST.get(), 24000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_THIRST = POTIONS.register("very_strong_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.THIRST.get(), 1200, 2)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_THIRST = POTIONS.register("long_strong_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.THIRST.get(), 9600, 1)));
    public static final RegistryObject<HunterPotion> BLINDNESS = POTIONS.register("blindness", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.BLINDNESS, 1200)));
    public static final RegistryObject<HunterPotion> LONG_BLINDNESS = POTIONS.register("long_blindness", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 4800)));
    public static final RegistryObject<HunterPotion> VERY_LONG_BLINDNESS = POTIONS.register("very_long_blindness", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 24000)));
    public static final RegistryObject<HunterPotion> HEALTH_BOOST = POTIONS.register("health_boost", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200)));
    public static final RegistryObject<HunterPotion> LONG_HEALTH_BOOST = POTIONS.register("long_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 4800)));
    public static final RegistryObject<HunterPotion> STRONG_HEALTH_BOOST = POTIONS.register("strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 1)));
    public static final RegistryObject<HunterPotion> VERY_LONG_HEALTH_BOOST = POTIONS.register("very_long_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 48000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_HEALTH_BOOST = POTIONS.register("very_strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 2)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_HEALTH_BOOST = POTIONS.register("long_strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 1)));
    public static final RegistryObject<HunterPotion> RESISTANCE = POTIONS.register("resistance", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800)));
    public static final RegistryObject<HunterPotion> LONG_RESISTANCE = POTIONS.register("long_resistance", () -> new HunterPotion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4800)));
    public static final RegistryObject<HunterPotion> STRONG_RESISTANCE = POTIONS.register("strong_resistance", () -> new HunterPotion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 1)));

    //Vampire
    public static final RegistryObject<VampirismPotion> VAMPIRE_FIRE_RESISTANCE = POTIONS.register("vampire_fire_resistance", () -> new VampirismPotion(null, new MobEffectInstance(ModEffects.FIRE_PROTECTION.get(), 3600, 5)));
    public static final RegistryObject<VampirismPotion> LONG_VAMPIRE_FIRE_RESISTANCE = POTIONS.register("long_vampire_fire_resistance", () -> new VampirismPotion("vampire_fire_resistance", new MobEffectInstance(ModEffects.FIRE_PROTECTION.get(), 9600, 5)));

    public static void registerPotions(IEventBus bus) {
        POTIONS.register(bus);
    }

    public static void registerPotionMixes() {
        veryDurable(Potions.LUCK, LONG_LUCK.get());
        veryDurable(Potions.LONG_SLOW_FALLING, VERY_LONG_SLOW_FALLING.get());
        veryDurable(Potions.LONG_WEAKNESS, VERY_LONG_WEAKNESS.get());
        veryStrong(Potions.STRONG_STRENGTH, VERY_STRONG_STRENGTH.get());
        veryDurable(Potions.LONG_STRENGTH, VERY_LONG_STRENGTH.get());
        veryDurable(VERY_STRONG_STRENGTH.get(), LONG_STRONG_STRENGTH.get());
        veryStrong(VERY_LONG_STRENGTH.get(), LONG_STRONG_STRENGTH.get());
        veryDurable(Potions.LONG_REGENERATION, VERY_LONG_REGENERATION.get());
        veryStrong(Potions.STRONG_REGENERATION, VERY_STRONG_REGENERATION.get());
        veryDurable(VERY_STRONG_REGENERATION.get(), LONG_STRONG_REGENERATION.get());
        veryStrong(VERY_LONG_REGENERATION.get(), LONG_STRONG_REGENERATION.get());
        veryDurable(Potions.LONG_POISON, VERY_LONG_POISON.get());
        veryStrong(Potions.STRONG_POISON, VERY_STRONG_POISON.get());
        veryDurable(VERY_STRONG_POISON.get(), LONG_STRONG_POISON.get());
        veryStrong(VERY_LONG_POISON.get(), LONG_STRONG_POISON.get());
        veryStrong(Potions.STRONG_HARMING, VERY_STRONG_HARMING.get());
        veryStrong(Potions.STRONG_HEALING, VERY_STRONG_HEALING.get());
        veryDurable(Potions.LONG_WATER_BREATHING, VERY_LONG_WATER_BREATHING.get());
        veryDurable(Potions.LONG_SLOWNESS, VERY_LONG_SLOWNESS.get());
        veryStrong(Potions.STRONG_SLOWNESS, VERY_STRONG_SLOWNESS.get());
        veryDurable(VERY_STRONG_SLOWNESS.get(), LONG_STRONG_SLOWNESS.get());
        veryStrong(VERY_LONG_SLOWNESS.get(), LONG_STRONG_SLOWNESS.get());
        veryDurable(Potions.LONG_SWIFTNESS, VERY_LONG_SWIFTNESS.get());
        veryStrong(Potions.STRONG_SWIFTNESS, VERY_STRONG_SWIFTNESS.get());
        veryDurable(VERY_STRONG_SWIFTNESS.get(), LONG_STRONG_SWIFTNESS.get());
        veryStrong(VERY_LONG_SWIFTNESS.get(), LONG_STRONG_SWIFTNESS.get());
        veryDurable(Potions.LONG_FIRE_RESISTANCE, VERY_LONG_FIRE_RESISTANCE.get());
        veryStrong(Potions.STRONG_LEAPING, VERY_STRONG_LEAPING.get());
        veryDurable(Potions.LONG_LEAPING, VERY_LONG_LEAPING.get());
        veryDurable(VERY_STRONG_LEAPING.get(), LONG_STRONG_LEAPING.get());
        veryStrong(VERY_LONG_LEAPING.get(), LONG_STRONG_LEAPING.get());
        veryDurable(Potions.LONG_INVISIBILITY, VERY_LONG_INVISIBILITY.get());
        veryDurable(Potions.LONG_NIGHT_VISION, VERY_LONG_NIGHT_VISION.get());
        master(NAUSEA.get(), () -> Ingredient.of(Tags.Items.MUSHROOMS), 32, 16);
        durable(NAUSEA.get(), LONG_NAUSEA.get());
        veryDurable(LONG_NAUSEA.get(), VERY_LONG_NAUSEA.get());
        master(THIRST.get(), () -> Ingredient.of(ModItems.VAMPIRE_FANG.get()), 10, 5);
        durable(THIRST.get(), LONG_THIRST.get());
        strong(THIRST.get(), STRONG_THIRST.get());
        veryDurable(LONG_THIRST.get(), VERY_LONG_THIRST.get());
        veryStrong(STRONG_THIRST.get(), VERY_STRONG_THIRST.get());
        veryDurable(VERY_STRONG_THIRST.get(), LONG_STRONG_THIRST.get());
        veryStrong(VERY_LONG_THIRST.get(), LONG_STRONG_THIRST.get());
        master(BLINDNESS.get(), () -> Ingredient.of(Items.INK_SAC), 64, 32);
        durable(BLINDNESS.get(), LONG_BLINDNESS.get());
        veryDurable(LONG_BLINDNESS.get(), VERY_LONG_BLINDNESS.get());
        master(HEALTH_BOOST.get(), () -> Ingredient.of(Items.APPLE), 64, 32);
        durable(HEALTH_BOOST.get(), LONG_HEALTH_BOOST.get());
        strong(HEALTH_BOOST.get(), STRONG_HEALTH_BOOST.get());
        veryDurable(LONG_HEALTH_BOOST.get(), VERY_LONG_HEALTH_BOOST.get());
        veryStrong(STRONG_HEALTH_BOOST.get(), VERY_STRONG_HEALTH_BOOST.get());
        veryDurable(VERY_STRONG_HEALTH_BOOST.get(), LONG_STRONG_HEALTH_BOOST.get());
        veryStrong(VERY_LONG_HEALTH_BOOST.get(), LONG_STRONG_HEALTH_BOOST.get());
        master(RESISTANCE.get(), () -> Ingredient.of(Items.GOLDEN_APPLE), 20, 10);
        durable(RESISTANCE.get(), LONG_RESISTANCE.get());
        strong(RESISTANCE.get(), STRONG_RESISTANCE.get());
    }

    private static void durable(Potion in, Potion out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.REDSTONE), 1).blood().build());
    }

    private static void strong(Potion in, Potion out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.GLOWSTONE_DUST), 1).blood().build());
    }

    private static void veryDurable(Potion in, Potion out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.REDSTONE_BLOCK), 32, 16).blood().durable().build());
    }

    private static void veryStrong(Potion in, Potion out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.GLOWSTONE), 64, 32).blood().concentrated().build());
    }

    private static void master(Potion out, NonNullSupplier<Ingredient> in, int count, int countReduced) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(Potions.AWKWARD, out).master().ingredient(in, count, countReduced).blood().build());
    }


    public static void fixMappings(RegistryEvent.MissingMappings<Potion> event) {
        event.getAllMappings().forEach(missingMapping -> {
            String key = missingMapping.key.toString();
            if (key.equals("vampirism:long_strong_resistance") || key.equals("vampirism:very_long_resistance")) {
                missingMapping.remap(ModPotions.LONG_RESISTANCE.get());
            } else if (key.equals("vampirism:very_strong_resistance")) {
                missingMapping.remap(ModPotions.STRONG_RESISTANCE.get());
            }
        });
    }
}
