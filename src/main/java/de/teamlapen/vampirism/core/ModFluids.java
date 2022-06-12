package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.fluids.BloodFluid;
import de.teamlapen.vampirism.fluids.ImpureBloodFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, REFERENCE.MODID);

    public static final RegistryObject<Fluid> BLOOD = FLUIDS.register("blood", () -> {
        Fluid blood = new BloodFluid();
        VReference.blood_fluid = blood;
        return blood;
    });
    public static final RegistryObject<Fluid> IMPURE_BLOOD = FLUIDS.register("impure_blood", ImpureBloodFluid::new);

    static void registerFluids(IEventBus bus) {
        FLUIDS.register(bus);
        VReference.blood_fluid_supplier = BLOOD;
    }
}
