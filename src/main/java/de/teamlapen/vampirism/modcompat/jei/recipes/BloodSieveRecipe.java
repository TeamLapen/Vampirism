package de.teamlapen.vampirism.modcompat.jei.recipes;

import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import net.neoforged.neoforge.fluids.FluidStack;

public record BloodSieveRecipe(FluidStack input, IFluidBloodConversion itemBlood) {

    public double conversionRate() {
        return this.itemBlood.conversionRate();
    }
}
