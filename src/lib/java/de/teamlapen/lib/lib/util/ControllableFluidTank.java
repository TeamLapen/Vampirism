package de.teamlapen.lib.lib.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An extension of {@link net.neoforged.neoforge.fluids.capability.templates.FluidTank} with fluid changing handling.
 */
public class ControllableFluidTank extends FluidTank {

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

    public ControllableFluidTank setOnFluidChanged(Consumer<FluidStack> onFluidChanged) {
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

    public Consumer<FluidStack> getOnFluidChanged() {
        return onFluidChanged;
    }

    public boolean isInputAllowed() {
        return allowInput;
    }

    public boolean isOutputAllowed() {
        return allowOutput;
    }

    public String getSaveKey() {
        return saveKey;
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

    public int doFill(FluidStack resource, FluidAction action) {
        return super.fill(resource, action);
    }

    public FluidStack doDrain(FluidStack resource, FluidAction action) {
        return super.drain(resource, action);
    }

    public FluidStack doDrain(int maxDrain, FluidAction action) {
        return super.drain(maxDrain, action);
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        getOnFluidChanged().accept(getFluid());
    }

    @Override
    public void setFluid(FluidStack stack) {
        super.setFluid(stack);
        getOnFluidChanged().accept(stack);
    }
}
