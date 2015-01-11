package de.teamlapen.vampirism.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.REFERENCE;

public class VampireMob implements IExtendedEntityProperties {

	private final EntityLiving entity;
	public final static String EXT_PROP_NAME = "VampireMob";
	private final String KEY_VAMPIRE = "vampire";
	private static final int VAMPIRE_WATCHER = 25;
	private final int blood;
	public static final VampireMob get(EntityLiving mob) {
		return (VampireMob) mob.getExtendedProperties(VampireMob.EXT_PROP_NAME);
	}

	/**
	 * Returns how much blood can be collected by biting this entity. Zero if
	 * not biteable
	 * 
	 * @param e
	 * @return
	 */
	public static int getMaxBloodAmount(EntityLiving e) {
		if (e instanceof EntityPig || e instanceof EntitySheep || e instanceof EntityOcelot || e instanceof EntityWolf) {
			return BALANCE.SMALL_BLOOD_AMOUNT;
		}
		if (e instanceof EntityCow || e instanceof EntityHorse || e instanceof EntityPigZombie || e instanceof EntityZombie) {
			return BALANCE.NORMAL_BLOOD_AMOUNT;
		}
		if (e instanceof EntityVillager || e instanceof EntityWitch) {
			return BALANCE.BIG_BLOOD_AMOUNT;
		}
		return -1;
	}

	public static final void register(EntityLiving mob) {
		mob.registerExtendedProperties(VampireMob.EXT_PROP_NAME, new VampireMob(mob));
	}

	

	public VampireMob(EntityLiving mob) {
		entity = mob;
		blood = getMaxBloodAmount(mob);
		entity.getDataWatcher().addObject(VAMPIRE_WATCHER, (short)0);
	}

	/**
	 * Bite the entity. Returns the retrieved blood
	 * 
	 * @return Retrieved blood, 0 if already empty, -1 if health too high, -2 if
	 *         not biteable
	 */
	public int bite() {
		
		if(blood==-1){
			return -2;
		}
		if(isVampire()){
			return 0;
		}
		if (entity.getHealth() / entity.getMaxHealth() > BALANCE.SUCK_BLOOD_HEALTH_REQUIREMENT) {
			// Cannot be bitten yet
			return -1;
		}
		makeVampire();
		return blood;

	}
	
	public boolean isVampire(){
		return this.entity.getDataWatcher().getWatchableObjectShort(VAMPIRE_WATCHER)==(short)1;
	}
	
	private boolean makeVampire(){
		if(blood==-1){
			return false;
		}
		entity.getDataWatcher().updateObject(VAMPIRE_WATCHER, (short)1);
		return true;
	}


	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub

	}

	public boolean canBeBitten() {
		return blood!=-1&&!isVampire();
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		if (properties != null) {
			this.entity.getDataWatcher().updateObject(VAMPIRE_WATCHER, properties.getShort(KEY_VAMPIRE));
		}

	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setShort(KEY_VAMPIRE, this.entity.getDataWatcher().getWatchableObjectShort(VAMPIRE_WATCHER));
		compound.setTag(EXT_PROP_NAME, properties);

	}


}
