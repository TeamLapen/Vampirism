package de.teamlapen.vampirism.tileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCoffin extends TileEntity {
	public int otherX;
	public int otherY;
	public int otherZ;

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("px", otherX);
		par1NBTTagCompound.setInteger("py", otherY);
		par1NBTTagCompound.setInteger("pz", otherZ);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.otherX = par1NBTTagCompound.getInteger("px");
		this.otherY = par1NBTTagCompound.getInteger("py");
		this.otherZ = par1NBTTagCompound.getInteger("pz");
	}
}
