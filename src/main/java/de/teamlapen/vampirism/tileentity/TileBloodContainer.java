package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.FluidTankWithListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import net.minecraft.block.BlockState;
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
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

import javax.annotation.Nonnull;

/**
 * Stores blood and other liquids in a {@link FluidTank}
 * Tank is synced if the block is marked for update
 */
public class TileBloodContainer extends net.minecraftforge.fluids.capability.TileFluidHandler implements FluidTankWithListener.IFluidTankListener {

    public static final int LEVEL_AMOUNT = ItemBloodBottle.AMOUNT * VReference.FOOD_TO_FLUID_BLOOD;
    public static final int CAPACITY = LEVEL_AMOUNT * 14;

    private int lastSyncedAmount = Integer.MIN_VALUE;

    public static final ModelProperty<Integer> FLUID_LEVEL_PROP = new ModelProperty<>();
    public static final ModelProperty<String> FLUID_NAME_PROP = new ModelProperty<>();


    public TileBloodContainer() {
        super(ModTiles.blood_container);
        this.tank = new FluidTankWithListener(CAPACITY).setListener(this);

    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        FluidStack fluid = getTankInfo().fluid;
        String n = "";
        int l = 0;
        if (fluid != null) {
            float amount = fluid == null ? 0 : fluid.amount / (float) TileBloodContainer.LEVEL_AMOUNT;
            l = (amount > 0 && amount < 1) ? 1 : (int) amount;
            n = fluid.getFluid().getName();
        }
        return new ModelDataMap.Builder().withInitial(FLUID_LEVEL_PROP, l).withInitial(FLUID_NAME_PROP, n).build();
    }

    public FluidTankInfo getTankInfo() {
        return tank.getInfo();
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        FluidStack old = tank.getFluid();
        this.read(pkt.getNbtCompound());
        if (old != null && !old.isFluidStackIdentical(tank.getFluid()) || old == null && tank.getFluid() != null) {
            this.world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
            ModelDataManager.requestModelDataRefresh(this);

        }
    }

    @Override
    public void onTankContentChanged() {
        FluidStack fluid = getTankInfo().fluid;
        if (fluid == null && lastSyncedAmount != Integer.MIN_VALUE || fluid != null && Math.abs(fluid.amount - lastSyncedAmount) >= VReference.FOOD_TO_FLUID_BLOOD) {
            BlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(pos, state, state, 3);
            this.markDirty();
            this.lastSyncedAmount = fluid == null ? Integer.MIN_VALUE : fluid.amount;
        }

    }

    public void setFluidStack(FluidStack stack) {
        tank.setFluid(stack);
    }
}
