package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModMenus;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.world.blockentity.VampireBeaconBlockEntity;
import de.teamlapen.vampirism.common.world.inventory.base.BaseContainerMenu;
import net.minecraft.core.Holder;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public class VampireBeaconMenu extends BaseContainerMenu {

    private final Container beacon;
    private final PaymentSlot paymentSlot;
    private final ContainerLevelAccess access;
    private final ContainerData beaconData;

    public VampireBeaconMenu(int pContainerId, Container container) {
        this(pContainerId, container, new SimpleContainerData(VampireBeaconBlockEntity.NUM_DATA_VALUES), ContainerLevelAccess.NULL);
    }

    public VampireBeaconMenu(int pContainerId, Container inventory, ContainerData beaconData, ContainerLevelAccess access) {
        super(ModMenus.VAMPIRE_BEACON.get(), pContainerId, 1);
        this.beacon = new SimpleContainer(1) {
            public boolean canPlaceItem(int slot, ItemStack itemStack) {
                return itemStack.is(ModItemTags.VAMPIRE_BEACON_PAYMENT_ITEM);
            }

            public int getMaxStackSize() {
                return 1;
            }
        };
        checkContainerDataCount(beaconData, 3);
        this.beaconData = beaconData;
        this.access = access;
        this.paymentSlot = new PaymentSlot(this.beacon, 0, 136, 110);
        this.addSlot(this.paymentSlot);
        this.addDataSlots(beaconData);
        this.addStandardInventorySlots(inventory, 36, 137);
    }


    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        if (pPlayer.level().isClientSide()) {
            ItemStack itemStack = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
            if (!itemStack.isEmpty()) {
                pPlayer.drop(itemStack, false);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex == 0) {
                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, clicked);
            } else {
                if (this.moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }

                if (slotIndex >= 1 && slotIndex < 28) {
                    if (!this.moveItemStackTo(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 28 && slotIndex < 37) {
                    if (!this.moveItemStackTo(stack, 1, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, 1, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == clicked.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return clicked;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(this.access, pPlayer, ModBlocks.VAMPIRE_BEACON.get());
    }

    @Override
    public void setData(int pId, int pData) {
        super.setData(pId, pData);
        this.broadcastChanges();
    }

    public int getLevels() {
        return this.beaconData.get(0);
    }

    @Nullable
    public Holder<MobEffect> getPrimaryEffect() {
        return BeaconMenu.decodeEffect(this.beaconData.get(VampireBeaconBlockEntity.DATA_PRIMARY));
    }

    public int getAmplifier() {
        return this.beaconData.get(VampireBeaconBlockEntity.DATA_AMPLIFIER);
    }

    public boolean isUpgraded() {
        return this.beaconData.get(VampireBeaconBlockEntity.DATA_UPGRADED) > 0;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void updateEffects(Optional<Holder<MobEffect>> primaryEffect, Optional<Integer> amplifier) {
        if (this.paymentSlot.hasItem()) {
            this.beaconData.set(VampireBeaconBlockEntity.DATA_PRIMARY, primaryEffect.map(BeaconMenu::encodeEffect).orElse(-1));
            this.beaconData.set(VampireBeaconBlockEntity.DATA_AMPLIFIER, amplifier.orElse(0));
            this.paymentSlot.remove(1);
            this.access.execute(Level::blockEntityChanged);
        }
    }

    public boolean hasPayment() {
        return !this.beacon.getItem(0).isEmpty();
    }

    public static class PaymentSlot extends Slot {
        public PaymentSlot(Container pContainer, int pIndex, int pX, int pY) {
            super(pContainer, pIndex, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return pStack.is(ModItemTags.VAMPIRE_BEACON_PAYMENT_ITEM);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
