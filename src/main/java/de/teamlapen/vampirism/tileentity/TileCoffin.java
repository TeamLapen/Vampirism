package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.blocks.BlockCoffin;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * TileEntity for coffins. Handles coffin lid position and color
 */
public class TileCoffin extends TileEntity implements ITickable {
    public int lidPos;
    public int color = 15;

    private boolean lastTickOccupied;


    public void changeColor(EnumDyeColor color) {
        this.color = color;
        markDirty();
        //TODO
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX() - 4, pos.getY(), pos.getZ() - 4, pos.getX() + 4, pos.getY() + 2, pos.getZ() + 4);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return write(new NBTTagCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        read(packet.getNbtCompound());
    }

    @Override
    public void read(NBTTagCompound par1NBTTagCompound) {
        super.read(par1NBTTagCompound);

        this.color = par1NBTTagCompound.getInteger("color");

    }


    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void update() {
        if (!hasWorld() || !world.isRemote || !BlockCoffin.isHead(world, pos)) {
            return;

        }

        boolean occupied = BlockCoffin.isOccupied(world, pos);
        if (lastTickOccupied != occupied) {
            this.world.playSound(pos.getX(), (double) this.pos.getY() + 0.5D, pos.getZ(), ModSounds.coffin_lid, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F, true);
            lastTickOccupied = occupied;
        }


    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        NBTTagCompound nbt = super.write(compound);
        nbt.setInteger("color", color);
        return nbt;
    }
}