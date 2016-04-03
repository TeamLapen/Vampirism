package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.TileFluidHandler;

/**
 * Stores blood and other liquids in a {@link FluidTank}
 * Tank is synced if the block is marked for update
 */
public class TileBloodContainer extends TileFluidHandler {

    public static final int LEVEL_AMOUNT = ItemBloodBottle.AMOUNT * VReference.FOOD_TO_FLUID_BLOOD;
    public static final int CAPACITY = LEVEL_AMOUNT * 14;

    public TileBloodContainer() {
        this.tank = new FluidTank(CAPACITY);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    public FluidTankInfo getTankInfo() {
        return tank.getInfo();
    }

    public boolean isFull() {
        return tank.getCapacity() == tank.getFluidAmount();
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        FluidStack old = tank.getFluid();
        this.readFromNBT(pkt.getNbtCompound());
        if (old != null && !old.isFluidStackIdentical(tank.getFluid()) || old == null && tank.getFluid() != null) {
            this.worldObj.markBlockForUpdate(getPos());
        }
    }

    public void setFluidStack(FluidStack stack) {
        tank.setFluid(stack);
    }
}
