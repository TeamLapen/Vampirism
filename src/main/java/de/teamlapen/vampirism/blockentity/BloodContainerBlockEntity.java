package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.blockentity.NetworkedBlockEntity;
import de.teamlapen.lib.lib.util.ControllableFluidTank;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.fluids.BloodHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class BloodContainerBlockEntity extends NetworkedBlockEntity {

    public static final int CAPACITY = FluidType.BUCKET_VOLUME * 5;

    public static final ModelProperty<FluidStack> FLUID = new ModelProperty<>();

    public final ControllableFluidTank fluidInventory;

    public BloodContainerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BLOOD_CONTAINER.get(), pos, blockState);
        this.fluidInventory = new ControllableFluidTank(CAPACITY, fluid -> fluid.is(ModFluids.BLOOD) || BloodHelper.isConvertibleToBlood(fluid)).setOnFluidChanged(fluid -> setChanged());
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

    public FluidStack getFluid() {
        return fluidInventory.getFluid();
    }

    public void setFluid(FluidStack fluid) {
        fluidInventory.setFluid(fluid);
    }
}
