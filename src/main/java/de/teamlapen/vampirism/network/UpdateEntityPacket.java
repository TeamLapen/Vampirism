package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Used to update custom entity data The entity has to either implement ISyncable or {@link UpdateEntityPacket.Handler#tryToGetISyncable(Entity)} has to match an ISyncable Object to the entity
 * 
 * @author Maxanier
 *
 */
public class UpdateEntityPacket implements IMessage {

	public static class Handler implements IMessageHandler<UpdateEntityPacket, IMessage> {

		/**
		 * Tries to find the according ISyncable object
		 * 
		 * @param e
		 * @return may be null
		 */
		public static ISyncable tryToGetISyncable(Entity e) {
			if (e instanceof ISyncable) {
				return (ISyncable) e;
			} else if (e instanceof EntityPlayer) {
				return VampirePlayer.get((EntityPlayer) e);
			} else if (e instanceof EntityCreature) {
				return VampireMob.get((EntityCreature) e);
			}
			return null;
		}

		@Override
		public IMessage onMessage(UpdateEntityPacket message, MessageContext ctx) {
			if(Minecraft.getMinecraft().theWorld==null){
				Logger.w("UpdateEntity","World not loaded yet");
				return null;
			}
			Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(message.id);
			if (e != null) {
				ISyncable s = tryToGetISyncable(e);
				if (s != null) {
					s.loadUpdateFromNBT(message.data);
				} else {
					Logger.w("UpdateEntityPacket", "Trying to load data for " + e.toString() + ", but it does not implement ISyncable");
				}
			}
			return null;
		}

	}

	public interface ISyncableExtendedProperties extends IExtendedEntityProperties, ISyncable {

		/**
		 * Returns the entity id of the representing entity
		 * 
		 * @return
		 */
		public int getTheEntityID();
	}

	private int id;
	private NBTTagCompound data;

	/**
	 * Dont use
	 */
	public UpdateEntityPacket() {

	}

	public UpdateEntityPacket(Entity entity) {
		if (!(entity instanceof ISyncable)) {
			throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
		}
		this.id = entity.getEntityId();
		this.data = new NBTTagCompound();
		((ISyncable) entity).writeFullUpdateToNBT(data);
	}

	public UpdateEntityPacket(Entity entity, NBTTagCompound data) {
		if (!(entity instanceof ISyncable)) {
			throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
		}
		this.id = entity.getEntityId();
		this.data = data;
	}

	public UpdateEntityPacket(ISyncableExtendedProperties prop) {

		this.id = prop.getTheEntityID();
		this.data = new NBTTagCompound();
		prop.writeFullUpdateToNBT(data);
	}

	public UpdateEntityPacket(ISyncableExtendedProperties prop, NBTTagCompound nbt) {

		this.id = prop.getTheEntityID();
		this.data = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		id = tag.getInteger("id");
		data = tag.getCompoundTag("data");

	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		tag.setTag("data", data);
		ByteBufUtils.writeTag(buf, tag);

	}
}
