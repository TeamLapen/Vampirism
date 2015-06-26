package de.teamlapen.vampirism.network;

import io.netty.buffer.ByteBuf;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;

public class SpawnParticlePacket implements IMessage {

	public static class Handler implements IMessageHandler<SpawnParticlePacket, IMessage> {

		@Override
		public IMessage onMessage(SpawnParticlePacket message, MessageContext ctx) {

			if (message.type.equals("blood_eat")) {
				spawnEatParticle(VampirismMod.proxy.getSPPlayer());
				Minecraft.getMinecraft().thePlayer.playSound(REFERENCE.MODID + ":player.bite", 1.0F, 1.0F);
				return null;
			}
			WorldClient world = Minecraft.getMinecraft().theWorld;
			world.spawnParticle(message.type, message.x, message.y, message.z, message.velX, message.velY, message.velZ);
			for (int i = 1; i < message.amount; i++) {
				Random ran = world.rand;
				double x = message.x + (ran.nextGaussian());
				double y = message.y + (ran.nextGaussian());
				double z = message.z + (ran.nextGaussian());
				world.spawnParticle(message.type, x, y, z, ran.nextDouble(), ran.nextGaussian(), ran.nextGaussian());
			}
			return null;
		}

	}

	private static void spawnEatParticle(EntityPlayer p) {
		for (int j = 0; j < 16; ++j) {
			Vec3 vec3 = Vec3.createVectorHelper((p.worldObj.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			vec3.rotateAroundX(-p.rotationPitch * (float) Math.PI / 180.0F);
			vec3.rotateAroundY(-p.rotationYaw * (float) Math.PI / 180.0F);
			Vec3 vec31 = Vec3.createVectorHelper((p.worldObj.rand.nextFloat() - 0.5D) * 0.3D, (-p.worldObj.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
			vec31.rotateAroundX(-p.rotationPitch * (float) Math.PI / 180.0F);
			vec31.rotateAroundY(-p.rotationYaw * (float) Math.PI / 180.0F);
			vec31 = vec31.addVector(p.posX, p.posY + p.getEyeHeight(), p.posZ);
			String s = "iconcrack_260";

			// if (p_71010_1_.getHasSubtypes())
			// {
			// s = s + "_" + p_71010_1_.getItemDamage();
			// }

			p.worldObj.spawnParticle(s, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord);
		}
	}
	private String type;

	private double x, y, z, velX, velY, velZ;

	private int amount;

	/**
	 * Dont use
	 */
	public SpawnParticlePacket() {

	}

	/**
	 * Tells the client to spawn a particle. Arguments are the same as in {@link net.minecraft.world.World#spawnParticle}, only pamount is added
	 * 
	 * @param ptype
	 * @param px
	 * @param py
	 * @param pz
	 * @param pvelx
	 * @param pvely
	 * @param pvelz
	 * @param pamount
	 *            Amount of randomly spawned particle
	 */
	public SpawnParticlePacket(String ptype, double px, double py, double pz, double pvelx, double pvely, double pvelz, int pamount) {
		type = ptype;
		x = px;
		y = py;
		z = pz;
		velX = pvelx;
		velY = pvely;
		velZ = pvelz;
		amount = pamount;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		type = tag.getString("type");
		x = tag.getDouble("x");
		y = tag.getDouble("y");
		z = tag.getDouble("z");
		velX = tag.getDouble("velX");
		velY = tag.getDouble("velY");
		velZ = tag.getDouble("velZ");
		amount = tag.getInteger("amount");

	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("type", type);
		tag.setDouble("x", x);
		tag.setDouble("y", y);
		tag.setDouble("z", z);
		tag.setDouble("velX", velX);
		tag.setDouble("velY", velY);
		tag.setDouble("velZ", velZ);
		tag.setInteger("amount", amount);
		ByteBufUtils.writeTag(buf, tag);

	}

}
