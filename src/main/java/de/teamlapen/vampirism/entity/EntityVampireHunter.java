package de.teamlapen.vampirism.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;

public class EntityVampireHunter extends EntityMob {

	private boolean isLookingForHome;

	public EntityVampireHunter(World p_i1738_1_) {
		super(p_i1738_1_);

		this.getNavigator().setAvoidsWater(true);
		this.setSize(0.6F, 1.8F);

		// Tasks (more tasks may be added in setLookingForHome()
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityVampire.class, 1.1, false));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.1, false));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityCreature.class, 0.9, false));
		this.tasks.addTask(6, new EntityAIWander(this, 0.7));
		this.tasks.addTask(9, new EntityAILookIdle(this));

		// TargetTasks (more tasks may be added in setLookingForHome()
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));

		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity instanceof EntityPlayer) {
					return VampirePlayer.get((EntityPlayer) entity).getLevel() > BALANCE.VAMPIRE_HUNTER_ATTACK_LEVEL;
				}
				return false;
			}

		}));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVampire.class, 0, true));
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityCreature.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity instanceof EntityCreature) {
					return VampireMob.get((EntityCreature) entity).isVampire();
				}
				return false;
			}

		}));

		// Default to not in a village, will be set to false in
		// WorldGenVampirism when generated on the surface in a village
		isLookingForHome = true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_HUNTER_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_HUNTER_ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_HUNTER_MOVEMENT_SPEED);
	}

	@Override
	protected boolean canDespawn() {
		return false; // keeps it from despawning when player is far away
	}

	/**
	 * Ignoew light level
	 */
	@Override
	public float getBlockPathWeight(int p_70783_1_, int p_70783_2_, int p_70783_3_) {
		return 0.5F;
	}

	@Override
	protected Item getDropItem() {
		return null;
	}

	/**
	 * Returns the home village if the hunter has a home
	 * 
	 * @return village or null
	 */
	public Village getHomeVillage() {
		if (isLookingForHome)
			return null;
		ChunkCoordinates cc = this.getHomePosition();
		return this.worldObj.villageCollectionObj.findNearestVillage(cc.posX, cc.posY, cc.posZ, 10);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	/**
	 * 
	 * @return Whether the hunter is looking for a village or not.
	 */
	public boolean isLookingForHome() {
		return isLookingForHome;
	}

	/**
	 * Ignore light level
	 */
	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	@Override
	public void onDeath(DamageSource s) {
	}

	@Override
	protected void onDeathUpdate() {
		this.worldObj.spawnParticle("depthsuspend", posX, posY, posZ, 0.5F, 0.5F, 0.5F);
		this.worldObj.spawnParticle("mobSpellAmbient", posX, posY, posZ, 0.5F, 0.5F, 0.5F);
		super.onDeathUpdate();
	}

	/**
	 * Makes the hunter not look for a new home anymore and adds village
	 * specific AI tasks
	 */
	public void setFoundHome() {
		isLookingForHome = false;
		this.tasks.addTask(3, new EntityAIMoveTowardsRestriction(this, 1.0F));
		this.tasks.addTask(4, new EntityAIMoveThroughVillage(this, 0.9F, false));
		this.targetTasks.addTask(2, new EntityAIDefendVillage(this));
	}
}
