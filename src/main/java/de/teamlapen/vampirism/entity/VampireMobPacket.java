package de.teamlapen.vampirism.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class VampireMobPacket implements IMessage{

	private NBTTagCompound nbt;
	public final static String KEY_ID="EntityId";
	
	public VampireMobPacket(){
		
	}
	
	public VampireMobPacket(NBTTagCompound nbt){
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
	
	public static class Handler implements IMessageHandler<VampireMobPacket,IMessage>{

		@Override
		public IMessage onMessage(VampireMobPacket message, MessageContext ctx) {
			EntityLiving e =(EntityLiving) Minecraft.getMinecraft().theWorld.getEntityByID(message.nbt.getInteger(KEY_ID));
			if(e!=null){
				VampireMob.get(e).loadNBTData(message.nbt);
			}
			
			return null;
		}
		
	}

}
