package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.fluids.BloodFluid;
import de.teamlapen.vampirism.fluids.ImpureBloodFluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, REFERENCE.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, REFERENCE.MODID);

    public static final RegistryObject<FluidType> BLOOD_TYPE = FLUID_TYPES.register("blood", () -> new FluidType(FluidType.Properties.create()
            .rarity(Rarity.UNCOMMON)
            .viscosity(3000)
            .temperature(309)
            .density(1300)
            .descriptionId(ModList.get().isLoaded(REFERENCE.INTEGRATIONS_MODID) ? "fluid.vampirism.blood.vampirism" : "fluid.vampirism.blood")) {

        @Override
        public void initializeClient(Consumer<IFluidTypeRenderProperties> consumer) {
            consumer.accept(new IFluidTypeRenderProperties() {

                @Override
                public ResourceLocation getStillTexture() {
                    return new ResourceLocation(REFERENCE.MODID, "block/blood_still");
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return new ResourceLocation(REFERENCE.MODID, "block/blood_flow");
                }

                @Override
                public int getColorTint() {
                    return 0xEEFF1111;
                }
            });
        }
    });

    public static final RegistryObject<FluidType> IMPURE_BLOOD_TYPE = FLUID_TYPES.register("impure_blood", () -> new FluidType(FluidType.Properties.create()
            .rarity(Rarity.UNCOMMON)
            .viscosity(3000)
            .temperature(309)
            .density(1300)
            .descriptionId(ModList.get().isLoaded(REFERENCE.INTEGRATIONS_MODID) ? "fluid.vampirism.blood.vampirism" : "fluid.vampirism.blood")) {

        @Override
        public void initializeClient(Consumer<IFluidTypeRenderProperties> consumer) {
            consumer.accept(new IFluidTypeRenderProperties() {

                @Override
                public ResourceLocation getStillTexture() {
                    return new ResourceLocation(REFERENCE.MODID, "block/impure_blood_still");
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return new ResourceLocation(REFERENCE.MODID, "block/impure_blood_flow");
                }

                @Override
                public int getColorTint() {
                    return 0xEEFF1111;
                }
            });
        }
    });
    public static final RegistryObject<Fluid> BLOOD = FLUIDS.register("blood", BloodFluid::new);
    public static final RegistryObject<Fluid> IMPURE_BLOOD = FLUIDS.register("impure_blood", ImpureBloodFluid::new);

    static void registerFluids(IEventBus bus) {
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
    }
}
