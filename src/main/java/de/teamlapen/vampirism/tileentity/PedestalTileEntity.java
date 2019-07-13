package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class PedestalTileEntity extends TileEntity implements ITickable, IItemHandler {

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

    private final LazyOptional<IItemHandler> opt = LazyOptional.of(() -> this);


    public PedestalTileEntity() {
        super(ModTiles.blood_pedestal);
    }

    @Nonnull
    private ItemStack internalStack = ItemStack.EMPTY;

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = this.internalStack;
        if (slot == 0 && !stack.isEmpty()) {
            if (!simulate) {
                this.removeStack();
                this.markDirtyAndUpdateClient();
            }
            return simulate ? stack.copy() : stack;
        }
        return ItemStack.EMPTY;
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing != Direction.DOWN)) {
            return opt.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    public ItemStack getStackForRender() {
        return internalStack;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? internalStack : ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    public int getTickForRender() {
        return ticksExistedClient;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }


    public boolean hasStack() {
        return !this.internalStack.isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot == 0) {
            if (this.internalStack.isEmpty()) {
                if (!simulate) {
                    setStack(stack);
                    this.markDirtyAndUpdateClient();
                }
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    public void markDirtyAndUpdateClient() {
        super.markDirty();
        BlockState block = this.world.getBlockState(this.pos);
        world.notifyBlockUpdate(pos, block, block, 3);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void read(CompoundNBT compound) {
        if (compound.contains("item")) {
            this.internalStack = ItemStack.read(compound.getCompound("item"));
        } else {
            this.internalStack = ItemStack.EMPTY;
        }
        this.bloodStored = compound.getInt("blood_stored");
        this.chargingTicks = compound.getInt("charging_ticks");
    }

    @Nonnull
    public ItemStack removeStack() {
        ItemStack stack = this.internalStack;
        this.internalStack = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (chargingTicks > 0) {
                chargingTicks--;
                if (chargingTicks == 0) {
                    IBloodChargeable chargeable = getChargeItem(this.internalStack);
                    if (chargeable != null) {
                        if (this.bloodStored > 0) {
                            int charged = chargeable.charge(this.internalStack, this.bloodStored);
                            this.bloodStored -= Math.max(0, charged);
                        }
                    }
                    this.markDirtyAndUpdateClient();
                }
            } else if (chargingTicks == 0) {
                IBloodChargeable chargeable = getChargeItem(this.internalStack);
                if (chargeable != null && chargeable.canBeCharged(internalStack)) {
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

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (hasStack()) {
            compound.put("item", this.internalStack.serializeNBT());
        }
        compound.putInt("blood_stored", bloodStored);
        compound.putInt("charging_ticks", chargingTicks);
        return super.write(compound);
    }

    private void drainBlood() {
        FluidUtil.getFluidHandler(this.world, this.pos.down(), Direction.UP).ifPresent(handler -> {
            FluidStack drained = handler.drain(new FluidStack(ModFluids.blood, VReference.FOOD_TO_FLUID_BLOOD), false);
            if (drained != null && drained.amount == VReference.FOOD_TO_FLUID_BLOOD) {
                drained = handler.drain(new FluidStack(ModFluids.blood, VReference.FOOD_TO_FLUID_BLOOD), true);
                if (drained != null) {//Just to be safe
                    bloodStored += drained.amount;
                }
            }
        });
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

    /**
     * Set the held stack.
     *
     * @return If successful
     */
    private boolean setStack(@Nonnull ItemStack stack) {
        this.chargingTicks = 0;
        if (this.internalStack.isEmpty()) {
            this.internalStack = stack;
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnChargedParticle() {
        Vec3d pos = new Vec3d(this.getPos()).add(0.5, 0.8, 0.5);
        ModParticles.spawnParticle(getWorld(), new FlyingBloodParticleData(ModParticles.flying_blood, (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), 177), this.pos.getX() + 0.20, this.getPos().getY() + 0.65, this.getPos().getZ() + 0.20, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1);
        ModParticles.spawnParticle(getWorld(), new FlyingBloodParticleData(ModParticles.flying_blood, (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), 177), this.pos.getX() + 0.80, this.getPos().getY() + 0.65, this.getPos().getZ() + 0.20, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1);
        ModParticles.spawnParticle(getWorld(), new FlyingBloodParticleData(ModParticles.flying_blood, (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), 177), this.pos.getX() + 0.20, this.getPos().getY() + 0.65, this.getPos().getZ() + 0.80, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1);
        ModParticles.spawnParticle(getWorld(), new FlyingBloodParticleData(ModParticles.flying_blood, (int) (3.0F / (rand.nextFloat() * 0.6F + 0.4F)), 177), this.pos.getX() + 0.80, this.getPos().getY() + 0.65, this.getPos().getZ() + 0.80, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1);

    }
}
