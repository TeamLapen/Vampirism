package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.items.IArrowContainer;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.items.component.ContainedProjectiles;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> texts, @NotNull TooltipFlag flag) {
        getArrows(stack).stream().map(ItemStack::getItem).collect(Collectors.groupingBy(a -> a)).forEach((item, items) -> texts.add(item.getName(item.getDefaultInstance()).copy().append(" " + items.size())));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return ((MutableComponent) super.getName(stack)).append(" (" + getArrows(stack).size() + "/" + this.maxCount +")");
    }

    @Override
    public Collection<ItemStack> getArrows(ItemStack container) {
        return container.getOrDefault(ModDataComponents.CONTAINED_PROJECTILES, ContainedProjectiles.EMPTY).getProjectiles();
    }

    @Override
    public boolean addArrow(ItemStack container, ItemStack arrow) {
        ArrayList<ItemStack> arrows = new ArrayList<>(getArrows(container));
        if (arrows.size() >= maxCount) return false;
        arrows.add(arrow);
        container.set(ModDataComponents.CONTAINED_PROJECTILES, ContainedProjectiles.of(arrows));
        return true;
    }

    @Override
    public void addArrows(ItemStack container, List<ItemStack> arrowStacks) {
        ArrayList<ItemStack> arrows = new ArrayList<>(getArrows(container));
        Iterator<ItemStack> iterator = arrowStacks.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            if (arrows.size() >= maxCount) break;
            arrows.add(stack);
            try {
                iterator.remove();
            } catch (UnsupportedOperationException e) {
                //Not all lists allow removing items via the iterator
            }
        }
        container.set(ModDataComponents.CONTAINED_PROJECTILES, ContainedProjectiles.of(arrows));
    }

    @Override
    public Collection<ItemStack> getAndRemoveArrows(ItemStack container) {
        var projectiles = container.remove(ModDataComponents.CONTAINED_PROJECTILES);
        return projectiles != null ? projectiles.getProjectiles() : List.of();
    }

    @Override
    public void removeArrows(ItemStack container) {
        container.remove(ModDataComponents.CONTAINED_PROJECTILES);
    }

    @Override
    public boolean removeArrow(ItemStack container, ItemStack arrow) {
        ContainedProjectiles containedProjectiles = container.getOrDefault(ModDataComponents.CONTAINED_PROJECTILES, ContainedProjectiles.EMPTY);
        List<ItemStack> arrows = containedProjectiles.getProjectiles();
        for (int i = 0; i < arrows.size(); i++) {
            if (ItemStack.matches(arrows.get(i), arrow)) {
                arrows.remove(i);
                container.set(ModDataComponents.CONTAINED_PROJECTILES, ContainedProjectiles.of(arrows));
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
            copy.setCount(1);
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
