package de.teamlapen.lib.lib.util;

import net.minecraftforge.fluids.FluidStack;

import java.util.function.Predicate;

public class FluidTankWithListener extends NotDrainableTank {

    private IFluidTankListener listener;

    public FluidTankWithListener(int capacity) {
        super(capacity);
    }

    public FluidTankWithListener(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
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
