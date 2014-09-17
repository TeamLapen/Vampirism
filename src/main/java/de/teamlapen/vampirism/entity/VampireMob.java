package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;

public class VampireMob implements IExtendedEntityProperties{

	private final EntityLiving entity;
	public final static String EXT_PROP_NAME="VampireMob";
	private final String KEY_BLOOD="blood";
	private static final int BLOOD_WATCHER=20;
	private final int maxBlood;
	
	
	public VampireMob(EntityLiving mob){
		entity=mob;
		maxBlood=getMaxBloodAmount(mob);
		entity.getDataWatcher().addObject(BLOOD_WATCHER, maxBlood);
	}
	
	public static final void register(EntityLiving mob){
		mob.registerExtendedProperties(VampireMob.EXT_PROP_NAME, new VampireMob(mob));
	}
	
	public static final VampireMob get(EntityLiving mob){
		return (VampireMob) mob.getExtendedProperties(VampireMob.EXT_PROP_NAME);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger(KEY_BLOOD, this.entity.getDataWatcher().getWatchableObjectInt(BLOOD_WATCHER));
		compound.setTag(EXT_PROP_NAME, properties);
		
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		if(properties!=null){
			this.entity.getDataWatcher().updateObject(BLOOD_WATCHER, properties.getInteger(KEY_BLOOD));
		}
		
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub
		
	}
	
	private int getBlood(){
		return this.entity.getDataWatcher().getWatchableObjectInt(BLOOD_WATCHER);
	}
	private void setBlood(int b){
		this.entity.getDataWatcher().updateObject(BLOOD_WATCHER,b);
	}
	public boolean isBitten(){
		return getBlood()==0;
	}
	
	/**
	 * Bite the entity. Returns the retrieved blood
	 * @return Retrieved blood, 0 if already empty, -1 if health too high, -2 if not biteable
	 */
	public int bite(){
		if(entity.getHealth()/entity.getMaxHealth()> REFERENCE.suckBloodHealthRequirement){
			//Cannot be bitten yet
			return -1;
		}
		int amount=getBlood();
		if(amount==-1){
			//Cannot be bitten at all
			return -2;
		}
		
		setBlood(0);
		return amount;
		
	}
	
	/**
	 * Returns how much blood can be collected by biting this entity. Zero if not biteable
	 * @param e
	 * @return
	 */
	public static int getMaxBloodAmount(EntityLiving e){
		if(e instanceof EntityPig || e instanceof EntitySheep || e instanceof EntityOcelot || e instanceof EntityWolf){
			return REFERENCE.smallBloodAmount;
		}
		if(e instanceof EntityCow || e instanceof EntityHorse || e instanceof EntityPigZombie || e instanceof EntityZombie){
			return REFERENCE.normalBloodAmount;
		}
		if(e instanceof EntityVillager || e instanceof EntityWitch){
			return REFERENCE.bigBloodAmount;
		}
		return -1;
	}

}
