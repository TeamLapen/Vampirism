package de.teamlapen.vampirism.entity;

import java.util.List;

import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.player.VampirePlayer;

public class EntityBlindingBat extends EntityBat {

	public EntityBlindingBat(World p_i1680_1_) {
		super(p_i1680_1_);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.ticksExisted > 600) {
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

	@Override public boolean getCanSpawnHere() {
		return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
	}
}
