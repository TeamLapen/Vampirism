package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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

public class RequestEntityUpdatePacket implements IMessage {
	public static class Handler implements IMessageHandler<RequestEntityUpdatePacket, IMessage> {

		@Override
		public IMessage onMessage(RequestEntityUpdatePacket message, MessageContext ctx) {
			Entity e=ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
			if(e!=null){
				if(!(e instanceof ISyncable)){
					Logger.w("EntityUpdatePacket","Can't get an update packet for "+e);
					return null;
				}
				else{
					return new UpdateEntityPacket(e,((ISyncable)e).getJoinWorldSyncData());
					
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

	public RequestEntityUpdatePacket(Entity entity) {
		if(!(entity instanceof ISyncable)){
			throw new IllegalArgumentException("You cannot request an update for this entity. The entity has to implement ISyncable");
		}
		this.id=entity.getEntityId();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = ByteBufUtils.readVarInt(buf, Integer.MAX_VALUE);
		
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeVarInt(buf, id, Integer.MAX_VALUE);

	}

}
