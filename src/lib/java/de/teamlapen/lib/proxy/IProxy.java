package de.teamlapen.lib.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public interface IProxy {
    EntityPlayer getPlayerEntity(MessageContext context);
}
