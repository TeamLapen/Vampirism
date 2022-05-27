package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.blockentity.InventoryBlockEntity;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.container.BloodGrinderContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BloodGrinderBlockEntity extends InventoryBlockEntity {


    private static List<ItemEntity> getCaptureItems(Level worldIn, BlockPos pos) {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        return worldIn.getEntitiesOfClass(ItemEntity.class, new AABB(posX, posY + 0.5D, posZ, posX + 1D, posY + 1.5D, posZ + 1D), EntitySelector.ENTITY_STILL_ALIVE);
    }

    //Used to provide ItemHandler compatibility
    private final IItemHandler itemHandler;
    private final LazyOptional<IItemHandler> itemHandlerOptional;
    private int cooldownPull = 0;
    private int cooldownProcess = 0;

    public BloodGrinderBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.GRINDER.get(), pos, state, 1, BloodGrinderContainer.SELECTOR_INFOS);
        this.itemHandler = createWrapper();
        this.itemHandlerOptional = LazyOptional.of(() -> itemHandler);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if ((side != Direction.DOWN) && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandlerOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(@Nonnull CompoundTag tagCompound) {
        super.load(tagCompound);
        cooldownPull = tagCompound.getInt("cooldown_pull");
        cooldownProcess = tagCompound.getInt("cooldown_process");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("cooldown_pull", cooldownPull);
        compound.putInt("cooldown_process", cooldownProcess);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloodGrinderBlockEntity blockEntity) {
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

    @Nonnull
    @Override
    protected AbstractContainerMenu createMenu(int id, @Nonnull Inventory player) {
        return new BloodGrinderContainer(id, player, this, ContainerLevelAccess.create(player.player.getCommandSenderWorld(), this.getBlockPos()));
    }

    @Nonnull
    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("tile.vampirism.blood_grinder");
    }

    private static boolean pullItems(BloodGrinderBlockEntity blockEntity, Level level, BlockPos pos) {

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
                int blood = BloodConversionRegistry.getImpureBloodValue(stack.getItem());
                if (blood > 0) {
                    FluidStack fluid = new FluidStack(ModFluids.IMPURE_BLOOD.get(), blood);
                    FluidUtil.getFluidHandler(this.level, this.worldPosition.below(), Direction.UP).ifPresent(handler -> {
                        int filled = handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE);
                        if (filled >= 0.9f * blood) {
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
