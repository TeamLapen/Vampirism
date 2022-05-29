package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.blocks.BloodContainerBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.tileentity.BloodContainerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Provides several utility methods that are related to blood
 */
public class BloodHelper {

    private static final Logger LOGGER = LogManager.getLogger(BloodHelper.class);

    /**
     * TODO 1.17 delete
     * Returns the first stack on the players hotbar that can store blood
     *
     * @param inventory
     * @return
     */
    public static ItemStack getBloodHandlerInHotbar(PlayerInventory inventory) {
        int hotbarSize = PlayerInventory.getSelectionSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && FluidUtil.getFluidHandler(stack).map(handler -> handler.fill(new FluidStack(ModFluids.BLOOD.get(), 1000), IFluidHandler.FluidAction.SIMULATE) > 0).orElse(false))
                return stack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Checks if the given stack can store blood
     */
    public static boolean canStoreBlood(@Nonnull ItemStack stack) {
        return fill(stack, 1000, IFluidHandler.FluidAction.SIMULATE) > 0;
    }

    /**
     * Returns the first glass bottle stack on the players hotbar
     */
    public static ItemStack getGlassBottleInHotbar(PlayerInventory inventory) {
        int hotbarSize = PlayerInventory.getSelectionSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack itemStack = inventory.getItem(i);
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
        return FluidUtil.getFluidContained(stack).map(FluidStack::getAmount).orElse(0);

    }

    public static int getBlood(@Nonnull IFluidHandler cap) {
        FluidStack stack = cap.drain(new FluidStack(ModFluids.BLOOD.get(), Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
        return stack.getAmount();
    }

    public static int getBlood(@Nonnull LazyOptional<IFluidHandler> opt) {
        return opt.map(handler -> {
            FluidStack stack = handler.drain(new FluidStack(ModFluids.BLOOD.get(), Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
            return stack.getAmount();
        }).orElse(0);
    }

    /**
     * Tries to drain the given amount out of the stack.
     *
     * @param action          actually drain
     * @param exact           If only the exact amount should be drained or if less is ok too
     * @param updateContainer Is called with the (new) container item after draining
     * @return Drained amount
     */
    public static int drain(@Nonnull ItemStack stack, int amount, IFluidHandler.FluidAction action, boolean exact, Consumer<ItemStack> updateContainer) {
        if (exact && action.execute()) {
            if (drain(stack, amount, IFluidHandler.FluidAction.SIMULATE, false, updateContainer) != amount) return 0;
        }
        return FluidUtil.getFluidHandler(stack).map(handler -> {
            FluidStack fluidStack = handler.drain(amount, action);
            updateContainer.accept(handler.getContainer());
            return fluidStack.getAmount();
        }).orElse(0);
    }

    public static int fill(@Nonnull ItemStack stack, int amount, IFluidHandler.FluidAction action) {
        return FluidUtil.getFluidHandler(stack).map(handler -> handler.fill(new FluidStack(ModFluids.BLOOD.get(), amount), action)).orElse(0);
    }

    /**
     * Fills the blood in container in the players inventory using multiple possible ways
     *
     * @param amt Fluid amount in mB
     * @return Blood amount that could not be filled
     */
    public static int fillBloodIntoInventory(PlayerEntity player, int amt) {
        if (amt <= 0) return 0;
        ItemStack stack = ItemStack.EMPTY;
        int hotbarSize = PlayerInventory.getSelectionSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack stack1 = player.inventory.getItem(i);
            if (!stack1.isEmpty() && fill(stack1, amt, IFluidHandler.FluidAction.SIMULATE) > 0) {
                stack = stack1;
                break;
            }
        }
        if (!stack.isEmpty()) {
            int filled = fill(stack, amt, IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0) {
                if (filled < amt) {
                    return fillBloodIntoInventory(player, amt - filled);
                } else {
                    return 0;
                }
            }
            LOGGER.warn("Could not execute bottle fill even though simulation was successful. Item: {} Amount: {}", stack, amt);
        }
        ItemStack glass = getGlassBottleInHotbar(player.inventory);
        if (!glass.isEmpty() && VampirismConfig.COMMON.autoConvertGlassBottles.get()) {
            ItemStack bloodBottle = new ItemStack(ModItems.BLOOD_BOTTLE.get(), 1);
            int filled = fill(bloodBottle, amt, IFluidHandler.FluidAction.EXECUTE);
            if (filled == 0) {
                LOGGER.warn("Failed to fill blood bottle with blood");
            }
            glass.shrink(1);
            if (glass.isEmpty()) {
                player.inventory.removeItem(glass);
            }
            if (!player.inventory.add(bloodBottle)) {
                player.drop(bloodBottle, false);
            }
            return fillBloodIntoInventory(player, amt - filled);
        }
        if (hasFeedingAdapterInHotbar(player.inventory)) {
            ItemStack container = getBloodContainerInInventory(player.inventory, false, true);
            if (!container.isEmpty()) {
                FluidStack content = BloodContainerBlock.getFluidFromItemStack(container);
                int filled = Math.min(amt, BloodContainerTileEntity.CAPACITY - content.getAmount());
                content.setAmount(content.getAmount() + filled);
                BloodContainerBlock.writeFluidToItemStack(container, content);
                return fillBloodIntoInventory(player, amt - filled);
            }
        }

        return amt;

    }

    public static boolean hasFeedingAdapterInHotbar(PlayerInventory inventory) {
        int hotbarSize = PlayerInventory.getSelectionSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty() && itemStack.getItem().equals(ModItems.FEEDING_ADAPTER.get())) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack getBloodContainerInInventory(PlayerInventory inventory, boolean allowFull, boolean allowEmpty) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            FluidStack content = BloodContainerBlock.getFluidFromItemStack(stack);
            if (content.getRawFluid() == ModFluids.BLOOD.get() && (allowFull || content.getAmount() < BloodContainerTileEntity.CAPACITY) && (allowEmpty || content.getAmount() > 0)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
