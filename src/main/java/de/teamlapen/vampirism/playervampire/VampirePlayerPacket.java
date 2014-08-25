package de.teamlapen.vampirism.playervampire;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.util.Logger;

public class VampirePlayerPacket implements IMessage{
	NBTTagCompound nbt;

	public VampirePlayerPacket(){
		
	}
	public VampirePlayerPacket(NBTTagCompound nbt){
		this.nbt=nbt;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		nbt=ByteBufUtils.readTag(buf);
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		ByteBufUtils.writeTag(buf, nbt);
		
	}
	
	public static class Handler implements IMessageHandler<VampirePlayerPacket,IMessage>{

		@Override
		public IMessage onMessage(VampirePlayerPacket message, MessageContext ctx) {
			Logger.i("test",message.nbt.toString());
			VampirePlayer.get(Minecraft.getMinecraft().thePlayer).loadNBTData(message.nbt);
			return null;
		}
		
	}
}
