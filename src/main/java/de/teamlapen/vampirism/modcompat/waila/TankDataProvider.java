package de.teamlapen.vampirism.modcompat.waila;

import de.teamlapen.vampirism.api.VReference;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.List;

/**
 * Provides information about the fluid level in blood containers
 */
class TankDataProvider implements IWailaDataProvider {
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock() instanceof ITileEntityProvider && accessor.getTileEntity().hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, accessor.getSide())) {
            net.minecraftforge.fluids.capability.IFluidHandler fluidHandler = accessor.getTileEntity().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, accessor.getSide());
            for (IFluidTankProperties info : fluidHandler.getTankProperties()) {
                FluidStack c = info.getContents();
                if (c != null) {
                    currenttip.add(String.format("%s%s: %d/%d", SpecialChars.RED, c.getLocalizedName(), c.amount / VReference.FOOD_TO_FLUID_BLOOD, info.getCapacity() / VReference.FOOD_TO_FLUID_BLOOD));
                }
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }
}
