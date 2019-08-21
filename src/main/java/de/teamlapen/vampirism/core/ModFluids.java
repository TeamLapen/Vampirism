package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.fluids.FluidBlood;
import net.minecraftforge.fluids.Fluid;

public class ModFluids {
    public static Fluid blood = new FluidBlood(VReference.FLUID_BLOOD_NAME);
    public static Fluid impure_blood = new FluidBlood(VReference.FLUID_IMPURE_BLOOD_NAME);


    static void registerFluids() {
        //FluidRegistry.registerFluid(blood); TODO 1.14
        //FluidRegistry.registerFluid(impure_blood); TODO 1.14
    }
}
