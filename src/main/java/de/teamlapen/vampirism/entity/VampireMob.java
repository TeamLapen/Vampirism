package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.VampireMobPacket;
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

	private boolean bitten;
	private final EntityLiving entity;
	public final static String EXT_PROP_NAME="VampireMob";
	private final String KEY_BITTEN="bitten";
	
	
	public VampireMob(EntityLiving mob){
		bitten=false;
		entity=mob;
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
		properties.setBoolean(KEY_BITTEN, this.bitten);
		compound.setTag(EXT_PROP_NAME, properties);
		
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		if(properties!=null){
			this.bitten=properties.getBoolean(KEY_BITTEN);
			if(bitten){
				Logger.i("test", "Entity is bitten "+entity);
			}
		}
		
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub
		
	}
	
	public void sync(){
		NBTTagCompound nbt= new NBTTagCompound();
		this.saveNBTData(nbt);
		VampirismMod.modChannel.sendToAll(new VampireMobPacket(nbt,entity.getEntityId()));
		
	}
	
	public boolean isBitten(){
		return bitten;
	}
	
	public void bite(){
		bitten=true;
		sync();
	}
	
	/**
	 * Returns how much blood can be collected by biting this entity. Zero if not biteable
	 * @param e
	 * @return
	 */
	public static int getBiteBloodAmount(EntityLiving e){
		if(e instanceof EntityPig || e instanceof EntitySheep || e instanceof EntityOcelot || e instanceof EntityWolf){
			return REFERENCE.smallBloodAmount;
		}
		if(e instanceof EntityCow || e instanceof EntityHorse || e instanceof EntityPigZombie || e instanceof EntityZombie){
			return REFERENCE.normalBloodAmount;
		}
		if(e instanceof EntityVillager || e instanceof EntityGiantZombie || e instanceof EntityWitch){
			return REFERENCE.bigBloodAmount;
		}
		return 0;
	}

}
