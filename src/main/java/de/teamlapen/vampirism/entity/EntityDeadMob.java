package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityDeadMob extends Entity {

	private static final int MAX_TICKS = 1200;

	public static List<String> mobs;

	static {
		mobs = new ArrayList<String>();
		mobs.add("Zombie");
		mobs.add("Skeleton");
	}

	public static boolean canBecomeDeadMob(EntityCreature entity) {
		if (entity != null && VampireMob.get(entity).isMinion())
			return false;
		return mobs.contains(EntityList.getEntityString(entity));
	}

	public static Entity createFromEntity(EntityCreature entity) {
		EntityDeadMob e = (EntityDeadMob) EntityList.createEntityByName(REFERENCE.ENTITY.DEAD_MOB_NAME, entity.worldObj);
		e.copyLocationAndAnglesFrom(entity);
		e.setDeadMob(EntityList.getEntityString(entity));
		return e;
	}

	private int health;

	private String TAG = "EntityDeadMob";

	private final int WATCHER_TYPE_ID = 10;

	public EntityDeadMob(World p_i1582_1_) {
		super(p_i1582_1_);
		this.setSize(0.98F, 0.4F);
		// this.yOffset = this.height / 2.0F;
		this.dataWatcher.addObject(WATCHER_TYPE_ID, 0);
		health = 5;
	}

	@Override
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
		if (this.isEntityInvulnerable(p_70097_1_)) {
			return false;
		} else {
			if (!this.isDead && !this.worldObj.isRemote) {
				this.health -= Math.round(p_70097_2_);

				if (this.health <= 0) {
					this.setDead();
				}
			}

			return true;
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public EntityCreature convertToMob() {
		EntityCreature e = (EntityCreature) EntityList.createEntityByName(getDeadMob(), worldObj);
		if (e != null) {
			e.copyLocationAndAnglesFrom(this);
			e.setHealth(e.getMaxHealth() * 2 / 3);
			worldObj.spawnEntityInWorld(e);
		} else {
			Logger.w(TAG, "Could not create entity: " + getDeadMob());
		}
		this.setDead();
		return e;
	}

	@Override
	protected void entityInit() {

	}

	public String getDeadMob() {
		int i = getDeadMobId();
		if (i >= mobs.size() || i < 0) {
			Logger.w(TAG, "Invalid Mob ID " + i);
			this.setDeadMobId(0);
			return "Zombie";
		}
		return mobs.get(i);
	}

	public int getDeadMobId() {
		return this.dataWatcher.getWatchableObjectInt(WATCHER_TYPE_ID);
	}




	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.ticksExisted > MAX_TICKS) {
			this.setDead();
		}
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.03999999910593033D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.onGround) {
			this.motionX *= 0.1D;
			this.motionZ *= 0.1D;
			this.motionY *= 0.1D;
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDeadMobId(nbt.getInteger("entity_id"));
	}

	public void setDeadMob(String s) {
		for (int i = 0; i < mobs.size(); i++) {
			if (mobs.get(i).equals(s)) {
				this.setDeadMobId(i);
				return;
			}
		}
		Logger.w(TAG, "Did not find an id for " + s);
	}

	public void setDeadMobId(int id) {
		if (id >= 0 && id < mobs.size()) {
			this.dataWatcher.updateObject(WATCHER_TYPE_ID, id);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldRenderSkull() {
		return getDeadMobId() == 0 || getDeadMobId() == 1;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("entity_id", getDeadMobId());
	}

}
