package de.teamlapen.lib.lib.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public abstract class NotDrainableTank extends FluidTank {
    private boolean drainable = true;

    public NotDrainableTank(int capacity) {
        super(capacity);
    }

    public NotDrainableTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    public void setDrainable(boolean drainable) {
        this.drainable = drainable;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (!drainable) return FluidStack.EMPTY;
        return super.drain(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (!drainable) return FluidStack.EMPTY;
        return super.drain(maxDrain, action);
    }
}
