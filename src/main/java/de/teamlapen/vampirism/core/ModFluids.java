package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.fluids.FluidBlood;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ModFluids {
    public static Fluid blood = new FluidBlood();


    static void registerFluids() {
        FluidRegistry.registerFluid(blood);
    }
}
