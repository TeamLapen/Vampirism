package de.teamlapen.vampirism.proxy;

import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Abstract proxy base for both client and server.
 * Try to keep this quite empty and move larger code parts into dedicated classes.
 *
 * @author Maxanier
 */
public abstract class CommonProxy implements IProxy {
    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
    }


}
