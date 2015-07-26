package de.teamlapen.vampirism.tileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import de.teamlapen.vampirism.block.BlockCoffin;
import de.teamlapen.vampirism.util.Logger;

public class TileEntityCoffin extends TileEntity {
	public int otherX;
	public int otherY;
	public int otherZ;
	public boolean occupied;
	private boolean lastTickOccupied;
	public int lidPos;
	public int color = 15;
	public boolean needsAnimation = false;

	public TileEntityCoffin() {

	}

	public void changeColor(int color) {
		this.color = color;
		needsAnimation = false;
		markDirty();
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	public TileEntityCoffin getPrimaryTileEntity() {
		if ((this.getBlockMetadata() & -8) == 0)
			return (TileEntityCoffin) worldObj.getTileEntity(otherX, otherY, otherZ);
		return this;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(this.xCoord - 4, this.yCoord, this.zCoord - 4, this.xCoord + 4, this.yCoord + 2, this.zCoord + 4);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
		((BlockCoffin) this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord)).setCoffinOccupied(this.worldObj, this.xCoord, this.yCoord, this.zCoord, null, this.occupied);
		// Logger.i("TECoffin", String.format("onDataPacket called, occupied=%s, remote=%s", this.occupied, this.worldObj.isRemote));
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.otherX = par1NBTTagCompound.getInteger("px");
		this.otherY = par1NBTTagCompound.getInteger("py");
		this.otherZ = par1NBTTagCompound.getInteger("pz");
		this.occupied = par1NBTTagCompound.getBoolean("occ");
		this.color = par1NBTTagCompound.getInteger("color");
		this.needsAnimation = par1NBTTagCompound.getBoolean("needsAnim");
		if (!occupied && needsAnimation)
			this.lidPos = 61;
		else
			this.lidPos = 0;
	}

	@Override
	public void updateEntity() {
		if ((this.getBlockMetadata() & -8) == 0)
			return;
		// On the server, metadata has priority over tile entity. On the client, tile entity has priority over metadata
		if (!this.worldObj.isRemote && (occupied == ((this.getBlockMetadata() & 4) == 0))) {
			occupied = !occupied;
			needsAnimation = true;
			markDirty();

		} else{
			BlockBed.func_149979_a(worldObj, xCoord, yCoord, zCoord, occupied);
		}

			if(lastTickOccupied!=occupied){
				this.worldObj.playSoundEffect(xCoord, (double)this.yCoord + 0.5D, zCoord, "vampirism:coffin_lid", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}
			lastTickOccupied=occupied;

		// Logger.i("TECoffin",
		// String.format("updateEntity called, now: occupied=%s, remote=%s",
		// occupied, this.worldObj.isRemote));
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("px", otherX);
		par1NBTTagCompound.setInteger("py", otherY);
		par1NBTTagCompound.setInteger("pz", otherZ);
		par1NBTTagCompound.setBoolean("occ", occupied);
		par1NBTTagCompound.setInteger("color", color);
		par1NBTTagCompound.setBoolean("needsAnim", needsAnimation);
	}

	/**
	 * Tries to find the second block/tile. Used by the castle generation
	 */
	public void tryToFindOtherTile(){
		for(int i=-1;i<2;i+=2){
				Block b=this.getWorldObj().getBlock(this.xCoord+i,this.yCoord,this.zCoord);
				if(b instanceof BlockCoffin){
					this.otherX=this.xCoord+i;
					this.otherY=this.yCoord;
					this.otherZ=this.zCoord;
					return;
				}
		}
		for(int j=-1;j<2;j+=2){
			Block b=this.getWorldObj().getBlock(this.xCoord,this.yCoord,this.zCoord+j);
			if(b instanceof BlockCoffin){
				this.otherX=this.xCoord;
				this.otherY=this.yCoord;
				this.otherZ=this.zCoord+j;
				return;
			}
		}
	}
}
