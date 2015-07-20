package de.teamlapen.vampirism;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/**
 * Created by Max on 19.07.2015.
 */
public class VampireLordData extends WorldSavedData {
	private final static String IDENTIFIER ="vampirism_lord_data";

	public boolean shouldRegenerateCastleDim() {
		return shouldRegenerateCastleDim;
	}

	public void setRegenerateCastleDim(boolean shouldRegenerateCastleDim) {
		this.shouldRegenerateCastleDim = shouldRegenerateCastleDim;
	}

	private boolean shouldRegenerateCastleDim=true;

	public static VampireLordData get(World world){
		VampireLordData data= (VampireLordData) world.mapStorage.loadData(VampireLordData.class,IDENTIFIER);
		if(data==null){
			data=new VampireLordData(IDENTIFIER);
			world.mapStorage.setData(IDENTIFIER,data);
		}
		return data;
	}
	public VampireLordData(String identifier) {
		super(identifier);
	}

	@Override public void readFromNBT(NBTTagCompound p_76184_1_) {

	}

	@Override public void writeToNBT(NBTTagCompound p_76187_1_) {

	}
}
