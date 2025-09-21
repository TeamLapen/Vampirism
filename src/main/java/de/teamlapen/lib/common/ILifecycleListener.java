package de.teamlapen.lib.common;


import net.neoforged.fml.event.lifecycle.ParallelDispatchEvent;

/**
 * Simple interface which provides ClientSetup, CommonSetup and LoadComplete.
 */
public interface ILifecycleListener {

    default void onInitStep(Step step, ParallelDispatchEvent event) {
    }

    enum Step {
        CLIENT_SETUP, COMMON_SETUP, LOAD_COMPLETE, PROCESS_IMC, ENQUEUE_IMC
    }
}
