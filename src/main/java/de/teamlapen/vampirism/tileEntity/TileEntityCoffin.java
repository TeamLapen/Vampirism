package de.teamlapen.vampirism.tileEntity;

import de.teamlapen.vampirism.block.BlockCoffin;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.BlockBed;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityCoffin extends TileEntity {
	public int otherX;
	public int otherY;
	public int otherZ;
	public boolean occupied;
	public int lidPos;
	public int color;
	
	 public static final String[] colors = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
	
	
	public TileEntityCoffin() {
		
	}

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("px", otherX);
		par1NBTTagCompound.setInteger("py", otherY);
		par1NBTTagCompound.setInteger("pz", otherZ);
		par1NBTTagCompound.setBoolean("occ", occupied);
		par1NBTTagCompound.setInteger("color", color);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.otherX = par1NBTTagCompound.getInteger("px");
		this.otherY = par1NBTTagCompound.getInteger("py");
		this.otherZ = par1NBTTagCompound.getInteger("pz");
		this.occupied = par1NBTTagCompound.getBoolean("occ");
		this.color = par1NBTTagCompound.getInteger("color");
		if(occupied)
			this.lidPos = 0;
		else
			this.lidPos = 61;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
				this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net,
			S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
		((BlockCoffin) this.worldObj.getBlock(this.xCoord, this.yCoord,
				this.zCoord)).setCoffinOccupied(this.worldObj, this.xCoord,
				this.yCoord, this.zCoord, null, this.occupied);
		Logger.i("TECoffin", String.format("onDataPacket called, occupied=%s, remote=%s", this.occupied, this.worldObj.isRemote));
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(this.xCoord - 4, this.yCoord,
				this.zCoord - 4, this.xCoord + 4, this.yCoord + 2,
				this.zCoord + 4);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public void updateEntity() {
		if((this.getBlockMetadata() & 8) == 0)
			return;
		//On the server, metadata has priority over tile entity. On the client, tile entity has priority over metadata
		if(!this.worldObj.isRemote && (occupied != ((this.getBlockMetadata() & 4) != 0))) {
			occupied = !occupied;
			markDirty();
		}
		else
			BlockBed.func_149979_a(worldObj, xCoord, yCoord, zCoord, occupied);
//		Logger.i("TECoffin",
//		String.format("updateEntity called, now: occupied=%s, remote=%s",
//		occupied, this.worldObj.isRemote));
	}

	public void changeColor(int color) {
		this.color = color;
	}
}
