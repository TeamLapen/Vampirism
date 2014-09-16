package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

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
		}
		
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub
		
	}
	
	private void sync(){
		NBTTagCompound nbt= new NBTTagCompound();
		this.saveNBTData(nbt);
		nbt.setInteger(VampireMobPacket.KEY_ID,entity.getEntityId());
		VampirismMod.modChannel.sendToAll(new VampireMobPacket(nbt));
		
	}
	
	public boolean isBitten(){
		return bitten;
	}
	
	public void bite(){
		bitten=true;
		sync();
	}

}
