package de.teamlapen.lib.common.blockentities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SingleItemHandler<T extends BlockEntity> implements IItemHandler {

    private final T blockEntity;
    private final Function<T, ItemStack> getter;
    private final BiConsumer<T, ItemStack> setter;
    private final Predicate<ItemStack> validator;
    private final int slotLimit;
    private final Runnable onChanged;

    public SingleItemHandler(T blockEntity, Function<T, ItemStack> getter, BiConsumer<T, ItemStack> setter, Predicate<ItemStack> validator, int slotLimit, Runnable onChanged) {
        this.blockEntity = blockEntity;
        this.getter = getter;
        this.setter = setter;
        this.validator = validator;
        this.slotLimit = slotLimit;
        this.onChanged = onChanged;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getter.apply(blockEntity);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !validator.test(stack)) return stack;

        ItemStack insideStack = getter.apply(blockEntity);
        int maxInsert = Math.min(slotLimit, stack.getMaxStackSize());
        ItemStack remainder = stack.copy();

        if (insideStack.isEmpty()) {
            int insertAmount = Math.min(stack.getCount(), maxInsert);
            if (!simulate) {
                setter.accept(blockEntity, stack.copyWithCount(insertAmount));
                onChanged.run();
            }
            remainder.shrink(insertAmount);
        } else if (ItemStack.isSameItemSameComponents(insideStack, stack)) {
            int space = maxInsert - insideStack.getCount();
            if (space <= 0) return stack;

            int insertAmount = Math.min(stack.getCount(), space);
            if (!simulate) {
                insideStack.grow(insertAmount);
                setter.accept(blockEntity, insideStack);
                onChanged.run();
            }
            remainder.shrink(insertAmount);
        }

        return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack insideStack = getter.apply(blockEntity);
        if (amount <= 0 || insideStack.isEmpty()) return ItemStack.EMPTY;

        int extractAmount = Math.min(amount, insideStack.getCount());
        ItemStack extracted = insideStack.copyWithCount(extractAmount);

        if (!simulate) {
            if (extractAmount == insideStack.getCount()) {
                setter.accept(blockEntity, ItemStack.EMPTY);
            } else {
                insideStack.shrink(extractAmount);
                setter.accept(blockEntity, insideStack);
            }
            onChanged.run();
        }

        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slotLimit;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return validator.test(stack);
    }
}
