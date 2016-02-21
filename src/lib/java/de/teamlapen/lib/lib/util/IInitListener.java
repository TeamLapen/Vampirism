package de.teamlapen.lib.lib.util;

import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Simple interface which provides preInit,init and postInit.
 */
public interface IInitListener {
    void onInitStep(Step step, FMLStateEvent event);

    enum Step {
        PRE_INIT, INIT, POST_INIT
    }
}
