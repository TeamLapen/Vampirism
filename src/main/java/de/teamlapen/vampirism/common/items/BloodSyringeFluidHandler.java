package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

public class BloodSyringeFluidHandler implements IFluidHandlerItem {

    public static final int LEVELS_PER_FILL = 1;
    public static final int AMOUNT = 50;

    protected ItemStack container;

    public BloodSyringeFluidHandler(ItemStack container) {
        this.container = container;
    }

    private boolean isEmptySyringe(ItemStack stack) {
        return stack.is(ModItems.SYRINGE_EMPTY.get());
    }

    private boolean isFullSyringe(ItemStack stack) {
        return stack.is(ModItems.SYRINGE_BLOOD.get());
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        if (!resource.getFluid().isSame(ModFluids.BLOOD.get())) return FluidStack.EMPTY;

        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (!isFullSyringe(container)) return FluidStack.EMPTY;
        if (maxDrain < AMOUNT) return FluidStack.EMPTY;

        if (action.execute()) {
            container = new ItemStack(ModItems.SYRINGE_EMPTY.get());
        }

        return new FluidStack(ModFluids.BLOOD.get(), AMOUNT);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        if (!resource.getFluid().isSame(ModFluids.BLOOD.get())) return 0;

        if (!isEmptySyringe(container) || resource.getAmount() < AMOUNT) {
            return 0;
        }

        if (action.execute()) {
            container = new ItemStack(ModItems.SYRINGE_BLOOD.get());
        }

        return AMOUNT;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (isFullSyringe(container)) {
            return new FluidStack(ModFluids.BLOOD.get(), AMOUNT);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return AMOUNT;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return stack.getFluid().isSame(ModFluids.BLOOD.get());
    }
}

