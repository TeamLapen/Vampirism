package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
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
 * Used to tell the client it should render the screen reddish
 * @author Maxanier
 *
 */
public class UpdateVampirePlayerPacket implements IMessage {

	public static class Handler implements IMessageHandler<UpdateVampirePlayerPacket, IMessage> {

		@Override
		public IMessage onMessage(UpdateVampirePlayerPacket message, MessageContext ctx) {
			VampirePlayer.get(Minecraft.getMinecraft().thePlayer).setLevel(message.level);
			return null;
		}

	}

	private int level;
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
	public UpdateVampirePlayerPacket(int level) {
		this.level=level;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		level=tag.getInteger("level");

	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("level", level);
		ByteBufUtils.writeTag(buf, tag);

	}
}
