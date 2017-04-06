package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

/**
 * Stores blood and other liquids in a {@link FluidTank}
 * Tank is synced if the block is marked for update
 */
public class TileBloodContainer extends net.minecraftforge.fluids.capability.TileFluidHandler {

    public static final int LEVEL_AMOUNT = ItemBloodBottle.AMOUNT * VReference.FOOD_TO_FLUID_BLOOD;
    public static final int CAPACITY = LEVEL_AMOUNT * 14;

    public TileBloodContainer() {
        this.tank = new FluidTank(CAPACITY);
    }

    public FluidTankInfo getTankInfo() {
        return tank.getInfo();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        FluidStack old = tank.getFluid();
        this.readFromNBT(pkt.getNbtCompound());
        if (old != null && !old.isFluidStackIdentical(tank.getFluid()) || old == null && tank.getFluid() != null) {
            this.world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    public void setFluidStack(FluidStack stack) {
        tank.setFluid(stack);
    }
}
