package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;

public class BloodAltarPacket implements IMessage {
	public static class Handler implements IMessageHandler<BloodAltarPacket, IMessage> {

		@Override
		public IMessage onMessage(BloodAltarPacket message, MessageContext ctx) {
			((TileEntityBloodAltar) Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z)).setOccupied(message.hasSword,
					false);
			return null;
		}
	}
	private String type;
	private boolean hasSword;

	private int x, y, z;

	public BloodAltarPacket() {
	}

	public BloodAltarPacket(boolean pHasSword, int px, int py, int pz) {
		hasSword = pHasSword;
		x = px;
		y = py;
		z = pz;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		hasSword = tag.getBoolean("hasSword");
		x = tag.getInteger("x");
		y = tag.getInteger("y");
		z = tag.getInteger("z");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("hasSword", hasSword);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		ByteBufUtils.writeTag(buf, tag);
	}

}
