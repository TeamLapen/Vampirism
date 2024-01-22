package de.teamlapen.vampirism.inventory.diffuser;

import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import de.teamlapen.vampirism.blockentity.diffuser.DiffuserBlockEntity;
import de.teamlapen.vampirism.data.provider.SundamageProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class DiffuserMenu extends PlayerOwnedMenu {

    private final Container pContainer;
    private final ContainerData pData;

    protected DiffuserMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory playerInventory, PlayerOwnedBlockEntity.LockDataHolder lockData) {
        this(pMenuType, pContainerId, playerInventory, new SimpleContainer(DiffuserBlockEntity.NUM_SLOTS), new SimpleContainerData(DiffuserBlockEntity.NUM_DATA_VALUES), lockData);
    }

    protected DiffuserMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory playerInventory, Container pContainer, ContainerData pData, PlayerOwnedBlockEntity.LockDataHolder lockData) {
        super(pMenuType, pContainerId, playerInventory.player, lockData);
        this.pContainer = pContainer;
        this.pData = pData;
        checkContainerSize(pContainer, DiffuserBlockEntity.NUM_SLOTS);
        checkContainerDataCount(pData, DiffuserBlockEntity.NUM_DATA_VALUES);
        this.addSlot(new DiffuserFuelSlot(this, pContainer, 0, 26, 53));
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(pData);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex != 1 && pIndex != 0) {
                if (this.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= 28 && pIndex < 37 && !this.moveItemStackTo(itemstack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 1, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return this.pContainer.stillValid(pPlayer);
    }

    public float getLitProgress() {
        int i = this.pData.get(DiffuserBlockEntity.DATA_LITE_DURATION);
        if (i == 0) {
            i = 200;
        }
        return Mth.clamp(this.pData.get(DiffuserBlockEntity.DATA_LITE_TIME) / (float)i, 0.0F, 1.0F);
    }

    public boolean isLit() {
        return this.pData.get(DiffuserBlockEntity.DATA_LITE_TIME) > 0;
    }

    public abstract boolean isFuel(ItemStack pStack);

    public float getBootProgress() {
        var timer = this.pData.get(DiffuserBlockEntity.DATA_LITE_BOOT_TIMER);
        return Mth.clamp(1f - ((float) timer / DiffuserBlockEntity.MAX_BOOT_TIMER), 0f, 1f);
    }

    protected static abstract class Factory<T extends DiffuserMenu> extends PlayerOwnedMenu.Factory<T> {

    }
}
