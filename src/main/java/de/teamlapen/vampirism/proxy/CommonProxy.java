package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import javax.annotation.Nullable;

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

    @Nullable
    @Override
    public EntityPlayer getSPPlayer() {
        return null;
    }
}
