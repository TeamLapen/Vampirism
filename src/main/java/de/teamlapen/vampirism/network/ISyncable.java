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
	public void loadUpdateFromNBT(NBTTagCompound nbt);

	/**
	 * This method is called to get update informations which should be send to the client
	 * @param worldJoin if the entity just joined the world
	 */
	public void writeFullUpdateToNBT(NBTTagCompound nbt);
	
}
