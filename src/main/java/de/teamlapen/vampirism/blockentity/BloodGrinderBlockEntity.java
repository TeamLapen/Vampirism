package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BloodGrinderBlockEntity extends BlockEntity {

    public static final String KEY_INPUT_STACK = "InputStack";
    public static final String KEY_GRIND_COOLDOWN_TIME = "GrindCooldown";
    public static final String KEY_PULL_COOLDOWN_TIME = "PullCooldown";

    public static final int GRIND_DELAY = 100;
    public static final int PULL_DELAY = 8;
    public static final AABB PULL_REACH_AABB = Block.box(5.0, 16.0, 5.0, 11.0, 22.0, 11.0).toAabbs().getFirst();

    private final IItemHandler inputItemHandler;

    public FluidTank fluidInventory;
    public ItemStack inputStack;
    private int grindCooldownTime = -1;
    private int pullCooldownTime = -1;

    public BloodGrinderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BLOOD_GRINDER.get(), pos, blockState);
        this.inputItemHandler = new InputHandler(this, BloodGrinderBlockEntity::isGrindable);
        this.fluidInventory = new FluidTank(FluidType.BUCKET_VOLUME).setValidator(fluid -> fluid.is(ModFluids.BLOOD));
        this.inputStack = ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BLOOD_GRINDER.get(), (blockEntity, side) -> blockEntity.inputItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BLOOD_GRINDER.get(), (blockEntity, side) -> blockEntity.fluidInventory);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fluidInventory.readFromNBT(registries, tag);
        inputStack = ItemStack.parseOptional(registries, tag.getCompound(KEY_INPUT_STACK));
        grindCooldownTime = tag.getInt(KEY_GRIND_COOLDOWN_TIME);
        pullCooldownTime = tag.getInt(KEY_PULL_COOLDOWN_TIME);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        fluidInventory.writeToNBT(registries, tag);
        tag.put(KEY_INPUT_STACK, inputStack.saveOptional(registries));
        tag.putInt(KEY_GRIND_COOLDOWN_TIME, grindCooldownTime);
        tag.putInt(KEY_PULL_COOLDOWN_TIME, pullCooldownTime);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloodGrinderBlockEntity blockEntity) {
        trySuckingItems(level, pos, blockEntity);
        processGrinding(level, pos, blockEntity);
        pourBloodDown(level, pos, blockEntity);
    }

    private static void trySuckingItems(Level level, BlockPos pos, BloodGrinderBlockEntity blockEntity) {
        if (blockEntity.pullCooldownTime > 0) {
            blockEntity.pullCooldownTime--;
            return;
        }

        List<ItemEntity> items = getItemsAbove(level, pos);
        for (ItemEntity itemEntity : items) {
            ItemStack inputStack = blockEntity.inputStack;
            ItemStack entityStack = itemEntity.getItem();
            if (entityStack.isEmpty() || !isGrindable(entityStack)) continue;

            if (inputStack.isEmpty()) {
                int transferAmount = Math.min(entityStack.getCount(), entityStack.getMaxStackSize());
                blockEntity.inputStack = entityStack.split(transferAmount);
            } else if (ItemStack.isSameItemSameComponents(inputStack, entityStack) && inputStack.getCount() < inputStack.getMaxStackSize()) {
                int transferAmount = Math.min(entityStack.getCount(), inputStack.getMaxStackSize() - inputStack.getCount());
                blockEntity.inputStack.grow(transferAmount);
                entityStack.shrink(transferAmount);
            } else {
                continue;
            }

            if (entityStack.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.setItem(entityStack);
            }

            blockEntity.pullCooldownTime = PULL_DELAY;
            blockEntity.setChanged();
            break;
        }
    }

    public static List<ItemEntity> getItemsAbove(Level level, BlockPos pos) {
        AABB aabb = PULL_REACH_AABB.move(pos.getX(), pos.getY(), pos.getZ());
        return level.getEntitiesOfClass(ItemEntity.class, aabb, EntitySelector.ENTITY_STILL_ALIVE);
    }

    public static void processGrinding(Level level, BlockPos pos, BloodGrinderBlockEntity blockEntity) {
        if (blockEntity.grindCooldownTime > 0) {
            blockEntity.grindCooldownTime--;
            return;
        }

        ItemStack inputStack = blockEntity.inputStack;
        if (inputStack.isEmpty() || !isGrindable(inputStack)) return;

        int blood = VampirismAPI.bloodConversionRegistry().getItemBlood(inputStack).blood();
        FluidTank fluidInventory = blockEntity.fluidInventory;
        int space = fluidInventory.getSpace();
        if (space < blood && !fluidInventory.isEmpty()) return;

        int numberAllowedToGrind = Math.min(Math.max((space - space % blood) / blood, 1), 4);
        blockEntity.fluidInventory.fill(new FluidStack(ModFluids.BLOOD, blood * numberAllowedToGrind), IFluidHandler.FluidAction.EXECUTE);
        blockEntity.inputStack.shrink(numberAllowedToGrind);
        level.playSound(null, pos, ModSounds.BLOOD_SQUEEZE.get(), SoundSource.BLOCKS, 0.5f + level.getRandom().nextFloat() / 4, 1.0f - level.getRandom().nextFloat() / 4);

        blockEntity.grindCooldownTime = GRIND_DELAY;
        blockEntity.setChanged();
    }

    public static void pourBloodDown(Level level, BlockPos pos, BloodGrinderBlockEntity blockEntity) {
        FluidStack fluidStack = blockEntity.fluidInventory.getFluid();
        if (fluidStack.isEmpty())
            return;

        FluidUtil.getFluidHandler(level, pos.below(), Direction.UP).ifPresent(belowFluidHandler -> {
            int allowed = belowFluidHandler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);

            if (allowed > 0) {
                FluidStack toTransfer = fluidStack.copyWithAmount(allowed);
                int filled = belowFluidHandler.fill(toTransfer, IFluidHandler.FluidAction.EXECUTE);

                blockEntity.fluidInventory.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                blockEntity.setChanged();
            }
        });
    }

    public static boolean isGrindable(ItemStack stack) {
        return VampirismAPI.bloodConversionRegistry().canBeConverted(stack);
    }

    public static class InputHandler implements IItemHandler {

        private final BloodGrinderBlockEntity blockEntity;
        private final Predicate<ItemStack> isItemValid;

        public InputHandler(BloodGrinderBlockEntity blockEntity, Predicate<ItemStack> isItemValid) {
            this.blockEntity = blockEntity;
            this.isItemValid = isItemValid;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return blockEntity.inputStack;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            ItemStack inputStack = blockEntity.inputStack;
            if (stack.isEmpty() || !isItemValid(slot, stack)) {
                return stack;
            }

            int maxInsert = getStackLimit(stack);
            ItemStack remainder = stack.copy();

            if (inputStack.isEmpty()) {
                int insertAmount = Math.min(stack.getCount(), maxInsert);
                if (!simulate) {
                    blockEntity.inputStack = stack.copyWithCount(insertAmount);
                    blockEntity.setChanged();
                }
                remainder.shrink(insertAmount);
            } else if (ItemStack.isSameItemSameComponents(stack, inputStack)) {
                int space = maxInsert - inputStack.getCount();
                if (space <= 0) return stack;

                int insertAmount = Math.min(stack.getCount(), space);
                if (!simulate) {
                    blockEntity.inputStack.grow(insertAmount);
                    blockEntity.setChanged();
                }
                remainder.shrink(insertAmount);
            } else {
                return stack;
            }

            return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack inputStack = blockEntity.inputStack;
            if (amount <= 0 || inputStack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            int toExtract = Math.min(amount, inputStack.getCount());
            ItemStack extracted = inputStack.copyWithCount(toExtract);

            if (!simulate) {
                if (toExtract == inputStack.getCount()) {
                    blockEntity.inputStack = ItemStack.EMPTY;
                } else {
                    blockEntity.inputStack.shrink(toExtract);
                }
                blockEntity.setChanged();
            }

            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            return Item.ABSOLUTE_MAX_STACK_SIZE;
        }

        protected int getStackLimit(ItemStack stack) {
            return Math.min(getSlotLimit(0), stack.getMaxStackSize());
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isItemValid.test(stack);
        }
    }
}
