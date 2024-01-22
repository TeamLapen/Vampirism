package de.teamlapen.vampirism.blockentity.diffuser;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.blockentity.PlayerOwnedBlockEntity;
import de.teamlapen.vampirism.inventory.diffuser.DiffuserMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class DiffuserBlockEntity extends PlayerOwnedBlockEntity {
    public static final int MAX_BOOT_TIMER = 5*20;
    protected static final int SLOT_FUEL = 0;
    public static final int DATA_LITE_TIME = 0;
    public static final int DATA_LITE_DURATION = 1;
    public static final int DATA_LITE_BOOT_TIMER = 2;
    public static final int NUM_DATA_VALUES = 3;
    public static final int NUM_SLOTS = 1;
    protected NonNullList<ItemStack> items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);

    private int litTime;
    private int litDuration;
    private int bootTimer;
    private boolean loaded = false;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case DATA_LITE_TIME -> DiffuserBlockEntity.this.litTime;
                case DATA_LITE_DURATION -> DiffuserBlockEntity.this.litDuration;
                case DATA_LITE_BOOT_TIMER -> DiffuserBlockEntity.this.bootTimer;
                default -> throw new IllegalArgumentException("Invalid index: " + pIndex);
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case DATA_LITE_TIME -> DiffuserBlockEntity.this.litTime = pValue;
                case DATA_LITE_DURATION -> DiffuserBlockEntity.this.litDuration = pValue;
                case DATA_LITE_BOOT_TIMER -> DiffuserBlockEntity.this.bootTimer = pValue;
                default -> throw new IllegalArgumentException("Invalid index: " + pIndex);
            }
        }

        @Override
        public int getCount() {
            return NUM_DATA_VALUES;
        }
    };

    protected DiffuserBlockEntity(BlockEntityType<? extends DiffuserBlockEntity> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
        this.litTime = pTag.getInt("litTime");
        this.bootTimer = pTag.getInt("bootTimer");
        this.litDuration = this.getBurnDuration(this.items.get(0));
        this.loaded = true;
    }

    protected abstract int getBurnDuration(ItemStack itemStack);

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("litTime", this.litTime);
        pTag.putInt("bootTimer", this.bootTimer);
        ContainerHelper.saveAllItems(pTag, this.items);
    }

    public boolean isActive() {
        return this.bootTimer == MAX_BOOT_TIMER;
    }

    @Override
    protected @NotNull abstract DiffuserMenu createMenu(int pContainerId, @NotNull Inventory pInventory, @NotNull LockDataHolder lockData);

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        ItemStack itemstack = this.items.get(pSlot);
        this.items.set(pSlot, pStack);

    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void onTouched(Player pPlayer) {

    }

    public static boolean tryAccess(Player player, IPlayableFaction<?> faction, Component displayName) {
        if (!player.isSpectator() && VampirismAPI.factionRegistry().getFaction(player) != faction) {
            player.displayClientMessage(Component.translatable("text.vampirism.cannot_access_menu", displayName), true);
            player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, DiffuserBlockEntity blockEntity) {
        if (blockEntity.litTime > 0) {
            blockEntity.litTime--;
            if (blockEntity.bootTimer > 0) {
                blockEntity.bootTimer--;
                if (blockEntity.bootTimer == 0) {
                    blockEntity.activateEffect(level, blockPos, blockState);
                }
            }
            if (blockEntity.loaded && blockEntity.bootTimer == 0) {
                blockEntity.loaded = false;
                blockEntity.activateEffect(level, blockPos, blockState);
            }
        } else if (blockEntity.litTime == 0 && blockEntity.getBurnDuration(blockEntity.items.get(SLOT_FUEL)) > 0){
            int burnDuration = blockEntity.getBurnDuration(blockEntity.items.get(SLOT_FUEL));
            blockEntity.litTime += burnDuration;
            blockEntity.litDuration = burnDuration;
            blockEntity.items.get(SLOT_FUEL).shrink(1);
            blockEntity.setChanged();
        } else {
            if (blockEntity.bootTimer == 0) {
                blockEntity.deactivateEffect(level, blockPos, blockState);
                blockEntity.bootTimer = MAX_BOOT_TIMER;
            } else if (blockEntity.bootTimer != MAX_BOOT_TIMER) {
                blockEntity.bootTimer = MAX_BOOT_TIMER;
            }
        }
    }

    protected void activateEffect(Level level, BlockPos blockPos, BlockState blockState) {

    }

    public void deactivateEffect(Level level, BlockPos blockPos, BlockState blockState) {

    }
}
