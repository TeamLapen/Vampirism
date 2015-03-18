package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.client.gui.VampireHudOverlay;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;

/**
 * Used to update custom entity data
 * @author Maxanier
 *
 */
public class UpdateEntityPacket implements IMessage {

	public static class Handler implements IMessageHandler<UpdateEntityPacket, IMessage> {

		@Override
		public IMessage onMessage(UpdateEntityPacket message, MessageContext ctx) {
			Entity e=Minecraft.getMinecraft().theWorld.getEntityByID(message.id);
			if(e!=null){
				if(!(e instanceof ISyncable)){
					Logger.w("UpdateEntityPacket", "Trying to load data for "+e.toString()+", but it does not implement ISyncable");
				}
				else{
					((ISyncable)e).loadPartialUpdate(message.data);
				}
			}
			return null;
		}

	}

	private int id;
	private NBTTagCompound data;
	/**
	 * Dont use
	 */
	public UpdateEntityPacket() {

	}


	public UpdateEntityPacket(Entity entity,NBTTagCompound data) {
		if(!(entity instanceof ISyncable)){
			throw new IllegalArgumentException("You cannot use this packet to sync this entity. The entity has to implement ISyncable");
		}
		this.id=entity.getEntityId();
		this.data=data;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		id=tag.getInteger("id");
		data=tag.getCompoundTag("data");

	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		tag.setTag("data", data);
		ByteBufUtils.writeTag(buf, tag);

	}
}
