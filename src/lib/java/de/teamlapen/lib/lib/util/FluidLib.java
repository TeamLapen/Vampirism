package de.teamlapen.lib.lib.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.*;

/**
 * Fluid related helper methods
 */
public class FluidLib {


    /**
     * Handle the interaction between a player holding a fluid containing item and an {@link IFluidHandler}, when he tries to fill the fluidhandler
     * <p>
     * Only respects tank 0 of the tank
     *
     * @param actor     Player that is holding the container item
     * @param container
     * @param tank
     * @param side
     */
    public static void drainContainerIntoTank(EntityPlayer actor, ItemStack container, IFluidHandler tank, EnumFacing side) {
        FluidTankInfo tankInfo = tank.getTankInfo(side)[0];
        if (tankInfo.fluid != null && tankInfo.fluid.amount == tankInfo.capacity) return;
        if (container.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem containerItem = (IFluidContainerItem) container.getItem();
            FluidStack containerFluid = containerItem.getFluid(container);
            FluidStack tankFluid = tankInfo.fluid;
            if (tankFluid == null || tankFluid.isFluidEqual(containerFluid)) {
                int drainAmount = Math.min(tankInfo.capacity - (tankFluid == null ? 0 : tankFluid.amount), containerFluid.amount);
                FluidStack drained = containerItem.drain(container, drainAmount, true);
                tank.fill(null, drained, true);
            }
        } else {
            FluidStack containerFluid = FluidContainerRegistry.getFluidForFilledItem(container);
            if (tank.fill(null, containerFluid, true) > 0 && !actor.capabilities.isCreativeMode) {
                ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(container);

                if (--container.stackSize <= 0) {
                    actor.inventory.setInventorySlotContents(actor.inventory.currentItem, null);
                }


                if (!actor.inventory.addItemStackToInventory(emptyContainer)) {
                    actor.worldObj.spawnEntityInWorld(new EntityItem(actor.worldObj, actor.posX + 0.5D, actor.posY + 1.5D, actor.posZ + 0.5D, emptyContainer));
                } else if (actor instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) actor).sendContainerToPlayer(actor.inventoryContainer);
                }
            }
        }
    }

    /**
     * Handle the interaction between a player holding a fluid containing item and an {@link IFluidHandler}, when he tries to fill the container item
     * <p>
     * Only respects tank 0 of the tank
     *
     * @param actor     Player that is holding the container item
     * @param container
     * @param tank
     * @param side
     */
    public static void fillContainerFromTank(EntityPlayer actor, ItemStack container, IFluidHandler tank, EnumFacing side) {
        FluidTankInfo tankInfo = tank.getTankInfo(side)[0];
        if (tankInfo.fluid == null) return;
        if (container.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem containerItem = (IFluidContainerItem) container.getItem();
            int filled = containerItem.fill(container, tankInfo.fluid, true);
            tank.drain(null, filled, true);
        } else {
            ItemStack filledStack = FluidContainerRegistry.fillFluidContainer(tankInfo.fluid, container);
            if (filledStack != null) {
                int capacity = FluidContainerRegistry.getContainerCapacity(tankInfo.fluid, container);
                if (capacity > 0) {
                    FluidStack drained = tank.drain(null, capacity, true);
                    if (drained != null && drained.amount == capacity) {
                        if (--container.stackSize <= 0) {
                            actor.inventory.setInventorySlotContents(actor.inventory.currentItem, null);
                        }


                        if (!actor.inventory.addItemStackToInventory(filledStack)) {
                            actor.worldObj.spawnEntityInWorld(new EntityItem(actor.worldObj, actor.posX + 0.5D, actor.posY + 1.5D, actor.posZ + 0.5D, filledStack));
                        } else if (actor instanceof EntityPlayerMP) {
                            ((EntityPlayerMP) actor).sendContainerToPlayer(actor.inventoryContainer);
                        }
                    }
                }
            }
        }
    }
}
