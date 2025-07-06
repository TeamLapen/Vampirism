package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.fluids.BloodFluid;
import de.teamlapen.vampirism.fluids.ImpureBloodFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, REFERENCE.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, REFERENCE.MODID);

    public static final DeferredHolder<FluidType, FluidType> BLOOD_TYPE = FLUID_TYPES.register("blood", () -> new FluidType(FluidType.Properties.create()
            .descriptionId(ModList.get().isLoaded(REFERENCE.INTEGRATIONS_MODID) ? "fluid.vampirism.blood.vampirism" : "fluid.vampirism.blood")
            .motionScale(0.01D)
            .fallDistanceModifier(0.1F)
            .canHydrate(true)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
            .density(1400)
            .viscosity(1400)
            .temperature(340)
    ));
    public static final DeferredHolder<FluidType, FluidType> IMPURE_BLOOD_TYPE = FLUID_TYPES.register("impure_blood", () -> new FluidType(FluidType.Properties.create()
            .viscosity(3000)
            .temperature(309)
            .density(1300)
            .descriptionId(ModList.get().isLoaded(REFERENCE.INTEGRATIONS_MODID) ? "fluid.vampirism.blood.vampirism" : "fluid.vampirism.blood"))
    );

    public static final DeferredHolder<Fluid, BaseFlowingFluid> BLOOD = FLUIDS.register("blood", () -> new BloodFluid.Source(BloodFluid.PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid> FLOWING_BLOOD = FLUIDS.register("flowing_blood", () -> new BloodFluid.Flowing(BloodFluid.PROPERTIES));
    public static final DeferredHolder<Fluid, ImpureBloodFluid> IMPURE_BLOOD = FLUIDS.register("impure_blood", ImpureBloodFluid::new);

    public static void registerFluidInteractions() {
        // Lava + Blood = Netherrack (both Source and Flowing Lava, add some other block as a source variant in the future if there is an option)
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                BLOOD_TYPE.get(),
                Blocks.NETHERRACK.defaultBlockState()
        ));
        // Blood + Blue Ice = Cobbled Dark Stone
        FluidInteractionRegistry.addInteraction(BLOOD_TYPE.value(), new FluidInteractionRegistry.InteractionInformation(
                (level, currentPos, relativePos, currentState) -> level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                ModBlocks.COBBLED_DARK_STONE.get().defaultBlockState()
        ));
    }

    static void register(IEventBus bus) {
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
    }
}
