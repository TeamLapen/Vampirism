package de.teamlapen.vampirism.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.ai.EntityAIDefendLord;
import de.teamlapen.vampirism.entity.ai.EntityAIFollowBoss;
import de.teamlapen.vampirism.entity.ai.IMinion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;

public abstract class EntityVampireMinion extends DefaultVampire implements IMinion, ISyncable {

	/**
	 * Used for the visual transition from normal vampire to players minion
	 */
	private int oldVampireTexture = -1;

	public EntityVampireMinion(World world) {
		super(world);
		this.setSize(0.3F, 0.6F);
		this.tasks.addTask(4, new EntityAIFollowBoss(this, 1.0D));
		this.tasks.addTask(5, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.0, false));
		this.targetTasks.addTask(2, new EntityAIDefendLord(this));

		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (getLord() != null && getLord().getRepresentingEntity().equals(entity)) {
					return false;
				}
				if (entity instanceof EntityPlayer) {
					return VampirePlayer.get((EntityPlayer) entity).getLevel() <= BALANCE.VAMPIRE_FRIENDLY_LEVEL || VampirePlayer.get((EntityPlayer) entity).isVampireLord();
				}
				return false;
			}

		}));
		// Search for villagers
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity instanceof EntityVillager) {
					return !VampireMob.get((EntityVillager) entity).isVampire();
				}
				return false;
			}

		}));

		this.targetTasks.addTask(8, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MINION_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MINION_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MINION_MOVEMENT_SPEED);
	}

	@Override
	public void copyDataFrom(Entity from, boolean p) {
		super.copyDataFrom(from, p);
		if (from instanceof EntityVampireMinion) {
			EntityVampireMinion m = (EntityVampireMinion) from;
			this.copyDataFromMinion(m);
		}
	}

	protected void copyDataFromMinion(EntityVampireMinion from) {
		this.setOldVampireTexture(from.getOldVampireTexture());
	}

	@Override
	public float getBlockPathWeight(int p_70783_1_, int p_70783_2_, int p_70783_3_) {
		float i = 0.5F - this.worldObj.getLightBrightness(p_70783_1_, p_70783_2_, p_70783_3_);
		if (i > 0)
			return i;
		return 0.01F;
	}

	public int getOldVampireTexture() {
		return oldVampireTexture;
	}

	@Override
	public EntityCreature getRepresentingEntity() {
		return this;
	}

	@Override
	public boolean isChild() {
		return true;
	}

	/**
	 * Vampire minions have no right to complain about sun damage ;)
	 */
	@Override
	public boolean isValidLightLevel() {
		return true;
	}

	/**
	 * Can be used by child classes to write info to {@link #loadUpdateFromNBT(NBTTagCompound)}
	 * 
	 * @param nbt
	 */
	protected void loadPartialUpdateFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public void loadUpdateFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("oldvampire")) {
			this.oldVampireTexture = nbt.getInteger("oldvampire");
		}
		loadPartialUpdateFromNBT(nbt);
	}

	@Override
	public void onLivingUpdate() {
		if (oldVampireTexture != -1 && this.ticksExisted > 50) {
			oldVampireTexture = -1;
		}
		if (oldVampireTexture != -1 && worldObj.isRemote) {
			Helper.spawnParticlesAroundEntity(this, "witchMagic", 1.0F, 3);
		}
		super.onLivingUpdate();
	}

	public void setOldVampireTexture(int oldVampireTexture) {
		this.oldVampireTexture = oldVampireTexture;
	}

	@Override
	public void writeFullUpdateToNBT(NBTTagCompound nbt) {
		nbt.setInteger("oldvampire", oldVampireTexture);
		writeUpdateToNBT(nbt);
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound nbt) {
		if (shouldBeSavedWithLord()) {
			return false;
		}
		return super.writeToNBTOptional(nbt);
	}

	/**
	 * Can be used by child classes to write info to {@link #writeFullUpdateToNBT(NBTTagCompound)}
	 * 
	 * @param nbt
	 */
	protected void writeUpdateToNBT(NBTTagCompound nbt) {

	}

}
