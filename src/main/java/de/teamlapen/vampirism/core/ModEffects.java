package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.effects.*;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potions;

/**
 * Handles all potion registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
public class ModEffects {
    public static final MobEffect sanguinare = getNull();
    public static final MobEffect thirst = getNull();
    public static final MobEffect saturation = getNull();
    public static final MobEffect sunscreen = getNull();
    public static final MobEffect disguise_as_vampire = getNull();
    public static final MobEffect fire_protection = getNull();
    public static final MobEffect garlic = getNull();
    public static final MobEffect poison = getNull();
    public static final MobEffect freeze = getNull();
    public static final MobEffect neonatal = getNull();
    public static final MobEffect oblivion = getNull();
    public static final MobEffect armor_regeneration = getNull();
    public static final MobEffect bad_omen_hunter = getNull();
    public static final MobEffect bad_omen_vampire = getNull();
    private static final Logger LOGGER = LogManager.getLogger();
    private static MobEffect modded_night_vision;  //Substituted version
    private static MobEffect vanilla_night_vision; //Vanilla night vision instance


    static void registerEffects(IForgeRegistry<MobEffect> registry) {
        vanilla_night_vision = MobEffects.NIGHT_VISION;
        registry.register(new VampirismNightVisionPotion());
        registry.register(new ThirstEffect("thirst", MobEffectCategory.HARMFUL, 859494));
        registry.register(new SanguinareEffect("sanguinare", MobEffectCategory.NEUTRAL, 0x6A0888));
        registry.register(new VampirismEffect("saturation", MobEffectCategory.BENEFICIAL, 0xDCFF00));
        registry.register(new VampirismEffect("sunscreen", MobEffectCategory.BENEFICIAL, 0xFFF100).addAttributeModifier(ModAttributes.sundamage, "9dc9420c-3e5e-41c7-9ba4-ff70e9dc69fc", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
        registry.register(new VampirismEffect("fire_protection", MobEffectCategory.BENEFICIAL, 14981690));
        registry.register(new VampirismEffect("disguise_as_vampire", MobEffectCategory.NEUTRAL, 0x999900));
        registry.register(new VampirismEffect("garlic", MobEffectCategory.HARMFUL, 0xFFFFFF));
        registry.register(new VampirismPoisonEffect("poison", 0x4E9331));
        registry.register(new FreezeEffect("freeze"));
        registry.register(new VampirismEffect("neonatal", MobEffectCategory.NEUTRAL, 0xFFBBBB).addAttributeModifier(Attributes.ATTACK_DAMAGE, "377d132d-d091-43b2-8a8f-b940f9bc894c", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.MOVEMENT_SPEED, "ad6d7def-46e2-485f-afba-39252767f114", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL));
        registry.register(new OblivionEffect("oblivion", MobEffectCategory.NEUTRAL, 0x4E9331));
        registry.register(new VampirismEffect("armor_regeneration", MobEffectCategory.NEUTRAL, 0xD17642));
        registry.register(new BadOmenEffect(REFERENCE.MODID, REFERENCE.HUNTER_PLAYER_KEY) {
            @Override
            public IFaction<?> getFaction() {
                return VReference.HUNTER_FACTION;
            }
        });
        registry.register(new BadOmenEffect(REFERENCE.MODID, REFERENCE.VAMPIRE_PLAYER_KEY) {
            @Override
            public IFaction<?> getFaction() {
                return VReference.VAMPIRE_FACTION;
            }
        });
    }

    static void fixNightVisionEffectTypes() {
        /*We have to fix the vanilla night vision potion types as they are created using the vanilla night vision potion before it can be replaced
        There are two options:
        1) Substitute the potion types too
            Less hacky
            If the vanilla duration or other things change it is less likely to be noticed (in development)
            Issue with JEI/Bug
            Annoying Forge warning
        2) Update the potion object in these potion types using reflection
            Reflection :/

        Using 2) for now
        */
        LOGGER.info("Fixing vanilla night vision potion types");
        try {
            for (MobEffectInstance effect : Potions.NIGHT_VISION.getEffects()) {
                if (effect.getEffect().equals(vanilla_night_vision)) { //If still referring to vanilla potion replace
                    ObfuscationReflectionHelper.setPrivateValue(MobEffectInstance.class, effect, modded_night_vision, SRGNAMES.EffectInstance_effect);
                }
            }
            for (MobEffectInstance effect : Potions.LONG_NIGHT_VISION.getEffects()) {
                if (effect.getEffect().equals(vanilla_night_vision)) {
                    ObfuscationReflectionHelper.setPrivateValue(MobEffectInstance.class, effect, modded_night_vision, SRGNAMES.EffectInstance_effect);
                }
            }
        } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            LOGGER.error("Unable to modify vanilla night vision types. Potion tileInventory and more might not work", e);
        }
    }

    static boolean checkNightVision() {
        if (!(MobEffects.NIGHT_VISION instanceof VampirismNightVisionPotion)) {
            LOGGER.warn("Vampirism was not able to register it's night vision potion");
            return false;
        }
        return true;
    }
}
