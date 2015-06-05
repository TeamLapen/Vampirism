package de.teamlapen.vampirism.entity;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.ai.EntityAIModifier;
import de.teamlapen.vampirism.entity.ai.IMinion;
import de.teamlapen.vampirism.entity.ai.IMinionLord;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.UpdateEntityPacket;
import de.teamlapen.vampirism.network.UpdateEntityPacket.ISyncableExtendedProperties;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.villages.VillageVampire;
import de.teamlapen.vampirism.villages.VillageVampireData;

public class VampireMob implements ISyncableExtendedProperties, IMinion {

	public static final VampireMob get(EntityCreature mob) {
		return (VampireMob) mob.getExtendedProperties(VampireMob.EXT_PROP_NAME);
	}

	/**
	 * Returns how much blood can be collected by biting this entity. -1 if poisonous, -2 if defending
	 * 
	 * @param e
	 * @return
	 */
	public static int getMaxBloodAmount(EntityCreature e) {
		if (e instanceof EntityPig || e instanceof EntitySheep || e instanceof EntityOcelot || e instanceof EntityWolf) {
			return BALANCE.SMALL_BLOOD_AMOUNT;
		}
		if (e instanceof EntityCow || e instanceof EntityHorse) {
			return BALANCE.NORMAL_BLOOD_AMOUNT;
		}
		if (e instanceof EntityVillager || e instanceof EntityWitch) {
			return BALANCE.BIG_BLOOD_AMOUNT;
		}
		if (e instanceof EntityVampireHunter) {
			return -2;
		}
		return -1;
	}

	public static final void register(EntityCreature mob) {
		mob.registerExtendedProperties(VampireMob.EXT_PROP_NAME, new VampireMob(mob));
	}

	private final EntityCreature entity;
	public final static String EXT_PROP_NAME = "VampireMob";

	private final String KEY_TYPE = "type";


	private final int blood;
	private byte type;

	private final static int MAX_SEARCH_TIME = 100;
	private UUID bossId = null;
	protected IMinionLord boss;
	private int lookForBossTimer = 0;

	public VampireMob(EntityCreature mob) {
		entity = mob;
		blood = getMaxBloodAmount(mob);
		type=(byte)0;
	}

	/**
	 * Bite the entity. Returns the retrieved blood
	 * 
	 * @return Retrieved blood, 0 if already empty, -1 if health too high, -2 if not biteable
	 */
	public int bite() {
		if (blood == -1) {
			return -2;
		}
		if (blood == -2) {
			return -1;
		}
		if (isVampire()) {
			return 0;
		}

		if (!lowEnoughHealth()) {
			// Cannot be bitten yet
			return -1;
		}
		if (entity.worldObj.rand.nextInt(2) == 0) {
			makeVampire();
		} else {
			if (entity instanceof EntityVampireHunter) {
				entity.attackEntityFrom(DamageSource.magic, 1);
			} else {
				//Type should be only changed by the dedicated methods, but since the mob will die instantly it should not cause any problems
				type = (byte) (type | 1);
				entity.attackEntityFrom(DamageSource.magic, 100);
			}

		}

		if (entity instanceof EntityVillager) {
			VillageVampire v = VillageVampireData.get(entity.worldObj).findNearestVillage(entity);
			if (v != null) {
				v.villagerBitten();
			}
		}
		// If entity is a child only give 1/3 blood
		if (entity instanceof EntityAgeable) {
			if (((EntityAgeable) entity).getGrowingAge() < 0) {
				return Math.round((float) blood / 3);
			}
		}
		return blood;

	}

	public boolean canBeBitten() {
		return blood > 0 && !isVampire();
	}

	@Override
	public IMinionLord getLord() {
		if (!isMinion()) {
			Logger.w("VampireMob", "Trying to get lord, but mob is no minion");
		}
		return boss;
	}

	@Override
	public EntityCreature getRepresentingEntity() {
		return entity;
	}

	@Override
	public void init(Entity entity, World world) {
	}

	public boolean isMinion() {
		return ((type & 2) == 2);
	}

