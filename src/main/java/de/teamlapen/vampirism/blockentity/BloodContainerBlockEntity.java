package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.util.FluidTankWithListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.items.BloodBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Stores blood and other liquids in a {@link FluidTank}
 * Tank is synced if the block is marked for update
 */
public class BloodContainerBlockEntity extends net.minecraftforge.fluids.capability.FluidHandlerBlockEntity implements FluidTankWithListener.IFluidTankListener {

    public static final int LEVEL_AMOUNT = BloodBottleItem.AMOUNT * VReference.FOOD_TO_FLUID_BLOOD;
    public static final int CAPACITY = LEVEL_AMOUNT * 14;
    public static final ModelProperty<Integer> FLUID_LEVEL_PROP = new ModelProperty<>();
    public static final ModelProperty<Boolean> FLUID_IMPURE = new ModelProperty<>();

    public static void setBloodValue(@NotNull BlockGetter worldIn, @NotNull Random randomIn, @NotNull BlockPos blockPosIn) {
        BlockEntity blockEntity = worldIn.getBlockEntity(blockPosIn);
        if (blockEntity instanceof BloodContainerBlockEntity bloodContainer) {
            bloodContainer.setFluidStack(new FluidStack(ModFluids.BLOOD.get(), BloodBottleFluidHandler.getAdjustedAmount((int) (CAPACITY * randomIn.nextFloat()))));
        }
    }

    private int lastSyncedAmount = Integer.MIN_VALUE;
    private ModelData modelData;

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
        if (modelData == null) updateModelData(false);
        return modelData;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbtTag = new CompoundTag();
        this.saveAdditional(nbtTag);
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = this.saveWithoutMetadata();
        tank.writeToNBT(tag);
        return tag;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        FluidStack old = tank.getFluid();
        if (hasLevel()) {
            super.onDataPacket(net, pkt);
            if (!old.isEmpty() && !old.isFluidStackIdentical(tank.getFluid()) || old.isEmpty() && !tank.getFluid().isEmpty()) {
                setChanged();
            }
        }
    }

    @Override
    public void onTankContentChanged() {
        FluidStack fluid = tank.getFluid();
        if (lastSyncedAmount != Integer.MIN_VALUE || !fluid.isEmpty() && Math.abs(fluid.getAmount() - lastSyncedAmount) >= VReference.FOOD_TO_FLUID_BLOOD) {
            this.setChanged();
            this.lastSyncedAmount = fluid.isEmpty() ? Integer.MIN_VALUE : fluid.getAmount();
        }

    }

    @Override
    public void setChanged() {
        if (level != null) {
            if (level.isClientSide) {
                updateModelData(true);
            }
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            super.setChanged();
        }

    }

    public void setFluidStack(FluidStack stack) {
        tank.setFluid(stack);
    }

    private void updateModelData(boolean refresh) {
        FluidStack fluid = tank.getFluid();
        int l = 0;
        if (!fluid.isEmpty()) {
            float amount = fluid.getAmount() / (float) BloodContainerBlockEntity.LEVEL_AMOUNT;
            l = (amount > 0 && amount < 1) ? 1 : (int) amount;
        }
        this.modelData = ModelData.builder().with(FLUID_LEVEL_PROP, l).with(FLUID_IMPURE, fluid.getFluid().equals(ModFluids.IMPURE_BLOOD.get())).build();
        if (refresh) {
            this.requestModelDataUpdate();
        }
    }
}
