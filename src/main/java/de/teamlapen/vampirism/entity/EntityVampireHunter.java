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
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.ai.EntityAIDefendVillage;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.network.UpdateEntityPacket;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.DifficultyCalculator.Difficulty;
import de.teamlapen.vampirism.util.DifficultyCalculator.IAdjustableLevel;
import de.teamlapen.vampirism.util.Helper;

/**
 * Vampire Hunter with three levels: Level 1: Agressive villager, Level 2: Professional hunter, Level 3: Professional hunter with axe and stake
 * 
 * @author Maxanier
 *
 */
public class EntityVampireHunter extends EntityMob implements ISyncable, IAdjustableLevel {

	private boolean isLookingForHome;
	protected int level = 0;
	private final static int MAX_LEVEL = 3;

	public EntityVampireHunter(World p_i1738_1_) {
		super(p_i1738_1_);

		this.getNavigator().setAvoidsWater(true);
		this.getNavigator().setBreakDoors(true);
		this.setSize(0.6F, 1.8F);

		// Tasks (more tasks may be added in setLookingForHome()
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIOpenDoor(this, true));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityVampire.class, 1.1, false));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.1, false));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityCreature.class, 0.9, false));

		this.tasks.addTask(6, new EntityAIWander(this, 0.7));
		this.tasks.addTask(9, new EntityAILookIdle(this));

		// TargetTasks (more tasks may be added in setHomeArea)
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
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, DefaultVampire.class, 0, true));
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

		this.setEquipmentDropChance(0, 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.updateEntityAttributes();

	}

	@Override
	protected boolean canDespawn() {
		return false; // keeps it from despawning when player is far away
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
		if (recentlyHit) {
			if (this.rand.nextInt(3) == 0) {
				this.dropItem(ModItems.humanHeart, 1);
			}
		}
	}

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
	public void writeFullUpdateToNBT(NBTTagCompound nbt) {
		this.writeEntityToNBT(nbt);
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
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
	public void loadUpdateFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("level")) {
			this.level = nbt.getInteger("level");
		}

	}

	@Override
	protected void onDeathUpdate() {
		this.worldObj.spawnParticle("depthsuspend", posX, posY, posZ, 0.5F, 0.5F, 0.5F);
		this.worldObj.spawnParticle("mobSpellAmbient", posX, posY, posZ, 0.5F, 0.5F, 0.5F);
		super.onDeathUpdate();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.loadUpdateFromNBT(nbt);
	}

	/**
	 * Makes the hunter not look for a new home anymore, sets the home area and adds village specific AI tasks
	 */
	@Override
	public void setHomeArea(int p_110171_1_, int p_110171_2_, int p_110171_3_, int p_110171_4_) {
		super.setHomeArea(p_110171_1_, p_110171_2_, p_110171_3_, p_110171_4_);
		if (isLookingForHome) {
			isLookingForHome = false;
			this.tasks.addTask(3, new EntityAIMoveTowardsRestriction(this, 1.0F));
			this.tasks.addTask(4, new EntityAIMoveThroughVillage(this, 0.9F, false));
			this.targetTasks.addTask(2, new EntityAIDefendVillage(this));
		}
	}

	@Override
	public void setLevel(int level) {
		this.setLevel(level, false);

	}

	/**
	 * Sets the vampire hunters level
	 * 
	 * @param l
	 * @param sync
	 *            whether to sync the level with the client or not
	 */
	public void setLevel(int l, boolean sync) {
		if (l > 0 && l != level) {
			this.level = l;
			this.updateEntityAttributes();
			if (level == 1) {
				this.setCurrentItemOrArmor(0, new ItemStack(ModItems.pitchfork));
			} else {
				this.setCurrentItemOrArmor(0, null);
			}
			if (sync && !this.worldObj.isRemote) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("level", level);
				Helper.sendPacketToPlayersAround(new UpdateEntityPacket(this, nbt), this);
			}

		}
	}

	@Override
	public int suggestLevel(Difficulty d) {
		return this.rand.nextInt(2) + 2;
	}

	protected void updateEntityAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_HUNTER_MAX_HEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(BALANCE.MOBPROP.VAMPIRE_HUNTER_ATTACK_DAMAGE * level);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BALANCE.MOBPROP.VAMPIRE_HUNTER_MOVEMENT_SPEED);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("level", level);
	}
}
