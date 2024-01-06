package de.teamlapen.vampirism.proxy;


import net.neoforged.fml.event.lifecycle.ParallelDispatchEvent;

/**
 * Abstract proxy base for both client and server.
 * Try to keep this quite empty and move larger code parts into dedicated classes.
 */
public abstract class CommonProxy implements IProxy {

    @Override
    public void onInitStep(Step step, ParallelDispatchEvent event) {
    }
}
