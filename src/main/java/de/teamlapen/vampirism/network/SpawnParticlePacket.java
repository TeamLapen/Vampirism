package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Random;

public class SpawnParticlePacket implements IMessage {

	public static class Handler implements IMessageHandler<SpawnParticlePacket, IMessage> {

		@Override
		public IMessage onMessage(SpawnParticlePacket message, MessageContext ctx) {

			//TODO move bite to CustomeParticle
			if (message.type.equals(EnumParticleTypes.WATER_WAKE)) {
				spawnEatParticle(VampirismMod.proxy.getSPPlayer());
				Minecraft.getMinecraft().thePlayer.playSound(REFERENCE.MODID + ":player.bite", 1.0F, 1.0F);
				return null;
			}
			WorldClient world = Minecraft.getMinecraft().theWorld;
			world.spawnParticle(message.type, message.x, message.y, message.z, message.velX, message.velY, message.velZ,message.params);
			for (int i = 1; i < message.amount; i++) {
				Random ran = world.rand;
				double x = message.x + (ran.nextGaussian());
				double y = message.y + (ran.nextGaussian());
				double z = message.z + (ran.nextGaussian());
				world.spawnParticle(message.type, x, y, z, ran.nextDouble(), ran.nextGaussian(), ran.nextGaussian(),message.params);
			}
			return null;
		}

	}

	private static void spawnEatParticle(EntityPlayer p) {
		for (int j = 0; j < 16; ++j) {
			Vec3 vec3 = new Vec3((p.getRNG().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
			vec3= vec3.rotatePitch(-p.rotationPitch*(float)Math.PI/180F);
			vec3= vec3.rotateYaw(-p.rotationYaw * (float) Math.PI / 180F);
			double d0 = (double)(-p.getRNG().nextFloat()) * 0.6D - 0.3D;
			Vec3 vec31 = new Vec3(((double)p.getRNG().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
			vec31 = vec31.rotatePitch(-p.rotationPitch * (float)Math.PI / 180.0F);
			vec31 = vec31.rotateYaw(-p.rotationYaw * (float)Math.PI / 180.0F);
			vec31 = vec31.addVector(p.posX, p.posY + (double)p.getEyeHeight(), p.posZ);
			
			p.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord, Item.getIdFromItem(Items.apple));
		}
	}
	private EnumParticleTypes type;

	private double x, y, z, velX, velY, velZ;

	private int[] params;

	private int amount;

	/**
	 * Dont use
	 */
	public SpawnParticlePacket() {

	}

	/**
	 * Tells the client to spawn a particle. Arguments are the same as in {@link net.minecraft.world.World#spawnParticle}, only pamount is added
	 *
	 * @param px
	 * @param py
	 * @param pz
	 * @param pvelx
	 * @param pvely
	 * @param pvelz
	 * @param pamount
	 *            Amount of randomly spawned particle
	 */
	public SpawnParticlePacket(EnumParticleTypes ptype, double px, double py, double pz, double pvelx, double pvely, double pvelz, int pamount,int... params) {
		type = ptype;
		x = px;
		y = py;
		z = pz;
		velX = pvelx;
		velY = pvely;
		velZ = pvelz;
		amount = pamount;
		this.params=params;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		type = EnumParticleTypes.getParticleFromId(tag.getInteger("type"));
		x = tag.getDouble("x");
		y = tag.getDouble("y");
		z = tag.getDouble("z");
		velX = tag.getDouble("velX");
		velY = tag.getDouble("velY");
		velZ = tag.getDouble("velZ");
		amount = tag.getInteger("amount");
		if(tag.hasKey("params")){
			params=tag.getIntArray("params");
		}
		else{
			params=new int[0];
		}

	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type.getParticleID());
		tag.setDouble("x", x);
		tag.setDouble("y", y);
		tag.setDouble("z", z);
		tag.setDouble("velX", velX);
		tag.setDouble("velY", velY);
		tag.setDouble("velZ", velZ);
		tag.setInteger("amount", amount);
		if(params!=null){
			tag.setIntArray("params",params);
		}
		ByteBufUtils.writeTag(buf, tag);

	}

}
