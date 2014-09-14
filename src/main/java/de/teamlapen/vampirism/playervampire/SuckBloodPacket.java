package de.teamlapen.vampirism.playervampire;

import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.util.Logger;

public class SuckBloodPacket implements IMessage{

	@Override
	public void fromBytes(ByteBuf buf) {
		
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		
	}
	
	public static class Handler implements IMessageHandler<SuckBloodPacket,IMessage>{

		@Override
		public IMessage onMessage(SuckBloodPacket message, MessageContext ctx) {
			EntityPlayer player=ctx.getServerHandler().playerEntity;
			//TODO suck blood
			return null;
		}
		
	}

}
