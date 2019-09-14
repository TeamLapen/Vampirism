package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.fluid.Fluid;

public abstract class VampirismFluid extends Fluid {
    private final String fluidname;

    VampirismFluid(String name) {
        this.setRegistryName(REFERENCE.MODID, name);
        this.fluidname = name;
    }
}
