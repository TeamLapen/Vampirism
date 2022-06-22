package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.effects.*;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles all potion registrations and reference.
 */
public class ModEffects {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, REFERENCE.MODID);

    public static final RegistryObject<SanguinareEffect> SANGUINARE = EFFECTS.register("sanguinare", () -> new SanguinareEffect(EffectType.NEUTRAL, 0x6A0888));
    public static final RegistryObject<VampirismEffect> SATURATION = EFFECTS.register("saturation", () -> new VampirismEffect(EffectType.BENEFICIAL, 0xDCFF00));
    public static final RegistryObject<VampirismEffect> SUNSCREEN = EFFECTS.register("sunscreen", () -> (VampirismEffect) new VampirismEffect(EffectType.BENEFICIAL, 0xFFF100).addAttributeModifier(ModAttributes.SUNDAMAGE.get(), "9dc9420c-3e5e-41c7-9ba4-ff70e9dc69fc", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<VampirismEffect> FIRE_PROTECTION = EFFECTS.register("fire_protection", () -> new VampirismEffect(EffectType.BENEFICIAL, 14981690));
    public static final RegistryObject<VampirismEffect> DISGUISE_AS_VAMPIRE = EFFECTS.register("disguise_as_vampire", () -> new VampirismEffect(EffectType.NEUTRAL, 0x999900));
    public static final RegistryObject<VampirismEffect> GARLIC = EFFECTS.register("garlic", () -> new VampirismEffect(EffectType.HARMFUL, 0xFFFFFF));
    public static final RegistryObject<VampirismPoisonEffect> POISON = EFFECTS.register("poison", () -> new VampirismPoisonEffect(0x4E9331));
    public static final RegistryObject<FreezeEffect> FREEZE = EFFECTS.register("freeze", FreezeEffect::new);
    public static final RegistryObject<VampirismEffect> NEONATAL = EFFECTS.register("neonatal", () -> (VampirismEffect) new VampirismEffect(EffectType.NEUTRAL, 0xFFBBBB).addAttributeModifier(Attributes.ATTACK_DAMAGE, "377d132d-d091-43b2-8a8f-b940f9bc894c", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.MOVEMENT_SPEED, "ad6d7def-46e2-485f-afba-39252767f114", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<OblivionEffect> OBLIVION = EFFECTS.register("oblivion", () -> new OblivionEffect(EffectType.NEUTRAL, 0x4E9331));
    public static final RegistryObject<VampirismEffect> ARMOR_REGENERATION = EFFECTS.register("armor_regeneration", () -> new VampirismEffect(EffectType.NEUTRAL, 0xD17642));

    public static final RegistryObject<BadOmenEffect> BAD_OMEN_HUNTER = EFFECTS.register("bad_omen_" + REFERENCE.HUNTER_PLAYER_KEY.getPath(), () -> new BadOmenEffect() {
                @Override
                public IFaction<?> getFaction() {
                    return VReference.HUNTER_FACTION;
                }
            });
    public static final RegistryObject<BadOmenEffect> BAD_OMEN_VAMPIRE = EFFECTS.register("bad_omen_" + REFERENCE.VAMPIRE_PLAYER_KEY.getPath(), () -> new BadOmenEffect() {
                @Override
                public IFaction<?> getFaction() {
                    return VReference.VAMPIRE_FACTION;
                }
            });

    private static final Logger LOGGER = LogManager.getLogger();
    private static Effect modded_night_vision;  //Substituted version
    private static Effect vanilla_night_vision; //Vanilla night vision instance


    static void replaceEffects(IForgeRegistry<Effect> registry) {
        vanilla_night_vision = Effects.NIGHT_VISION;
        modded_night_vision = new VampirismNightVisionPotion();
        registry.register(modded_night_vision);
    }

    static void registerEffects(IEventBus bus) {
        EFFECTS.register(bus);
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
            for (EffectInstance effect : Potions.NIGHT_VISION.getEffects()) {
                if (effect.getEffect().equals(vanilla_night_vision)) { //If still referring to vanilla potion replace
                    ObfuscationReflectionHelper.setPrivateValue(EffectInstance.class, effect, modded_night_vision, SRGNAMES.EffectInstance_potion);
                }
            }
            for (EffectInstance effect : Potions.LONG_NIGHT_VISION.getEffects()) {
                if (effect.getEffect().equals(vanilla_night_vision)) {
                    ObfuscationReflectionHelper.setPrivateValue(EffectInstance.class, effect, modded_night_vision, SRGNAMES.EffectInstance_potion);
                }
            }
        } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            LOGGER.error("Unable to modify vanilla night vision types. Potion tileInventory and more might not work", e);
        }
    }

    static boolean checkNightVision() {
        if (!(Effects.NIGHT_VISION instanceof VampirismNightVisionPotion)) {
            LOGGER.warn("Vampirism was not able to register it's night vision potion");
            return false;
        }
        return true;
    }

    public static void fixMappings(RegistryEvent.MissingMappings<Effect> event) {
        event.getAllMappings().forEach(missingMapping -> {
            switch (missingMapping.key.toString()) {
                case "vampirism:thirst":
                    missingMapping.remap(Effects.HUNGER);
                    break;
            }
        });
    }
}
