package de.teamlapen.vampirism.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.playervampire.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;

public class VampireMobPacket implements IMessage{

	private NBTTagCompound nbt;
	private final static String KEY_ID="EntityId";
	
	public VampireMobPacket(){
		
	}
	
	/**
	 * Created a Packet used to sync VampireMobs or VampirePlayers
	 * @param nbt
	 * @param entityId
	 */
	public VampireMobPacket(NBTTagCompound nbt, int entityId){
		this.nbt=nbt;
		this.nbt.setInteger(KEY_ID, entityId);
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
			Logger.i("MobPacketHandler","Receiving data for "+message.nbt.getInteger(KEY_ID));
			EntityLivingBase e =(EntityLivingBase) Minecraft.getMinecraft().theWorld.getEntityByID(message.nbt.getInteger(KEY_ID));
			if(e!=null){
				if(e instanceof EntityPlayer){
					Logger.i("Test", "Loadoutng player");
					VampirePlayer.get((EntityPlayer)e).loadNBTData(message.nbt);
					return null;
				}
				else if(e instanceof EntityLiving){
					Logger.i("test", "asdf");
					VampireMob.get((EntityLiving)e).loadNBTData(message.nbt);
					return null;
				}
				
			}
			Logger.w("MobPacketHandler", "No entity with id "+message.nbt.getInteger(KEY_ID)+" found");
			return null;
		}
		
	}

}
