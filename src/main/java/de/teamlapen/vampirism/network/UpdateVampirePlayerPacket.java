package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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

/**
 * Used to tell the client it should render the screen reddish
 * @author Maxanier
 *
 */
public class UpdateVampirePlayerPacket implements IMessage {


	public static class Handler implements IMessageHandler<UpdateVampirePlayerPacket, IMessage> {
		
		@Override
		public IMessage onMessage(UpdateVampirePlayerPacket message, MessageContext ctx) {
			VampirePlayer.get(VampirismMod.proxy.getSPPlayer()).loadSyncUpdate(message.level,message.timers);
			return null;
		}

	}

	private int level;
	
	private int[] timers;
	/**
	 * Dont use
	 */
	public UpdateVampirePlayerPacket() {

	}


	/**
	 * 
	 * @param durationOn duration(in ticks) which should it take to turn full red
	 * @param durationOff duration(in ticks) which should it take to turn normal again
	 */
	public UpdateVampirePlayerPacket(int level,int[] timers) {
		this.level=level;
		this.timers=timers;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		level=tag.getInteger("level");
		timers=tag.getIntArray("timers");

	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("level", level);
		tag.setIntArray("timers", timers);;
		ByteBufUtils.writeTag(buf, tag);

	}
}
