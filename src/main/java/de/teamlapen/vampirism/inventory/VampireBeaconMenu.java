package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.blockentity.VampireBeaconBlockEntity;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModMenus;
import de.teamlapen.vampirism.core.tags.ModItemTags;
import net.minecraft.core.Holder;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class VampireBeaconMenu extends VampirismContainerMenu {

    private final Container beacon = new SimpleContainer(1) {

        @Override
        public boolean canPlaceItem(int pIndex, ItemStack pStack) {
            return pStack.is(ModItemTags.VAMPIRE_BEACON_PAYMENT_ITEM);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    private final PaymentSlot paymentSlot;
    private final ContainerData beaconData;
    private final ContainerLevelAccess access;

    public VampireBeaconMenu(int pContainerId, Container container) {
        this(pContainerId, container, new SimpleContainerData(VampireBeaconBlockEntity.NUM_DATA_VALUES), ContainerLevelAccess.NULL);
    }

    public VampireBeaconMenu(int pContainerId, Container container, ContainerData beaconData, ContainerLevelAccess pLevelAccess) {
        super(ModMenus.VAMPIRE_BEACON.get(), pContainerId, 1);
        this.beaconData = beaconData;
        this.access = pLevelAccess;
        this.paymentSlot = new PaymentSlot(this.beacon, 0, 136, 110);
        this.addSlot(this.paymentSlot);
        this.addDataSlots(beaconData);
        addPlayerSlots(container, 36, 137);
    }


    @Override
    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);
        if (pPlayer.level().isClientSide) {
            ItemStack itemStack = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
            if (!itemStack.isEmpty()) {
                pPlayer.drop(itemStack, false);
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
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
