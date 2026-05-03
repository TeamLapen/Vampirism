package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.teamlapen.vampirism.common.world.items.CrossbowArrowItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record QuarrelPouchContents(List<ItemStack> items) implements TooltipComponent {

    public static final QuarrelPouchContents EMPTY = new QuarrelPouchContents(List.of());
    public static final int MAX_ITEMS = 256;

    public static final Codec<QuarrelPouchContents> CODEC = ItemStack.CODEC.listOf().flatXmap(QuarrelPouchContents::checkAndCreate, x -> DataResult.success(x.items));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuarrelPouchContents> STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(QuarrelPouchContents::new, o -> o.items);

    private static DataResult<QuarrelPouchContents> checkAndCreate(List<ItemStack> items) {
        int sum = items.stream().mapToInt(ItemStack::getCount).sum();
        if (sum > MAX_ITEMS) {
            return DataResult.error(() -> "Excessive total item count");
        }
        return DataResult.success(new QuarrelPouchContents(items));
    }

    @Override
    public List<ItemStack> items() {
        return List.copyOf(items);
    }

    public static boolean canItemBeInPouch(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().canFitInsideContainerItems() && stack.getItem() instanceof CrossbowArrowItem;
    }

    public Mutable asMutable() {
        return new Mutable(this);
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public int getCount() {
        return this.items.stream().mapToInt(ItemStack::getCount).sum();
    }

    public ItemStack getFirst() {
        if (this.items.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            return this.items.getFirst().copyWithCount(1);
        }
    }

    public ItemStack getSpecific(Item item) {
        return this.items.stream().filter(x -> x.is(item)).findFirst().map(s -> s.copyWithCount(1)).orElse(ItemStack.EMPTY);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashStackList(this.items);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof QuarrelPouchContents(List<ItemStack> items1)) {
            return ItemStack.listMatches(this.items, items1);
        }
        return false;
    }

    public static class Mutable {

        private final List<ItemStack> items;

        public Mutable(QuarrelPouchContents contents) {
            this.items = new ArrayList<>(contents.items);
        }

        public Mutable clear() {
            this.items.clear();
            return this;
        }

        public int getCount() {
            return this.items.stream().mapToInt(ItemStack::getCount).sum();
        }

        public boolean tryAdd(ItemStack stack) {
            if (!canItemBeInPouch(stack)) {
                return false;
            }

            int remainingSpace = MAX_ITEMS - getCount();
            if (remainingSpace > 0) {
                if (stack.getCount() <= remainingSpace) {
                    this.add(stack.copy());
                    stack.setCount(0);
                } else {
                    ItemStack itemStack = stack.copyWithCount(remainingSpace);
                    stack.shrink(remainingSpace);
                    this.add(itemStack);
                }
                return true;
            }
            return false;
        }

        private void add(ItemStack newStack) {
            for (ItemStack existingStack : this.items) {
                if (ItemStack.isSameItemSameComponents(existingStack, newStack)) {
                    int i = existingStack.getMaxStackSize() - existingStack.getCount();
                    i = Math.min(i, newStack.getCount());
                    if (i > 0) {
                        newStack.shrink(i);
                        existingStack.grow(i);
                        if (newStack.isEmpty()) {
                            return;
                        }
                    }
                }
            }
            if (!newStack.isEmpty()) {
                this.items.add(newStack);
            }
        }

        public ItemStack getFirst() {
            if (this.items.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                var first = this.items.getFirst();
                var result = first.copyWithCount(1);
                first.shrink(1);
                if (first.isEmpty()) {
                    this.items.remove(first);
                }
                return result;
            }
        }

        public ItemStack getFirstStack() {
            if (this.items.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                return this.items.removeFirst();
            }
        }

        public ItemStack getSpecific(Item item) {
            Optional<ItemStack> first = this.items.stream().filter(x -> x.is(item)).findFirst();
            if (first.isPresent()) {
                var result = first.get().copyWithCount(1);
                first.get().shrink(1);
                if (first.get().isEmpty()) {
                    this.items.remove(first.get());
                }
                return result;
            } else {
                return ItemStack.EMPTY;
            }
        }

        public QuarrelPouchContents toImmutable() {
            return new QuarrelPouchContents(List.copyOf(this.items));
        }
    }
}
