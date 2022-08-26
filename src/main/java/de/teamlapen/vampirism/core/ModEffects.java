package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.effects.*;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Handles all potion registrations and reference.
 */
public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, REFERENCE.MODID);

    public static final RegistryObject<MobEffect> SANGUINARE = EFFECTS.register("sanguinare", () -> new SanguinareEffect(MobEffectCategory.NEUTRAL, 0x6A0888));
    public static final RegistryObject<MobEffect> SATURATION = EFFECTS.register("saturation", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xDCFF00));
    public static final RegistryObject<MobEffect> SUNSCREEN = EFFECTS.register("sunscreen", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xFFF100).addAttributeModifier(ModAttributes.SUNDAMAGE.get(), "9dc9420c-3e5e-41c7-9ba4-ff70e9dc69fc", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> DISGUISE_AS_VAMPIRE = EFFECTS.register("disguise_as_vampire", () -> new VampirismEffect(MobEffectCategory.NEUTRAL, 0x999900));
    public static final RegistryObject<MobEffect> FIRE_PROTECTION = EFFECTS.register("fire_protection", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 14981690));
    public static final RegistryObject<MobEffect> GARLIC = EFFECTS.register("garlic", () -> new VampirismEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> POISON = EFFECTS.register("poison", () -> new VampirismPoisonEffect(0x4E9331));
    public static final RegistryObject<MobEffect> FREEZE = EFFECTS.register("freeze", FreezeEffect::new);
    public static final RegistryObject<MobEffect> NEONATAL = EFFECTS.register("neonatal", () -> new VampirismEffect(MobEffectCategory.NEUTRAL, 0xFFBBBB).addAttributeModifier(Attributes.ATTACK_DAMAGE, "377d132d-d091-43b2-8a8f-b940f9bc894c", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.MOVEMENT_SPEED, "ad6d7def-46e2-485f-afba-39252767f114", -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> OBLIVION = EFFECTS.register("oblivion", OblivionEffect::new);
    public static final RegistryObject<MobEffect> ARMOR_REGENERATION = EFFECTS.register("armor_regeneration", () -> new VampirismEffect(MobEffectCategory.NEUTRAL, 0xD17642));
    public static final RegistryObject<MobEffect> BAD_OMEN_HUNTER = EFFECTS.register("bad_omen_hunter", () -> new BadOmenEffect() {
        @Override
        public IFaction<?> getFaction() {
            return VReference.HUNTER_FACTION;
        }
    });
    public static final RegistryObject<MobEffect> BAD_OMEN_VAMPIRE = EFFECTS.register("bad_omen_vampire", () -> new BadOmenEffect() {
        @Override
        public IFaction<?> getFaction() {
            return VReference.VAMPIRE_FACTION;
        }
    });
    public static final RegistryObject<MobEffect> LORD_SPEED = EFFECTS.register("lord_speed", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.MOVEMENT_SPEED, "efe607d8-db8a-4156-b9d0-6a0640593057", 0.07F, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> LORD_ATTACK_SPEED = EFFECTS.register("lord_attack_speed", () -> new VampirismEffect(MobEffectCategory.BENEFICIAL, 0xffffff).addAttributeModifier(Attributes.ATTACK_SPEED, "a2ca9534-3baf-404f-b159-bc835bf963e6", 0.05F, AttributeModifier.Operation.MULTIPLY_TOTAL));

    private static final Logger LOGGER = LogManager.getLogger();
    private static MobEffect modded_night_vision;  //Substituted version
    private static MobEffect vanilla_night_vision; //Vanilla night vision instance


    static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }

    static void replaceEffects(@NotNull IForgeRegistry<MobEffect> registry) {
        vanilla_night_vision = MobEffects.NIGHT_VISION;
        modded_night_vision = new VampirismNightVisionPotion();
        registry.register(registry.getKey(vanilla_night_vision), modded_night_vision);
    }

    static void fixNightVisionEffectTypesUnsafe() {
        /*We have to fix the vanilla night vision potion types as they are created using the vanilla night vision potion before it can be replaced
        There are two options:
        1) Substitute the potion types too
            Less hacky
            If the vanilla duration or other things change it is less likely to be noticed (in development)
            Issue with JEI/Bug
            Annoying Forge warning
        2) Update the potion object in these potion types using reflection

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

    public static void fixMappings(@NotNull MissingMappingsEvent event) {
        event.getAllMappings(ForgeRegistries.Keys.MOB_EFFECTS).forEach(missingMapping -> {
            if ("vampirism:thirst".equals(missingMapping.getKey().toString())) {
                missingMapping.remap(MobEffects.HUNGER);
            }
        });
    }


}
