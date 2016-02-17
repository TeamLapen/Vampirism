package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.potion.PotionThirst;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Handles all potion registrations and reference.
 */
public class ModPotions {

    public static Potion sanguinare;
    public static Potion thirst;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                preInit((FMLPreInitializationEvent) event);
                break;
        }

    }

    private static void preInit(FMLPreInitializationEvent event) {
        thirst = new PotionThirst(new ResourceLocation(REFERENCE.MODID, "thirst"), true, 859494);
        sanguinare = new PotionSanguinare(new ResourceLocation(REFERENCE.MODID, "sanguinare"), false, 0x6A0888);
    }


}
