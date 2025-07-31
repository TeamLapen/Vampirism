package de.teamlapen.lib.lib.util;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * normal {@link net.neoforged.neoforge.fluids.capability.templates.FluidTank} with the ability do disable the draining out of this tank and adding a listener for notifications if the content of the tank changes
 */
public class FluidTankWithListener extends FluidTank {

    private boolean drainable = true;
    @Nullable
    private IFluidTankListener listener;

    public FluidTankWithListener(int capacity) {
        super(capacity);
    }

    public FluidTankWithListener(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        if (!this.drainable) {
            return FluidStack.EMPTY;
        }
        return super.drain(maxDrain, action);
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
        if (this.listener != null) {
            this.listener.onTankContentChanged();
        }
    }

    public interface IFluidTankListener {
        void onTankContentChanged();
    }
}
