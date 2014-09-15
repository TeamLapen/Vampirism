package de.teamlapen.vampirism.playervampire;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;

public class SuckBloodPacket implements IMessage{

	private int id=0;
	public SuckBloodPacket(int id){
		this.id=id;
	}
	
	public SuckBloodPacket(){
		
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		id=ByteBufUtils.readVarInt(buf, 3);
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, id, 3);
		
	}
	
	public static class Handler implements IMessageHandler<SuckBloodPacket,IMessage>{

		@Override
		public IMessage onMessage(SuckBloodPacket message, MessageContext ctx) {
			if(message.id!=0){
				EntityPlayer player=ctx.getServerHandler().playerEntity;
				Entity e=player.worldObj.getEntityByID(message.id);
				if(e!=null&&e instanceof EntityLiving){
					VampirePlayer.get(player).suckBlood((EntityLiving)e);
				}
			}
			
			
			return null;
		}
		
	}

}
