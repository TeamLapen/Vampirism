package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.potion.PotionThirst;
import de.teamlapen.vampirism.potion.VampirismNightVisionPotion;
import de.teamlapen.vampirism.potion.VampirismPotion;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all potion registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
public class ModPotions {

    private static final Logger LOGGER = LogManager.getLogger(ModPotions.class);
    public static final Potion sanguinare = getNull();
    public static final Potion thirst = getNull();
    public static final Potion saturation = getNull();
    public static final Potion sunscreen = getNull();
    public static final Potion disguise_as_vampire = getNull();
    public static final Potion fire_protection = getNull();
    public static final Potion garlic = getNull();
    private static Potion modded_night_vision;  //Substituted version
    private static Potion vanilla_night_vision; //Vanilla night vision instance


    static void registerPotions(IForgeRegistry<Potion> registry) {
        registry.register(new PotionThirst("thirst", true, 859494));
        vanilla_night_vision = MobEffects.NIGHT_VISION;
        modded_night_vision = new VampirismNightVisionPotion();
        registry.register(modded_night_vision);
        registry.register(new PotionSanguinare("sanguinare", false, 0x6A0888));
        registry.register(new VampirismPotion("saturation", false, 0xDCFF00).setIconIndex(2, 0).setBeneficial());
        Potion sunscreen = new VampirismPotion("sunscreen", false, 0xFFF100).setIconIndex(3, 0).setBeneficial();
        sunscreen.registerPotionAttributeModifier(VReference.sunDamage, "9dc9420c-3e5e-41c7-9ba4-ff70e9dc69fc", -0.5, 2);
        registry.register(sunscreen);
        registry.register(new VampirismPotion("fire_protection", false, 14981690).setIconIndex(6, 0).setBeneficial());
        registry.register(new VampirismPotion("disguise_as_vampire", false, 0x999900).setIconIndex(4, 0).setBeneficial());
        registry.register(new VampirismPotion("garlic", true, 0xFFFFFF).setIconIndex(5, 0));
    }


    static void fixNightVisionPotionTypes() {
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
            for (PotionEffect effect : PotionTypes.NIGHT_VISION.getEffects()) {
                if (effect.getPotion().equals(vanilla_night_vision)) { //If still referring to vanilla potion replace
                    ObfuscationReflectionHelper.setPrivateValue(PotionEffect.class, effect, modded_night_vision, SRGNAMES.PotionEffect_potion);
                }
            }
            for (PotionEffect effect : PotionTypes.LONG_NIGHT_VISION.getEffects()) {
                if (effect.getPotion().equals(vanilla_night_vision)) {
                    ObfuscationReflectionHelper.setPrivateValue(PotionEffect.class, effect, modded_night_vision, SRGNAMES.PotionEffect_potion);
                }
            }
        } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            LOGGER.error("Unable to modify vanilla night vision types. Potion items and more might not work", e);
        }
    }


    static void fixMapping(RegistryEvent.MissingMappings.Mapping<Potion> m) {
        if ("night_vision".equals(m.key.getPath())) {
            m.ignore();
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
