package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.effects.VampirismPotion.HunterPotion;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class ModPotions {
    public static final DeferredRegister<Potion> POTION_TYPES = DeferredRegister.create(ForgeRegistries.POTION_TYPES, REFERENCE.MODID);

    //Hunter
    public static final RegistryObject<HunterPotion> VERY_LONG_SLOW_FALLING = POTION_TYPES.register("very_long_slow_falling", () -> new HunterPotion("slow_falling", new EffectInstance(Effects.SLOW_FALLING, 48000)));
    public static final RegistryObject<HunterPotion> LONG_LUCK = POTION_TYPES.register("long_luck", () -> new HunterPotion("luck", new EffectInstance(Effects.LUCK, 60000)));
    public static final RegistryObject<HunterPotion> VERY_LONG_WEAKNESS = POTION_TYPES.register("very_long_weakness", () -> new HunterPotion("weakness", new EffectInstance(Effects.WEAKNESS, 48000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_STRENGTH = POTION_TYPES.register("very_strong_strength", () -> new HunterPotion("strength", new EffectInstance(Effects.DAMAGE_BOOST, 600, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_STRENGTH = POTION_TYPES.register("very_long_strength", () -> new HunterPotion("strength", new EffectInstance(Effects.DAMAGE_BOOST, 96000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_STRENGTH = POTION_TYPES.register("long_strong_strength", () -> new HunterPotion("strength", new EffectInstance(Effects.DAMAGE_BOOST, 4800, 1)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_REGENERATION = POTION_TYPES.register("very_strong_regeneration", () -> new HunterPotion("regeneration", new EffectInstance(Effects.REGENERATION, 450, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_REGENERATION = POTION_TYPES.register("very_long_regeneration", () -> new HunterPotion("regeneration", new EffectInstance(Effects.REGENERATION, 18000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_REGENERATION = POTION_TYPES.register("long_strong_regeneration", () -> new HunterPotion("regeneration", new EffectInstance(Effects.REGENERATION, 1200, 1)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_POISON = POTION_TYPES.register("very_strong_poison", () -> new HunterPotion("poison", new EffectInstance(Effects.POISON, 432, 2)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_POISON = POTION_TYPES.register("long_strong_poison", () -> new HunterPotion("poison", new EffectInstance(Effects.POISON, 1200, 1)));
    public static final RegistryObject<HunterPotion> VERY_LONG_POISON = POTION_TYPES.register("very_long_poison", () -> new HunterPotion("poison", new EffectInstance(Effects.POISON, 18000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_HEALING = POTION_TYPES.register("very_strong_healing", () -> new HunterPotion("healing", new EffectInstance(Effects.HEAL, 1, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_WATER_BREATHING = POTION_TYPES.register("very_long_water_breathing", () -> new HunterPotion("water_breathing", new EffectInstance(Effects.WATER_BREATHING, 96000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_SLOWNESS = POTION_TYPES.register("very_strong_slowness", () -> new HunterPotion("slowness", new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 400, 5)));
    public static final RegistryObject<HunterPotion> VERY_LONG_SLOWNESS = POTION_TYPES.register("very_long_slowness", () -> new HunterPotion("slowness", new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 48000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_SLOWNESS = POTION_TYPES.register("long_strong_slowness", () -> new HunterPotion("slowness", new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 4800, 3)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_SWIFTNESS = POTION_TYPES.register("very_strong_swiftness", () -> new HunterPotion("swiftness", new EffectInstance(Effects.MOVEMENT_SPEED, 1200, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_SWIFTNESS = POTION_TYPES.register("very_long_swiftness", () -> new HunterPotion("swiftness", new EffectInstance(Effects.MOVEMENT_SPEED, 48000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_SWIFTNESS = POTION_TYPES.register("long_strong_swiftness", () -> new HunterPotion("swiftness", new EffectInstance(Effects.MOVEMENT_SPEED, 4800, 1)));
    public static final RegistryObject<HunterPotion> VERY_LONG_FIRE_RESISTANCE = POTION_TYPES.register("very_long_fire_resistance", () -> new HunterPotion("fire_resistance", new EffectInstance(Effects.FIRE_RESISTANCE, 96000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_LEAPING = POTION_TYPES.register("very_strong_leaping", () -> new HunterPotion("leaping", new EffectInstance(Effects.JUMP, 1800, 2)));
    public static final RegistryObject<HunterPotion> VERY_LONG_LEAPING = POTION_TYPES.register("very_long_leaping", () -> new HunterPotion("leaping", new EffectInstance(Effects.JUMP, 96000)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_LEAPING = POTION_TYPES.register("long_strong_leaping", () -> new HunterPotion("leaping", new EffectInstance(Effects.JUMP, 9600, 1)));
    public static final RegistryObject<HunterPotion> VERY_LONG_INVISIBILITY = POTION_TYPES.register("very_long_invisibility", () -> new HunterPotion("invisibility", new EffectInstance(Effects.INVISIBILITY, 96000)));
    public static final RegistryObject<HunterPotion> VERY_LONG_NIGHT_VISION = POTION_TYPES.register("very_long_night_vision", () -> new HunterPotion("night_vision", new EffectInstance(Effects.NIGHT_VISION, 96000)));
    public static final RegistryObject<HunterPotion> NAUSEA = POTION_TYPES.register("nausea", () -> new HunterPotion(null, new EffectInstance(Effects.CONFUSION, 1200)));
    public static final RegistryObject<HunterPotion> LONG_NAUSEA = POTION_TYPES.register("long_nausea", () -> new HunterPotion("nausea", new EffectInstance(Effects.CONFUSION, 2400)));
    public static final RegistryObject<HunterPotion> VERY_LONG_NAUSEA = POTION_TYPES.register("very_long_nausea", () -> new HunterPotion("nausea", new EffectInstance(Effects.CONFUSION, 24000)));
    public static final RegistryObject<HunterPotion> BLINDNESS = POTION_TYPES.register("blindness", () -> new HunterPotion(null, new EffectInstance(Effects.BLINDNESS, 1200)));
    public static final RegistryObject<HunterPotion> LONG_BLINDNESS = POTION_TYPES.register("long_blindness", () -> new HunterPotion("blindness", new EffectInstance(Effects.BLINDNESS, 4800)));
    public static final RegistryObject<HunterPotion> VERY_LONG_BLINDNESS = POTION_TYPES.register("very_long_blindness", () -> new HunterPotion("blindness", new EffectInstance(Effects.BLINDNESS, 24000)));
    public static final RegistryObject<HunterPotion> HEALTH_BOOST = POTION_TYPES.register("health_boost", () -> new HunterPotion(null, new EffectInstance(Effects.HEALTH_BOOST, 1200)));
    public static final RegistryObject<HunterPotion> LONG_HEALTH_BOOST = POTION_TYPES.register("long_health_boost", () -> new HunterPotion("health_boost", new EffectInstance(Effects.HEALTH_BOOST, 4800)));
    public static final RegistryObject<HunterPotion> STRONG_HEALTH_BOOST = POTION_TYPES.register("strong_health_boost", () -> new HunterPotion("health_boost", new EffectInstance(Effects.HEALTH_BOOST, 400, 1)));
    public static final RegistryObject<HunterPotion> VERY_LONG_HEALTH_BOOST = POTION_TYPES.register("very_long_health_boost", () -> new HunterPotion("health_boost", new EffectInstance(Effects.HEALTH_BOOST, 48000)));
    public static final RegistryObject<HunterPotion> VERY_STRONG_HEALTH_BOOST = POTION_TYPES.register("very_strong_health_boost", () -> new HunterPotion("health_boost", new EffectInstance(Effects.HEALTH_BOOST, 400, 2)));
    public static final RegistryObject<HunterPotion> LONG_STRONG_HEALTH_BOOST = POTION_TYPES.register("long_strong_health_boost", () -> new HunterPotion("health_boost", new EffectInstance(Effects.HEALTH_BOOST, 1200, 1)));
    public static final RegistryObject<HunterPotion> RESISTANCE = POTION_TYPES.register("resistance", () -> new HunterPotion(null, new EffectInstance(Effects.DAMAGE_RESISTANCE, 1800)));
    public static final RegistryObject<HunterPotion> LONG_RESISTANCE = POTION_TYPES.register("long_resistance", () -> new HunterPotion("resistance", new EffectInstance(Effects.DAMAGE_RESISTANCE, 4800)));
    public static final RegistryObject<HunterPotion> STRONG_RESISTANCE = POTION_TYPES.register("strong_resistance", () -> new HunterPotion("resistance", new EffectInstance(Effects.DAMAGE_RESISTANCE, 400, 1)));

    //Vampire
    public static final RegistryObject<VampirismPotion> VAMPIRE_FIRE_RESISTANCE = POTION_TYPES.register("vampire_fire_resistance", () -> new VampirismPotion(null, new EffectInstance(ModEffects.FIRE_PROTECTION.get(), 3600, 5)));
    public static final RegistryObject<VampirismPotion> LONG_VAMPIRE_FIRE_RESISTANCE = POTION_TYPES.register("long_vampire_fire_resistance", () -> new VampirismPotion("vampire_fire_resistance", new EffectInstance(ModEffects.FIRE_PROTECTION.get(), 9600, 5)));

    public static void registerPotions(IEventBus bus) {
        POTION_TYPES.register(bus);
    }

    public static void registerPotionMixes() {
        IExtendedBrewingRecipeRegistry registry = VampirismAPI.extendedBrewingRecipeRegistry();
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
            switch (missingMapping.key.toString()) {
                case "vampirism:long_strong_resistance":
                case "vampirism:very_long_resistance":
                    missingMapping.remap(LONG_RESISTANCE.get());
                    break;
                case "vampirism:very_strong_resistance":
                    missingMapping.remap(STRONG_RESISTANCE.get());
                    break;
                case "vampirism:thirst":
                case "vampirism:long_thirst":
                case "vampirism:strong_thirst":
                case "vampirism:very_long_thirst":
                case "vampirism:very_strong_thirst":
                case "vampirism:long_strong_thirst":
                    missingMapping.ignore();
                    break;
                case "vampirism:very_strong_harming":
                    missingMapping.remap(Potions.STRONG_HARMING);
                    break;
            }
        });
    }
}
