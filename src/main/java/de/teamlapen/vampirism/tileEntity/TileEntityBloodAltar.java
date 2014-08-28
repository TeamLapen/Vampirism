package de.teamlapen.vampirism.tileEntity;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBloodAltar extends TileEntity {
	public TileEntityBloodAltar() {
		super();
		Logger.i("TileEntityBloodAltar", "Constructor called");
	}

	@Override
	public void writeToNBT(NBTTagCompound par1) {
		super.writeToNBT(par1);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1) {
		super.readFromNBT(par1);
	}
}
