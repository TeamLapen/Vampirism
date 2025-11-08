package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.common.blockentity.BloodContainerBlockEntity;
import de.teamlapen.vampirism.common.blocks.BloodContainerBlock;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.items.component.BottleBlood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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
        if (!glass.isEmpty() && ModConfig.COMMON.autoConvertGlassBottles.get()) {
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
            if (content.getFluid().isSame(ModFluids.BLOOD.get()) && (allowFull || content.getAmount() < BloodContainerBlockEntity.CAPACITY) && (allowEmpty || content.getAmount() > 0)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean handleFluidBlockInteraction(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, Direction side) {
        if (stack.isEmpty()) return false;

        if (tryDefaultFluid(stack, level, pos, player, hand, side)) return true;

        if (stack.is(Items.GLASS_BOTTLE)) {
            try (var transaction = Transaction.openRoot()) {
                ItemStack bloodBottleStack = ModItems.BLOOD_BOTTLE.toStack();
                var moved = ResourceHandlerUtil.move(level.getCapability(Capabilities.Fluid.BLOCK, pos, side), stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(bloodBottleStack)), y -> y.is(ModFluids.BLOOD), Integer.MAX_VALUE, transaction);
                if (moved > 0 && bloodBottleStack.getOrDefault(ModDataComponents.BOTTLE_BLOOD, BottleBlood.EMPTY).blood() > 0) {
                    transaction.commit();
                    player.setItemInHand(hand, bloodBottleStack);
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean tryDefaultFluid(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, Direction side) {
        try (var transaction = Transaction.openRoot()) {
            var itemCapability = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forPlayerInteraction(player, hand));
            var blockCapability = level.getCapability(Capabilities.Fluid.BLOCK, pos, side);
            var moved = ResourceHandlerUtil.move(itemCapability, blockCapability, x -> true, Integer.MAX_VALUE, transaction);
            if (moved > 0) {
                transaction.commit();
                return true;
            }

            moved = ResourceHandlerUtil.move(blockCapability, itemCapability, x -> true, Integer.MAX_VALUE, transaction);

            if (moved > 0) {
                transaction.commit();
                return true;
            }
        }

        return false;
    }

    public static boolean handleFluidItemBlockInteraction(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, Direction side) {
        boolean interacted = FluidUtil.interactWithFluidHandler(player, hand, level, pos, side);
        if (!interacted && stack.getItem().equals(Items.GLASS_BOTTLE) && ModConfig.COMMON.autoConvertGlassBottles.get()) {
            interacted = FluidUtil.getFluidHandler(level, pos, side).map((fluidHandler -> {
                if (!fluidHandler.getFluidInTank(0).getFluid().equals(ModFluids.BLOOD.get())) return false;

                ItemStack glass = player.getItemInHand(hand);
                ItemStack bloodBottle = FluidUtil.tryFillContainer(new ItemStack(ModItems.BLOOD_BOTTLE.get()), fluidHandler, Integer.MAX_VALUE, player, true).getResult();
                if (bloodBottle.isEmpty()) return false;

                if (player.getAbilities().instabuild) {
                    player.addItem(bloodBottle);
                } else {
                    if (glass.getCount() > 1) {
                        glass.shrink(1);
                        player.setItemInHand(hand, glass);
                        player.addItem(bloodBottle);
                    } else {
                        player.setItemInHand(hand, bloodBottle);
                    }
                }

                return true;
            })).orElse(false);
        }

        return interacted;
    }

    public static boolean isConvertibleToBlood(FluidStack fluid) {
        return isConvertibleToBlood(fluid.getFluid());
    }

    public static boolean isConvertibleToBlood(FluidResource fluid) {
        return isConvertibleToBlood(fluid.getFluid());
    }

    public static boolean isConvertibleToBlood(Fluid fluid) {
        return VampirismAPI.bloodConversionRegistry().hasConversion(fluid);
    }

    public static IFluidBloodConversion getBloodConversion(FluidStack fluid) {
        return getBloodConversion(fluid.getFluid());
    }

    public static IFluidBloodConversion getBloodConversion(Fluid fluid) {
        return VampirismAPI.bloodConversionRegistry().getFluidConversion(fluid);
    }
}
