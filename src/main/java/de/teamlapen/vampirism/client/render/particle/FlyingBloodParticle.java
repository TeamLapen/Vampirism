package de.teamlapen.vampirism.client.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Flying Blood Particle for rituals
 * 
 * @author maxanier
 *
 */
@SideOnly(Side.CLIENT)
public class FlyingBloodParticle extends EntityFX {
	public static void addParticle(FlyingBloodParticle p) {
		Minecraft.getMinecraft().effectRenderer.addEffect(p);
	}
	private final String TAG = "FlyingBloodParticle";

	private final double destX, destY, destZ;

	public FlyingBloodParticle(double posX, double posY, double posZ, NBTTagCompound data) {
		super(Minecraft.getMinecraft().theWorld, posX + 0.5, posY + 0.5, posZ + 0.5, 0D, 0D, 0D);
		destX = data.getInteger("destX") + 0.5;
		destY = data.getInteger("destY") + 0.5;
		destZ = data.getInteger("destZ") + 0.5;
		this.particleMaxAge = data.getInteger("age");
		this.particleRed = 1.0F;
		this.particleBlue = this.particleGreen = 0.0F;
		this.noClip = true;
		this.setParticleTextureIndex(65);
		double wayX = destX - this.posX;
		double wayZ = destZ - this.posZ;
		double wayY = destY - this.posY;
		this.motionX = (this.worldObj.rand.nextDouble() / 10 - 0.05) + wayX / particleMaxAge;
		this.motionY = (this.worldObj.rand.nextDouble() / 10 - 0.01) + wayY / particleMaxAge;
		this.motionZ = (this.worldObj.rand.nextDouble() / 10 - 0.05) + wayZ / particleMaxAge;
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
		double wayX = destX - this.posX;
		double wayY = destY - this.posY;
		double wayZ = destZ - this.posZ;

		int tleft = this.particleMaxAge - this.particleAge;
		if (tleft < this.particleMaxAge / 1.2) {
			this.motionX = wayX / tleft;
			this.motionY = wayY / tleft;
			this.motionZ = wayZ / tleft;
		}
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
	}

}
