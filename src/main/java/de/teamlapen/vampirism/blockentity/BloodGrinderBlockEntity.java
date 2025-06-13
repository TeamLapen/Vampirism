package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.blockentity.NetworkedBlockEntity;
import de.teamlapen.lib.lib.util.ControllableFluidTank;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.blocks.BloodGrinderBlock;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BloodGrinderBlockEntity extends NetworkedBlockEntity {

    public static final String KEY_INPUT_STACK = "InputStack";
    public static final String KEY_FILTER_STACK = "FilterStack";
    public static final String KEY_GRIND_COOLDOWN_TIME = "GrindCooldown";
    public static final String KEY_PULL_COOLDOWN_TIME = "PullCooldown";

    public static final int CAPACITY = FluidType.BUCKET_VOLUME * 2;
    public static final int GRIND_DELAY = 300;
    public static final int PULL_DELAY = 8;
    public static final AABB PULL_REACH_AABB = Block.box(5.0, 16.0, 5.0, 11.0, 22.0, 11.0).toAabbs().getFirst();

    public static final ModelProperty<Integer> FLUID_AMOUNT = new ModelProperty<>();

    private final IItemHandler inputItemHandler;
    private final IItemHandler filterItemHandler;

    public ControllableFluidTank fluidInventory;
    public ItemStack inputStack;
    public ItemStack filterStack;
    private int grindCooldownTime = -1;
    private int pullCooldownTime = -1;

    public BloodGrinderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BLOOD_GRINDER.get(), pos, blockState);
        this.inputItemHandler = new InputHandler(this);
        this.filterItemHandler = new FilterInputHandler(this);
        this.fluidInventory = new ControllableFluidTank(CAPACITY, fluid -> fluid.is(ModFluids.BLOOD)).setOnFluidChanged(fluid -> setChanged()).setAllowInput(false);
        this.inputStack = ItemStack.EMPTY;
        this.filterStack = ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BLOOD_GRINDER.get(), (blockEntity, side) -> {
            if (side == Direction.UP) return blockEntity.inputItemHandler;
            if (side != null && side.getAxis().isHorizontal()) return blockEntity.filterItemHandler;
            return null;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BLOOD_GRINDER.get(), (blockEntity, side) -> blockEntity.fluidInventory);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fluidInventory.readFromNBT(registries, tag);
        inputStack = ItemStack.parseOptional(registries, tag.getCompound(KEY_INPUT_STACK));
        filterStack = ItemStack.parseOptional(registries, tag.getCompound(KEY_FILTER_STACK));
        grindCooldownTime = tag.getInt(KEY_GRIND_COOLDOWN_TIME);
        pullCooldownTime = tag.getInt(KEY_PULL_COOLDOWN_TIME);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        fluidInventory.writeToNBT(registries, tag);
        tag.put(KEY_INPUT_STACK, inputStack.saveOptional(registries));
        tag.put(KEY_FILTER_STACK, filterStack.saveOptional(registries));
        tag.putInt(KEY_GRIND_COOLDOWN_TIME, grindCooldownTime);
        tag.putInt(KEY_PULL_COOLDOWN_TIME, pullCooldownTime);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(FLUID_AMOUNT, fluidInventory.getFluid().getAmount())
                .build();
    }

    @Override
    public void loadMetaData(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        fluidInventory.readFromNBT(lookupProvider, tag);
    }

    @Override
    public void saveMetaData(CompoundTag tag, HolderLookup.Provider registries) {
        fluidInventory.writeToNBT(registries, tag);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloodGrinderBlockEntity blockEntity) {
        trySuckingItems(level, pos, blockEntity);

        if (state.getValue(BloodGrinderBlock.POWERED)) {
            processGrinding(level, pos, blockEntity);
        } else {
            updateGrindState(level, pos, false);
        }

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
        ItemStack inputStack = blockEntity.inputStack;
        ItemStack filterStack = blockEntity.filterStack;
        ControllableFluidTank fluidInventory = blockEntity.fluidInventory;

        boolean canGrind = !inputStack.isEmpty() && isGrindable(inputStack);
        int blood = canGrind ? getBlood(inputStack) : 0;
        int space = fluidInventory.getSpace();

        if (!canGrind || (space < blood && !fluidInventory.isEmpty()) || filterStack.isEmpty() || !filterStack.isDamageableItem() || filterStack.getDamageValue() >= filterStack.getMaxDamage()) {
            updateGrindState(level, pos, false);
            blockEntity.grindCooldownTime = -1;
            return;
        }

        if (blockEntity.grindCooldownTime < 0) {
            blockEntity.grindCooldownTime = GRIND_DELAY;
            blockEntity.setChanged();
            updateGrindState(level, pos, true);
            return;
        }

        if (blockEntity.grindCooldownTime > 0) {
            blockEntity.grindCooldownTime--;
            updateGrindState(level, pos, true);
            return;
        }

        int numberAllowedByFilter = Math.min(filterStack.getMaxDamage() - filterStack.getDamageValue(), 4);
        int numberAllowedToGrind = Mth.clamp((space - space % blood) / blood, 1, numberAllowedByFilter);
        fluidInventory.doFill(new FluidStack(ModFluids.BLOOD, blood * numberAllowedToGrind), IFluidHandler.FluidAction.EXECUTE);
        inputStack.shrink(numberAllowedToGrind);

        filterStack.setDamageValue(filterStack.getDamageValue() + numberAllowedToGrind);
        if (filterStack.isBroken()) {
            blockEntity.filterStack = ItemStack.EMPTY;
        }
        blockEntity.updateFilterState(level, pos);

        level.playSound(null, pos, ModSounds.BLOOD_SQUEEZE.get(), SoundSource.BLOCKS, 0.5f + level.getRandom().nextFloat() / 4, 1.0f - level.getRandom().nextFloat() / 4);

        blockEntity.grindCooldownTime = -1;
        updateGrindState(level, pos, false);
        blockEntity.setChanged();
    }

    private static void updateGrindState(Level level, BlockPos pos, boolean isGrinding) {
        BlockState current = level.getBlockState(pos);
        if (current.getBlock() instanceof BloodGrinderBlock && current.getValue(BloodGrinderBlock.GRINDING) != isGrinding) {
            level.setBlock(pos, current.setValue(BloodGrinderBlock.GRINDING, isGrinding), Block.UPDATE_CLIENTS);
        }
    }

    public void updateFilterState(@Nullable Level level, BlockPos pos) {
        if (level == null) return;

        boolean hasFilter = !filterStack.isEmpty() && !filterStack.isBroken();
        BlockState current = level.getBlockState(pos);
        if (current.getBlock() instanceof BloodGrinderBlock && current.getValue(BloodGrinderBlock.HAS_FILTER) != hasFilter) {
            level.setBlock(pos, current.setValue(BloodGrinderBlock.HAS_FILTER, hasFilter), Block.UPDATE_CLIENTS);
        }
    }

    public static void pourBloodDown(Level level, BlockPos pos, BloodGrinderBlockEntity blockEntity) {
        FluidStack fluidStack = blockEntity.fluidInventory.getFluid();
        if (fluidStack.isEmpty()) return;

        int maxTransfer = Mth.clamp(50 * fluidStack.getAmount() * 2 / CAPACITY, 10, fluidStack.getAmount());

        FluidUtil.getFluidHandler(level, pos.below(), Direction.UP).ifPresent(belowFluidHandler -> {
            FluidStack simulatedStack = fluidStack.copyWithAmount(maxTransfer);
            int allowed = belowFluidHandler.fill(simulatedStack, IFluidHandler.FluidAction.SIMULATE);

            if (allowed > 0) {
                FluidStack toTransfer = fluidStack.copyWithAmount(allowed);
                int filled = belowFluidHandler.fill(toTransfer, IFluidHandler.FluidAction.EXECUTE);
                blockEntity.fluidInventory.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                blockEntity.setChanged();

                RandomSource random = level.getRandom();
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new DustColorTransitionOptions(0x750014, 0x46011a, 0.8f + random.nextFloat() / 2), pos.getX() + 0.5, pos.getY() - 0.1, pos.getZ() + 0.5, 4, 2.5 / 16d, 0.2 / 16d, 2.5 / 16d, 4);
                }
            }
        });
    }

    public static boolean isGrindable(ItemStack stack) {
        return VampirismAPI.bloodConversionRegistry().canBeConverted(stack);
    }

    public static int getBlood(ItemStack stack) {
        return VampirismAPI.bloodConversionRegistry().getItemBlood(stack).blood();
    }

    public static class InputHandler implements IItemHandler {

        private final BloodGrinderBlockEntity blockEntity;

        public InputHandler(BloodGrinderBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
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
            return isGrindable(stack);
        }
    }

    public static class FilterInputHandler implements IItemHandler {

        private final BloodGrinderBlockEntity blockEntity;

        public FilterInputHandler(BloodGrinderBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return blockEntity.filterStack;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty() || !stack.isDamageableItem()) return stack;

            ItemStack filterStack = blockEntity.filterStack;

            if (filterStack.isEmpty()) {
                if (!simulate) {
                    blockEntity.filterStack = stack.copyWithCount(1);
                    blockEntity.updateFilterState(blockEntity.level, blockEntity.worldPosition);
                    blockEntity.setChanged();
                }
                return stack.copyWithCount(stack.getCount() - 1);
            }

            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (blockEntity.filterStack.isEmpty() || amount <= 0) return ItemStack.EMPTY;

            ItemStack extracted = blockEntity.filterStack.copy();

            if (!simulate) {
                blockEntity.filterStack = ItemStack.EMPTY;
                blockEntity.updateFilterState(blockEntity.level, blockEntity.worldPosition);
                blockEntity.setChanged();
            }

            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModItems.FABRIC_FILTER);
        }
    }
}
