package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.util.IInitListener;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Handles all block render registration including TileEntities
 */
public class ModBlocksRender {

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                preInit((FMLPreInitializationEvent) event);
                break;
        }

    }

    private static void preInit(FMLPreInitializationEvent event) {

    }


}
