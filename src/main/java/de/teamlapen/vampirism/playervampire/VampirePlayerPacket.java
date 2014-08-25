package de.teamlapen.vampirism.playervampire;

import net.minecraft.client.Minecraft;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class VampirePlayerPacket implements IMessage{
	private String text;

	@Override
	public void fromBytes(ByteBuf buf) {
		text = ByteBufUtils.readUTF8String(buf); // this class is very useful in general for writing more complex objects
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, text);
		
	}
	
	public static class Handler implements IMessageHandler<VampirePlayerPacket,IMessage>{

		@Override
		public IMessage onMessage(VampirePlayerPacket message, MessageContext ctx) {
			Minecraft.getMinecraft().thePlayer
		}
		
	}
}
