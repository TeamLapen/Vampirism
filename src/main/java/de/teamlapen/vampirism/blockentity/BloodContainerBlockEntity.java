package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.util.FluidTankWithListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.items.BloodBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * Stores blood and other liquids in a {@link net.neoforged.neoforge.fluids.capability.templates.FluidTank}
 * Tank is synced if the block is marked for update
 */
public class BloodContainerBlockEntity extends BlockEntity implements FluidTankWithListener.IFluidTankListener {

    public static final int LEVEL_AMOUNT = BloodBottleItem.AMOUNT * VReference.FOOD_TO_FLUID_BLOOD;
    public static final int CAPACITY = LEVEL_AMOUNT * 14;
    public static final ModelProperty<Integer> FLUID_LEVEL_PROP = new ModelProperty<>();
    public static final ModelProperty<Boolean> FLUID_IMPURE = new ModelProperty<>();
    private final FluidTankWithListener tank;

    public BloodContainerBlockEntity(@NotNull BlockPos pos, BlockState state) {
        super(ModTiles.BLOOD_CONTAINER.get(), pos, state);
        this.tank = new FluidTankWithListener(CAPACITY, fluidStack -> ModFluids.BLOOD.get().isSame(fluidStack.getFluid()) || ModFluids.IMPURE_BLOOD.get().isSame(fluidStack.getFluid())).setListener(this);

    }

    @NotNull
    public FluidStack getFluid() {
        return tank.getFluid();
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        FluidStack fluid = tank.getFluid();
        int l = 0;
        if (!fluid.isEmpty()) {
            float amount = fluid.getAmount() / (float) BloodContainerBlockEntity.LEVEL_AMOUNT;
            l = (amount > 0 && amount < 1) ? 1 : (int) amount;
        }
        return ModelData.builder().with(FLUID_LEVEL_PROP, l).with(FLUID_IMPURE, fluid.getFluid().equals(ModFluids.IMPURE_BLOOD.get())).build();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.Provider provider) {
        super.saveAdditional(pTag, provider);
        CompoundTag tag = new CompoundTag();
        tank.writeToNBT(provider, tag);
        pTag.put("tank", tag);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.Provider provider) {
        super.loadAdditional(pTag, provider);
        tank.readFromNBT(provider, pTag.getCompound("tank"));
        setChanged();
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = this.saveWithoutMetadata(provider);
        tank.writeToNBT(provider, tag);
        return tag;
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        super.onDataPacket(net, pkt, provider);
        int oldAmount = tank.getFluid().getAmount();
        Fluid fluid = tank.getFluid().getFluid();
        tank.readFromNBT(provider, pkt.getTag());
        if (oldAmount != tank.getFluid().getAmount() || fluid != tank.getFluid().getFluid()) {
            setChanged();
        }
    }

    @NotNull
    public FluidTankWithListener getTank() {
        return this.tank;
    }

    @Override
    public void onTankContentChanged() {
        this.setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void setChanged() {
        if (level != null && level.isClientSide) {
            this.requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
        super.setChanged();
    }

    public void setFluidStack(FluidStack stack) {
        this.tank.setFluid(stack);
    }
}
