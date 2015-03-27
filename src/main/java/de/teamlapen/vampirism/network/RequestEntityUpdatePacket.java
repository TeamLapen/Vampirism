package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.GuiHandler;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;

/**
 * Send by the client to request an update for the entity with the included id
 * @author Maxanier
 *
 */
public class RequestEntityUpdatePacket implements IMessage {
	public static class Handler implements IMessageHandler<RequestEntityUpdatePacket, IMessage> {

		@Override
		public IMessage onMessage(RequestEntityUpdatePacket message, MessageContext ctx) {
			Entity e=ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
			if(e!=null){
				if(e instanceof ISyncable){
					NBTTagCompound nbt=((ISyncable)e).getJoinWorldSyncData();
					if(nbt!=null){
						return new UpdateEntityPacket(e,nbt);
					}
				}
				else if(e instanceof EntityPlayer){
					return VampirePlayer.get((EntityPlayer) e).createUpdatePacket();
				}
				else{
					Logger.w("EntityUpdatePacket","Can't get an update packet for "+e);
					return null;
				}
			}
			return null;
		}

	}

	private int id;
	/**
	 * Dont use!
	 */
	public RequestEntityUpdatePacket() {

	}

	/**
	 * Only accepts Players or entitys which implement ISyncable
	 * @param entity
	 */
	public RequestEntityUpdatePacket(Entity entity) {
		if(!(entity instanceof ISyncable)&&!(entity instanceof EntityPlayer)){
			throw new IllegalArgumentException("You cannot request an update for this entity. The entity has to implement ISyncable or be a Player");
		}
		this.id=entity.getEntityId();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = ByteBufUtils.readTag(buf).getInteger("id");
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag=new NBTTagCompound();
		tag.setInteger("id", id);
		ByteBufUtils.writeTag(buf, tag);

	}

}
