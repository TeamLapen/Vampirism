package de.teamlapen.lib.common.fluids;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An extension of {@link net.neoforged.neoforge.fluids.capability.templates.FluidTank} with fluid changing handling.
 */
public class ControllableFluidTank extends FluidTank {

    @Nullable
    private Consumer<FluidStack> onFluidChanged;
    private String saveKey = "Fluid";
    private boolean allowInput = true;
    private boolean allowOutput = true;

    public ControllableFluidTank(int capacity) {
        super(capacity);
    }

    public ControllableFluidTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    public ControllableFluidTank setOnFluidChanged(@Nullable Consumer<FluidStack> onFluidChanged) {
        this.onFluidChanged = onFluidChanged;
        return this;
    }

    public ControllableFluidTank setSaveKey(String saveKey) {
        this.saveKey = saveKey;
        return this;
    }

    public ControllableFluidTank setAllowInput(boolean allowInput) {
        this.allowInput = allowInput;
        return this;
    }

    public ControllableFluidTank setAllowOutput(boolean allowOutput) {
        this.allowOutput = allowOutput;
        return this;
    }

    public @Nullable Consumer<FluidStack> getOnFluidChanged() {
        return onFluidChanged;
    }

    public String getSaveKey() {
        return saveKey;
    }

    public boolean isInputAllowed() {
        return allowInput;
    }

    public boolean isOutputAllowed() {
        return allowOutput;
    }

    @Override
    public FluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        fluid = FluidStack.parseOptional(lookupProvider, nbt.getCompound(getSaveKey()));
        return this;
    }

    @Override
    public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        if (!fluid.isEmpty()) {
            nbt.put(getSaveKey(), fluid.save(lookupProvider));
        }

        return nbt;
    }

    public int forceFill(FluidStack resource, FluidAction action) {
        return super.fill(resource, action);
    }

    public FluidStack forceDrain(FluidStack resource, FluidAction action) {
        return super.drain(resource, action);
    }

    public FluidStack forceDrain(int maxDrain, FluidAction action) {
        return super.drain(maxDrain, action);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return isInputAllowed() ? super.fill(resource, action) : 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return isOutputAllowed() ? super.drain(resource, action) : FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return isOutputAllowed() ? super.drain(maxDrain, action) : FluidStack.EMPTY;
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        if (this.onFluidChanged != null) {
            this.onFluidChanged.accept(getFluid());
        }
    }

    @Override
    public void setFluid(FluidStack stack) {
        super.setFluid(stack);
        if (this.onFluidChanged != null) {
            this.onFluidChanged.accept(stack);
        }
    }
}
