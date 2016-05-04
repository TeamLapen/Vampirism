package de.teamlapen.lib.proxy;

import de.teamlapen.lib.util.ParticleHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public interface IProxy {
    ParticleHandler getParticleHandler();

    EntityPlayer getPlayerEntity(MessageContext context);
}
