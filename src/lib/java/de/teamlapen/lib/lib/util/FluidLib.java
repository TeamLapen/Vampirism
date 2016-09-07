package de.teamlapen.lib.lib.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Fluid related helper methods
 */
public class FluidLib {


    /**
     * Moves as much fluid as possible from the container into the tank
     *
     */
    public static void drainContainerIntoTank(IFluidHandler container, IFluidHandler tank) {
        FluidStack fluidStack = container.drain(Integer.MAX_VALUE, false);
        int filled = tank.fill(fluidStack, true);
        container.drain(filled, true);
    }

}
