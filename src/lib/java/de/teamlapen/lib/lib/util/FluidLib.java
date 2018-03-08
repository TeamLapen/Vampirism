package de.teamlapen.lib.lib.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Fluid related helper methods
 */
public class FluidLib {


    /**
     * Checks if either both stacks are null or if they are equal. DOES NOT CHECK AMOUNTS
     *
     * @param stackA
     * @param stackB
     * @return
     */
    public static boolean areFluidStacksEqual(@Nullable FluidStack stackA, @Nullable FluidStack stackB) {
        return stackA == null && stackB == null || ((stackA != null && stackB != null) && stackA.isFluidEqual(stackB));
    }

    /**
     * Checks if either both stacks are null or if they are identical. DOES  CHECK AMOUNTS
     *
     * @param stackA
     * @param stackB
     * @return
     */
    public static boolean areFluidStacksIdentical(@Nullable FluidStack stackA, @Nullable FluidStack stackB) {
        return stackA == null && stackB == null || ((stackA != null && stackB != null) && stackA.isFluidStackIdentical(stackB));
    }

    public static boolean hasFluidItemCap(@Nonnull ItemStack stack) {
        return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
    }

    public static IFluidHandler getFluidItemCap(@Nonnull ItemStack stack) {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
    }

    public static int getFluidAmount(@Nonnull IFluidHandler handler, @Nullable Fluid f) {
        FluidStack s = f == null ? handler.drain(Integer.MAX_VALUE, false) : handler.drain(new FluidStack(f, Integer.MAX_VALUE), false);
        if (s != null) return s.amount;
        return 0;
    }



}
