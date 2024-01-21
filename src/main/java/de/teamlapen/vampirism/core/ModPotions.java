package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.effects.VampirismPotion.HunterPotion;
import de.teamlapen.vampirism.mixin.accessor.PotionBrewingAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.NonNullSupplier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, REFERENCE.MODID);

    //Hunter

    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOW_FALLING = POTIONS.register("very_long_slow_falling", () -> new HunterPotion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_LUCK = POTIONS.register("long_luck", () -> new HunterPotion("luck", new MobEffectInstance(MobEffects.LUCK, 60000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WEAKNESS = POTIONS.register("very_long_weakness", () -> new HunterPotion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_STRENGTH = POTIONS.register("very_long_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_STRENGTH = POTIONS.register("long_strong_strength", () -> new HunterPotion("strength", new MobEffectInstance(MobEffects.DAMAGE_BOOST, 4800, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_REGENERATION = POTIONS.register("very_strong_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 450, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_REGENERATION = POTIONS.register("very_long_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 18000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_REGENERATION = POTIONS.register("long_strong_regeneration", () -> new HunterPotion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, 1200, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_POISON = POTIONS.register("very_strong_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 432, 2)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_POISON = POTIONS.register("long_strong_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 1200, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_POISON = POTIONS.register("very_long_poison", () -> new HunterPotion("poison", new MobEffectInstance(MobEffects.POISON, 18000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALING = POTIONS.register("very_strong_healing", () -> new HunterPotion("healing", new MobEffectInstance(MobEffects.HEAL, 1, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_WATER_BREATHING = POTIONS.register("very_long_water_breathing", () -> new HunterPotion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SLOWNESS = POTIONS.register("very_strong_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 5)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SLOWNESS = POTIONS.register("very_long_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SLOWNESS = POTIONS.register("long_strong_slowness", () -> new HunterPotion("slowness", new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 4800, 3)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_SWIFTNESS = POTIONS.register("very_strong_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_SWIFTNESS = POTIONS.register("very_long_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_SWIFTNESS = POTIONS.register("long_strong_swiftness", () -> new HunterPotion("swiftness", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 4800, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_FIRE_RESISTANCE = POTIONS.register("very_long_fire_resistance", () -> new HunterPotion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_LEAPING = POTIONS.register("very_strong_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP, 1800, 2)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_LEAPING = POTIONS.register("very_long_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_LEAPING = POTIONS.register("long_strong_leaping", () -> new HunterPotion("leaping", new MobEffectInstance(MobEffects.JUMP, 9600, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_INVISIBILITY = POTIONS.register("very_long_invisibility", () -> new HunterPotion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NIGHT_VISION = POTIONS.register("very_long_night_vision", () -> new HunterPotion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, 96000)));
    public static final DeferredHolder<Potion, HunterPotion> NAUSEA = POTIONS.register("nausea", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.CONFUSION, 1200)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_NAUSEA = POTIONS.register("long_nausea", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.CONFUSION, 2400)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_NAUSEA = POTIONS.register("very_long_nausea", () -> new HunterPotion("nausea", new MobEffectInstance(MobEffects.CONFUSION, 24000)));
    public static final DeferredHolder<Potion, HunterPotion> BLINDNESS = POTIONS.register("blindness", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.BLINDNESS, 1200)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_BLINDNESS = POTIONS.register("long_blindness", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 4800)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_BLINDNESS = POTIONS.register("very_long_blindness", () -> new HunterPotion("blindness", new MobEffectInstance(MobEffects.BLINDNESS, 24000)));
    public static final DeferredHolder<Potion, HunterPotion> HEALTH_BOOST = POTIONS.register("health_boost", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_HEALTH_BOOST = POTIONS.register("long_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 4800)));
    public static final DeferredHolder<Potion, HunterPotion> STRONG_HEALTH_BOOST = POTIONS.register("strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 1)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_LONG_HEALTH_BOOST = POTIONS.register("very_long_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 48000)));
    public static final DeferredHolder<Potion, HunterPotion> VERY_STRONG_HEALTH_BOOST = POTIONS.register("very_strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 2)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_STRONG_HEALTH_BOOST = POTIONS.register("long_strong_health_boost", () -> new HunterPotion("health_boost", new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 1)));
    public static final DeferredHolder<Potion, HunterPotion> RESISTANCE = POTIONS.register("resistance", () -> new HunterPotion(null, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800)));
    public static final DeferredHolder<Potion, HunterPotion> LONG_RESISTANCE = POTIONS.register("long_resistance", () -> new HunterPotion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4800)));
    public static final DeferredHolder<Potion, HunterPotion> STRONG_RESISTANCE = POTIONS.register("strong_resistance", () -> new HunterPotion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800, 1)));
    public static final DeferredHolder<Potion, Potion> GARLIC = POTIONS.register("garlic", () -> new Potion(new MobEffectInstance(ModEffects.GARLIC.get(), 1200)));

    //Vampire
    public static final DeferredHolder<Potion, VampirismPotion> VAMPIRE_FIRE_RESISTANCE = POTIONS.register("vampire_fire_resistance", () -> new VampirismPotion(null, new MobEffectInstance(ModEffects.FIRE_PROTECTION.get(), 3600, 5)));
    public static final DeferredHolder<Potion, VampirismPotion> LONG_VAMPIRE_FIRE_RESISTANCE = POTIONS.register("long_vampire_fire_resistance", () -> new VampirismPotion("vampire_fire_resistance", new MobEffectInstance(ModEffects.FIRE_PROTECTION.get(), 9600, 5)));

    public static void register(IEventBus bus) {
        POTIONS.register(bus);
    }

    public static void registerPotionMixes() {
        veryDurable(() -> Potions.LUCK, LONG_LUCK);
        veryDurable(() -> Potions.LONG_SLOW_FALLING, VERY_LONG_SLOW_FALLING);
        veryDurable(() -> Potions.LONG_WEAKNESS, VERY_LONG_WEAKNESS);
        veryDurable(() -> Potions.LONG_STRENGTH, VERY_LONG_STRENGTH);
        veryStrong(VERY_LONG_STRENGTH, LONG_STRONG_STRENGTH);
        veryDurable(() -> Potions.LONG_REGENERATION, VERY_LONG_REGENERATION);
        veryStrong(() -> Potions.STRONG_REGENERATION, VERY_STRONG_REGENERATION);
        veryDurable(VERY_STRONG_REGENERATION, LONG_STRONG_REGENERATION);
        veryStrong(VERY_LONG_REGENERATION, LONG_STRONG_REGENERATION);
        veryDurable(() -> Potions.LONG_POISON, VERY_LONG_POISON);
        veryStrong(() -> Potions.STRONG_POISON, VERY_STRONG_POISON);
        veryDurable(VERY_STRONG_POISON, LONG_STRONG_POISON);
        veryStrong(VERY_LONG_POISON, LONG_STRONG_POISON);
        veryStrong(() -> Potions.STRONG_HEALING, VERY_STRONG_HEALING);
        veryDurable(() -> Potions.LONG_WATER_BREATHING, VERY_LONG_WATER_BREATHING);
        veryDurable(() -> Potions.LONG_SLOWNESS, VERY_LONG_SLOWNESS);
        veryStrong(() -> Potions.STRONG_SLOWNESS, VERY_STRONG_SLOWNESS);
        veryDurable(VERY_STRONG_SLOWNESS, LONG_STRONG_SLOWNESS);
        veryStrong(VERY_LONG_SLOWNESS, LONG_STRONG_SLOWNESS);
        veryDurable(() -> Potions.LONG_SWIFTNESS, VERY_LONG_SWIFTNESS);
        veryStrong(() -> Potions.STRONG_SWIFTNESS, VERY_STRONG_SWIFTNESS);
        veryDurable(VERY_STRONG_SWIFTNESS, LONG_STRONG_SWIFTNESS);
        veryStrong(VERY_LONG_SWIFTNESS, LONG_STRONG_SWIFTNESS);
        veryDurable(() -> Potions.LONG_FIRE_RESISTANCE, VERY_LONG_FIRE_RESISTANCE);
        veryStrong(() -> Potions.STRONG_LEAPING, VERY_STRONG_LEAPING);
        veryDurable(() -> Potions.LONG_LEAPING, VERY_LONG_LEAPING);
        veryDurable(VERY_STRONG_LEAPING, LONG_STRONG_LEAPING);
        veryStrong(VERY_LONG_LEAPING, LONG_STRONG_LEAPING);
        veryDurable(() -> Potions.LONG_INVISIBILITY, VERY_LONG_INVISIBILITY);
        veryDurable(() -> Potions.LONG_NIGHT_VISION, VERY_LONG_NIGHT_VISION);
        master(NAUSEA, () -> Ingredient.of(Tags.Items.MUSHROOMS), 32, 16);
        durable(NAUSEA, LONG_NAUSEA);
        veryDurable(LONG_NAUSEA, VERY_LONG_NAUSEA);
        master(BLINDNESS, () -> Ingredient.of(Items.INK_SAC), 64, 32);
        durable(BLINDNESS, LONG_BLINDNESS);
        veryDurable(LONG_BLINDNESS, VERY_LONG_BLINDNESS);
        master(HEALTH_BOOST, () -> Ingredient.of(Items.APPLE), 64, 32);
        durable(HEALTH_BOOST, LONG_HEALTH_BOOST);
        strong(HEALTH_BOOST, STRONG_HEALTH_BOOST);
        veryDurable(LONG_HEALTH_BOOST, VERY_LONG_HEALTH_BOOST);
        veryStrong(STRONG_HEALTH_BOOST, VERY_STRONG_HEALTH_BOOST);
        veryDurable(VERY_STRONG_HEALTH_BOOST, LONG_STRONG_HEALTH_BOOST);
        veryStrong(VERY_LONG_HEALTH_BOOST, LONG_STRONG_HEALTH_BOOST);
        master(RESISTANCE, () -> Ingredient.of(Items.GOLDEN_APPLE), 20, 10);
        durable(RESISTANCE, LONG_RESISTANCE);
        strong(RESISTANCE, STRONG_RESISTANCE);
        PotionBrewingAccessor.addMix(Potions.WATER, ModItems.ITEM_GARLIC.get(), GARLIC.get());
    }

    private static void durable(Supplier<? extends Potion> in, Supplier<? extends Potion> out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.REDSTONE), 1).blood().build());
    }

    private static void strong(Supplier<? extends Potion> in, Supplier<? extends Potion> out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.GLOWSTONE_DUST), 1).blood().build());
    }

    private static void veryDurable(Supplier<? extends Potion> in, Supplier<? extends Potion> out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.REDSTONE_BLOCK), 32, 16).blood().durable().build());
    }

    private static void veryStrong(Supplier<? extends Potion> in, Supplier<? extends Potion> out) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(in, out).ingredient(() -> Ingredient.of(Items.GLOWSTONE), 64, 32).blood().concentrated().build());
    }

    private static void master(Supplier<? extends Potion> out, Supplier<Ingredient> in, int count, int countReduced) {
        VampirismAPI.extendedBrewingRecipeRegistry().addMix(new ExtendedPotionMix.Builder(() -> Potions.AWKWARD, out).master().ingredient(in, count, countReduced).blood().build());
    }
}
