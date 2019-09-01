package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.fluids.BloodFluid;
import de.teamlapen.vampirism.fluids.ImpureBloodFluid;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(REFERENCE.MODID)
public class ModFluids {
    public static Fluid blood = new BloodFluid();
    public static Fluid impure_blood = new ImpureBloodFluid();

    static void registerFluids(IForgeRegistry<Fluid> registry) {
        registry.register(blood);
        registry.register(impure_blood);
    }
}
