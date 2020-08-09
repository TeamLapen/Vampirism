package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.potion.VampirismPotion;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModPotions {

    public static final VampirismPotion very_long_slow_falling = getNull();
    public static final VampirismPotion long_luck = getNull();
    public static final VampirismPotion very_long_weakness = getNull();
    public static final VampirismPotion very_strong_strength = getNull();
    public static final VampirismPotion very_long_strength = getNull();
    public static final VampirismPotion long_strong_strength = getNull();
    public static final VampirismPotion very_long_regeneration = getNull();
    public static final VampirismPotion very_strong_regeneration = getNull();
    public static final VampirismPotion long_strong_regeneration = getNull();
    public static final VampirismPotion very_long_poison = getNull();
    public static final VampirismPotion very_strong_poison = getNull();
    public static final VampirismPotion long_strong_poison = getNull();
    public static final VampirismPotion very_strong_harming = getNull();
    public static final VampirismPotion very_strong_healing = getNull();
    public static final VampirismPotion very_long_water_breathing = getNull();
    public static final VampirismPotion very_strong_slowness = getNull();
    public static final VampirismPotion very_long_slowness = getNull();
    public static final VampirismPotion long_strong_slowness = getNull();
    public static final VampirismPotion very_long_swiftness = getNull();
    public static final VampirismPotion very_strong_swiftness = getNull();
    public static final VampirismPotion long_strong_swiftness = getNull();
    public static final VampirismPotion very_long_fire_resistance = getNull();
    public static final VampirismPotion very_strong_leaping = getNull();
    public static final VampirismPotion very_long_leaping = getNull();
    public static final VampirismPotion long_strong_leaping = getNull();
    public static final VampirismPotion very_long_invisibility = getNull();
    public static final VampirismPotion very_long_night_vision = getNull();
    public static final VampirismPotion nausea = getNull();
    public static final VampirismPotion long_nausea = getNull();
    public static final VampirismPotion very_long_nausea = getNull();
    public static final VampirismPotion thirst = getNull();
    public static final VampirismPotion long_thirst = getNull();
    public static final VampirismPotion very_long_thirst = getNull();
    public static final VampirismPotion strong_thirst = getNull();
    public static final VampirismPotion very_strong_thirst = getNull();
    public static final VampirismPotion long_strong_thirst = getNull();
    public static final VampirismPotion blindness = getNull();
    public static final VampirismPotion long_blindness = getNull();
    public static final VampirismPotion very_long_blindness = getNull();
    public static final VampirismPotion health_boost = getNull();
    public static final VampirismPotion long_health_boost = getNull();
    public static final VampirismPotion very_long_health_boost = getNull();
    public static final VampirismPotion strong_health_boost = getNull();
    public static final VampirismPotion very_strong_health_boost = getNull();
    public static final VampirismPotion long_strong_health_boost = getNull();
    public static final VampirismPotion resistance = getNull();
    public static final VampirismPotion long_resistance = getNull();
    public static final VampirismPotion very_long_resistance = getNull();
    public static final VampirismPotion strong_resistance = getNull();
    public static final VampirismPotion very_strong_resistance = getNull();
    public static final VampirismPotion long_strong_resistance = getNull();


    public static void registerPotions(IForgeRegistry<Potion> registry) {
        registry.register(new VampirismPotion("very_long_slow_falling", "slow_falling", new EffectInstance(Effects.SLOW_FALLING, 48000)));
        registry.register(new VampirismPotion("long_luck", "luck", new EffectInstance(Effects.LUCK, 60000)));
        registry.register(new VampirismPotion("very_long_weakness", "weakness", new EffectInstance(Effects.WEAKNESS, 48000)));
        registry.register(new VampirismPotion("very_strong_strength", "strength", new EffectInstance(Effects.STRENGTH, 1800, 2)));
        registry.register(new VampirismPotion("very_long_strength", "strength", new EffectInstance(Effects.STRENGTH, 96000)));
        registry.register(new VampirismPotion("long_strong_strength", "strength", new EffectInstance(Effects.STRENGTH, 9600, 1)));
        registry.register(new VampirismPotion("very_strong_regeneration", "regeneration", new EffectInstance(Effects.REGENERATION, 450, 2)));
        registry.register(new VampirismPotion("very_long_regeneration", "regeneration", new EffectInstance(Effects.REGENERATION, 18000)));
        registry.register(new VampirismPotion("long_strong_regeneration", "regeneration", new EffectInstance(Effects.REGENERATION, 1800, 1)));
        registry.register(new VampirismPotion("very_strong_poison", "poison", new EffectInstance(Effects.POISON, 432, 2)));
        registry.register(new VampirismPotion("long_strong_poison", "poison", new EffectInstance(Effects.POISON, 1800, 1)));
        registry.register(new VampirismPotion("very_long_poison", "poison", new EffectInstance(Effects.POISON, 18000)));
        registry.register(new VampirismPotion("very_strong_harming", "harming", new EffectInstance(Effects.INSTANT_DAMAGE, 1, 2)));
        registry.register(new VampirismPotion("very_strong_healing", "healing", new EffectInstance(Effects.INSTANT_HEALTH, 1, 2)));
        registry.register(new VampirismPotion("very_long_water_breathing", "water_breathing", new EffectInstance(Effects.WATER_BREATHING, 96000)));
        registry.register(new VampirismPotion("very_strong_slowness", "slowness", new EffectInstance(Effects.SLOWNESS, 400, 5)));
        registry.register(new VampirismPotion("very_long_slowness", "slowness", new EffectInstance(Effects.SLOWNESS, 48000)));
        registry.register(new VampirismPotion("long_strong_slowness", "slowness", new EffectInstance(Effects.SLOWNESS, 4800, 3)));
        registry.register(new VampirismPotion("very_strong_swiftness", "swiftness", new EffectInstance(Effects.SPEED, 1800, 2)));
        registry.register(new VampirismPotion("very_long_swiftness", "swiftness", new EffectInstance(Effects.SPEED, 48000)));
        registry.register(new VampirismPotion("long_strong_swiftness", "swiftness", new EffectInstance(Effects.SPEED, 4800, 1)));
        registry.register(new VampirismPotion("very_long_fire_resistance", "fire_resistance", new EffectInstance(Effects.FIRE_RESISTANCE, 96000)));
        registry.register(new VampirismPotion("very_strong_leaping", "leaping", new EffectInstance(Effects.JUMP_BOOST, 1800, 2)));
        registry.register(new VampirismPotion("very_long_leaping", "leaping", new EffectInstance(Effects.JUMP_BOOST, 96000)));
        registry.register(new VampirismPotion("long_strong_leaping", "leaping", new EffectInstance(Effects.JUMP_BOOST, 9600, 1)));
        registry.register(new VampirismPotion("very_long_invisibility", "invisibility", new EffectInstance(Effects.INVISIBILITY, 96000)));
        registry.register(new VampirismPotion("very_long_night_vision", "night_vision", new EffectInstance(Effects.NIGHT_VISION, 96000)));
        registry.register(new VampirismPotion("nausea", null, new EffectInstance(Effects.NAUSEA, 1800)));
        registry.register(new VampirismPotion("long_nausea", "nausea", new EffectInstance(Effects.NAUSEA, 4800)));
        registry.register(new VampirismPotion("very_long_nausea", "nausea", new EffectInstance(Effects.NAUSEA, 48000)));
        registry.register(new VampirismPotion("thirst", null, new EffectInstance(ModEffects.thirst, 1800)));
        registry.register(new VampirismPotion("long_thirst", "thirst", new EffectInstance(ModEffects.thirst, 4800)));
        registry.register(new VampirismPotion("strong_thirst", "thirst", new EffectInstance(ModEffects.thirst, 400, 1)));
        registry.register(new VampirismPotion("very_long_thirst", "thirst", new EffectInstance(ModEffects.thirst, 48000)));
        registry.register(new VampirismPotion("very_strong_thirst", "thirst", new EffectInstance(ModEffects.thirst, 1800, 2)));
        registry.register(new VampirismPotion("long_strong_thirst", "thirst", new EffectInstance(ModEffects.thirst, 9600, 1)));
        registry.register(new VampirismPotion("blindness", null, new EffectInstance(Effects.BLINDNESS, 1800)));
        registry.register(new VampirismPotion("long_blindness", "blindness", new EffectInstance(Effects.BLINDNESS, 4800)));
        registry.register(new VampirismPotion("very_long_blindness", "blindness", new EffectInstance(Effects.BLINDNESS, 48000)));
        registry.register(new VampirismPotion("health_boost", null, new EffectInstance(Effects.HEALTH_BOOST, 1800)));
        registry.register(new VampirismPotion("long_health_boost", "health_boost", new EffectInstance(Effects.HEALTH_BOOST, 4800)));
        registry.register(new VampirismPotion("strong_health_boost", "health_boost", new EffectInstance(Effects.HEALTH_BOOST, 400, 1)));
        registry.register(new VampirismPotion("very_long_health_boost", "health_boost", new EffectInstance(Effects.HEALTH_BOOST, 48000)));
        registry.register(new VampirismPotion("very_strong_health_boost", "health_boost", new EffectInstance(Effects.HEALTH_BOOST, 400, 2)));
        registry.register(new VampirismPotion("long_strong_health_boost", "health_boost", new EffectInstance(Effects.HEALTH_BOOST, 1800, 1)));
        registry.register(new VampirismPotion("resistance", null, new EffectInstance(Effects.RESISTANCE, 1800)));
        registry.register(new VampirismPotion("long_resistance", "resistance", new EffectInstance(Effects.RESISTANCE, 4800)));
        registry.register(new VampirismPotion("strong_resistance", "resistance", new EffectInstance(Effects.RESISTANCE, 400, 1)));
        registry.register(new VampirismPotion("very_long_resistance", "resistance", new EffectInstance(Effects.RESISTANCE, 48000)));
        registry.register(new VampirismPotion("very_strong_resistance", "resistance", new EffectInstance(Effects.RESISTANCE, 400, 2)));
        registry.register(new VampirismPotion("long_strong_resistance", "resistance", new EffectInstance(Effects.RESISTANCE, 1800, 1)));

    }

    public static void registerPotionMixes() {
        IExtendedBrewingRecipeRegistry registry = VampirismAPI.extendedBrewingRecipeRegistry();
        veryDurable(Potions.LUCK, long_luck);
        veryDurable(Potions.LONG_SLOW_FALLING, very_long_slow_falling);
        veryDurable(Potions.LONG_WEAKNESS, very_long_weakness);
        veryStrong(Potions.STRONG_STRENGTH, very_strong_strength);
        veryDurable(Potions.LONG_STRENGTH, very_long_strength);
        veryDurable(very_strong_strength, long_strong_strength);
        veryStrong(very_long_strength, long_strong_strength);
        veryDurable(Potions.LONG_REGENERATION, very_long_regeneration);
        veryStrong(Potions.STRONG_REGENERATION, very_strong_regeneration);
        veryDurable(very_strong_regeneration, long_strong_regeneration);
        veryStrong(very_long_regeneration, long_strong_regeneration);
        veryDurable(Potions.LONG_POISON, very_long_poison);
        veryStrong(Potions.STRONG_POISON, very_strong_poison);
        veryDurable(very_strong_poison, long_strong_poison);
        veryStrong(very_long_poison, long_strong_poison);
        veryStrong(Potions.STRONG_HARMING, very_strong_harming);
        veryStrong(Potions.STRONG_HEALING, very_strong_healing);
        veryDurable(Potions.LONG_WATER_BREATHING, very_long_water_breathing);
        veryDurable(Potions.LONG_SLOWNESS, very_long_slowness);
        veryStrong(Potions.STRONG_SLOWNESS, very_strong_slowness);
        veryDurable(very_strong_slowness, long_strong_slowness);
        veryStrong(very_long_slowness, long_strong_slowness);
        veryDurable(Potions.LONG_SWIFTNESS, very_long_swiftness);
        veryStrong(Potions.STRONG_SWIFTNESS, very_strong_swiftness);
        veryDurable(very_strong_swiftness, long_strong_swiftness);
        veryStrong(very_long_swiftness, long_strong_swiftness);
        veryDurable(Potions.LONG_FIRE_RESISTANCE, very_long_fire_resistance);
        veryStrong(Potions.STRONG_LEAPING, very_strong_leaping);
        veryDurable(Potions.LONG_LEAPING, very_long_leaping);
        veryDurable(very_strong_leaping, long_strong_leaping);
        veryStrong(very_long_leaping, long_strong_leaping);
        veryDurable(Potions.LONG_INVISIBILITY, very_long_invisibility);
        veryDurable(Potions.LONG_NIGHT_VISION, very_long_night_vision);
        master(nausea, Ingredient.fromTag(Tags.Items.MUSHROOMS), 32, 16);
        durable(nausea, long_nausea);
        veryDurable(long_nausea, very_long_nausea);
        master(thirst, Ingredient.fromItems(ModItems.vampire_fang), 10, 5);
        durable(thirst, long_thirst);
        strong(thirst, strong_thirst);
        veryDurable(long_thirst, very_long_thirst);
        veryStrong(strong_thirst, very_strong_thirst);
        veryDurable(very_strong_thirst, long_strong_thirst);
        veryStrong(very_long_thirst, long_strong_thirst);
        master(blindness, Ingredient.fromItems(Items.INK_SAC), 64, 32);
        durable(blindness, long_blindness);
        veryDurable(long_blindness, very_long_blindness);
        master(health_boost, Ingredient.fromItems(Items.APPLE), 64, 32);
        durable(health_boost, long_health_boost);
        strong(health_boost, strong_health_boost);
        veryDurable(long_health_boost, very_long_health_boost);
        veryStrong(strong_health_boost, very_strong_health_boost);
        veryDurable(very_strong_health_boost, long_strong_health_boost);
        veryStrong(very_long_health_boost, long_strong_health_boost);
        master(resistance, Ingredient.fromItems(Items.GOLDEN_APPLE), 20, 10);
        durable(resistance, long_resistance);
        strong(resistance, strong_resistance);
        veryDurable(long_resistance, very_long_resistance);
        veryStrong(strong_resistance, very_strong_resistance);
        veryDurable(very_strong_resistance, long_strong_resistance);
        veryStrong(very_long_resistance, long_strong_resistance);
    }

    private static void durable(Potion in, Potion out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(Ingredient.fromItems(Items.REDSTONE), 1).blood().build());
    }

    private static void strong(Potion in, Potion out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(Ingredient.fromItems(Items.GLOWSTONE_DUST), 1).blood().build());
    }

    private static void veryDurable(Potion in, Potion out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(Ingredient.fromItems(Items.REDSTONE_BLOCK), 32, 16).blood().durable().build());
    }

    private static void veryStrong(Potion in, Potion out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(Ingredient.fromItems(Items.GLOWSTONE), 64, 32).blood().concentrated().build());
    }

    private static void master(Potion out, Ingredient in, int count, int countReduced) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(Potions.AWKWARD, out).master().ingredient(in, count, countReduced).blood().build());
    }


}
