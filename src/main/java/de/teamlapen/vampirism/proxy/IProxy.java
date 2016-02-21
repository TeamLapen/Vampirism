package de.teamlapen.vampirism.proxy;

import de.teamlapen.lib.lib.util.IInitListener;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Proxy interface
 */
public interface IProxy extends IInitListener {

    boolean isClientPlayerNull();

    boolean isPlayerThePlayer(EntityPlayer player);
}
