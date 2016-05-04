package de.teamlapen.lib.proxy;

import de.teamlapen.lib.util.ParticleHandler;
import de.teamlapen.lib.util.ParticleHandlerServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class CommonProxy implements IProxy {

    protected ParticleHandler serverParticleHandler = new ParticleHandlerServer();//Not required on client side, but since on an integrated server only client proxy exist we need it here

    @Override
    public ParticleHandler getParticleHandler() {
        return serverParticleHandler;
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

}