	public boolean isVampire() {
		return ((type & 1) == 1);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		if (properties != null) {
			this.loadUpdateFromNBT(properties);
			if (isMinion()) {
				if (properties.hasKey("BossUUIDMost")) {
					this.bossId = new UUID(properties.getLong("BossUUIDMost"), properties.getLong("BossUUIDLeast"));
				}
			}
		}

	}

	protected void lookForBoss() {
		List<EntityLivingBase> list = entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, entity.boundingBox.expand(15, 10, 15));
		for (EntityLivingBase e : list) {
			if (e.getPersistentID().equals(bossId)) {
				if (e instanceof IMinionLord) {
					boss = (IMinionLord) e;
					lookForBossTimer = 0;
				} else if (e instanceof EntityPlayer) {
					boss = VampirePlayer.get((EntityPlayer) e);
					lookForBossTimer = 0;
				} else {
					Logger.w("VampireMob", "Found boss with UUID " + bossId + " but it isn't a Minion Lord");
					bossId = null;
					boss = null;
					lookForBossTimer = 0;
				}

				break;
			}
		}
	}

	public boolean lowEnoughHealth() {
		return (entity.getHealth() / entity.getMaxHealth()) <= BALANCE.SUCK_BLOOD_HEALTH_REQUIREMENT;
	}

	public void makeMinion(IMinionLord lord) {
		setMinion();
		this.setLord(lord);
	}

	private boolean makeVampire() {
		if (blood < 0) {
			return false;
		}
		setVampire();

		entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 200));
		entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100));
		return true;
	}

	public void onUpdate() {
		if (isMinion() && !entity.worldObj.isRemote) {
			if (boss == null) {
				if (bossId != null) {
					lookForBoss();
				}
				if (boss == null) {
					lookForBossTimer++;
				}
				if (lookForBossTimer > MAX_SEARCH_TIME) {
					entity.attackEntityFrom(DamageSource.generic, 5);
				}

			} else if (!boss.isTheEntityAlive()) {
				boss = null;
				bossId = null;
			} else if (boss.getTheDistanceSquared(entity) > 1000 && !entity.worldObj.isRemote) {
				if (entity.worldObj.rand.nextInt(80) == 0) {
					entity.attackEntityFrom(DamageSource.generic, 3);
				}
			}
			if (boss != null) {
				if (boss.getRepresentingEntity().equals(entity.getAttackTarget())) {
					entity.setAttackTarget(boss.getMinionTarget());
				}
				if(entity.equals(entity.getAttackTarget())){
					entity.setAttackTarget(null);
				}
			}
		}
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		this.writeFullUpdateToNBT(properties);
		if (isMinion()) {
			if (this.bossId != null) {
				properties.setLong("BossUUIDMost", this.bossId.getMostSignificantBits());
				properties.setLong("BossUUIDLeast", this.bossId.getLeastSignificantBits());
			}
		}
		compound.setTag(EXT_PROP_NAME, properties);

	}

	@Override
	public void setLord(IMinionLord b) {
		if (!b.equals(boss)) {
			this.setLordId(b.getThePersistentID());
			b.getMinionHandler().registerMinion(this, true);
			boss = b;
		}

	}

	private void setLordId(UUID id) {
		if (!id.equals(bossId)) {
			bossId = id;
			boss = null;
			lookForBossTimer = 0;
		}

	}

	private void setMinion() {
		type = (byte) (type | 2);
		EntityAIModifier.makeMinion(this, entity);
		this.sync();
	}

	private void setVampire() {
		type = (byte) (type | 1);
		EntityAIModifier.addVampireMobTasks(entity);
		this.sync();
	}

	@Override
	public void loadUpdateFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey(KEY_TYPE)){
			type=nbt.getByte(KEY_TYPE);
		}
		
	}

	@Override
	public void writeFullUpdateToNBT(NBTTagCompound nbt) {
		nbt.setByte(KEY_TYPE, type);
	}

	@Override
	public int getTheEntityID() {
		return entity.getEntityId();
	}

	public void sync() {
		if(!entity.worldObj.isRemote){
			Helper.sendPacketToPlayersAround(new UpdateEntityPacket(this), entity);
		}
		
	}

}
