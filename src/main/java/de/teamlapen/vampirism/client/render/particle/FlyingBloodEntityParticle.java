package de.teamlapen.vampirism.client.render.particle;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Flying Blood Particle for rituals
 * 
 * @author maxanier
 *
 */
@SideOnly(Side.CLIENT)
public class FlyingBloodEntityParticle extends EntityFX {
	public static void addParticle(FlyingBloodEntityParticle p) {
		Minecraft.getMinecraft().effectRenderer.addEffect(p);
	}
	private final int MAX_AGE = 60;
	private final String TAG = "FlyingBloodParticle";

	private final Entity entity;

	public FlyingBloodEntityParticle(double posX, double posY, double posZ, NBTTagCompound data) {

		super(Minecraft.getMinecraft().theWorld, posX, posY, posZ, 0D, 0D, 0D);
		entity = this.worldObj.getEntityByID(data.getInteger("player_id"));
		if (entity == null) {
			Logger.e(TAG, "Entity with id " + data.getInteger("player_id") + " cannot be found");
			throw new NullPointerException("Entity not found");
		}
		boolean direct = false;
		if (data.hasKey("direct")) {
			direct = true;
		}
		this.particleRed = 1.0F;
		this.particleBlue = this.particleGreen = 0.0F;
		this.noClip = true;
		if (direct) {
			this.particleMaxAge = MAX_AGE / 2;
		} else {
			this.particleMaxAge = MAX_AGE;
		}

		this.setParticleTextureIndex(65);
		if (direct) {
			this.motionX = ((this.worldObj.rand.nextDouble() - 0.5F) / 5f);
			this.motionY = (this.worldObj.rand.nextDouble() / 5f);
			this.motionZ = ((this.worldObj.rand.nextDouble() - 0.5F) / 5f);
		} else {
			this.motionX = (this.worldObj.rand.nextDouble() - 0.5);
			this.motionY = (this.worldObj.rand.nextDouble() + 0.2);
			this.motionZ = (this.worldObj.rand.nextDouble() - 0.5);
		}

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
		if (tleft < this.particleMaxAge / 2) {
			this.motionX = wayX / tleft;
			this.motionY = wayY / tleft;
			this.motionZ = wayZ / tleft;
		} else {

		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
	}

}
