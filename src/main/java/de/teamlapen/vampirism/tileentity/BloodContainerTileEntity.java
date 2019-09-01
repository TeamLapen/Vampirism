package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.FluidTankWithListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.items.BloodBottleIItem;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;

/**
 * Stores blood and other liquids in a {@link FluidTank}
 * Tank is synced if the block is marked for update
 */
public class BloodContainerTileEntity extends net.minecraftforge.fluids.capability.TileFluidHandler implements FluidTankWithListener.IFluidTankListener {

    public static final int LEVEL_AMOUNT = BloodBottleIItem.AMOUNT * VReference.FOOD_TO_FLUID_BLOOD;
    public static final int CAPACITY = LEVEL_AMOUNT * 14;

    private int lastSyncedAmount = Integer.MIN_VALUE;

    public static final ModelProperty<Integer> FLUID_LEVEL_PROP = new ModelProperty<>();
    public static final ModelProperty<Fluid> FLUID_PROP = new ModelProperty<>();
    private IModelData modelData;


    public BloodContainerTileEntity() {
        super(ModTiles.blood_container);
        this.tank = new FluidTankWithListener(CAPACITY).setListener(this);

    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        if (modelData == null) updateModelData(false);
        return modelData;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        FluidStack old = tank.getFluid();
        this.read(pkt.getNbtCompound());
        if (!old.isEmpty() && !old.isFluidStackIdentical(tank.getFluid()) || old.isEmpty() && !tank.getFluid().isEmpty()) {
            updateModelData(true);
            this.world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        this.write(nbtTag);
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    private void updateModelData(boolean refresh) {
        FluidStack fluid = tank.getFluid();
        int l = 0;
        if (!fluid.isEmpty()) {
            float amount = fluid.getAmount() / (float) BloodContainerTileEntity.LEVEL_AMOUNT;
            l = (amount > 0 && amount < 1) ? 1 : (int) amount;
        }
        modelData = new ModelDataMap.Builder().withInitial(FLUID_LEVEL_PROP, l).withInitial(FLUID_PROP, fluid.getFluid()).build();
        if (refresh) ModelDataManager.requestModelDataRefresh(this);
    }

    @Override
    public void onTankContentChanged() {
        FluidStack fluid = tank.getFluid();
        if (fluid.isEmpty() && lastSyncedAmount != Integer.MIN_VALUE || !fluid.isEmpty() && Math.abs(fluid.getAmount() - lastSyncedAmount) >= VReference.FOOD_TO_FLUID_BLOOD) {
            this.markDirty();
            this.lastSyncedAmount = fluid.isEmpty() ? Integer.MIN_VALUE : fluid.getAmount();
        }

    }

    @Override
    public void markDirty() {
        if (world.isRemote)
            updateModelData(true);
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        super.markDirty();
    }

    public void setFluidStack(FluidStack stack) {
        tank.setFluid(stack);
    }

    @Nonnull
    public FluidStack getFluid() {
        return tank.getFluid();
    }
}
