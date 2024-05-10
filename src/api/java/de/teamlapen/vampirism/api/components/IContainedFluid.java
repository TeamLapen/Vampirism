package de.teamlapen.vampirism.api.components;

import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Used to store fluids in an item.
 */
public interface IContainedFluid {

    /**
     * @return The contained fluid
     */
    FluidStack fluid();
}
