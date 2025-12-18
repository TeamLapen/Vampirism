package de.teamlapen.vampirism.common.world.blockentity.diffuser;

import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionRegistry;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.blockentity.PlayerOwnedBlockEntity;
import de.teamlapen.vampirism.common.world.blocks.diffuser.DiffuserBlock;
import de.teamlapen.vampirism.common.world.inventory.diffuser.DiffuserMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public abstract class DiffuserBlockEntity extends PlayerOwnedBlockEntity {
    public static final int SLOT_FUEL = 0;
    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_BOOT_TIMER = 2;
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
                case DATA_LIT_TIME -> DiffuserBlockEntity.this.litTime;
                case DATA_LIT_DURATION -> DiffuserBlockEntity.this.litDuration;
                case DATA_BOOT_TIMER -> DiffuserBlockEntity.this.bootTimer;
                default -> throw new IllegalArgumentException("Invalid index: " + pIndex);
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case DATA_LIT_TIME -> DiffuserBlockEntity.this.litTime = pValue;
                case DATA_LIT_DURATION -> DiffuserBlockEntity.this.litDuration = pValue;
                case DATA_BOOT_TIMER -> DiffuserBlockEntity.this.bootTimer = pValue;
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

    public boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.litTime = input.getIntOr("litTime", 0);
        this.bootTimer = input.getIntOr("bootTimer", 0);
        this.litDuration = this.getBurnDuration(this.items.get(0));
        this.loaded = true;
    }

    protected abstract int getBurnDuration(ItemStack itemStack);

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("litTime", this.litTime);
        output.putInt("bootTimer", this.bootTimer);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected @NotNull
    abstract DiffuserMenu createMenu(int pContainerId, @NotNull Inventory pInventory, @NotNull LockDataHolder lockData);

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

    public static boolean tryAccess(Player player, Holder<? extends IPlayableFaction<?>> faction, Component displayName) {
        if (!player.isSpectator() && IFaction.is(IFactionRegistry.get().getFaction(player), faction)) {
            return true;
        } else {
            player.displayClientMessage(Component.translatable("text.vampirism.cannot_access_menu", displayName), true);
            player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, DiffuserBlockEntity blockEntity) {
        boolean hasChanged = false;
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
        } else if (blockEntity.litTime == 0 && blockEntity.getBurnDuration(blockEntity.items.get(SLOT_FUEL)) > 0) {
            ItemStack fuelStack = blockEntity.items.get(SLOT_FUEL);
            int burnDuration = blockEntity.getBurnDuration(fuelStack);
            blockEntity.litTime += burnDuration;
            blockEntity.litDuration = burnDuration;
            fuelStack.shrink(1);
            blockEntity.items.set(SLOT_FUEL, fuelStack);
            hasChanged = true;
        } else {
            int maxBootTimer = ModConfig.BALANCE.diffuserBootTime.get() * 20;
            if (blockEntity.bootTimer == 0) {
                blockEntity.deactivateEffect(level, blockPos, blockState);
                blockEntity.bootTimer = maxBootTimer;

            } else if (blockEntity.bootTimer != maxBootTimer) {
                blockEntity.bootTimer = maxBootTimer;
            }
        }

        boolean shouldBeLit = blockEntity.bootTimer == 0 && blockEntity.litTime > 0;
        if (blockState.getValue(DiffuserBlock.LIT) != shouldBeLit) {
            blockState = blockState.setValue(DiffuserBlock.LIT, shouldBeLit);
            level.setBlock(blockPos, blockState, Block.UPDATE_ALL);
            hasChanged = true;
        }

        if (hasChanged && !level.isClientSide()) {
            setChanged(level, blockPos, blockState);
        }
    }

    protected void activateEffect(Level level, BlockPos blockPos, BlockState blockState) {

    }

    public void deactivateEffect(Level level, BlockPos blockPos, BlockState blockState) {

    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        deactivateEffect(level, pos, state);
    }

    public int getParticleNumber(Level level, BlockPos blockPos, BlockState blockState, DiffuserBlockEntity blockEntity) {
        return 3;
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> pItems) {
        this.items = pItems;
    }
}
