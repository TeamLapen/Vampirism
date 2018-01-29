package de.teamlapen.lib.lib.util;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;

public class FluidTankWithListener extends FluidTank {

    private IFluidTankListener listener;

    public FluidTankWithListener(int capacity) {
        super(capacity);
    }

    public FluidTankWithListener(@Nullable FluidStack fluidStack, int capacity) {
        super(fluidStack, capacity);
    }

    public FluidTankWithListener(Fluid fluid, int amount, int capacity) {
        super(fluid, amount, capacity);
    }

    /**
     * @return This
     */
    public FluidTankWithListener setListener(IFluidTankListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected void onContentsChanged() {
        if (listener != null) listener.onTankContentChanged();
    }

    public interface IFluidTankListener {
        void onTankContentChanged();
    }
}
