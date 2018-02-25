package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.core.ModFluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileSieve extends TileEntity implements ITickable {


    protected FluidTank tank;
    private int cooldownProcess = 0;

    public TileSieve() {
        tank = new FilteringFluidTank(2 * Fluid.BUCKET_VOLUME);
        tank.setCanDrain(false);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        tank.writeToNBT(tag);
        return tag;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return ((facing == null || facing != EnumFacing.DOWN) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if ((facing == null || facing != EnumFacing.DOWN) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) tank;
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        if (--cooldownProcess < 0) {
            cooldownProcess = 10;
            if (tank.getFluidAmount() > 0) {
                IFluidHandler handler = FluidUtil.getFluidHandler(this.getWorld(), this.pos.down(), EnumFacing.UP);
                if (handler != null) {
                    tank.setCanDrain(true);
                    FluidStack transferred = FluidUtil.tryFluidTransfer(handler, tank, 4 * VReference.FOOD_TO_FLUID_BLOOD, true);
                    tank.setCanDrain(false);
                }
            }
        }

    }

    private class FilteringFluidTank extends FluidTank {

        public FilteringFluidTank(int capacity) {
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
            return (int) (filled / factor);
        }
    }
}
