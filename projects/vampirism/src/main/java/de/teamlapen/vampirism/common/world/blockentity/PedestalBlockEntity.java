package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.faction.common.world.blockentity.NetworkedBlockEntity;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.world.items.IBloodChargeable;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModParticles;
import de.teamlapen.vampirism.common.particles.BloodShredParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PedestalBlockEntity extends NetworkedBlockEntity {

    private final Random rand = new Random();
    private final int chargeRate = 30;
    private int ticksExistedClient;
    /**
     * If larger zero: Charging
     * If zero: Ready to restart
     * If below zero: Check cooldown
     */
    private int chargingTicks;
    private int bloodStored = 0;
    private ItemStack internalStack;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOD_PEDESTAL.get(), pos, state);
        this.internalStack = ItemStack.EMPTY;
    }

    public ItemStack getStackForRender() {
        return internalStack;
    }

    public int getTickForRender() {
        return ticksExistedClient;
    }

    public boolean hasStack() {
        return !this.internalStack.isEmpty();
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.internalStack = input.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.bloodStored = input.getIntOr("blood_stored", 0);
        this.chargingTicks = input.getIntOr("charging_ticks", 0);
    }

    public ItemStack removeStack() {
        ItemStack stack = this.internalStack;
        this.internalStack = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (hasStack()) {
            output.store("item", ItemStack.CODEC, this.internalStack);
        }
        output.putInt("blood_stored", bloodStored);
        output.putInt("charging_ticks", chargingTicks);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PedestalBlockEntity blockEntity) {
        if (blockEntity.chargingTicks > 0) {
            blockEntity.chargingTicks--;
            if (blockEntity.chargingTicks == 0) {
                IBloodChargeable chargeable = getChargeItem(blockEntity.internalStack);
                if (chargeable != null) {
                    if (blockEntity.bloodStored > 0) {
                        int charged = chargeable.charge(blockEntity.internalStack, blockEntity.bloodStored);
                        blockEntity.bloodStored -= Math.max(0, charged);
                    }
                }
                blockEntity.markDirtyAndUpdateClient();
            }
        } else if (blockEntity.chargingTicks == 0) {
            IBloodChargeable chargeable = getChargeItem(blockEntity.internalStack);
            if (chargeable != null && chargeable.canBeCharged(blockEntity.internalStack)) {
                if (blockEntity.bloodStored < blockEntity.chargeRate) {
                    blockEntity.drainBlood();
                }
                if (blockEntity.bloodStored > 0) {
                    blockEntity.chargingTicks = 20;
                    blockEntity.markDirtyAndUpdateClient();
                } else {
                    blockEntity.chargingTicks = -40;
                }
            } else {
                blockEntity.chargingTicks = -40;
            }
        } else {
            blockEntity.chargingTicks++;
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, PedestalBlockEntity blockEntity) {
        blockEntity.ticksExistedClient++;
        if (blockEntity.chargingTicks > 0 && blockEntity.ticksExistedClient % 8 == 0) {
            spawnChargedParticle(level, pos, blockEntity.rand);
        }
    }

    private void drainBlood() {
        if (level == null) return;
        try (var transaction = Transaction.openRoot()) {
            var extracted = ResourceHandlerUtil.extractFirst(this.level.getCapability(Capabilities.Fluid.BLOCK, this.worldPosition.below(), Direction.UP), x -> x.is(ModFluids.BLOOD), VReference.FOOD_TO_FLUID_BLOOD, transaction);
            if (extracted != null) {
                bloodStored += extracted.amount();
                transaction.commit();
            }
        }
    }

    public int getChargedProgress() {
        IBloodChargeable chargeItem = getChargeItem(this.internalStack);
        if (chargeItem != null) {
            return (int) (chargeItem.getChargePercentage(this.internalStack) * 10);
        } else {
            return 0;
        }
    }

    /**
     * Tries to retrieve a {@link IBloodChargeable} instance from the given stack
     *
     * @return May be null
     */
    @Nullable
    private static IBloodChargeable getChargeItem(ItemStack stack) {
        return stack.isEmpty() ? null : (stack.getItem() instanceof IBloodChargeable chargeable ? chargeable : null);
    }

    private void markDirtyAndUpdateClient() {
        if (level != null) {
            super.setChanged();
            BlockState block = this.level.getBlockState(this.worldPosition);
            level.sendBlockUpdated(worldPosition, block, block, 3);
        }
    }

    private static void spawnChargedParticle(Level level, BlockPos blockPos, Random rand) {
        Vec3 pos = Vec3.upFromBottomCenterOf(blockPos, 0.8);
        ModParticles.spawnParticleClient(level, new BloodShredParticleOptions(new Vec3(pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1), (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), false, BloodShredParticleOptions.DEFAULT_COLOR, 0.8f), blockPos.getX() + 0.20, blockPos.getY() + 0.65, blockPos.getZ() + 0.20);
        ModParticles.spawnParticleClient(level, new BloodShredParticleOptions(new Vec3(pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1), (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), false, BloodShredParticleOptions.DEFAULT_COLOR, 0.8f), blockPos.getX() + 0.80, blockPos.getY() + 0.65, blockPos.getZ() + 0.20);
        ModParticles.spawnParticleClient(level, new BloodShredParticleOptions(new Vec3(pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1), (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), false, BloodShredParticleOptions.DEFAULT_COLOR, 0.8f), blockPos.getX() + 0.20, blockPos.getY() + 0.65, blockPos.getZ() + 0.80);
        ModParticles.spawnParticleClient(level, new BloodShredParticleOptions(new Vec3(pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1), (int) (3.0F / (rand.nextFloat() * 0.6F + 0.4F)), false, BloodShredParticleOptions.DEFAULT_COLOR, 0.8f), blockPos.getX() + 0.80, blockPos.getY() + 0.65, blockPos.getZ() + 0.80);
    }

    public class ItemWrapper extends SnapshotJournal<ItemStack> implements ResourceHandler<ItemResource> {

        @Override
        public int size() {
            return 1;
        }

        @Override
        public ItemResource getResource(int index) {
            return ItemResource.of(internalStack);
        }

        @Override
        public long getAmountAsLong(int index) {
            return internalStack.getCount();
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return 1;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return index == 0 && resource.getMaxStackSize() == 1;
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (!internalStack.isEmpty() || amount < 1) {
                return 0;
            }

            updateSnapshots(transaction);
            internalStack = resource.toStack();

            return 1;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (!internalStack.isEmpty() && amount < 1) {
                return 0;
            }

            if (resource.matches(internalStack)) {
                updateSnapshots(transaction);
                internalStack = ItemStack.EMPTY;
                return 1;
            }

            return 0;
        }

        @Override
        protected ItemStack createSnapshot() {
            return internalStack;
        }

        @Override
        protected void revertToSnapshot(@Nullable ItemStack snapshot) {
            internalStack = snapshot == null ? ItemStack.EMPTY : snapshot;
        }

        @Override
        protected void onRootCommit(@Nullable ItemStack originalState) {
            super.onRootCommit(originalState);
            markDirtyAndUpdateClient();
        }
    }
}
