package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.FluidTankWithListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.items.BloodBottleItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Stores blood and other liquids in a {@link FluidTank}
 * Tank is synced if the block is marked for update
 */
public class BloodContainerTileEntity extends net.minecraftforge.fluids.capability.TileFluidHandler implements FluidTankWithListener.IFluidTankListener {

    public static final int LEVEL_AMOUNT = BloodBottleItem.AMOUNT * VReference.FOOD_TO_FLUID_BLOOD;
    public static final int CAPACITY = LEVEL_AMOUNT * 14;
    public static final ModelProperty<Integer> FLUID_LEVEL_PROP = new ModelProperty<>();
    public static final ModelProperty<Boolean> FLUID_IMPURE = new ModelProperty<>();

    private int lastSyncedAmount = Integer.MIN_VALUE;
    private IModelData modelData;


    public BloodContainerTileEntity() {
        super(ModTiles.blood_container);
        this.tank = new FluidTankWithListener(CAPACITY, fluidStack -> ModFluids.blood.isEquivalentTo(fluidStack.getFluid()) || ModFluids.impure_blood.isEquivalentTo(fluidStack.getFluid())).setListener(this);

    }

    @Nonnull
    public FluidStack getFluid() {
        return tank.getFluid();
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        if (modelData == null) updateModelData(false);
        return modelData;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        this.write(nbtTag);
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void markDirty() {
        if (world != null) {
            if (world.isRemote)
                updateModelData(true);
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            super.markDirty();
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        FluidStack old = tank.getFluid();
        if (hasWorld()) {
            this.read(this.world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
            if (!old.isEmpty() && !old.isFluidStackIdentical(tank.getFluid()) || old.isEmpty() && !tank.getFluid().isEmpty()) {
                markDirty();
            }
        }
    }

    @Override
    public void onTankContentChanged() {
        FluidStack fluid = tank.getFluid();
        if (lastSyncedAmount != Integer.MIN_VALUE || !fluid.isEmpty() && Math.abs(fluid.getAmount() - lastSyncedAmount) >= VReference.FOOD_TO_FLUID_BLOOD) {
            this.markDirty();
            this.lastSyncedAmount = fluid.isEmpty() ? Integer.MIN_VALUE : fluid.getAmount();
        }

    }

    public void setFluidStack(FluidStack stack) {
        tank.setFluid(stack);
    }

    private void updateModelData(boolean refresh) {
        FluidStack fluid = tank.getFluid();
        int l = 0;
        if (!fluid.isEmpty()) {
            float amount = fluid.getAmount() / (float) BloodContainerTileEntity.LEVEL_AMOUNT;
            l = (amount > 0 && amount < 1) ? 1 : (int) amount;
        }
        modelData = new ModelDataMap.Builder().withInitial(FLUID_LEVEL_PROP, l).withInitial(FLUID_IMPURE, fluid.getFluid().equals(ModFluids.impure_blood)).build();
        if (refresh) ModelDataManager.requestModelDataRefresh(this);
    }

    public static void setBloodValue(IBlockReader worldIn, Random randomIn, BlockPos blockPosIn) {
        TileEntity tileEntity = worldIn.getTileEntity(blockPosIn);
        if (tileEntity instanceof BloodContainerTileEntity) {
            ((BloodContainerTileEntity) tileEntity).setFluidStack(new FluidStack(ModFluids.blood, BloodBottleFluidHandler.getAdjustedAmount((int) (CAPACITY * randomIn.nextFloat()))));
        }
    }
}
