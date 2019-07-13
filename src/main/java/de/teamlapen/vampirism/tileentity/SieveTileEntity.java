package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.FluidTankWithListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.blocks.SieveBlock;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SieveTileEntity extends TileEntity implements ITickable, FluidTankWithListener.IFluidTankListener {


    private FluidTank tank;
    private int cooldownPull = 0;
    private int cooldownProcess = 0;
    private boolean active;

    private final LazyOptional<IFluidHandler> cap;

    public SieveTileEntity() {
        super(ModTiles.sieve);
        tank = new FilteringFluidTank(2 * Fluid.BUCKET_VOLUME).setListener(this);
        tank.setCanDrain(false);
        cap = LazyOptional.of(() -> tank);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if ((facing != Direction.DOWN) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return cap.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("active", isActive());
        return nbt;
    }


    public boolean isActive() {
        return active;
    }

    private void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            this.world.setBlockState(getPos(), world.getBlockState(pos).with(SieveBlock.PROPERTY_ACTIVE, active));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        boolean old = active;
        active = pkt.getNbtCompound().getBoolean("active");
        if (active != old)
            this.world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);

    }

    @Override
    public void onTankContentChanged() {
        this.setActive(true);

    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        tank.readFromNBT(tag);
        cooldownProcess = tag.getInt("cooldown_process");
        cooldownPull = tag.getInt("cooldown_pull");
    }

    @Override
    public void tick() {
        //Process content
        if (--cooldownProcess < 0) {
            cooldownProcess = 15;
            if (tank.getFluidAmount() > 0) {
                FluidUtil.getFluidHandler(this.getWorld(), this.pos.down(), Direction.UP).ifPresent(handler -> {
                    tank.setCanDrain(true);
                    FluidStack transferred = FluidUtil.tryFluidTransfer(handler, tank, 2 * VReference.FOOD_TO_FLUID_BLOOD, true);
                    tank.setCanDrain(false);
                    if (transferred != null && transferred.amount > 0) {
                        cooldownProcess = 30;
                        setActive(true);
                    }
                });
            } else if (active) {
                setActive(false);
            }
        }
        //Pull new content. Cooldown is increased when liquid is filled into the tank (regardless of way)
        if (--cooldownPull < 0) {
            cooldownPull = 10;
            FluidUtil.getFluidHandler(this.getWorld(), this.pos.up(), Direction.DOWN).ifPresent(handler -> {
                FluidStack transferred = FluidUtil.tryFluidTransfer(tank, handler, 2 * VReference.FOOD_TO_FLUID_BLOOD, true);
            });
        }

    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);
        tank.writeToNBT(tag);
        cooldownProcess = tag.getInt("cooldown_process");
        cooldownPull = tag.getInt("cooldown_pull");
        return tag;
    }

    private class FilteringFluidTank extends FluidTankWithListener {

        private FilteringFluidTank(int capacity) {
            super(capacity);
        }

        @Override
        public int fillInternal(FluidStack resource, boolean doFill) {
            float factor = BloodConversionRegistry.getFluidBloodConversionFactor(resource.getFluid().getName());
            if (factor == 0f) {
                return 0;
            }
            FluidStack converted = new FluidStack(ModFluids.blood, (int) (factor * resource.amount));
            int filled = super.fillInternal(converted, doFill);
            if (doFill) SieveTileEntity.this.cooldownPull = 10;
            return (int) (filled / factor);
        }
    }
}
