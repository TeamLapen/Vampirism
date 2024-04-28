package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.blockentity.InventoryBlockEntity;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.BloodGrinderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BloodGrinderBlockEntity extends InventoryBlockEntity {


    private static @NotNull List<ItemEntity> getCaptureItems(@NotNull Level worldIn, @NotNull BlockPos pos) {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        return worldIn.getEntitiesOfClass(ItemEntity.class, new AABB(posX, posY + 0.5D, posZ, posX + 1D, posY + 1.5D, posZ + 1D), EntitySelector.ENTITY_STILL_ALIVE);
    }

    //Used to provide ItemHandler compatibility
    private final @NotNull IItemHandler itemHandler;
    private int cooldownPull = 0;
    private int cooldownProcess = 0;

    public BloodGrinderBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModTiles.GRINDER.get(), pos, state, 1, BloodGrinderMenu.SELECTOR_INFOS);
        this.itemHandler = new SelectorInvWrapper(this);
    }

    public @NotNull IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.loadAdditional(tagCompound, provider);
        cooldownPull = tagCompound.getInt("cooldown_pull");
        cooldownProcess = tagCompound.getInt("cooldown_process");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("cooldown_pull", cooldownPull);
        compound.putInt("cooldown_process", cooldownProcess);
    }

    public static void serverTick(@NotNull Level level, @NotNull BlockPos pos, BlockState state, @NotNull BloodGrinderBlockEntity blockEntity) {
        --blockEntity.cooldownPull;
        if (blockEntity.cooldownPull <= 0) {
            blockEntity.cooldownPull = 10;
            if (!blockEntity.isFull()) {
                boolean flag = pullItems(blockEntity, level, pos);
                if (flag) {
                    blockEntity.cooldownPull = 20;
                }
            }
        }

        --blockEntity.cooldownProcess;
        if (blockEntity.cooldownProcess <= 0) {
            blockEntity.cooldownProcess = 10;
            blockEntity.updateProcess();
        }
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new BloodGrinderMenu(id, player, this, ContainerLevelAccess.create(player.player.getCommandSenderWorld(), this.getBlockPos()));
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return Component.translatable("tile.vampirism.blood_grinder");
    }

    private static boolean pullItems(@NotNull BloodGrinderBlockEntity blockEntity, @NotNull Level level, @NotNull BlockPos pos) {

        boolean flag = de.teamlapen.lib.lib.inventory.InventoryHelper.tryGetItemHandler(level, pos.above(), Direction.DOWN).map(pair -> {
            IItemHandler handler = pair.getLeft();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack extracted = handler.extractItem(i, 1, true);
                if (!extracted.isEmpty()) {
                    ItemStack simulated = ItemHandlerHelper.insertItemStacked(blockEntity.itemHandler, extracted, true);

                    if (simulated.isEmpty()) {
                        extracted = handler.extractItem(i, 1, false);
                        ItemHandlerHelper.insertItemStacked(blockEntity.itemHandler, extracted, false);
                        return true;
                    }

                }
            }
            return false;
        }).orElse(false);

        if (flag) {
            return true;
        } else {
            for (ItemEntity entityItem : getCaptureItems(level, pos)) {
                ItemStack stack = entityItem.getItem();
                for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
                    ItemStack stack2 = blockEntity.itemHandler.insertItem(i, stack, true);
                    if (stack2.isEmpty()) {
                        stack2 = blockEntity.itemHandler.insertItem(i, stack, false);
                        if (stack2.getCount() < stack.getCount()) {
                            entityItem.discard();
                        } else {
                            entityItem.setItem(stack2);
                        }
                        return true;
                    }
                }
            }
            return false;
        }

    }

    private void updateProcess() {
        if (level != null && !isEmpty()) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                final int slot = i;
                ItemStack stack = itemHandler.extractItem(i, 1, true);
                IItemBlood data = VampirismAPI.bloodConversionRegistry().getItemBlood(stack);
                if (data.blood() > 0) {
                    FluidStack fluid = new FluidStack(ModFluids.IMPURE_BLOOD.get(), data.blood());
                    FluidUtil.getFluidHandler(this.level, this.worldPosition.below(), Direction.UP).ifPresent(handler -> {
                        int filled = handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE);
                        if (filled >= 0.9f * data.blood()) {
                            ItemStack extractedStack = itemHandler.extractItem(slot, 1, false);
                            handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                            this.level.playSound(null, this.getBlockPos(), ModSounds.GRINDER.get(), SoundSource.BLOCKS, 0.5f, 0.7f);
                            this.cooldownProcess = Mth.clamp(20 * filled / VReference.FOOD_TO_FLUID_BLOOD, 20, 100);
                        }
                    });
                }
            }
        }
    }
}
