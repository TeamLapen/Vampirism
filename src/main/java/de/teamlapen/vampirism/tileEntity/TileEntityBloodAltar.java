package de.teamlapen.vampirism.tileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.BloodAltarPacket;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

public class TileEntityBloodAltar extends TileEntity {
	private boolean occupied = false;
	public String BLOODALTAR_OCCUPIED_NBTKEY = "bloodaltaroccupied";
	
	public TileEntityBloodAltar() {
		super();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.occupied = nbt.getBoolean(BLOODALTAR_OCCUPIED_NBTKEY);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean(BLOODALTAR_OCCUPIED_NBTKEY, occupied);
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	public void setOccupied(boolean flag) {
		if(flag!=occupied) 
			VampirismMod.modChannel.sendToAll(new BloodAltarPacket(flag, this.xCoord, this.yCoord, this.zCoord));
		occupied = flag;
	}
}
