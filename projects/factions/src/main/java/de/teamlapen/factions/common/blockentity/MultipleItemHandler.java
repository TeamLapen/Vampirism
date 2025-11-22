package de.teamlapen.factions.common.blockentity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MultipleItemHandler<T extends BlockEntity> extends SnapshotJournal<ItemStack[]> implements ResourceHandler<ItemResource> {

    private final T blockEntity;
    private final Runnable onChanged;
    private final SlotProperties<T>[] slots;

    @SafeVarargs
    public MultipleItemHandler(T blockEntity, Runnable onChanged, SlotProperties<T>... slots) {
        this.blockEntity = blockEntity;
        this.onChanged = onChanged;
        this.slots = slots;
    }

    @Override
    public int size() {
        return slots.length;
    }

    @Override
    public ItemResource getResource(int index) {
        return index < size() ? ItemResource.of(getStack(index)) : ItemResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        return index < size() ? getStack(index).getCount() : 0;
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return index < size() ? Math.min(getSlotLimit(index), resource.getMaxStackSize()) : 0;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return index < size() && validate(index, resource);
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (!isValid(index, resource)) {
            return 0;
        }

        ItemStack existingItem = getStack(index);
        if (existingItem.isEmpty()) return 0;
        if (resource.matches(existingItem)) {
            createSnapshot();
            int extract = Math.min(amount, existingItem.getCount());
            setStack(index, existingItem.copyWithCount(existingItem.getCount() - extract));
            return extract;
        }

        return 0;
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (!isValid(index, resource)) {
            return 0;
        }

        ItemStack existingItem = getStack(index);

        int maxInsert = Math.min(getSlotLimit(index), resource.getMaxStackSize()) - existingItem.getCount();

        int insert = Math.min(amount, maxInsert);

        if (existingItem.isEmpty()) {
            createSnapshot();
            setStack(index, resource.toStack().copyWithCount(insert));
        } else if (resource.matches(existingItem)) {
            createSnapshot();
            setStack(index, existingItem.copyWithCount(existingItem.getCount() + insert));
        } else {
            return 0;
        }
        return insert;
    }

    private ItemStack getStack(int index) {
        return getSlot(index).getter.apply(blockEntity);
    }

    private int getSlotLimit(int index) {
        return getSlot(index).slotLimit;
    }

    private void setStack(int index, ItemStack stack) {
        slots[index].setter.accept(blockEntity, stack);
    }

    private boolean validate(int index, ItemResource resource) {
        return getSlot(index).validator.test(resource);
    }

    @Override
    protected ItemStack[] createSnapshot() {
        return Arrays.stream(this.slots).map(x -> x.getter.apply(blockEntity)).toArray(ItemStack[]::new);
    }

    @Override
    protected void revertToSnapshot(@Nullable ItemStack[] snapshot) {
        if (snapshot == null) {
            Arrays.fill(snapshot = new ItemStack[this.slots.length], ItemStack.EMPTY);
        }

        for (int i = 0; i < snapshot.length; i++) {
            setStack(i, snapshot[i]);
        }
    }

    @Override
    protected void onRootCommit(@Nullable ItemStack[] originalState) {
        this.onChanged.run();
    }


    private SlotProperties<T> getSlot(int index) {
        if (index < 0 || index >= slots.length)
            throw new IndexOutOfBoundsException("Invalid slot index " + index);
        return slots[index];
    }

    public record SlotProperties<T extends BlockEntity>(Function<T, ItemStack> getter, BiConsumer<T, ItemStack> setter,
                                                        Predicate<ItemResource> validator, int slotLimit) {
    }
}
