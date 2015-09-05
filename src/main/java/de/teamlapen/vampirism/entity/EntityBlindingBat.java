package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class EntityBlindingBat extends EntityBat {

	private boolean restrictLiveSpan;
	public EntityBlindingBat(World p_i1680_1_) {
		super(p_i1680_1_);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (restrictLiveSpan&&this.ticksExisted > 600) {
			this.attackEntityFrom(DamageSource.magic, 10F);
		}
		if (!this.worldObj.isRemote) {
			List l = worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox);
			for (Object e : l) {
				if (VampirePlayer.get((EntityPlayer) e).getLevel() == 0) {
					((EntityPlayer) e).addPotionEffect(new PotionEffect(Potion.blindness.id, 40));
				}
			}
		}
	}

	public void restrictLiveSpan(){
		this.restrictLiveSpan=true;
	}

	@Override public boolean getCanSpawnHere() {
		return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
	}
}
