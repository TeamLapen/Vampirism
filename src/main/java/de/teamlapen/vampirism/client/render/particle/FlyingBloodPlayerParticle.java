package de.teamlapen.vampirism.client.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.util.Logger;

/**
 * Flying Blood Particle for rituals
 * 
 * @author maxanier
 *
 */
@SideOnly(Side.CLIENT)
public class FlyingBloodPlayerParticle extends EntityFX {
	public static void addParticle(FlyingBloodPlayerParticle p) {
		Minecraft.getMinecraft().effectRenderer.addEffect(p);
	}
	private final int MAX_AGE = 60;
	private final String TAG = "FlyingBloodParticle";

	private final Entity entity;

	public FlyingBloodPlayerParticle(double posX, double posY, double posZ, NBTTagCompound data) {

		super(Minecraft.getMinecraft().theWorld, posX, posY, posZ, 0D, 0D, 0D);
		entity = this.worldObj.getEntityByID(data.getInteger("player_id"));
		if (entity == null) {
			Logger.e(TAG, "Entity with id " + data.getInteger("player_id") + " cannot be found");
			throw new NullPointerException("Entity not found");
		}
		this.particleRed = 1.0F;
		this.particleBlue = this.particleGreen = 0.0F;
		this.noClip = true;
		this.particleMaxAge = MAX_AGE;
		this.setParticleTextureIndex(65);
		this.motionX = (this.worldObj.rand.nextDouble() - 0.5);
		this.motionY = (this.worldObj.rand.nextDouble() + 0.2);
		this.motionZ = (this.worldObj.rand.nextDouble() - 0.5);
		this.onUpdate();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}
		double wayX = entity.posX - this.posX;
		double wayY = entity.posY - this.posY;
		double wayZ = entity.posZ - this.posZ;

		int tleft = this.particleMaxAge - this.particleAge;
		if (tleft < MAX_AGE / 2) {
			this.motionX = wayX / tleft;
			this.motionY = wayY / tleft;
			this.motionZ = wayZ / tleft;
		} else {

		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
	}

}
