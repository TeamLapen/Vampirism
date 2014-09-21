package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class BloodAltarPacket implements IMessage {
	
	public static class Handler implements IMessageHandler<BloodAltarPacket, IMessage> {

		@Override
		public IMessage onMessage(BloodAltarPacket message, MessageContext ctx) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	public BloodAltarPacket() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

}
