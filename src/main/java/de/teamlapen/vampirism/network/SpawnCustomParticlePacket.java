package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.render.particle.DarkLordParticle;
import de.teamlapen.vampirism.client.render.particle.FlyingBloodEntityParticle;
import de.teamlapen.vampirism.client.render.particle.FlyingBloodParticle;
import de.teamlapen.vampirism.client.render.particle.ParticleHandler;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.TickRunnable;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet to spawn custom particles
 * 
 * @author Maxanier
 *
 */
public class SpawnCustomParticlePacket implements IMessage {

	public static class Handler implements IMessageHandler<SpawnCustomParticlePacket, IMessage> {

		@Override
		public IMessage onMessage(final SpawnCustomParticlePacket message, MessageContext ctx) {
			final WorldClient world = Minecraft.getMinecraft().theWorld;
			if(world==null)return null;
			try {
				switch (message.data.getInteger("type")) {
				case 0:
					for (int i = 0; i < message.amount; i++) {

						FlyingBloodEntityParticle.addParticle(new FlyingBloodEntityParticle(message.posX, message.posY, message.posZ, message.data));

					}
					break;
				case 1:
					for (int i = 0; i < message.amount; i++) {
						FlyingBloodParticle.addParticle(new FlyingBloodParticle(message.posX, message.posY, message.posZ, message.data));
					}
					break;
				case 2:
					Entity e = world.getEntityByID(message.data.getInteger("id"));
					if (e != null && e instanceof EntityLivingBase) {
						Helper.spawnParticlesAroundEntity((EntityLivingBase) e, message.data.getString("particle"), message.data.getDouble("distance"), message.amount);
					}
					break;
				case 3:
					final Entity entity = world.getEntityByID(message.data.getInteger("id"));
					if(entity==null)return null;
					final boolean thePlayer=entity.equals(Minecraft.getMinecraft().thePlayer);
					TickRunnable run=new TickRunnable() {
						int tick=0;
						@Override public boolean shouldContinue() {
							return !entity.isDead&&tick<100;
						}

						@Override public void onTick() {
							if(++tick%5==0) {

								for (int i = 0; i < message.amount; i++) {
									ParticleHandler.instance().addEffect(new DarkLordParticle(world, entity,thePlayer));
								}
							}


						}
					};
					VampirismMod.proxy.addTickRunnable(run);
					break;

				default:
					Logger.w("CustomParticlePacket", "Particle of type " + message.data.getInteger("type") + " is unknown");
					return null;
				}

			} catch (Exception e) {
				Logger.e("CustomParticlePacket", "Error", e);
			}
			return null;

		}

	}

	private NBTTagCompound data;
	private double posX, posY, posZ;

	private int amount;

	/**
	 * Dont use
	 */
	public SpawnCustomParticlePacket() {

	}

	/**
	 * @param type
	 *            0:Flying_Blood_Player,1:Flying_Blood,2:{@link Helper#spawnParticlesAroundEntity(EntityLivingBase, String, double, int)}
	 * @param data
	 *            CustomData
	 */
	public SpawnCustomParticlePacket(int type, double posX, double posY, double posZ, int amount, NBTTagCompound data) {
		this.data = data;
		this.data.setInteger("type", type);
		this.data.setDouble("posX", posX);
		this.data.setDouble("posY", posY);
		this.data.setDouble("posZ", posZ);
		this.data.setInteger("amount", amount);
	}

	public SpawnCustomParticlePacket(int type, double posX, double posY, double posZ, int amount){
		this(type, posX, posY, posZ, amount,new NBTTagCompound());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = ByteBufUtils.readTag(buf);
		posX = data.getDouble("posX");
		posY = data.getDouble("posY");
		posZ = data.getDouble("posZ");
		amount = data.getInteger("amount");

	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data);
	}

}
