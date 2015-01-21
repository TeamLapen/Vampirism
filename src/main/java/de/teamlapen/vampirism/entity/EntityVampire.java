package de.teamlapen.vampirism.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.MobProperties;
import de.teamlapen.vampirism.util.REFERENCE;

public class EntityVampire extends MobVampirism {
	// TODO Sounds

	public EntityVampire(World par1World) {
		super(par1World);

		// Attack player
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		// Attack vampire hunter
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityVampireHunter.class, 1.0D, true));
		// Attack villager
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
		// Search for players
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true,false,new IEntitySelector(){

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if(entity instanceof EntityPlayer){
					return VampirePlayer.get((EntityPlayer)entity).getLevel()<=BALANCE.VAMPIRE_FRIENDLY_LEVEL;
				}
				return false;
			}
			
		}));
		// Search for vampire hunters
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVampireHunter.class, 0, true));
		// Search for villagers
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, true));
		this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
		// Avoids Vampire Hunters TODO Distance (3rd argument)
		this.tasks.addTask(5, new EntityAIAvoidEntity(this, EntityVampireHunter.class, 10.0F, MobProperties.vampire_movementSpeed,
				MobProperties.vampire_movementSpeed * 1.5));
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(MobProperties.vampire_maxHealth);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(MobProperties.vampire_attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(MobProperties.vampire_movementSpeed);
	}

	@Override
	public void onKillEntity(EntityLivingBase p_70074_1_) {


		if ((this.worldObj.difficultySetting == EnumDifficulty.NORMAL || this.worldObj.difficultySetting == EnumDifficulty.HARD)
				&& p_70074_1_ instanceof EntityVillager) {

			Entity e = EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_NAME, this.worldObj);
			e.copyLocationAndAnglesFrom(p_70074_1_);

			this.worldObj.spawnEntityInWorld(e);
		}
		super.onKillEntity(p_70074_1_);
	}

	@Override
	public void onLivingUpdate() {
		if (!this.worldObj.isRemote) {
			float brightness = this.getBrightness(1.0F);
			boolean canSeeSky = this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY),
					MathHelper.floor_double(this.posZ));
			if (brightness > 0.5F) {
				if (this.worldObj.isDaytime() && canSeeSky) {
					this.attackEntityFrom(DamageSource.magic, 0.5F);
				} else {
					this.setFire(2);
				}
			}
		}
		super.onLivingUpdate();
	}
}
