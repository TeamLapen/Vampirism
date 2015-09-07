package de.teamlapen.vampirism.tileEntity;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.block.BlockCoffin;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class TileEntityCoffin extends TileEntity {
	public BlockPos otherPos;
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
		return new S35PacketUpdateTileEntity(this.getPos(), 1, nbtTag);
	}

	public TileEntityCoffin getPrimaryTileEntity() {
		if ((this.getBlockMetadata() & -8) == 0)
			return (TileEntityCoffin) worldObj.getTileEntity(otherPos);
		return this;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.fromBounds(pos.getX() - 4,pos.getY(), pos.getZ()- 4, pos.getX() + 4, pos.getY() + 2, pos.getZ() + 4);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.worldObj.markBlockForUpdate(pos);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
		ModBlocks.coffin.setCoffinOccupied(this.worldObj, pos, null, this.occupied);
		// Logger.i("TECoffin", String.format("onDataPacket called, occupied=%s, remote=%s", this.occupied, this.worldObj.isRemote));
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		otherPos=Helper.readPos(par1NBTTagCompound,"op");
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
			BlockBed.func_149979_a(worldObj, pos, occupied);
		}

			if(lastTickOccupied!=occupied){
				this.worldObj.playSoundEffect(pos.getX(), (double)this.pos.getY() + 0.5D, pos.getZ(), "vampirism:coffin_lid", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}
			lastTickOccupied=occupied;

		// Logger.i("TECoffin",
		// String.format("updateEntity called, now: occupied=%s, remote=%s",
		// occupied, this.worldObj.isRemote));
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		Helper.write(par1NBTTagCompound,"op",otherPos);
		par1NBTTagCompound.setBoolean("occ", occupied);
		par1NBTTagCompound.setInteger("color", color);
		par1NBTTagCompound.setBoolean("needsAnim", needsAnimation);
	}

	/**
	 * Tries to find the second block/tile. Used by the castle generation
	 */
	public void tryToFindOtherTile(){
		for(int i=-1;i<2;i+=2){
				Block b=this.getWorld().getBlockState(pos.add(i,0,0)).getBlock();
				if(b instanceof BlockCoffin){
					otherPos=pos.add(i,0,0);
					return;
				}
		}
		for(int j=-1;j<2;j+=2){
			Block b=this.getWorld().getBlockState(pos.add(0,0,j)).getBlock();
			if(b instanceof BlockCoffin){
				otherPos=pos.add(0,0,j);
				return;
			}
		}
	}
}
