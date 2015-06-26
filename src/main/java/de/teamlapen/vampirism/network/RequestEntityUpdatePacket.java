package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.network.UpdateEntityPacket.ISyncableExtendedProperties;
import de.teamlapen.vampirism.util.Logger;

/**
 * Send by the client to request an update for the entity with the included id. The entity has to either implement ISyncable or {@link UpdateEntityPacket.Handler#tryToGetISyncable(Entity)} has to
 * match an ISyncable Object to the entity
 * 
 * @author Maxanier
 *
 */
public class RequestEntityUpdatePacket implements IMessage {
	public static class Handler implements IMessageHandler<RequestEntityUpdatePacket, IMessage> {

		@Override
		public IMessage onMessage(RequestEntityUpdatePacket message, MessageContext ctx) {
			Entity e = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
			if (e != null) {
				ISyncable s = UpdateEntityPacket.Handler.tryToGetISyncable(e);
				if (s instanceof Entity) {
					return new UpdateEntityPacket(e);
				} else if (s instanceof ISyncableExtendedProperties) {
					return new UpdateEntityPacket((ISyncableExtendedProperties) s);
				} else {
					Logger.w("ReqEntityUpdatePacket", "Entity " + e + " can't be synced");
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
	 * 
	 * @param entity
	 */
	public RequestEntityUpdatePacket(Entity entity) {
		if (UpdateEntityPacket.Handler.tryToGetISyncable(entity) == null) {
			throw new IllegalArgumentException("You cannot request an update for this entity. The entity has to implement ISyncable or be a Player");
		}
		this.id = entity.getEntityId();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = ByteBufUtils.readTag(buf).getInteger("id");

	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		ByteBufUtils.writeTag(buf, tag);

	}

}
