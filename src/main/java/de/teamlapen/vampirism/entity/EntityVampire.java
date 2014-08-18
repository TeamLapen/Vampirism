package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.MobProperties;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityVampire extends EntityZombie {
	// TODO Sounds

	public EntityVampire(World par1World) {
		super(par1World);

		// Avoids Vampire Hunters TODO Distance (3rd argument)
		this.tasks.addTask(5, new EntityAIAvoidEntity(this,
				EntityVampireHunter.class, 10.0F,
				MobProperties.vampire_movementSpeed,
				MobProperties.vampire_movementSpeed * 1.5));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
				.setBaseValue(MobProperties.vampire_maxHealth);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
				.setBaseValue(MobProperties.vampire_attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setBaseValue(MobProperties.vampire_movementSpeed);
	}

	public void onKillEntity(EntityLivingBase p_70074_1_) {
		super.onKillEntity(p_70074_1_);

		if ((this.worldObj.difficultySetting == EnumDifficulty.NORMAL || this.worldObj.difficultySetting == EnumDifficulty.HARD)
				&& p_70074_1_ instanceof EntityVillager) {
			if (this.worldObj.difficultySetting != EnumDifficulty.HARD
					&& this.rand.nextBoolean()) {
				return;
			}

			EntityVampire entityvampire = new EntityVampire(this.worldObj);
			entityvampire.copyLocationAndAnglesFrom(p_70074_1_);
			this.worldObj.removeEntity(p_70074_1_);
			entityvampire.onSpawnWithEgg((IEntityLivingData) null);
			entityvampire.setVillager(true);

			if (p_70074_1_.isChild()) {
				entityvampire.setChild(true);
			}

			this.worldObj.spawnEntityInWorld(entityvampire);
			this.worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1016,
					(int) this.posX, (int) this.posY, (int) this.posZ, 0);
		}
	}

	//Sets it on fire in light, kills it in sunlight
	public void onLivingUpdate() {
		float brightness = this.getBrightness(1.0F);
		boolean canSeeSky = this.worldObj.canBlockSeeTheSky(
				MathHelper.floor_double(this.posX),
				MathHelper.floor_double(this.posY),
				MathHelper.floor_double(this.posZ));
		if(!this.worldObj.isRemote) {
			if (this.worldObj.isDaytime()) {
				if (brightness > 0.5F
						&& this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
						&& canSeeSky) {
					this.setDead();
				}
			} else {
				if (brightness > 0.5F
						&& this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
					this.setFire(8);
				}
			}
		}
	}

	// Overrides methods that are not needed, but declared in EntityZombie.class
	@Override
	protected void convertToVillager() {
	}
}
