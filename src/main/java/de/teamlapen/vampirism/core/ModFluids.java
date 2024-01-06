package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.extensions.FluidExtensions;
import de.teamlapen.vampirism.fluids.BloodFluid;
import de.teamlapen.vampirism.fluids.ImpureBloodFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, REFERENCE.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, REFERENCE.MODID);

    public static final DeferredHolder<FluidType, FluidType> BLOOD_TYPE = FLUID_TYPES.register("blood", () -> new FluidType(FluidType.Properties.create()
            .rarity(Rarity.UNCOMMON)
            .viscosity(3000)
            .temperature(309)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
            .density(1300)
            .descriptionId(ModList.get().isLoaded(REFERENCE.INTEGRATIONS_MODID) ? "fluid.vampirism.blood.vampirism" : "fluid.vampirism.blood")) {

        @Override
        public void initializeClient(@NotNull Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(FluidExtensions.BLOOD);
        }
    });

    public static final DeferredHolder<FluidType, FluidType> IMPURE_BLOOD_TYPE = FLUID_TYPES.register("impure_blood", () -> new FluidType(FluidType.Properties.create()
            .rarity(Rarity.UNCOMMON)
            .viscosity(3000)
            .temperature(309)
            .density(1300)
            .descriptionId(ModList.get().isLoaded(REFERENCE.INTEGRATIONS_MODID) ? "fluid.vampirism.blood.vampirism" : "fluid.vampirism.blood")) {

        @Override
        public void initializeClient(@NotNull Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(FluidExtensions.IMPURE_BLOOD);
        }
    });
    public static final DeferredHolder<Fluid, BloodFluid> BLOOD = FLUIDS.register("blood", BloodFluid::new);
    public static final DeferredHolder<Fluid, ImpureBloodFluid> IMPURE_BLOOD = FLUIDS.register("impure_blood", ImpureBloodFluid::new);

    static void register(IEventBus bus) {
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
    }
}
