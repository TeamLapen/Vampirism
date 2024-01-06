package de.teamlapen.lib.lib.util;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * normal {@link net.neoforged.neoforge.fluids.capability.templates.FluidTank} with the ability do disable the draining out of this tank and adding a listener for notifications if the content of the tank changes
 */
public class FluidTankWithListener extends FluidTank {

    private boolean drainable = true;
    private IFluidTankListener listener;

    public FluidTankWithListener(int capacity) {
        super(capacity);
    }

    public FluidTankWithListener(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (!drainable) return FluidStack.EMPTY;
        FluidStack stack = super.drain(maxDrain, action);
        listener.onTankContentChanged();
        return stack;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int amount = super.fill(resource, action);
        listener.onTankContentChanged();
        return amount;
    }

    public void setDrainable(boolean drainable) {
        this.drainable = drainable;
    }

    public @NotNull FluidTankWithListener setListener(IFluidTankListener listener) {
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
