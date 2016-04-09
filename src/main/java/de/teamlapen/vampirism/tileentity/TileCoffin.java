package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.BlockCoffin;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Max on 12.03.2016.
 */
public class TileCoffin extends TileEntity implements ITickable {
    public BlockPos otherPos;
    public boolean occupied;
    public int lidPos;
    public int color = 15;
    public boolean needsAnimation = false;
    private boolean lastTickOccupied;

    public TileCoffin() {

    }

    public void changeColor(int color) {
        this.color = color;
        needsAnimation = false;
        markDirty();
        //TODO
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(this.getPos(), 1, nbtTag);
    }

    public TileCoffin getPrimaryTileEntity() {
        if ((this.getBlockMetadata() & -8) == 0)
            return (TileCoffin) worldObj.getTileEntity(otherPos);
        return this;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX() - 4, pos.getY(), pos.getZ() - 4, pos.getX() + 4, pos.getY() + 2, pos.getZ() + 4);
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        ModBlocks.coffin.setCoffinOccupied(this.worldObj, pos, null, this.occupied);
        VampirismMod.log.t("onDataPacket called, occupied=%s, remote=%s", this.occupied, this.worldObj.isRemote);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        otherPos = UtilLib.readPos(par1NBTTagCompound, "op");
        this.occupied = par1NBTTagCompound.getBoolean("occ");
        this.color = par1NBTTagCompound.getInteger("color");
        this.needsAnimation = par1NBTTagCompound.getBoolean("needsAnim");
        if (!occupied && needsAnimation)
            this.lidPos = 61;
        else
            this.lidPos = 0;
    }

    /**
     * Tries to find the second block/tile. Used by the castle generation
     */
    public void tryToFindOtherTile() {
        for (int i = -1; i < 2; i += 2) {
            Block b = this.getWorld().getBlockState(pos.add(i, 0, 0)).getBlock();
            if (b instanceof BlockCoffin) {
                otherPos = pos.add(i, 0, 0);
                return;
            }
        }
        for (int j = -1; j < 2; j += 2) {
            Block b = this.getWorld().getBlockState(pos.add(0, 0, j)).getBlock();
            if (b instanceof BlockCoffin) {
                otherPos = pos.add(0, 0, j);
                return;
            }
        }
    }

    @Override
    public void update() {
        if (!BlockCoffin.isHead(worldObj, pos))
            return;
        // On the server, metadata has priority over tile entity. On the client, tile entity has priority over metadata
        if (!this.worldObj.isRemote && (occupied == (!BlockCoffin.isOccupied(worldObj, pos)))) {
            occupied = !occupied;
            needsAnimation = true;
            markDirty();

        } else {
            BlockCoffin.setCoffinOccupied(worldObj, pos, occupied);
        }

        if (lastTickOccupied != occupied) {

            this.worldObj.playSound(pos.getX(), (double) this.pos.getY() + 0.5D, pos.getZ(), ModSounds.block_coffin_lid, SoundCategory.BLOCKS, 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F, true);
        }
        lastTickOccupied = occupied;

        VampirismMod.log.t("updateEntity called, now: occupied=%s, remote=%s", occupied, this.worldObj.isRemote);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        UtilLib.write(par1NBTTagCompound, "op", otherPos);
        par1NBTTagCompound.setBoolean("occ", occupied);
        par1NBTTagCompound.setInteger("color", color);
        par1NBTTagCompound.setBoolean("needsAnim", needsAnimation);
    }
}