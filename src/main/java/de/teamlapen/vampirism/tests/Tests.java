package de.teamlapen.vampirism.tests;

import com.google.common.base.Stopwatch;
import de.teamlapen.vampirism.blocks.WeaponTableBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.fluids.BloodHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Since I'm not familiar with JUnit or similar, and it does not work that well with Minecraft anyway, this is a some kind of ingame test which is executed via command
 * <p>
 * Usage of lambda and stuff is probably unnecessary and stuff, but fun.
 */
public class Tests {

    private final static Logger LOGGER = LogManager.getLogger(Tests.class);

    public static void runTests(@NotNull Level world, @NotNull ServerPlayer player) {
        sendMsg(player, "Starting tests");
        LOGGER.warn("Clearing area", new Object[]{});
        clearArea(world);
        boolean wasCreative = player.isCreative();
        player.setGameMode(GameType.SURVIVAL);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 100));
        player.randomTeleport(0, 5, 0, true);
        TestInfo info = new TestInfo(world, player, new BlockPos(-20, 2, -20), "BloodFluidHandler");

        runTest(Tests::bloodFluidHandler, info);
        runTest(Tests::blockWeaponTableFluids, info.next("BlockWeaponTableFluids"));

        LOGGER.warn("Finished tests -> teleporting player", new Object[]{});
        player.randomTeleport(0, 5, 0, true);
        if (wasCreative) player.setGameMode(GameType.CREATIVE);
        sendMsg(player, "Finished tests");
    }

    private static void runTest(@NotNull Tester tester, @NotNull TestInfo info) {
        boolean result;
        try {
            result = tester.run(info);
        } catch (Throwable t) {
            LOGGER.warn(info.name + " failed with exception %s", new Object[]{t});
            result = false;
        }
        sendMsg(info.player, info.name + " test " + (result ? "§2was successful§r" : "§4failed§r"));
    }

    private static void runLightTest(@NotNull LightTester tester, String name, @Nullable Player player) {
        boolean result;
        try {
            result = tester.run();
        } catch (Throwable t) {
            LOGGER.warn(name + " failed with exception {}", new Object[]{t});
            result = false;
        }
        if (player != null) {
            sendMsg(player, name + " test " + (result ? "§2was successful§r" : "§4failed§r"));
        } else {
            String msg = name + "test " + (result ? "was successful" : "failed");
            LOGGER.warn(msg, new Object[]{});
        }
    }

    /**
     * Should be run in POST INIT
     */
    public static void runBackgroundTests() {
        LOGGER.warn("Running background tests", new Object[]{});
        Stopwatch w = Stopwatch.createStarted();
        LOGGER.warn("Finished background tests after {} ms", new Object[]{w.stop().elapsed(TimeUnit.MILLISECONDS)});
    }

    private static boolean bloodFluidHandler(@NotNull TestInfo info) {
        info.world.setBlockAndUpdate(info.pos, ModBlocks.BLOOD_CONTAINER.get().defaultBlockState());
        BlockEntity t = info.world.getBlockEntity(info.pos);
        LazyOptional<IFluidHandler> opt = t.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.getRandom(info.world.random));
        opt.ifPresent(handler -> handler.fill(new FluidStack(ModFluids.BLOOD.get(), 10000000), IFluidHandler.FluidAction.EXECUTE));
        int blood = BloodHelper.getBlood(opt);
        assert blood > 0 : "Could not fill blood container";

        ItemStack bloodBottle1 = new ItemStack(ModItems.BLOOD_BOTTLE.get());
        ItemStack bloodBottle2 = new ItemStack(ModItems.BLOOD_BOTTLE.get());
        IFluidHandler handler = opt.orElse(null);
        FluidActionResult result1 = FluidUtil.tryFillContainer(bloodBottle1, handler, Integer.MAX_VALUE, null, true);
        assert result1.isSuccess() : "Transaction 1 failed";
        bloodBottle1 = result1.getResult();
        FluidActionResult result2 = FluidUtil.tryFillContainer(bloodBottle2, handler, Integer.MAX_VALUE, null, true);
        assert result2.isSuccess() : "Transaction 2 failed";
        bloodBottle2 = result2.getResult();
        assert BloodHelper.getBlood(handler) < blood : "Failed to drain from container into bottles";
        FluidActionResult result3 = FluidUtil.tryEmptyContainer(bloodBottle1, handler, Integer.MAX_VALUE, null, true);
        assert result3.isSuccess() : "Transaction 3 failed";
        bloodBottle1 = result3.getResult();
        FluidActionResult result4 = FluidUtil.tryEmptyContainer(bloodBottle2, handler, Integer.MAX_VALUE, null, true);
        assert result4.isSuccess() : "Transaction 4 failed";
        bloodBottle2 = result4.getResult();
        LOGGER.warn("{} {}", new Object[]{BloodHelper.getBlood(handler), blood});
        assert BloodHelper.getBlood(handler) == blood : "Lost blood somewhere";
        return true;

    }

    private static boolean blockWeaponTableFluids(@NotNull TestInfo info) {
        info.world.setBlockAndUpdate(info.pos, ModBlocks.WEAPON_TABLE.get().defaultBlockState());
        info.player.setItemInHand(info.player.getUsedItemHand(), new ItemStack(Items.LAVA_BUCKET));
        BlockState block = info.world.getBlockState(info.pos);
        block.use(info.world, info.player, info.player.getUsedItemHand(), new BlockHitResult(new Vec3(0, 0, 0), Direction.getRandom(info.world.random), info.pos, false));
        block = info.world.getBlockState(info.pos);
        assert info.player.getItemInHand(info.player.getUsedItemHand()).getItem().equals(Items.BUCKET) : "Incorrect Fluid Container Handling";
        LOGGER.warn("Block lava level: {}", new Object[]{block.getValue(WeaponTableBlock.LAVA)});
        assert (block.getValue(WeaponTableBlock.LAVA) * WeaponTableBlock.MB_PER_META) == FluidType.BUCKET_VOLUME : "Incorrect Fluid Transaction";
        return true;
    }

    private static void sendMsg(@NotNull Player player, String msg) {
        player.displayClientMessage(Component.literal("§1[V-TEST]§r " + msg), false);
    }

    private static void clearArea(@NotNull Level world) {
        for (int x = -21; x < 22; x++) {
            for (int y = 1; y < 22; y++) {
                for (int z = -21; z < 22; z++) {
                    BlockState s = (y == 1 || x == -21 || x == 21 || z == -21 || z == 21 || y == 21) ? ModBlocks.CASTLE_BLOCK_DARK_STONE.get().defaultBlockState() : Blocks.AIR.defaultBlockState();
                    world.setBlockAndUpdate(new BlockPos(x, y, z), s);
                }
            }
        }
    }

    @FunctionalInterface
    private interface Tester {
        /**
         * Runs the given test
         *
         * @param t the function argument
         * @return the function result
         */
        Boolean run(TestInfo t) throws Throwable;
    }

    @FunctionalInterface
    private interface LightTester {
        /**
         * Runs the given test
         *
         * @return Success
         * @throws Throwable any exception
         */
        Boolean run() throws Throwable;
    }

    private static class TestInfo {
        final Level world;
        final Player player;
        BlockPos pos;
        String name;

        private TestInfo(Level world, Player player, BlockPos pos, String name) {
            this.world = world;
            this.player = player;
            this.pos = pos;
            this.name = name;
        }


        private @NotNull TestInfo next(String name) {
            int x = pos.getX();
            int z = pos.getZ();
            x += 5;
            if (x > 20) {
                x = -20;
                z += 5;
                if (z > 20) {
                    throw new IllegalStateException("Not enough room -> Too many tests");
                }
            }
            this.pos = new BlockPos(x, pos.getY(), z);
            this.name = name;
            return this;
        }

    }
}
