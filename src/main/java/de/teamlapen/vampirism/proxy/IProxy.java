package de.teamlapen.vampirism.proxy;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.util.IParticleHandler;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Proxy interface
 */
public interface IProxy extends IInitListener {

    boolean isClientPlayerNull();

    boolean isPlayerThePlayer(EntityPlayer player);

    /**
     * On client side returns a particle handler that spawns particles.
     * On server side it currently does nothing
     */
    IParticleHandler getParticleHandler();

    void renderScreenRed(int ticksOn, int ticksOff);
}
