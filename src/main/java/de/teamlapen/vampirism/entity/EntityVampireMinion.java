package de.teamlapen.vampirism.entity;

import java.util.List;
import java.util.UUID;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.ai.EntityAIDefendLord;
import de.teamlapen.vampirism.entity.ai.EntityAIFollowBoss;
import de.teamlapen.vampirism.entity.ai.IMinion;
import de.teamlapen.vampirism.entity.ai.IMinionLord;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;

public class EntityVampireMinion extends DefaultVampire implements IMinion {

	private final static int MAX_SEARCH_TIME = 100;
	private UUID bossId = null;
	protected IMinionLord boss;
	private int lookForBossTimer = 0;

	public EntityVampireMinion(World world) {
		super(world);

		this.tasks.addTask(4, new EntityAIFollowBoss(this, 1.0D));
		this.targetTasks.addTask(2, new EntityAIDefendLord(this));
		
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if(boss!=null&&boss.getRepresentingEntity().equals(entity)){
					return false;
				}
				if (entity instanceof EntityPlayer) {
					return VampirePlayer.get((EntityPlayer) entity).getLevel() <= BALANCE.VAMPIRE_FRIENDLY_LEVEL;
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
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_MOVEMENT_SPEED);
	}

	@Override
	public IMinionLord getLord() {
		return boss;
	}

	@Override
	public void onLivingUpdate() {
		if (!this.worldObj.isRemote) {
			if (boss == null) {
				if(bossId!=null){
					List<EntityLivingBase> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(15, 10, 15));
					for (EntityLivingBase e : list) {
						if (e.getPersistentID().equals(bossId)) {
							if(e instanceof IMinionLord){
								boss = (IMinionLord)e;
								lookForBossTimer = 0;
							}
							else if(e instanceof EntityPlayer){
								boss=VampirePlayer.get((EntityPlayer)e);
								lookForBossTimer=0;
							}
							else{
								Logger.w("VampireMinion", "Found boss with UUID "+bossId+" but it isn't a Minion Lord");
								bossId=null;
								boss=null;
								lookForBossTimer=0;
							}

							break;
						}
					}
				}
				
				if (boss == null) {
					lookForBossTimer++;
				}
				if (lookForBossTimer > MAX_SEARCH_TIME) {
					this.attackEntityFrom(DamageSource.generic, 5);
				}
			} else if (!boss.isEntityAlive()) {
				boss = null;
				bossId = null;
			} else if (boss.getDistanceSquared(this)> 1000) {
				if (this.rand.nextInt(80) == 0) {
					this.attackEntityFrom(DamageSource.generic, 3);
				}
			}

		}
		super.onLivingUpdate();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("BossUUIDMost")) {
			this.bossId = new UUID(nbt.getLong("BossUUIDMost"), nbt.getLong("BossUUIDLeast"));
		}

	}

	private void setBossId(UUID id) {
		if (!id.equals(bossId)) {
			bossId = id;
			boss = null;
			lookForBossTimer = 0;
		}
	}

	@Override
	public void setLord(IMinionLord b) {
		if (!b.equals(boss)) {
			this.setBossId(b.getPersistentID());
			boss = b;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (this.bossId != null) {
			nbt.setLong("BossUUIDMost", this.bossId.getMostSignificantBits());
			nbt.setLong("BossUUIDLeast", this.bossId.getLeastSignificantBits());
		}

	}

}
