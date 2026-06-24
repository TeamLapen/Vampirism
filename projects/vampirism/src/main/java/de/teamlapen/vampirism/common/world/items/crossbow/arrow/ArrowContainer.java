package de.teamlapen.vampirism.common.world.items.crossbow.arrow;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ArrowContainer extends Item {

    private final int maxCount;
    private final Predicate<ItemStack> arrowPredicate;

    public ArrowContainer(Properties properties, int maxCount, Predicate<ItemStack> arrowPredicate) {
        super(properties);
        this.maxCount = maxCount;
        this.arrowPredicate = arrowPredicate;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> texts, TooltipFlag flag) {
        ItemStackTemplate itemStackTemplate = stack.get(ModDataComponents.CONTAINED_PROJECTILES);
        if (itemStackTemplate != null) {
            texts.accept(itemStackTemplate.item().value().getName(itemStackTemplate.item().value().getDefaultInstance()).copy().append(" " + itemStackTemplate.count()));
        }
        texts.accept(Component.translatable("tooltip.vampirism.arrow_clip.desc1").withStyle(ChatFormatting.GRAY));
        texts.accept(Component.translatable("tooltip.vampirism.arrow_clip.desc2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().append(" (" + getArrowCount(stack) + "/" + this.maxCount + ")");
    }

    public int maxCount() {
        return maxCount;
    }

    private int getArrowCount(ItemStack stack) {
        ItemStackTemplate itemStackTemplate = stack.get(ModDataComponents.CONTAINED_PROJECTILES);
        return  itemStackTemplate == null ? 0 : itemStackTemplate.count();
    }

    public boolean accepts(ItemStack stack) {
        return this.arrowPredicate.test(stack);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack container, ItemStack otherStack, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            try (var transaction = Transaction.openRoot()) {
                var inserted = ResourceHandlerUtil.insertStacking(new ResourceHandler(ItemAccess.forStack(container)), ItemResource.of(otherStack), otherStack.count() , transaction);
                if (inserted > 0) {
                    otherStack.shrink(inserted);
                    BundleItem.playInsertSound(player);
                    transaction.commit();
                    return true;
                }
            }
            BundleItem.playInsertFailSound(player);
        }
        return false;
    }

    public static class ResourceHandler implements net.neoforged.neoforge.transfer.ResourceHandler<ItemResource> {

        private final ItemAccess itemAccess;
        private final ArrowContainer container;

        public ResourceHandler(ItemAccess itemAccess) {
            this.itemAccess = itemAccess;
            this.container = (ArrowContainer) itemAccess.getResource().getItem();
        }

        @Override
        public int size() {
            return 1;
        }

        @Nullable
        private ItemStackTemplate getArrows() {
            return this.itemAccess.getResource().get(ModDataComponents.CONTAINED_PROJECTILES);
        }

        @Override
        public ItemResource getResource(int index) {
            if (index != 0) {
                return ItemResource.EMPTY;
            }
            return ItemResource.of(getArrows());
        }

        @Override
        public long getAmountAsLong(int index) {
            if (index != 0) {
                return 0;
            }
            ItemStackTemplate arrows = getArrows();
            return arrows == null ? 0 : arrows.count();
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            if (index != 0) {
                return 0;
            }
            return this.container.maxCount;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return index == 0 && this.container.accepts(resource.toStack());
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            TransferPreconditions.checkNonNegative(index);

            int accessAmount = this.itemAccess.getAmount();
            if (accessAmount <= 0) {
                return 0;
            }

            ItemResource accessResource = this.itemAccess.getResource();
            ItemStackTemplate arrows = accessResource.get(ModDataComponents.CONTAINED_PROJECTILES);
            if (arrows != null && !resource.matches(arrows)) {
                return 0;
            }
            int currentCount = arrows == null ? 0 : arrows.count();
            int amountToInsert = Math.min(amount, this.container.maxCount - currentCount);

            if (arrows != null) {
                accessResource = accessResource.with(ModDataComponents.CONTAINED_PROJECTILES, arrows.withCount(arrows.count() + amountToInsert));
            } else {
                accessResource = accessResource.with(ModDataComponents.CONTAINED_PROJECTILES, new ItemStackTemplate(resource.getItem(), amountToInsert, resource.getComponentsPatch()));
            }

            return amountToInsert * itemAccess.exchange(accessResource, accessAmount, transaction);
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            TransferPreconditions.checkNonNegative(index);

            int accessAmount = this.itemAccess.getAmount();
            if (accessAmount <= 0) {
                return 0;
            }

            ItemResource accessResource = this.itemAccess.getResource();
            ItemStackTemplate arrows = accessResource.get(ModDataComponents.CONTAINED_PROJECTILES);
            if (arrows == null || !resource.matches(arrows)) {
                return 0;
            }

            int currentCount = arrows.count();
            int amountToExtract = Math.min(amount, currentCount);

            if (amountToExtract == currentCount) {
                accessResource = accessResource.without(ModDataComponents.CONTAINED_PROJECTILES);
            } else {
                accessResource = accessResource.with(ModDataComponents.CONTAINED_PROJECTILES, arrows.withCount(currentCount - amountToExtract));
            }

            return amountToExtract * itemAccess.exchange(accessResource, accessAmount, transaction);
        }
    }
}
