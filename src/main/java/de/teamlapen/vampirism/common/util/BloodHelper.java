package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.components.IBottleBlood;
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
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Provides several utility methods that are related to blood
 */
public class BloodHelper {

    private static final Logger LOGGER = LogManager.getLogger(BloodHelper.class);

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
        try (var ignored = Transaction.openRoot()) {
            var fluid = ResourceHandlerUtil.extractFirst(stack.getCapability(Capabilities.Fluid.ITEM, null), x -> x.is(ModFluids.BLOOD), Integer.MAX_VALUE, null);
            return fluid != null ? fluid.amount() : 0;
        }
    }

    /**
     * Fills the blood in container in the players inventory using multiple possible ways
     *
     * @param amt Fluid amount in mB
     * @return Blood amount that could not be filled
     */
    public static int fillBloodIntoInventory(@NotNull Player player, int amt) {
        if (amt <= 0) return 0;
        int hotbarSize = Inventory.getSelectionSize();
        for (int i = 0; i < hotbarSize; i++) {
            ItemStack stack1 = player.getInventory().getItem(i);
            amt = amt - ResourceHandlerUtil.insertStacking(stack1.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forPlayerSlot(player, i)), FluidResource.of(ModFluids.BLOOD), amt, null);
        }


        ItemStack glass = getGlassBottleInHotbar(player.getInventory());
        if (!glass.isEmpty() && ModConfig.COMMON.autoConvertGlassBottles.get()) {
            ItemStack bloodBottle = new ItemStack(ModItems.BLOOD_BOTTLE.get(), 1);
            int filled = ResourceHandlerUtil.insertStacking(bloodBottle.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(bloodBottle)), FluidResource.of(ModFluids.BLOOD), amt, null);
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
        PlayerInventoryWrapper inventory = PlayerInventoryWrapper.of(player);
        ResourceHandler<ItemResource> handSlot = inventory.getHandSlot(hand);
        boolean interacted = ResourceHandlerUtil.move(level.getCapability(Capabilities.Fluid.BLOCK, pos, side), stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forHandlerIndex(handSlot, 0)), x -> x.is(ModFluids.BLOOD), IBottleBlood.MAX_VALUE * IBottleBlood.MULTIPLIER, null) > 0;
        if (!interacted && stack.getItem().equals(Items.GLASS_BOTTLE) && ModConfig.COMMON.autoConvertGlassBottles.get()) {
            try(var transaction = Transaction.openRoot()) {
                ItemStack bloodBottle = new ItemStack(ModItems.BLOOD_BOTTLE.get());
                ItemAccess itemAccess = ItemAccess.forStack(new ItemStack(ModItems.BLOOD_BOTTLE.get()));
                if (ResourceHandlerUtil.move(level.getCapability(Capabilities.Fluid.BLOCK, pos, side), bloodBottle.getCapability(Capabilities.Fluid.ITEM, itemAccess), x -> x.is(ModFluids.BLOOD), IBottleBlood.MAX_VALUE * IBottleBlood.MULTIPLIER, transaction) > 0) {
                    var bottle = ResourceHandlerUtil.extractFirst(handSlot, x -> x.is(Items.GLASS_BOTTLE), 1, transaction);
                    if (bottle != null && bottle.amount() == 1) {
                        var inserted = ResourceHandlerUtil.insertStacking(handSlot, itemAccess.getResource(), 1, transaction);
                        if (inserted != 1) {
                            inserted = inventory.insert(itemAccess.getResource(), 1, transaction);
                        }

                        if (inserted == 1) {
                            transaction.commit();
                            return true;
                        }
                    }
                }
                return false;
            }
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
        return VampirismAPI.services().bloodConversionRegistry().hasConversion(fluid);
    }

    public static IFluidBloodConversion getBloodConversion(FluidStack fluid) {
        return getBloodConversion(fluid.getFluid());
    }

    public static IFluidBloodConversion getBloodConversion(Fluid fluid) {
        return VampirismAPI.services().bloodConversionRegistry().getFluidConversion(fluid);
    }
}
