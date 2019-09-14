package de.teamlapen.lib.lib.util;

import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
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

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int amount = super.fill(resource, action);
        listener.onTankContentChanged();
        return amount;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack stack = super.drain(maxDrain, action);
        listener.onTankContentChanged();
        return stack;
    }

    public interface IFluidTankListener {
        void onTankContentChanged();
    }
}
