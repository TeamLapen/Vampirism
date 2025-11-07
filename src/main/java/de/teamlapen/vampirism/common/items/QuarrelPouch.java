package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.items.tooltip.QuarrelPouchTooltip;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.Optional;

public class QuarrelPouch extends Item {

    private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);

    public QuarrelPouch(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (stack.getCount() != 1) return false;
        if (action == ClickAction.PRIMARY && other.isEmpty()) return false;

        QuarrelPouchContents.Mutable content = stack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY).asMutable();
        if (action == ClickAction.PRIMARY && !other.isEmpty()) {
            if (content.tryAdd(other)) {
                playInsertSound(player);
            } else {
                playInsertFailSound(player);
            }
            stack.set(ModDataComponents.QUARREL_POUCH_CONTENTS, content.toImmutable());
            this.broadcastChangesOnContainerMenu(player);
            return true;
        } else if (action == ClickAction.SECONDARY && other.isEmpty()) {
            ItemStack first = content.getFirstStack();
            if (!first.isEmpty()) {
                playRemoveOneSound(player);
                access.set(first);
            }

            stack.set(ModDataComponents.QUARREL_POUCH_CONTENTS, content.toImmutable());
            this.broadcastChangesOnContainerMenu(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        QuarrelPouchContents.Mutable mutable = stack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY).asMutable();
        ItemStack item = slot.getItem();
        if (action == ClickAction.PRIMARY && !item.isEmpty()) {
            if (mutable.tryAdd(item)) {
                playInsertSound(player);
            } else {
                playInsertFailSound(player);
            }

            stack.set(ModDataComponents.QUARREL_POUCH_CONTENTS, mutable.toImmutable());
            this.broadcastChangesOnContainerMenu(player);
            return true;
        } else if(action == ClickAction.SECONDARY && item.isEmpty()) {
            ItemStack firstStack = mutable.getFirstStack();
            if (!stack.isEmpty()) {
                ItemStack itemStack = slot.safeInsert(firstStack);
                if (itemStack.getCount() > 0) {
                    mutable.tryAdd(itemStack);
                } else {
                    playRemoveOneSound(player);
                }
            }

            stack.set(ModDataComponents.QUARREL_POUCH_CONTENTS, mutable.toImmutable());
            this.broadcastChangesOnContainerMenu(player);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        QuarrelPouchContents contents = stack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
        return contents.getCount() > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        QuarrelPouchContents contents = stack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
        return (int) (contents.getCount() / (float) QuarrelPouchContents.MAX_ITEMS * 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        QuarrelPouchContents contents = stack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
        var fullness = contents.getCount() / (float) QuarrelPouchContents.MAX_ITEMS;
        return fullness == 1 ? FULL_BAR_COLOR : BAR_COLOR;
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
    }

    private void broadcastChangesOnContainerMenu(Player player) {
        AbstractContainerMenu abstractcontainermenu = player.containerMenu;
        abstractcontainermenu.slotsChanged(player.getInventory());
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        TooltipDisplay tooltipDisplay = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        return tooltipDisplay.shows(ModDataComponents.QUARREL_POUCH_CONTENTS.get())
                ? Optional.ofNullable(stack.get(ModDataComponents.QUARREL_POUCH_CONTENTS)).map(QuarrelPouchTooltip::new)
                : Optional.empty();
    }
}
