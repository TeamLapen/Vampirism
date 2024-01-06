package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.blocks.BloodContainerBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Provides several utility methods that are related to blood
 */
public class BloodHelper {

    private static final Logger LOGGER = LogManager.getLogger(BloodHelper.class);

    /**
     * Checks if the given stack can store blood
     */
    public static boolean canStoreBlood(@NotNull ItemStack stack) {
        return fill(stack, 1000, IFluidHandler.FluidAction.SIMULATE) > 0;
    }

    /**
     * Returns the first glass bottle stack on the players hotbar
     */
    public static @NotNull ItemStack getGlassBottleInHotbar(@NotNull Inventory inventory) {
        int hotbarSize = Inventory.getSelectionSize();
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
    public static int getBlood(@NotNull ItemStack stack) {
        return FluidUtil.getFluidContained(stack).map(FluidStack::getAmount).orElse(0);

    }

    public static int getBlood(@NotNull IFluidHandler cap) {
        FluidStack stack = cap.drain(new FluidStack(ModFluids.BLOOD.get(), Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
        return stack.getAmount();
    }

    public static int getBlood(@NotNull Optional<IFluidHandler> opt) {
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
    public static int drain(@NotNull ItemStack stack, int amount, IFluidHandler.@NotNull FluidAction action, boolean exact, @NotNull Consumer<ItemStack> updateContainer) {
        if (exact && action.execute()) {
            if (drain(stack, amount, IFluidHandler.FluidAction.SIMULATE, false, updateContainer) != amount) return 0;
        }
        return FluidUtil.getFluidHandler(stack).map(handler -> {
            FluidStack fluidStack = handler.drain(amount, action);
            updateContainer.accept(handler.getContainer());
            return fluidStack.getAmount();
        }).orElse(0);
    }

    public static int fill(@NotNull ItemStack stack, int amount, IFluidHandler.FluidAction action) {
        return FluidUtil.getFluidHandler(stack).map(handler -> handler.fill(new FluidStack(ModFluids.BLOOD.get(), amount), action)).orElse(0);
    }

    /**
     * Fills the blood in container in the players inventory using multiple possible ways
     *
     * @param amt Fluid amount in mB
     * @return Blood amount that could not be filled
     */
    public static int fillBloodIntoInventory(@NotNull Player player, int amt) {
        if (amt <= 0) return 0;
        ItemStack stack = ItemStack.EMPTY;
        int hotbarSize = Inventory.getSelectionSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack stack1 = player.getInventory().getItem(i);
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
        ItemStack glass = getGlassBottleInHotbar(player.getInventory());
        if (!glass.isEmpty() && VampirismConfig.COMMON.autoConvertGlassBottles.get()) {
            ItemStack bloodBottle = new ItemStack(ModItems.BLOOD_BOTTLE.get(), 1);
            int filled = fill(bloodBottle, amt, IFluidHandler.FluidAction.EXECUTE);
            if (filled == 0) {
                LOGGER.warn("Failed to fill blood bottle with blood");
            }
            glass.shrink(1);
            if (glass.isEmpty()) {
                player.getInventory().removeItem(glass);
            }
            if (!player.getInventory().add(bloodBottle)) {
                player.drop(bloodBottle, false);
            }
            return fillBloodIntoInventory(player, amt - filled);
        }
        if (hasFeedingAdapterInHotbar(player.getInventory())) {
            ItemStack container = getBloodContainerInInventory(player.getInventory(), false, true);
            if (!container.isEmpty()) {
                FluidStack content = BloodContainerBlock.getFluidFromItemStack(container);
                int filled = Math.min(amt, BloodContainerBlockEntity.CAPACITY - content.getAmount());
                content.setAmount(content.getAmount() + filled);
                BloodContainerBlock.writeFluidToItemStack(container, content);
                return fillBloodIntoInventory(player, amt - filled);
            }
        }

        return amt;

    }

    public static boolean hasFeedingAdapterInHotbar(@NotNull Inventory inventory) {
        int hotbarSize = Inventory.getSelectionSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty() && itemStack.getItem().equals(ModItems.FEEDING_ADAPTER.get())) {
                return true;
            }
        }
        return false;
    }

    public static @NotNull ItemStack getBloodContainerInInventory(@NotNull Inventory inventory, boolean allowFull, boolean allowEmpty) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            FluidStack content = BloodContainerBlock.getFluidFromItemStack(stack);
            if (content.getRawFluid() == ModFluids.BLOOD.get() && (allowFull || content.getAmount() < BloodContainerBlockEntity.CAPACITY) && (allowEmpty || content.getAmount() > 0)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
