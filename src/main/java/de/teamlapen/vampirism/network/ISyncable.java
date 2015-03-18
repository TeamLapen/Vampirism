package de.teamlapen.vampirism.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public interface ISyncable {

	/**
	 * This method should load all included information. It might contain some or all synchable information.
	 * @param nbt
	 */
	@SideOnly(Side.CLIENT)
	public void loadPartialUpdate(NBTTagCompound nbt);
	
	/**
	 * This method is called when the entity joins a world and notifies the client
	 * @return
	 */
	public NBTTagCompound getJoinWorldSyncData();
	
}
