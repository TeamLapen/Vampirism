package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.teamlapen.vampirism.api.world.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.items.CrossbowArrowItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record QuarrelPouchContents(List<ItemStackTemplate> items) implements TooltipComponent {

    public static final QuarrelPouchContents EMPTY = new QuarrelPouchContents(List.of());
    public static final int MAX_ITEMS = 256;

    public static final Codec<QuarrelPouchContents> CODEC = ItemStackTemplate.CODEC.listOf().flatXmap(QuarrelPouchContents::checkAndCreate, x -> DataResult.success(x.items));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuarrelPouchContents> STREAM_CODEC = ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()).map(QuarrelPouchContents::new, o -> o.items);

    private static DataResult<QuarrelPouchContents> checkAndCreate(List<ItemStackTemplate> items) {
        int sum = items.stream().mapToInt(ItemStackTemplate::count).sum();
        if (sum > MAX_ITEMS) {
            return DataResult.error(() -> "Excessive total item count");
        }
        return DataResult.success(new QuarrelPouchContents(items));
    }

    @Override
    public List<ItemStackTemplate> items() {
        return this.items;
    }

    public static boolean canItemBeInPouch(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof CrossbowArrowItem;
    }

    public Mutable asMutable() {
        return new Mutable(this);
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public int getCount() {
        return this.items.stream().mapToInt(ItemStackTemplate::count).sum();
    }

    @Override
    public int hashCode() {
        return this.items.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof QuarrelPouchContents(List<ItemStackTemplate> items1)) {
            return this.items.equals(items1);
        }
        return false;
    }

    public static class Mutable {

        private final List<ItemStack> items;

        public Mutable(QuarrelPouchContents contents) {
            this.items = contents.items.stream().map(ItemStackTemplate::create).collect(Collectors.toList());
        }

        public Mutable clear() {
            this.items.clear();
            return this;
        }

        public int getCount() {
            return this.items.stream().mapToInt(ItemStack::getCount).sum();
        }

        public int tryAdd(ItemStack stack) {
            if (!canItemBeInPouch(stack)) {
                return 0;
            }

            int remainingSpace = MAX_ITEMS - getCount();
            if (remainingSpace > 0) {
                if (stack.getCount() <= remainingSpace) {
                    int value = stack.getCount();
                    this.add(stack.copyWithCount(value));
                    stack.setCount(0);
                    return value;
                } else {
                    ItemStack itemStack = stack.copyWithCount(remainingSpace);
                    stack.shrink(remainingSpace);
                    this.add(itemStack);
                    return remainingSpace;
                }
            }
            return 0;
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
            return new QuarrelPouchContents(this.items.stream().filter(x -> !x.isEmpty()).map(ItemStackTemplate::fromNonEmptyStack).toList());
        }
    }

    public static class ResourceHandler implements net.neoforged.neoforge.transfer.ResourceHandler<ItemResource> {

        private final ItemAccess itemAccess;

        public ResourceHandler(ItemAccess itemAccess) {
            this.itemAccess = itemAccess;
        }

        @Nullable
        private QuarrelPouchContents getResource() {
            return this.itemAccess.getResource().get(ModDataComponents.QUARREL_POUCH_CONTENTS);
        }

        @Override
        public int size() {
            var contents = this.getResource();
            if (contents == null) return 0;
            return contents.items().size();
        }

        @Override
        public ItemResource getResource(int index) {
            QuarrelPouchContents resource = this.getResource();
            if (resource == null || resource.items().size() <= index) {
                return ItemResource.EMPTY;
            }
            return ItemResource.of(resource.items.get(index));
        }

        @Override
        public long getAmountAsLong(int index) {
            QuarrelPouchContents resource = this.getResource();
            if (resource == null || resource.items().size() <= index) {
                return 0;
            }
            return resource.items.get(index).count();
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            if (!resource.is(x -> x.value() instanceof IVampirismCrossbowArrow<?>)) {
                return 0;
            }
            return MAX_ITEMS;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return resource.is(x -> x.value() instanceof IVampirismCrossbowArrow<?>);
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            TransferPreconditions.checkNonNegative(index);

            int accessAmount = itemAccess.getAmount();
            if (accessAmount <= 0) {
                return 0;
            }

            ItemResource accessResource = itemAccess.getResource();
            QuarrelPouchContents contents = accessResource.get(ModDataComponents.QUARREL_POUCH_CONTENTS);
            if (contents == null || contents.items().size() <= index) return 0;

            int allowed = MAX_ITEMS - contents.getCount();

            Mutable mutable = contents.asMutable();
            int inserted = 0;
            if (index == contents.items.size()) {
                inserted = mutable.tryAdd(resource.toStack(Math.min(amount, allowed)));
            } else {
                ItemStackTemplate itemStackTemplate = contents.items.get(index);
                if (resource.matches(itemStackTemplate)) {
                    inserted += mutable.tryAdd(resource.toStack(Math.min(amount, allowed)));
                } else {
                    return 0;
                }
            }


            return inserted * itemAccess.extract(accessResource.with(ModDataComponents.QUARREL_POUCH_CONTENTS, mutable.toImmutable()), accessAmount, transaction);
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            TransferPreconditions.checkNonNegative(index);

            int accessAmount = itemAccess.getAmount();
            if (accessAmount <= 0) {
                return 0;
            }

            ItemResource accessResource = itemAccess.getResource();
            QuarrelPouchContents contents = accessResource.get(ModDataComponents.QUARREL_POUCH_CONTENTS);
            if (contents == null || contents.items().size() <= index) return 0;

            ItemStackTemplate stack = contents.items.get(index);
            if (!resource.matches(stack)) return 0;

            int toExtract = Math.min(amount, stack.count());
            if (toExtract <= 0) return 0;

            List<ItemStackTemplate> items = new ObjectArrayList<>(contents.items());

            if (toExtract == stack.count()) {
                items.remove(index);
            } else {
                items.set(index, new ItemStackTemplate(stack.item(), stack.count() - toExtract, stack.components()));
            }

            return toExtract * itemAccess.exchange(accessResource.with(ModDataComponents.QUARREL_POUCH_CONTENTS, new QuarrelPouchContents(items)), accessAmount, transaction);
        }
    }
}
