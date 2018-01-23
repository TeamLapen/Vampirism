package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class TilePedestal extends TileEntity implements ITickable {

    private final Random rand = new Random();
    private int ticksExisted;
    private boolean charging;

    @Nonnull
    private ItemStack stack = ItemStack.EMPTY;

    public TilePedestal() {
    }

    @Override
    public void update() {
        this.ticksExisted++;
        if (this.world.isRemote) {
            if (charging && ticksExisted % 8 == 0) {
                spawnChargedParticle();
            }
        }
    }

    public int getTickForRender() {
        return ticksExisted;
    }


    @Nonnull
    public ItemStack getStackForRender() {
        return stack;
    }

    /**
     * Set the held stack.
     *
     * @return If successful
     */
    public boolean setStack(@Nonnull ItemStack stack) {
        if (this.stack.isEmpty()) {
            this.stack = stack;
            return true;
        }
        return true;
    }

    public boolean hasStack() {
        return !this.stack.isEmpty();
    }

    @Nonnull
    public ItemStack removeStack() {
        ItemStack stack = this.stack;
        this.stack = ItemStack.EMPTY;
        return stack;
    }


    @SideOnly(Side.CLIENT)
    private void spawnChargedParticle() {
        Vec3d pos = new Vec3d(this.getPos()).addVector(0.5, 0.8, 0.5);
        VampLib.proxy.getParticleHandler().spawnParticle(this.getWorld(), ModParticles.FLYING_BLOOD, this.pos.getX() + 0.20, this.getPos().getY() + 0.65, this.getPos().getZ() + 0.20, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1, (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), 177);
        VampLib.proxy.getParticleHandler().spawnParticle(this.getWorld(), ModParticles.FLYING_BLOOD, this.pos.getX() + 0.80, this.getPos().getY() + 0.65, this.getPos().getZ() + 0.20, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1, (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), 177);
        VampLib.proxy.getParticleHandler().spawnParticle(this.getWorld(), ModParticles.FLYING_BLOOD, this.pos.getX() + 0.20, this.getPos().getY() + 0.65, this.getPos().getZ() + 0.80, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1, (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), 177);
        VampLib.proxy.getParticleHandler().spawnParticle(this.getWorld(), ModParticles.FLYING_BLOOD, this.pos.getX() + 0.80, this.getPos().getY() + 0.65, this.getPos().getZ() + 0.80, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1, (int) (3.0F / (rand.nextFloat() * 0.6F + 0.4F)), 177);

    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("item")) {
            this.stack = new ItemStack(compound.getCompoundTag("item"));
        } else {
            this.stack = ItemStack.EMPTY;
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        IBlockState block = this.world.getBlockState(this.pos);
        world.notifyBlockUpdate(pos, block, block, 3);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (hasStack()) {
            compound.setTag("item", this.stack.serializeNBT());
        }
        return super.writeToNBT(compound);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }


}
