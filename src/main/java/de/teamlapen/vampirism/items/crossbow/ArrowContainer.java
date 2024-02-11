package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.items.IArrowContainer;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ArrowContainer extends Item implements IArrowContainer {

    private final int maxCount;
    private final Predicate<ItemStack> arrowPredicate;

    public ArrowContainer(Properties properties, int maxCount, Predicate<ItemStack> arrowPredicate) {
        super(properties);
        this.maxCount = maxCount;
        this.arrowPredicate = arrowPredicate;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> texts, @NotNull TooltipFlag flag) {
        getArrows(stack).stream().map(ItemStack::getItem).collect(Collectors.groupingBy(a -> a)).forEach((item, items) -> texts.add(item.getName(item.getDefaultInstance()).copy().append(" " + items.size())));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return ((MutableComponent) super.getName(stack)).append(" (" + getArrows(stack).size() + "/" + this.maxCount +")");
    }

    @Override
    public Collection<ItemStack> getArrows(ItemStack container) {
        ListTag arrows = container.getOrCreateTag().getList("arrows", Tag.TAG_COMPOUND);
        return arrows.stream().map(t -> ItemStack.of((CompoundTag)t)).collect(Collectors.toList());
    }

    @Override
    public boolean addArrow(ItemStack container, ItemStack arrow) {
        CompoundTag tag = container.getOrCreateTag();
        ListTag arrows = tag.getList("arrows", Tag.TAG_COMPOUND);
        if (arrows.size() >= maxCount) return false;
        arrows.add(arrow.save(new CompoundTag()));
        tag.put("arrows", arrows);
        return true;
    }

    @Override
    public void addArrows(ItemStack container, Collection<ItemStack> arrowStacks) {
        CompoundTag tag = container.getOrCreateTag();
        ListTag arrows = tag.getList("arrows", Tag.TAG_COMPOUND);
        for (ItemStack arrowStack : arrowStacks) {
            if (arrows.size() >= maxCount) break;
            arrows.add(arrowStack.save(new CompoundTag()));
        }
        tag.put("arrows", arrows);
    }

    @Override
    public Collection<ItemStack> getAndRemoveArrows(ItemStack container) {
        Collection<ItemStack> arrows = getArrows(container);
        //noinspection ConstantConditions
        container.getTag().remove("arrows");
        return arrows;
    }

    @Override
    public void removeArrows(ItemStack container) {
        container.getOrCreateTag().remove("arrows");
    }

    @Override
    public boolean removeArrow(ItemStack container, ItemStack arrow) {
        CompoundTag tag = container.getOrCreateTag();
        ListTag arrows = tag.getList("arrows", Tag.TAG_COMPOUND);
        for (int i = 0; i < arrows.size(); i++) {
            if (ItemStack.of((CompoundTag) arrows.get(i)).equals(arrow)) {
                arrows.remove(i);
                tag.put("arrows", arrows);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDiscardedOnUse(ItemStack container) {
        return false;
    }

    @Override
    public int getMaxArrows(ItemStack container) {
        return this.maxCount;
    }

    @Override
    public boolean canBeRefilled(ItemStack container) {
        return true;
    }

    @Override
    public boolean canContainArrow(ItemStack container, ItemStack arrow) {
        return this.arrowPredicate.test(arrow);
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack container, @NotNull ItemStack otherStack, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (!otherStack.isEmpty()) {
                int i = addArrows(container, otherStack);
                if (i > 0) {
                    this.playInsertSound(player);
                    otherStack.shrink(i);
                }
            }
            return true;
        }
        return false;
    }

    private int addArrows(ItemStack container, ItemStack otherStack) {
        int i = 0;
        if (otherStack.getItem() instanceof IVampirismCrossbowArrow<?> && canContainArrow(container, otherStack)) {
            i = otherStack.getCount();
            if (i > getMaxArrows(container) - getArrows(container).size()) {
                i = getMaxArrows(container) - getArrows(container).size();
            }
            List<ItemStack> arrows = new ArrayList<>();
            ItemStack copy = otherStack.copy();
            copy.setCount(i);
            for (int j = 0; j < i; j++) {
                arrows.add(copy.copy());
            }
            if (i > 0) {
                addArrows(container, arrows);
            }
        }
        return i;
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
