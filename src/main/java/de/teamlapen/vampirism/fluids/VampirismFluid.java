package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.fluid.Fluid;

public abstract class VampirismFluid extends Fluid {
    protected final String fluidname;

    public VampirismFluid(String name) {
        this.setRegistryName(REFERENCE.MODID, name);
        this.fluidname = name;
    }
}
