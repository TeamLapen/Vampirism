package de.teamlapen.vampirism.tileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class TileEntityBloodAltar extends TileEntity {
	public boolean hasSword = false;
	public final String teID = "" + Math.random();
	
	public TileEntityBloodAltar() {
		super();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.hasSword = nbt.getBoolean(REFERENCE.BLOODALTAR_HASSWORD_NBT_KEY);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean(REFERENCE.BLOODALTAR_HASSWORD_NBT_KEY, hasSword);
	}
}
