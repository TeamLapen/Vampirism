package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.blocks.BloodGrinderBlock;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import net.minecraft.world.level.block.entity.BlockEntity;
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
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class BloodGrinderBlockEntity extends BlockEntity {

    public static final String KEY_INPUT_STACK = "InputStack";
    public static final String KEY_GRIND_COOLDOWN_TIME = "GrindCooldown";
    public static final String KEY_PULL_COOLDOWN_TIME = "PullCooldown";

    public static final int CAPACITY = FluidType.BUCKET_VOLUME * 2;
    public static final int GRIND_DELAY = 300;
    public static final int PULL_DELAY = 8;
    public static final AABB PULL_REACH_AABB = Block.box(5.0, 16.0, 5.0, 11.0, 22.0, 11.0).toAabbs().getFirst();

    public static final ModelProperty<FluidStack> FLUID = new ModelProperty<>();
    public static final ModelProperty<ItemStack> STACK_INSIDE = new ModelProperty<>();
    public static final ModelProperty<Integer> GRIND_COOLDOWN = new ModelProperty<>();

    private final IItemHandler inputItemHandler;

    public FluidTank fluidInventory;
    public ItemStack inputStack;
    private int grindCooldownTime = -1;
    private int pullCooldownTime = -1;

    public BloodGrinderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BLOOD_GRINDER.get(), pos, blockState);
        this.inputItemHandler = new InputHandler(this, BloodGrinderBlockEntity::isGrindable);
        this.fluidInventory = new FluidTank(CAPACITY).setValidator(fluid -> fluid.is(ModFluids.BLOOD));
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
        FluidTank fluidInventory = blockEntity.fluidInventory;

        boolean canGrind = !inputStack.isEmpty() && isGrindable(inputStack);
        int blood = canGrind ? getBlood(inputStack) : 0;
        int space = fluidInventory.getSpace();

        if (!canGrind || (space < blood && !fluidInventory.isEmpty())) {
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

        int numberAllowedToGrind = Mth.clamp((space - space % blood) / blood, 1, 4);
        fluidInventory.fill(new FluidStack(ModFluids.BLOOD, blood * numberAllowedToGrind), IFluidHandler.FluidAction.EXECUTE);
        inputStack.shrink(numberAllowedToGrind);

        level.playSound(null, pos, ModSounds.BLOOD_SQUEEZE.get(), SoundSource.BLOCKS, 0.5f + level.getRandom().nextFloat() / 4, 1.0f - level.getRandom().nextFloat() / 4);

        blockEntity.grindCooldownTime = -1;
        updateGrindState(level, pos, false);
        blockEntity.setChanged();

        /*
        RandomSource random = level.getRandom();

        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; i++) {
                double x = pos.getX() + random.nextInt(4, 14) / 16d;
                double z = random.nextInt(4, 14) / 16d;

                double xSpeed = random.nextDouble() / 2;
                double zSpeed = random.nextDouble() / 2;
                double ySpeed = random.nextDouble() * 2;
                serverLevel.sendParticles(new DustColorTransitionOptions(0x750014, 0x46011a, 0.8f + random.nextFloat() / 2), x, 0.0d, z, xSpeed, ySpeed, zSpeed);
                serverLevel.sendParticles(null, new DustColorTransitionOptions(), false, false, x, y, z, 1, 8 / 16d, 8 / 16d, 8 / 16d, 10);
            }
        }
         */
    }

    private static void updateGrindState(Level level, BlockPos pos, boolean isGrinding) {
        BlockState current = level.getBlockState(pos);
        if (current.getBlock() instanceof BloodGrinderBlock && current.getValue(BloodGrinderBlock.GRINDING) != isGrinding) {
            level.setBlock(pos, current.setValue(BloodGrinderBlock.GRINDING, isGrinding), Block.UPDATE_CLIENTS);
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

    public static void clientTick(Level level, BlockPos pos, BlockState state, BloodGrinderBlockEntity blockEntity) {
        //spawnGrindingParticles(level, pos, blockEntity);
    }

    public static void spawnGrindingParticles(Level level, BlockPos pos, BloodGrinderBlockEntity blockEntity) {
        ModelData modelData = blockEntity.getModelData();
        //FluidStack fluidStack = modelData.get(BloodGrinderBlockEntity.FLUID);
        ItemStack itemStack = modelData.get(BloodGrinderBlockEntity.STACK_INSIDE);
        Integer grindCooldown = modelData.get(BloodGrinderBlockEntity.GRIND_COOLDOWN);

        if (!level.isClientSide || itemStack == null || itemStack.isEmpty() || grindCooldown == null || grindCooldown < 0) return;

        RandomSource random = level.getRandom();

        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < 3; i++) {
            double y = pos.getY() + random.nextInt(4, 12) / 16d;

            double min = 0.0;
            double max = 1.0;

            double x = 0;
            double z = 0;

            int side = random.nextInt(4);
            double offset = random.nextDouble();

            switch (side) {
                // North
                case 0 -> {
                    x = offset;
                    z = min;
                }
                // South
                case 1 -> {
                    x = offset;
                    z = max;
                }
                // West
                case 2 -> {
                    x = min;
                    z = offset;
                }
                // East
                case 3 -> {
                    x = max;
                    z = offset;
                }
            }

            x += pos.getX();
            z += pos.getZ();

            double xSpeed = (x - centerX);
            double zSpeed = (z - centerZ);
            double ySpeed = -random.nextDouble();

            //level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), x, y, z, xSpeed, ySpeed, zSpeed);
            level.addParticle(new DustColorTransitionOptions(0x750014, 0x46011a, 0.8f + random.nextFloat() / 2), x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    public static boolean isGrindable(ItemStack stack) {
        return VampirismAPI.bloodConversionRegistry().canBeConverted(stack);
    }

    public static int getBlood(ItemStack stack) {
        return VampirismAPI.bloodConversionRegistry().getItemBlood(stack).blood();
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(FLUID, fluidInventory.getFluid())
                .with(STACK_INSIDE, inputStack)
                .with(GRIND_COOLDOWN, grindCooldownTime)
                .build();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            if (level.isClientSide) {
                this.requestModelDataUpdate();
            } else {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        fluidInventory.writeToNBT(registries, tag);
        tag.put(KEY_INPUT_STACK, inputStack.saveOptional(registries));
        tag.putInt(KEY_GRIND_COOLDOWN_TIME, grindCooldownTime);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        fluidInventory.readFromNBT(lookupProvider, pkt.getTag());
        inputStack = ItemStack.parseOptional(lookupProvider, pkt.getTag().getCompound(KEY_INPUT_STACK));
        grindCooldownTime = pkt.getTag().getInt(KEY_GRIND_COOLDOWN_TIME);
        setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
