package de.teamlapen.lib.proxy;


import de.teamlapen.lib.util.ParticleHandler;
import de.teamlapen.lib.util.ParticleHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {

    private ParticleHandler clientParticleHandler = new ParticleHandlerClient();

    @Override
    public ParticleHandler getParticleHandler() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient() ? clientParticleHandler : serverParticleHandler;
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {

        //Need to double check the side for some reason
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }
}
