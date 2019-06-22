package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * Provides several utility methods that are related to blood
 */
public class BloodHelper {

    private static final Logger LOGGER = LogManager.getLogger(BloodHelper.class);

    /**
     * Returns the first stack on the players hotbar that can store blood
     *
     * @param inventory
     * @return
     */
    public static ItemStack getBloodContainerInHotbar(InventoryPlayer inventory) {
        int hotbarSize = InventoryPlayer.getHotbarSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && canStoreBlood(stack)) return stack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Checks if the given stack can store blood
     */
    public static boolean canStoreBlood(@Nonnull ItemStack stack) {
        return FluidUtil.getFluidHandler(stack).map(handler -> handler.fill(new FluidStack(ModFluids.blood, 1000), false) > 0).orElse(false);
    }

    /**
     * Returns the first glas bottle stack on the players hotbar
     *
     * @param inventory
     * @return
     */
    public static ItemStack getGlassBottleInHotbar(InventoryPlayer inventory) {
        int hotbarSize = InventoryPlayer.getHotbarSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (!itemStack.isEmpty() && itemStack.getItem().equals(Items.GLASS_BOTTLE)) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns the amount of blood stored in the given stack
     */
    public static int getBlood(@Nonnull ItemStack stack) {
        return FluidUtil.getFluidContained(stack).map(s -> s.amount).orElse(0);

    }

    public static int getBlood(@Nonnull IFluidHandler cap) {
        FluidStack stack = cap.drain(new FluidStack(ModFluids.blood, Integer.MAX_VALUE), false);
        return stack == null ? 0 : stack.amount;
    }

    /**
     * Tries to drain the given amount out of the stack.
     *
     * @param doDrain actually drain
     * @param exact   If only the exact amount should be drained or if less is ok too
     * @return Drained amount
     */
    public static int drain(@Nonnull ItemStack stack, int amount, boolean doDrain, boolean exact) {
        if (exact && doDrain) {
            if (drain(stack, amount, false, false) != amount) return 0;
        }
        return FluidUtil.getFluidHandler(stack).map(handler -> {
            FluidStack fluidStack = handler.drain(amount, doDrain);
            return fluidStack == null ? 0 : fluidStack.amount;
        }).orElse(0);
    }

    public static int fill(@Nonnull ItemStack stack, int amount, boolean doFill) {
        return FluidUtil.getFluidHandler(stack).map(handler -> handler.fill(new FluidStack(ModFluids.blood, amount), doFill)).orElse(0);
    }

    /**
     * Fills the blood in container in the players inventory using multiple possible ways
     *
     * @param player
     * @param amt    Fluid amount in mB
     * @return Blood amount that could not be filled
     */
    public static int fillBloodIntoInventory(EntityPlayer player, int amt) {
        if (amt <= 0) return 0;
        ItemStack stack = getBloodContainerInHotbar(player.inventory);
        if (!stack.isEmpty()) {
            int filled = fill(stack, amt, true);
            if (filled > 0) {
                if (filled < amt) {
                    return fillBloodIntoInventory(player, amt - filled);

                } else {
                    return 0;
                }
            }
        }
        ItemStack glas = getGlassBottleInHotbar(player.inventory);
        if (!glas.isEmpty() && Configs.autoConvertGlasBottles) {
            ItemStack bloodBottle = new ItemStack(ModItems.blood_bottle, 1);
            int filled = fill(bloodBottle, amt, true);
            if (filled == 0) {
                LOGGER.warn("Failed to fill blood bottle with blood");
            }
            glas.shrink(1);
            if (glas.isEmpty()) {
                player.inventory.deleteStack(glas);
            }
            if (!player.inventory.addItemStackToInventory(bloodBottle)) {
                player.dropItem(bloodBottle, false);
            }
            return fillBloodIntoInventory(player, amt - filled);
        }
        return amt;

    }
}
