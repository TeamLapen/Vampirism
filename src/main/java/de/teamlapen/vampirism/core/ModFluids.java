package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.fluids.FluidBlood;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ModFluids {//TODO Fluids
    public static Fluid blood = new FluidBlood(VReference.FLUID_BLOOD_NAME);
    public static Fluid impure_blood = new FluidBlood(VReference.FLUID_IMPURE_BLOOD_NAME);


    static void registerFluids() {
        FluidRegistry.registerFluid(blood);
        FluidRegistry.registerFluid(impure_blood);
        BloodConversionRegistry.registerFluidConversionRatio(VReference.FLUID_IMPURE_BLOOD_NAME, VReference.BLOOD_IMPURE_TO_PURE);
    }
}
