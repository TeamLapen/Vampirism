package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.potion.PotionThirst;
import de.teamlapen.vampirism.potion.VampirismPotion;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Handles all potion registrations and reference.
 */
public class ModPotions {

    public static Potion sanguinare;
    public static Potion thirst;
    public static Potion saturation;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                preInit((FMLPreInitializationEvent) event);
                break;
        }

    }

    private static void preInit(FMLPreInitializationEvent event) {
        thirst = new PotionThirst("thirst", true, 859494);

        sanguinare = new PotionSanguinare("sanguinare", false, 0x6A0888);
        saturation = new VampirismPotion("saturation", false, 0xDCFF00);
    }


}
