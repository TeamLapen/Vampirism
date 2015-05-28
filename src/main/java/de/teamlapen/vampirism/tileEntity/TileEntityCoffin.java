package de.teamlapen.vampirism.tileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

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
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(this.xCoord-4, this.yCoord, this.zCoord-4, this.xCoord + 4, this.yCoord + 2, this.zCoord + 4);
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}
}
