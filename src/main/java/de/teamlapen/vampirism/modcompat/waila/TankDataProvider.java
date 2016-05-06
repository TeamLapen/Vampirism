package de.teamlapen.vampirism.modcompat.waila;

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
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

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
        if (accessor.getBlock() instanceof ITileEntityProvider && accessor.getTileEntity() instanceof IFluidHandler) {
            IFluidHandler handler = (IFluidHandler) accessor.getTileEntity();
            FluidTankInfo[] infos = handler.getTankInfo(accessor.getSide());
            for (FluidTankInfo info : infos) {
                currenttip.add(String.format("%s%s: %d/%d mB", SpecialChars.RED, I18n.translateToLocal("text.vampirism.blood"), info.fluid == null ? 0 : info.fluid.amount, info.capacity));
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
