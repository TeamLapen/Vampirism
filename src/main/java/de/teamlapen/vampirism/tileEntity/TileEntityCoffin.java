package de.teamlapen.vampirism.tileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCoffin extends TileEntity {
	public int secondary_x;
	public int secondary_y;
	public int secondary_z;

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("px", secondary_x);
		par1NBTTagCompound.setInteger("py", secondary_y);
		par1NBTTagCompound.setInteger("pz", secondary_z);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.secondary_x = par1NBTTagCompound.getInteger("px");
		this.secondary_y = par1NBTTagCompound.getInteger("py");
		this.secondary_z = par1NBTTagCompound.getInteger("pz");
	}
}
