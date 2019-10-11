package de.teamlapen.lib.lib.util;


import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

/**
 * Simple interface which provides ClientSetup, CommonSetup and LoadComplete.
 */
public interface IInitListener {
    void onInitStep(Step step, ModLifecycleEvent event);

    enum Step {
        CLIENT_SETUP, COMMON_SETUP, LOAD_COMPLETE, PROCESS_IMC
    }
}
