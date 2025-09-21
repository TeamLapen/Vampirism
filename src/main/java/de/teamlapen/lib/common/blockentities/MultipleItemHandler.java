package de.teamlapen.lib.common.blockentities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MultipleItemHandler<T extends BlockEntity> implements IItemHandler {

    private final T blockEntity;
    private final SlotProperties<T>[] slots;

    @SafeVarargs
    public MultipleItemHandler(T blockEntity, SlotProperties<T>... slots) {
        this.blockEntity = blockEntity;
        this.slots = slots;
    }

    @Override
    public int getSlots() {
        return slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getSlot(slot).getter().apply(blockEntity);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !getSlot(slot).validator.test(stack)) return stack;

        ItemStack insideStack = getSlot(slot).getter.apply(blockEntity);
        int maxInsert = Math.min(getSlot(slot).slotLimit, stack.getMaxStackSize());
        ItemStack remainder = stack.copy();

        if (insideStack.isEmpty()) {
            int insertAmount = Math.min(stack.getCount(), maxInsert);
            if (!simulate) {
                getSlot(slot).setter.accept(blockEntity, stack.copyWithCount(insertAmount));
                getSlot(slot).onChanged.run();
            }
            remainder.shrink(insertAmount);
        } else if (ItemStack.isSameItemSameComponents(insideStack, stack)) {
            int space = maxInsert - insideStack.getCount();
            if (space <= 0) return stack;

            int insertAmount = Math.min(stack.getCount(), space);
            if (!simulate) {
                insideStack.grow(insertAmount);
                getSlot(slot).setter.accept(blockEntity, insideStack);
                getSlot(slot).onChanged.run();
            }
            remainder.shrink(insertAmount);
        }

        return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack insideStack = getSlot(slot).getter.apply(blockEntity);
        if (amount <= 0 || insideStack.isEmpty()) return ItemStack.EMPTY;

        int extractAmount = Math.min(amount, insideStack.getCount());
        ItemStack extracted = insideStack.copyWithCount(extractAmount);

        if (!simulate) {
            if (extractAmount == insideStack.getCount()) {
                getSlot(slot).setter.accept(blockEntity, ItemStack.EMPTY);
            } else {
                insideStack.shrink(extractAmount);
                getSlot(slot).setter.accept(blockEntity, insideStack);
            }
            getSlot(slot).onChanged.run();
        }

        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return getSlot(slot).slotLimit;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return getSlot(slot).validator.test(stack);
    }

    private SlotProperties<T> getSlot(int index) {
        if (index < 0 || index >= slots.length)
            throw new IndexOutOfBoundsException("Invalid slot index " + index);
        return slots[index];
    }

    public record SlotProperties<T extends BlockEntity>(Function<T, ItemStack> getter, BiConsumer<T, ItemStack> setter, Predicate<ItemStack> validator, int slotLimit, Runnable onChanged) {}
}
