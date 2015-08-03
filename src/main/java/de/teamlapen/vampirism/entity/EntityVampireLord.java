package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.network.UpdateEntityPacket;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.DifficultyCalculator.Difficulty;
import de.teamlapen.vampirism.util.DifficultyCalculator.IAdjustableLevel;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityVampireLord extends DefaultVampireWithMinion implements ISyncable, IAdjustableLevel {

	private final static int MAX_LEVEL = 5;

	protected int level = 0;

	private boolean prevAttacking = false;

	public EntityVampireLord(World par1World) {
		super(par1World);

		//this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityVampireHunter.class, BALANCE.MOBPROP.VAMPIRE_DISTANCE_HUNTER, 1.0, 1.2));
		this.tasks.addTask(6, new EntityAIWander(this, 0.2));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.tasks.addTask(6,new EntityAIAttackOnCollide(this,EntityVampireLord.class,1.0D,false));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));
		this.targetTasks.addTask(3,new EntityAINearestAttackableTarget(this,EntityVampireLord.class,5,false));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.applyEntityAttributes(false);
	}

	protected void applyEntityAttributes(boolean aggressive) {
		if (aggressive) {
			this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(20D);
			this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(
					BALANCE.MOBPROP.VAMPIRE_LORD_MOVEMENT_SPEED * Math.pow((BALANCE.MOBPROP.VAMPIRE_LORD_IMPROVEMENT_PER_LEVEL-1)/3+1, (level - 1)));
		} else {
			this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(5D);
			this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(
					BALANCE.MOBPROP.VAMPIRE_LORD_MOVEMENT_SPEED * Math.pow(BALANCE.MOBPROP.VAMPIRE_LORD_IMPROVEMENT_PER_LEVEL, level - 1) / 3);
		}
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BALANCE.MOBPROP.VAMPIRE_LORD_MAX_HEALTH * Math.pow(BALANCE.MOBPROP.VAMPIRE_LORD_IMPROVEMENT_PER_LEVEL, level - 1));
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage)
				.setBaseValue(BALANCE.MOBPROP.VAMPIRE_LORD_ATTACK_DAMAGE * Math.pow(BALANCE.MOBPROP.VAMPIRE_LORD_IMPROVEMENT_PER_LEVEL, level - 1));
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		boolean flag = super.attackEntityAsMob(entity);
		if (flag && entity instanceof EntityLivingBase) {
			float tm=1f;
			int mr=1;
			if(entity instanceof EntityPlayer){
				float pld=this.getLevel()-VampirePlayer.get((EntityPlayer) entity).getLevel()/3f;
				tm=pld+1;
				mr=pld<1.5f?1:(pld<3?2:3);
			}
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.weakness.id, (int)(200*tm), rand.nextInt(mr) + 1));
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, (int)(100*tm), rand.nextInt(mr) + 1));
		}
		return flag;
	}

	@Override
	public boolean getAlwaysRenderNameTagForRender() {
		return true;
	}

	@Override
	public String getCommandSenderName() {
		return super.getCommandSenderName() + " " + StatCollector.translateToLocal("text.vampirism.entity_level") + " " + level;
	}



	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public int getMaxInPortalTime() {
		return 500;
	}

	@Override
	public int getMaxLevel() {
		return MAX_LEVEL;
	}

	@Override
	public void loadUpdateFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("level")) {
			this.level = nbt.getInteger("level");
		}

	}

	@Override
	public void onDeath(DamageSource s) {
		if (this.recentlyHit > 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
			if (level > 0 && level < 6) {
				this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, level - 1), 0.3F);
			} else if (level > 5) {
				this.entityDropItem(new ItemStack(ModItems.pureBlood, 1, 4), 0.3F);
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		if (!prevAttacking && this.getAttackTarget() != null) {
			prevAttacking = true;
			this.applyEntityAttributes(true);
		}
		if (prevAttacking && this.getAttackTarget() == null) {
			prevAttacking = false;
			this.applyEntityAttributes(false);
		}
		if (!worldObj.isRemote && shouldSpawnMinion()) {
			int i = 0;
			if (this.recentlyHit > 0) {
				i = this.rand.nextInt(3);
			}
			IMinion m = null;

			if (i == 1) {
				EntityLiving e = (EntityLiving) EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_MINION_SAVEABLE_NAME, this.worldObj);
				e.copyLocationAndAnglesFrom(this);
				worldObj.spawnEntityInWorld(e);
				m = (IMinion) e;
			} else if (i == 2 && this.getAttackTarget() != null) {
				m = (IMinion) Helper.spawnEntityBehindEntity(this.getAttackTarget(), REFERENCE.ENTITY.VAMPIRE_MINION_SAVEABLE_NAME);
			}
			if (m == null) {
				m = (IMinion) Helper.spawnEntityInWorld(worldObj, this.boundingBox.expand(19, 4, 19), REFERENCE.ENTITY.VAMPIRE_MINION_SAVEABLE_NAME, 3);
			}
			if (m != null) {
				m.setLord(this);
			}
		}
		if (!this.worldObj.isRemote && VampirismMod.isSunDamageTime(this.worldObj)) {
			if (this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
				this.teleportAway();
			}
		}
		super.onLivingUpdate();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.loadUpdateFromNBT(nbt);
	}

	@Override
	public void setLevel(int level) {
		this.setLevel(level, false);

	}

	/**
	 * Sets the vampire lords level and updates his nametag
	 * 
	 * @param l
	 * @param sync
	 *            whether to sync the level with the client or not
	 */
	public void setLevel(int l, boolean sync) {
		if (l > 0 && l != level) {
			this.level = l;
			float hp = this.getHealth() / this.getMaxHealth();
			this.applyEntityAttributes(false);
			this.setHealth(this.getMaxHealth() * hp);
			if (sync && !this.worldObj.isRemote) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("level", level);
				Helper.sendPacketToPlayersAround(new UpdateEntityPacket(this, nbt), this);
			}

		}
	}

	/**
	 * Decides if a new minion should be spawned. Therefore randomly checks the existing minion count
	 * 
	 * @return
	 */
	protected boolean shouldSpawnMinion() {
		if (this.ticksExisted % 40 == 0) {
			int count = getMinionHandler().getMinionCount();
			if (count < level + 1) {
				return true;
			}
			if (recentlyHit > 0 && count < 2 + level) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int suggestLevel(Difficulty d) {
		int avg = Math.round((d.avgLevel - 4) / 2F);
		int max = Math.round((d.maxLevel - 4) / 2F);
		int min = Math.round((d.minLevel - 4) / 2F);

		switch (rand.nextInt(6)) {
		case 0:
			return min;
		case 1:
			return max + 1;
		case 2:
			return avg;
		case 3:
			return avg + 1;
		default:
			return rand.nextInt(max + 2 - min) + min;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("level", level);
	}

	@Override
	public void writeFullUpdateToNBT(NBTTagCompound nbt) {
		this.writeEntityToNBT(nbt);
	}

	@Override public boolean getCanSpawnHere() {

		int i = MathHelper.floor_double(this.boundingBox.minY);
		//Only spawn on the surface
		if(i<60)return false;
		CastlePositionData data = CastlePositionData.get(worldObj);
		if(data.findPosAt(MathHelper.floor_double(posX),MathHelper.floor_double(posZ),true)!=null){
			return false;
		}
		return super.getCanSpawnHere();
	}

	@Override
	public int getMaxMinionCount() {
		return 100;
	}
}
