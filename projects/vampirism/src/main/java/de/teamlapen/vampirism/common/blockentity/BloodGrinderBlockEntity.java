package de.teamlapen.vampirism.common.blockentity;

import de.teamlapen.factions.common.blockentity.MultipleItemHandler;
import de.teamlapen.factions.common.blockentity.NetworkedBlockEntity;
import de.teamlapen.vampirism.common.fluids.ControllableFluidTank;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.common.blocks.BloodGrinderBlock;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodGrinderBlockEntity extends NetworkedBlockEntity {

    public static final String KEY_INPUT_STACK = "InputStack";
    public static final String KEY_FILTER_STACK = "FilterStack";
    public static final String KEY_GRIND_COOLDOWN_TIME = "GrindCooldown";
    public static final String KEY_PULL_COOLDOWN_TIME = "PullCooldown";

    public static final int CAPACITY = FluidType.BUCKET_VOLUME * 2;
    public static final int GRIND_DELAY = 400;
    public static final int PULL_DELAY = 8;
    public static final AABB PULL_REACH_AABB = Block.box(5.0, 16.0, 5.0, 11.0, 22.0, 11.0).toAabbs().getFirst();

    public final MultipleItemHandler<@NotNull BloodGrinderBlockEntity> itemHandler;

    public ControllableFluidTank fluidInventory;
    public ItemStack inputStack;
    public ItemStack filterStack;
    private int grindCooldownTime = -1;
    private int pullCooldownTime = -1;

    public BloodGrinderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BLOOD_GRINDER.get(), pos, blockState);
        this.itemHandler = new MultipleItemHandler<>(this,
                () -> {
                    updateFilterState(level, worldPosition);
                    setChanged();
                },
                new MultipleItemHandler.SlotProperties<>(blockEntity -> blockEntity.inputStack, (blockEntity, stack) -> blockEntity.inputStack = stack, BloodGrinderBlockEntity::isGrindable, Item.ABSOLUTE_MAX_STACK_SIZE),
                new MultipleItemHandler.SlotProperties<>(blockEntity -> blockEntity.filterStack, (blockEntity, stack) -> blockEntity.filterStack = stack, BloodGrinderBlockEntity::isFilter, 1)
        );
        this.fluidInventory = new ControllableFluidTank(CAPACITY, this::setChanged, fluid -> fluid.is(ModFluids.BLOOD), false, true);
        this.inputStack = ItemStack.EMPTY;
        this.filterStack = ItemStack.EMPTY;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fluidInventory.deserialize(input.childOrEmpty("fluid"));
        inputStack = input.read(KEY_INPUT_STACK, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        filterStack = input.read(KEY_FILTER_STACK, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        grindCooldownTime = input.getIntOr(KEY_GRIND_COOLDOWN_TIME, 0);
        pullCooldownTime = input.getIntOr(KEY_PULL_COOLDOWN_TIME, 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        fluidInventory.serialize(output.child("fluid"));
        output.store(KEY_INPUT_STACK, ItemStack.OPTIONAL_CODEC, inputStack);
        output.store(KEY_FILTER_STACK, ItemStack.OPTIONAL_CODEC, filterStack);
        output.putInt(KEY_GRIND_COOLDOWN_TIME, grindCooldownTime);
        output.putInt(KEY_PULL_COOLDOWN_TIME, pullCooldownTime);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloodGrinderBlockEntity blockEntity) {
        trySuckingItems(level, pos, blockEntity);

        if (state.getValue(BloodGrinderBlock.ENABLED)) {
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

        if (level.getBlockState(pos.above()).isFaceSturdy(level, pos, Direction.DOWN, SupportType.CENTER)) {
            blockEntity.pullCooldownTime = PULL_DELAY;
            blockEntity.setChanged();
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
        int space = fluidInventory.getCapacity(FluidResource.of(ModFluids.BLOOD)) - fluidInventory.getAmount();

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

        // The durability of filters is multiplied by 100 for parity with the blood sieve, so it must be considered
        int numberAllowedByFilter = Mth.clamp((int) Math.ceil((double) (filterStack.getMaxDamage() - filterStack.getDamageValue()) / 100), 0, 4);
        int numberAllowedToGrind = Mth.clamp((space - space % blood) / blood, 1, numberAllowedByFilter);
        try (var access = fluidInventory.beginAccess()) {
            int added = ResourceHandlerUtil.insertStacking(fluidInventory, FluidResource.of(ModFluids.BLOOD), blood * numberAllowedToGrind, null);
            inputStack.shrink(numberAllowedToGrind);
        }

        filterStack.setDamageValue(filterStack.getDamageValue() + numberAllowedToGrind * 100);
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
        updateFilterState(level, pos, BloodGrinderBlock.HAS_FILTER, !filterStack.isEmpty() && !filterStack.isBroken());
    }

    public static void updateFilterState(@Nullable Level level, BlockPos pos, BooleanProperty property, boolean hasFilter) {
        if (level == null) return;

        BlockState current = level.getBlockState(pos);
        if (current.hasProperty(property) && current.getValue(property) != hasFilter) {
            level.setBlock(pos, current.setValue(property, hasFilter), Block.UPDATE_CLIENTS);
        }
    }

    public static void pourBloodDown(Level level, BlockPos pos, BloodGrinderBlockEntity blockEntity) {
        try (var access = blockEntity.fluidInventory.beginAccess()) {
            var transfer = ResourceHandlerUtil.move(blockEntity.fluidInventory, level.getCapability(Capabilities.Fluid.BLOCK, pos.below(), Direction.UP), x -> x.is(ModFluids.BLOOD), 50, null);
            if (transfer > 0) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new DustColorTransitionOptions(0x750014, 0x46011a, 0.8f + level.getRandom().nextFloat() / 2), pos.getX() + 0.5, pos.getY() - 0.1, pos.getZ() + 0.5, 4, 2.5 / 16d, 0.2 / 16d, 2.5 / 16d, 4);
                }
            }
        }
    }

    public static boolean isGrindable(ItemResource resource) {
        return VampirismAPI.bloodConversionRegistry().canBeConverted(resource);
    }

    public static boolean isGrindable(ItemStack stack) {
        return VampirismAPI.bloodConversionRegistry().canBeConverted(stack);
    }

    public static int getBlood(ItemStack stack) {
        return VampirismAPI.bloodConversionRegistry().getItemBlood(stack).blood();
    }

    public static boolean isFilter(ItemResource resource) {
        return resource.is(ModItems.FABRIC_FILTER.get());
    }

    public static boolean isFilter(ItemStack resource) {
        return resource.is(ModItems.FABRIC_FILTER.get());
    }
}
