//package de.teamlapen.vampirism.modcompat.waila;
//
//import de.teamlapen.vampirism.blocks.BlockGarlicBeacon;
//import de.teamlapen.vampirism.blocks.BlockWeaponTable;
//import de.teamlapen.vampirism.tileentity.TileAltarInspiration;
//import de.teamlapen.vampirism.tileentity.TileBloodContainer;
//import de.teamlapen.vampirism.util.REFERENCE;
//import mcp.mobius.waila.api.IWailaConfigHandler;
//import mcp.mobius.waila.api.IWailaDataAccessor;
//import mcp.mobius.waila.api.IWailaDataProvider;
//import mcp.mobius.waila.api.IWailaRegistrar;
//import net.minecraft.entity.EntityCreature;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//import java.util.List;
//
///**
// * Mod compatibility with Waila (What am I looking at)
// */
//public class WailaHandler {
//    public static void onRegister(IWailaRegistrar registrar) {
//        registrar.addConfig(REFERENCE.MODID, getShowCreatureInfoConf(), true);
//        registrar.addConfig(REFERENCE.MODID, getShowPlayerInfoConf(), true);
//
//        registrar.registerBodyProvider(new CreatureDataProvider(), EntityCreature.class);
//        registrar.registerBodyProvider(new PlayerDataProvider(), EntityPlayer.class);
//        IWailaDataProvider tankDataProvider = new TankDataProvider();
//        registrar.registerBodyProvider(tankDataProvider, TileAltarInspiration.class);
//        registrar.registerBodyProvider(tankDataProvider, TileBloodContainer.class);
//        StackProviderIgnoreMeta stackProviderIgnoreMeta = new StackProviderIgnoreMeta();
//        registrar.registerStackProvider(stackProviderIgnoreMeta, BlockWeaponTable.class);
//        GarlicBeaconProvider garlicBeaconProvider = new GarlicBeaconProvider();
//        registrar.registerBodyProvider(garlicBeaconProvider, BlockGarlicBeacon.class);
//        registrar.registerStackProvider(garlicBeaconProvider, BlockGarlicBeacon.class);
//    }
//
//    static String getShowCreatureInfoConf() {
//        return REFERENCE.MODID + ".showCreatureInfo";
//    }
//
//    static String getShowPlayerInfoConf() {
//        return REFERENCE.MODID + ".showPlayerInfo";
//    }
//
//    private static class StackProviderIgnoreMeta implements IWailaDataProvider {
//
//        @Override
//        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
//            return null;
//        }
//
//        @Override
//        public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//            return currenttip;
//        }
//
//        @Override
//        public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//            return currenttip;
//        }
//
//        @Override
//        public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
//            ItemStack stack = accessor.getStack();
//            stack.setItemDamage(0);
//            return stack;
//        }
//
//        @Override
//        public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
//            return currenttip;
//        }
//    }
//}
