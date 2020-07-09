package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.api.items.MixPredicate;
import de.teamlapen.vampirism.potion.VampirismPotion;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
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

    }

    public static void registerPotionMixes() {
        IExtendedBrewingRecipeRegistry registry = VampirismAPI.extendedBrewingRecipeRegistry();
        registry.addMix(new MixPredicate.Builder(Potions.LONG_SLOW_FALLING, very_long_slow_falling).ingredient(Ingredient.fromItems(Items.REDSTONE_BLOCK), 64, 32).durable().build());

    }

}
