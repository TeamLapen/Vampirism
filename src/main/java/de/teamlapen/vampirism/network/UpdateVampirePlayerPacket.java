package de.teamlapen.vampirism.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;


/**
 * Supposed to be used to update the sleepingCoffin flag on the server, but is currently not in use
 * @author Moritz
 *
 */
public class UpdateVampirePlayerPacket implements IMessage {

	public static class Handler implements
			IMessageHandler<UpdateVampirePlayerPacket, IMessage> {
		@Override
		public IMessage onMessage(UpdateVampirePlayerPacket message,
				MessageContext ctx) {
			VampirePlayer.get((EntityPlayer) Minecraft.getMinecraft().theWorld
					.getEntityByID(message.id)).sleepingCoffin = message.sleepingCoffin;
			Logger.i("UpdateVampirePlayerPacket", String.format(
					"onMessage caled, sleepingCoffin=%s, remote=%s",
					message.sleepingCoffin,
					Minecraft.getMinecraft().theWorld.isRemote));
			return null;
		}
	}

	private NBTTagCompound nbt;
	private boolean sleepingCoffin;
	private int id;

	public UpdateVampirePlayerPacket() {
	}

	public UpdateVampirePlayerPacket(VampirePlayer vp) {
		id = vp.getTheEntityID();
		sleepingCoffin = vp.sleepingCoffin;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		Logger.i("UpdateVampirePlayerPacket", "fromBytes called");
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		sleepingCoffin = tag.getBoolean("sc");
		id = tag.getInteger("id");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("sc", sleepingCoffin);
		tag.setInteger("id", id);
		ByteBufUtils.writeTag(buf, tag);
	}
}
