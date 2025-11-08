package de.teamlapen.lib.common;


/**
 * Simple interface which provides ClientSetup, CommonSetup and LoadComplete.
 */
public interface ILifecycleListener {

    enum Step {
        CLIENT_SETUP, COMMON_SETUP, LOAD_COMPLETE, PROCESS_IMC, ENQUEUE_IMC
    }
}
