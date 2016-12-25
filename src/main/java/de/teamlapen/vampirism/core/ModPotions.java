package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.potion.FakeNightVisionPotion;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.potion.PotionThirst;
import de.teamlapen.vampirism.potion.VampirismPotion;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Handles all potion registrations and reference.
 */
public class ModPotions {

    public static Potion sanguinare;
    public static Potion thirst;
    public static Potion saturation;
    public static Potion sunscreen;
    public static Potion disguiseAsVampire;
    public static Potion fireProtection;
    public static Potion garlic;
    public static FakeNightVisionPotion fakeNightVisionPotion;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                preInit((FMLPreInitializationEvent) event);
                break;
            default://Do nothing
        }

    }

    private static void preInit(FMLPreInitializationEvent event) {
        thirst = register(new PotionThirst("thirst", true, 859494));
        fakeNightVisionPotion = register(new FakeNightVisionPotion());
        sanguinare = register(new PotionSanguinare("sanguinare", false, 0x6A0888));
        saturation = register(new VampirismPotion("saturation", false, 0xDCFF00).setBeneficial());
        sunscreen = register(new VampirismPotion("sunscreen", false, 0xFFF100).setBeneficial());
        sunscreen.registerPotionAttributeModifier(VReference.sunDamage, "9dc9420c-3e5e-41c7-9ba4-ff70e9dc69fc", -0.5, 2);
        fireProtection = register(new VampirismPotion("fire_protection", false, 14981690).setIconIndex(7, 1).setBeneficial());
        disguiseAsVampire = register(new VampirismPotion("disguise_as_vampire", false, 0x999900).setBeneficial());
        garlic = register(new VampirismPotion("garlic", true, 0xFFFFFF));
    }

    private static <T extends Potion> T register(T potion) {
        GameRegistry.register(potion);
        return potion;
    }


}
