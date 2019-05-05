package de.teamlapen.lib.lib.util;


import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

/**
 * Simple interface which provides preInit,init and postInit.
 */
public interface IInitListener {
    void onInitStep(Step step, ModLifecycleEvent event);

    enum Step {
        CLIENT_SETUP, COMMON_SETUP, LOAD_COMPLETE
    }
}
