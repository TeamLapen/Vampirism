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

    public static final RegistryObject<HunterPotion> very_long_slow_falling = POTIONS.register("very_long_slow_falling", () -> new HunterPotion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, 48000)));
    public static final RegistryObject<HunterPotion> long_luck = POTIONS.register("long_luck", () -> new HunterPotion("luck", new MobEffectInstance(MobEffects.LUCK, 60000)));
    public static final RegistryObject<HunterPotion> very_long_weakness = POTIONS.register("very_long_weakness", () -> new HunterPotion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, 48000)));
    public static final RegistryObject<HunterPotion> very_strong_strength = POTIONS.register("very_strong_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 2)));
    public static final RegistryObject<HunterPotion> very_long_strength = POTIONS.register("very_long_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 96000)));
    public static final RegistryObject<HunterPotion> long_strong_strength = POTIONS.register("long_strong_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 4800, 1)));
    public static final RegistryObject<HunterPotion> very_strong_regeneration = POTIONS.register("very_strong_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 450, 2)));
    public static final RegistryObject<HunterPotion> very_long_regeneration = POTIONS.register("very_long_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 18000)));
    public static final RegistryObject<HunterPotion> long_strong_regeneration = POTIONS.register("long_strong_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 1200, 1)));
    public static final RegistryObject<HunterPotion> very_strong_poison = POTIONS.register("very_strong_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 432, 2)));
    public static final RegistryObject<HunterPotion> long_strong_poison = POTIONS.register("long_strong_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 1200, 1)));
    public static final RegistryObject<HunterPotion> very_long_poison = POTIONS.register("very_long_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 18000)));
    public static final RegistryObject<HunterPotion> very_strong_harming = POTIONS.register("very_strong_harming", () -> new HunterPotion("harming", new MobEffectInstance(MobEffects.HARM, 1, 2)));
    public static final RegistryObject<HunterPotion> very_strong_healing = POTIONS.register("very_strong_healing", () -> new HunterPotion("healing", new MobEffectInstance(MobEffects.HEAL, 1, 2)));
    public static final RegistryObject<HunterPotion> very_long_water_breathing = POTIONS.register("very_long_water_breathing", () -> new HunterPotion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, 96000)));
    public static final RegistryObject<HunterPotion> very_strong_slowness = POTIONS.register("very_strong_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 5)));
    public static final RegistryObject<HunterPotion> very_long_slowness = POTIONS.register("very_long_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 48000)));
    public static final RegistryObject<HunterPotion> long_strong_slowness = POTIONS.register("long_strong_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 4800, 3)));
    public static final RegistryObject<HunterPotion> very_strong_swiftness = POTIONS.register("very_strong_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 2)));
    public static final RegistryObject<HunterPotion> very_long_swiftness = POTIONS.register("very_long_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 48000)));
    public static final RegistryObject<HunterPotion> long_strong_swiftness = POTIONS.register("long_strong_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 4800, 1)));
    public static final RegistryObject<HunterPotion> very_long_fire_resistance = POTIONS.register("very_long_fire_resistance", () -> new HunterPotion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 96000)));
    public static final RegistryObject<HunterPotion> very_strong_leaping = POTIONS.register("very_strong_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP, 1800, 2)));
    public static final RegistryObject<HunterPotion> very_long_leaping = POTIONS.register("very_long_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP, 96000)));
    public static final RegistryObject<HunterPotion> long_strong_leaping = POTIONS.register("long_strong_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP, 9600, 1)));
    public static final RegistryObject<HunterPotion> very_long_invisibility = POTIONS.register("very_long_invisibility", () -> new HunterPotion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 96000)));
    public static final RegistryObject<HunterPotion> very_long_night_vision = POTIONS.register("very_long_night_vision", () -> new HunterPotion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, 96000)));
    public static final RegistryObject<HunterPotion> nausea = POTIONS.register("nausea", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.CONFUSION, 1200)));
    public static final RegistryObject<HunterPotion> long_nausea = POTIONS.register("long_nausea", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.CONFUSION, 2400)));
    public static final RegistryObject<HunterPotion> very_long_nausea = POTIONS.register("very_long_nausea", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.CONFUSION, 24000)));
    public static final RegistryObject<HunterPotion> thirst = POTIONS.register("thirst", () -> new HunterPotion(null, new MobEffectInstance(ModEffects.thirst.get(), 1200)));
    public static final RegistryObject<HunterPotion> long_thirst = POTIONS.register("long_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.thirst.get(), 4800)));
    public static final RegistryObject<HunterPotion> strong_thirst = POTIONS.register("strong_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.thirst.get(), 400, 1)));
    public static final RegistryObject<HunterPotion> very_long_thirst = POTIONS.register("very_long_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.thirst.get(), 24000)));
    public static final RegistryObject<HunterPotion> very_strong_thirst = POTIONS.register("very_strong_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.thirst.get(), 1200, 2)));
    public static final RegistryObject<HunterPotion> long_strong_thirst = POTIONS.register("long_strong_thirst", () -> new HunterPotion("thirst", new MobEffectInstance(ModEffects.thirst.get(), 9600, 1)));
    public static final RegistryObject<HunterPotion> blindness = POTIONS.register("blindness", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.BLINDNESS, 1200)));
    public static final RegistryObject<HunterPotion> long_blindness = POTIONS.register("long_blindness", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 4800)));
    public static final RegistryObject<HunterPotion> very_long_blindness = POTIONS.register("very_long_blindness", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 24000)));
    public static final RegistryObject<HunterPotion> health_boost = POTIONS.register("health_boost", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200)));
    public static final RegistryObject<HunterPotion> long_health_boost = POTIONS.register("long_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 4800)));
    public static final RegistryObject<HunterPotion> strong_health_boost = POTIONS.register("strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 1)));
    public static final RegistryObject<HunterPotion> very_long_health_boost = POTIONS.register("very_long_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 48000)));
    public static final RegistryObject<HunterPotion> very_strong_health_boost = POTIONS.register("very_strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 2)));
    public static final RegistryObject<HunterPotion> long_strong_health_boost = POTIONS.register("long_strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 1)));
    public static final RegistryObject<HunterPotion> resistance = POTIONS.register("resistance", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800)));
    public static final RegistryObject<HunterPotion> long_resistance = POTIONS.register("long_resistance", () -> new HunterPotion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4800)));
    public static final RegistryObject<HunterPotion> strong_resistance = POTIONS.register("strong_resistance", () -> new HunterPotion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 1)));

    //Vampire
    public static final RegistryObject<VampirismPotion> vampire_fire_resistance = POTIONS.register("vampire_fire_resistance", () -> new VampirismPotion(null, new MobEffectInstance(ModEffects.fire_protection.get(), 3600, 5)));
    public static final RegistryObject<VampirismPotion> long_vampire_fire_resistance = POTIONS.register("long_vampire_fire_resistance", () -> new VampirismPotion("vampire_fire_resistance", new MobEffectInstance(ModEffects.fire_protection.get(), 9600, 5)));

    public static void registerPotions(IEventBus bus) {
        POTIONS.register(bus);
    }

    public static void registerPotionMixes() {
        veryDurable(Potions.LUCK, long_luck.get());
        veryDurable(Potions.LONG_SLOW_FALLING, very_long_slow_falling.get());
        veryDurable(Potions.LONG_WEAKNESS, very_long_weakness.get());
        veryStrong(Potions.STRONG_STRENGTH, very_strong_strength.get());
        veryDurable(Potions.LONG_STRENGTH, very_long_strength.get());
        veryDurable(very_strong_strength.get(), long_strong_strength.get());
        veryStrong(very_long_strength.get(), long_strong_strength.get());
        veryDurable(Potions.LONG_REGENERATION, very_long_regeneration.get());
        veryStrong(Potions.STRONG_REGENERATION, very_strong_regeneration.get());
        veryDurable(very_strong_regeneration.get(), long_strong_regeneration.get());
        veryStrong(very_long_regeneration.get(), long_strong_regeneration.get());
        veryDurable(Potions.LONG_POISON, very_long_poison.get());
        veryStrong(Potions.STRONG_POISON, very_strong_poison.get());
        veryDurable(very_strong_poison.get(), long_strong_poison.get());
        veryStrong(very_long_poison.get(), long_strong_poison.get());
        veryStrong(Potions.STRONG_HARMING, very_strong_harming.get());
        veryStrong(Potions.STRONG_HEALING, very_strong_healing.get());
        veryDurable(Potions.LONG_WATER_BREATHING, very_long_water_breathing.get());
        veryDurable(Potions.LONG_SLOWNESS, very_long_slowness.get());
        veryStrong(Potions.STRONG_SLOWNESS, very_strong_slowness.get());
        veryDurable(very_strong_slowness.get(), long_strong_slowness.get());
        veryStrong(very_long_slowness.get(), long_strong_slowness.get());
        veryDurable(Potions.LONG_SWIFTNESS, very_long_swiftness.get());
        veryStrong(Potions.STRONG_SWIFTNESS, very_strong_swiftness.get());
        veryDurable(very_strong_swiftness.get(), long_strong_swiftness.get());
        veryStrong(very_long_swiftness.get(), long_strong_swiftness.get());
        veryDurable(Potions.LONG_FIRE_RESISTANCE, very_long_fire_resistance.get());
        veryStrong(Potions.STRONG_LEAPING, very_strong_leaping.get());
        veryDurable(Potions.LONG_LEAPING, very_long_leaping.get());
        veryDurable(very_strong_leaping.get(), long_strong_leaping.get());
        veryStrong(very_long_leaping.get(), long_strong_leaping.get());
        veryDurable(Potions.LONG_INVISIBILITY, very_long_invisibility.get());
        veryDurable(Potions.LONG_NIGHT_VISION, very_long_night_vision.get());
        master(nausea.get(), () -> Ingredient.of(Tags.Items.MUSHROOMS), 32, 16);
        durable(nausea.get(), long_nausea.get());
        veryDurable(long_nausea.get(), very_long_nausea.get());
        master(thirst.get(), () -> Ingredient.of(ModItems.vampire_fang.get()), 10, 5);
        durable(thirst.get(), long_thirst.get());
        strong(thirst.get(), strong_thirst.get());
        veryDurable(long_thirst.get(), very_long_thirst.get());
        veryStrong(strong_thirst.get(), very_strong_thirst.get());
        veryDurable(very_strong_thirst.get(), long_strong_thirst.get());
        veryStrong(very_long_thirst.get(), long_strong_thirst.get());
        master(blindness.get(), () -> Ingredient.of(Items.INK_SAC), 64, 32);
        durable(blindness.get(), long_blindness.get());
        veryDurable(long_blindness.get(), very_long_blindness.get());
        master(health_boost.get(), () -> Ingredient.of(Items.APPLE), 64, 32);
        durable(health_boost.get(), long_health_boost.get());
        strong(health_boost.get(), strong_health_boost.get());
        veryDurable(long_health_boost.get(), very_long_health_boost.get());
        veryStrong(strong_health_boost.get(), very_strong_health_boost.get());
        veryDurable(very_strong_health_boost.get(), long_strong_health_boost.get());
        veryStrong(very_long_health_boost.get(), long_strong_health_boost.get());
        master(resistance.get(), () -> Ingredient.of(Items.GOLDEN_APPLE), 20, 10);
        durable(resistance.get(), long_resistance.get());
        strong(resistance.get(), strong_resistance.get());
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
                missingMapping.remap(ModPotions.long_resistance.get());
            } else if (key.equals("vampirism:very_strong_resistance")) {
                missingMapping.remap(ModPotions.strong_resistance.get());
            }
        });
    }
}
