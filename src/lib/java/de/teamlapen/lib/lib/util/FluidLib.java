package de.teamlapen.lib.lib.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

/**
 * Fluid related helper methods
 */
public class FluidLib {


    /**
     * Moves as much fluid as possible from the container into the tank
     */
    public static void drainContainerIntoTank(IFluidHandler container, IFluidHandler tank) {
        FluidStack fluidStack = container.drain(Integer.MAX_VALUE, false);
        int filled = tank.fill(fluidStack, true);
        container.drain(filled, true);
    }

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

}
