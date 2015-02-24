package de.teamlapen.vampirism.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.villages.VillageVampire;
import de.teamlapen.vampirism.villages.VillageVampireData;

public class EntityVampire extends EntityMob {
	// TODO Sounds

	public EntityVampire(World par1World) {
		super(par1World);

		this.getNavigator().setAvoidsWater(true);
		this.getNavigator().setBreakDoors(true);
		this.setSize(0.6F, 1.8F);

		// Attack player
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIBreakDoor(this));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.1, false));
		// Attack vampire hunter
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityVampireHunter.class, 1.0, true));
		// Attack villager
		this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 0.9, true));
		// Avoids Vampire Hunters
		this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityVampireHunter.class, BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER, 1.0, 1.2));
		// Low priority tasks
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 0.6, false));
		this.tasks.addTask(6, new EntityAIWander(this, 0.7));
		this.tasks.addTask(9, new EntityAILookIdle(this));

		// Search for players
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity instanceof EntityPlayer) {
					return VampirePlayer.get((EntityPlayer) entity).getLevel() <= BALANCE.VAMPIRE_FRIENDLY_LEVEL;
				}
				return false;
			}

		}));
		// Search for villagers
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity instanceof EntityVillager) {
					return !VampireMob.get((EntityVillager) entity).isVampire();
				}
				return false;
			}

		}));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MOVEMENT_SPEED);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onDeath(DamageSource s) {
		if (s.getEntity() != null && s.getEntity() instanceof EntityPlayer) {
			this.dropItem(ModItems.vampireFang, 1);
		}
	}

	@Override
	public void onKillEntity(EntityLivingBase entity) {

		if (entity instanceof EntityVillager) {

			Entity e = EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_NAME, this.worldObj);
			e.copyLocationAndAnglesFrom(entity);
			VillageVampire v=VillageVampireData.get(entity.worldObj).findNearestVillage(entity);
			if(v!=null){
				v.villagerConvertedByVampire();
			}
			this.worldObj.spawnEntityInWorld(e);
		}
		super.onKillEntity(entity);
	}

	@Override
	public void onLivingUpdate() {
		if (!this.worldObj.isRemote) {
			float brightness = this.getBrightness(1.0F);
			boolean canSeeSky = this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY),
					MathHelper.floor_double(this.posZ));
			if (brightness > 0.5F) {
				if (this.worldObj.isDaytime() && canSeeSky) {
					this.attackEntityFrom(VampirismMod.sunDamage, 0.5F);
				}
			}
		}
		super.onLivingUpdate();
	}

}
