package de.teamlapen.vampirism.api.datamaps;

/**
 * Blood conversion extension for fluids.
 * <br>
 * <br>
 * This interface is used as <a href="https://docs.neoforged.net/docs/datamaps/">neoforge datamap</a> entry for {@link de.teamlapen.vampirism.api.VampirismRegistries#FLUID_BLOOD_CONVERSION_MAP}
 */
public interface IFluidBloodConversion {

    /**
     * @return The conversion rate af a fluid into blood
     */
    float conversionRate();
}
