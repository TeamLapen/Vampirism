package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;

/**
 * Used to update the clients vampire information
 * @author Maxanier
 *
 */
public class UpdateVampirePlayerPacket implements IMessage {


	public static class Handler implements IMessageHandler<UpdateVampirePlayerPacket, IMessage> {
		
		@Override
		public IMessage onMessage(UpdateVampirePlayerPacket message, MessageContext ctx) {
			Entity e=Minecraft.getMinecraft().theWorld.getEntityByID(message.id);
			if(e!=null){
				if(e instanceof EntityPlayer){
					VampirePlayer.get((EntityPlayer) e).loadSyncUpdate(message.level, message.timers,message.lord);
				}
				else{
					Logger.w("UpdaVampirePlayer", "Entity with id "+message.id+" is not a player");
				}
			}
			return null;
		}

	}

	private int level;
	private int id;
	private boolean lord;
	
	private int[] timers;
	/**
	 * Dont use
	 */
	public UpdateVampirePlayerPacket() {

	}


	/**
	 * 
	 * @param id Entity id
	 * @param level Vampire level
	 * @param timers Skill timer
	 * @param lord if the player is a vampire lord
	 */
	public UpdateVampirePlayerPacket(int id,int level,int[] timers,boolean lord) {
		this.id=id;
		this.level=level;
		this.timers=timers;
		this.lord=lord;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		level=tag.getInteger("level");
		timers=tag.getIntArray("timers");
		id=tag.getInteger("id");
		lord=tag.getBoolean("lord");

	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("level", level);
		tag.setIntArray("timers", timers);;
		tag.setInteger("id", id);
		tag.setBoolean("lord", lord);
		ByteBufUtils.writeTag(buf, tag);

	}
}
