package de.teamlapen.vampirism.tests;

import de.teamlapen.lib.lib.util.FluidLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.fluids.BloodHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Since I'm not familiar with JUnit or similar and it does not work that well with Minecraft anyway, this is a some kind of ingame test which is executed via command
 */
public class Tests {
    public static void runTests(World world, EntityPlayer player) {
        sendMsg(player, "Starting tests");
        log("Clearing area");
        clearArea(world);
        boolean test1 = false;
        try {
            test1 = bloodFluidHandler(world, new BlockPos(-20, 2, -20));
        } catch (Throwable e) {
            log("bloodFluidHandler test failed with exception %s", e);
        }
        sendMsg(player, "bloodFluidHandler test " + (test1 ? "§2was successful§r" : "§4failed§r"));

        log("Finished tests -> teleporting player");
        player.attemptTeleport(0, 5, 0);
        sendMsg(player, "Finished tests");
    }

    private static boolean bloodFluidHandler(World world, BlockPos pos) throws Throwable {
        world.setBlockState(pos, ModBlocks.bloodContainer.getDefaultState());
        TileEntity t = world.getTileEntity(pos);
        IFluidHandler handler = t.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.random(world.rand));
        handler.fill(new FluidStack(ModFluids.blood, 10000000), true);
        int blood = BloodHelper.getBlood(handler);
        assert blood > 0 : "Could not fill blood container";

        ItemStack bloodBottle1 = new ItemStack(ModItems.bloodBottle);
        ItemStack bloodBottle2 = new ItemStack(ModItems.bloodBottle);
        FluidLib.drainContainerIntoTank(handler, bloodBottle1.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null));
        FluidLib.drainContainerIntoTank(handler, bloodBottle2.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null));
        assert BloodHelper.getBlood(handler) < blood : "Failed to train from container into bottles";
        FluidLib.drainContainerIntoTank(bloodBottle1.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null), handler);
        //FluidLib.drainContainerIntoTank(bloodBottle2.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,null),handler);
        log("%d %d", BloodHelper.getBlood(handler), blood);
        assert BloodHelper.getBlood(handler) == blood : "Lost blood somewhere";
        return true;

    }

    private static void log(String msg, Object... format) {
        VampirismMod.log.w("TEST", msg, format);
    }

    private static void sendMsg(EntityPlayer player, String msg) {
        player.sendMessage(new TextComponentString("§1[V-TEST]§r " + msg));
    }

    private static void clearArea(World world) {
        for (int x = -21; x < 22; x++) {
            for (int y = 1; y < 22; y++) {
                for (int z = -21; z < 22; z++) {
                    IBlockState s = (y == 1 || x == -21 || x == 21 || z == -21 || z == 21 || y == 21) ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState();
                    world.setBlockState(new BlockPos(x, y, z), s);
                }
            }
        }
    }
}
