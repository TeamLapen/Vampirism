package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.blocks.BlockCoffin;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *   TileEntity for coffins. Handles coffin lid position and color
 */
public class TileCoffin extends TileEntity implements ITickable {
    public int lidPos;
    public int color = 15;

    private boolean lastTickOccupied;


    public void changeColor(int color) {
        this.color = color;
        markDirty();
        //TODO
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(this.getPos(), 1, nbtTag);
    }



    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX() - 4, pos.getY(), pos.getZ() - 4, pos.getX() + 4, pos.getY() + 2, pos.getZ() + 4);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);

        this.color = par1NBTTagCompound.getInteger("color");

    }


    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) return;
        if (!BlockCoffin.isHead(worldObj, pos))
            return;

        boolean occupied = BlockCoffin.isOccupied(worldObj, pos);
        if (lastTickOccupied != occupied) {
            this.worldObj.playSound(pos.getX(), (double) this.pos.getY() + 0.5D, pos.getZ(), ModSounds.block_coffin_lid, SoundCategory.BLOCKS, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F, true);
            lastTickOccupied = occupied;
        }



    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("color", color);

    }
}