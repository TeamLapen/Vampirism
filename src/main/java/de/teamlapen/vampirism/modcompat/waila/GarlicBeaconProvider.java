package de.teamlapen.vampirism.modcompat.waila;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blocks.BlockGarlicBeacon;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TileGarlicBeacon;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * 1.10
 *
 * @author maxanier
 */
public class GarlicBeaconProvider implements IWailaDataProvider {
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return tag;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        itemStack.getItem().addInformation(itemStack, accessor.getPlayer(), currenttip, false);
        TileEntity t = accessor.getTileEntity();
        if (t != null && t instanceof TileGarlicBeacon) {
            int fueled = ((TileGarlicBeacon) t).getFuelTime();
            if (fueled > 0) {
                currenttip.add(UtilLib.translateFormatted("Fueled for %s min.", fueled / 20 / 20));
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
        return new ItemStack(ModBlocks.garlicBeacon, 1, accessor.getBlockState().getValue(BlockGarlicBeacon.TYPE).getId());

    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }
}
