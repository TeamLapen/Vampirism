package de.teamlapen.lib.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Currently not implemented by the library itself but by Vampirism Mod since the library is no mod yet
 */
public interface IProxy {
    EntityPlayer getPlayerEntity(MessageContext context);
}
