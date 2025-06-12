package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.util.ControllableFluidTank;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

/**
 * Stores blood and other liquids in a {@link net.neoforged.neoforge.fluids.capability.templates.FluidTank}
 */
@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BloodContainerBlockEntity extends NetworkedBlockEntity {

    public static final int CAPACITY = FluidType.BUCKET_VOLUME * 5;

    public static final ModelProperty<FluidStack> FLUID = new ModelProperty<>();

    public ControllableFluidTank fluidInventory;

    public BloodContainerBlockEntity(@NotNull BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOD_CONTAINER.get(), pos, state);
        this.fluidInventory = new ControllableFluidTank(CAPACITY, fluid -> fluid.is(ModFluids.BLOOD)).setOnFluidChanged(fluid -> requestModelDataUpdate());
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BLOOD_CONTAINER.get(), (blockEntity, side) -> blockEntity.fluidInventory);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fluidInventory.readFromNBT(registries, tag);
        // Added for older words that were created with Vampirism 1.10 and lower
        if (tag.contains("tank")) {
            fluidInventory.readFromNBT(registries, tag.getCompound("tank"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        fluidInventory.writeToNBT(registries, tag);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(FLUID, fluidInventory.getFluid())
                .build();
    }

    @Override
    public void loadMetaData(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        fluidInventory.readFromNBT(lookupProvider, tag);
    }

    @Override
    public void saveMetaData(CompoundTag tag, HolderLookup.Provider registries) {
        fluidInventory.writeToNBT(registries, tag);
    }
}
