package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class TilePedestal extends TileEntity implements ITickable {

    private final Random rand = new Random();
    private int ticksExistedClient;
    /**
     * If larger zero: Charging
     * If zero: Ready to restart
     * If below zero: Check cooldown
     */
    private int chargingTicks;
    private int bloodStored = 0;
    private int chargeRate = 30;

    @Nonnull
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public void update() {
        if (!this.world.isRemote) {
            if (chargingTicks > 0) {
                chargingTicks--;
                if (chargingTicks == 0) {
                    IBloodChargeable chargeable = getChargeItem(this.stack);
                    if (chargeable != null) {
                        if (this.bloodStored > 0) {
                            int charged = chargeable.charge(this.stack, this.bloodStored);
                            this.bloodStored -= Math.max(0, charged);
                        }
                    }
                    this.markDirtyAndUpdateClient();
                }
            } else if (chargingTicks == 0) {
                IBloodChargeable chargeable = getChargeItem(this.stack);
                if (chargeable != null && chargeable.canBeCharged(stack)) {
                    if (this.bloodStored < chargeRate) {
                        this.drainBlood();
                    }
                    if (this.bloodStored > 0) {
                        this.chargingTicks = 20;
                        this.markDirtyAndUpdateClient();
                    } else {
                        this.chargingTicks = -40;
                    }
                } else {
                    this.chargingTicks = -40;
                }
            } else {
                this.chargingTicks++;
            }
        } else {
            this.ticksExistedClient++;
            if (chargingTicks > 0 && ticksExistedClient % 8 == 0) {
                spawnChargedParticle();
            }
        }
    }

    /**
     * Tries to retrieve a {@link IBloodChargeable} instance from the given stack
     *
     * @return May be null
     */
    @Nullable
    private IBloodChargeable getChargeItem(@Nonnull ItemStack stack) {
        return stack.isEmpty() ? null : (stack.getItem() instanceof IBloodChargeable ? (IBloodChargeable) stack.getItem() : null);
    }

    @SideOnly(Side.CLIENT)
    public int getTickForRender() {
        return ticksExistedClient;
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
        this.chargingTicks=0;
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
        this.bloodStored = compound.getInteger("blood_stored");
        this.chargingTicks = compound.getInteger("charging_ticks");
    }

    public void markDirtyAndUpdateClient() {
        super.markDirty();
        IBlockState block = this.world.getBlockState(this.pos);
        world.notifyBlockUpdate(pos, block, block, 3);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (hasStack()) {
            compound.setTag("item", this.stack.serializeNBT());
        }
        compound.setInteger("blood_stored", bloodStored);
        compound.setInteger("charging_ticks", chargingTicks);
        return super.writeToNBT(compound);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }


    private void drainBlood() {
        IFluidHandler handler = FluidUtil.getFluidHandler(this.world, this.pos.down(), EnumFacing.UP);
        if (handler != null) {
            FluidStack drained = handler.drain(new FluidStack(ModFluids.blood, VReference.FOOD_TO_FLUID_BLOOD), false);
            if (drained != null && drained.amount == VReference.FOOD_TO_FLUID_BLOOD) {
                drained = handler.drain(new FluidStack(ModFluids.blood, VReference.FOOD_TO_FLUID_BLOOD), true);
                if (drained != null) {//Just to be safe
                    bloodStored += drained.amount;
                }
            }
        }
    }
}
