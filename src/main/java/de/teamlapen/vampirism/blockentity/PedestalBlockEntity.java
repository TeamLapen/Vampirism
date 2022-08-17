package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IBloodChargeable;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PedestalBlockEntity extends BlockEntity implements IItemHandler {

    private final Random rand = new Random();
    private final LazyOptional<IItemHandler> opt = LazyOptional.of(() -> this);
    private final int chargeRate = 30;
    private int ticksExistedClient;
    /**
     * If larger zero: Charging
     * If zero: Ready to restart
     * If below zero: Check cooldown
     */
    private int chargingTicks;
    private int bloodStored = 0;
    @NotNull
    private ItemStack internalStack;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.BLOOD_PEDESTAL.get(), pos, state);
        this.internalStack = ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = this.internalStack;
        if (slot == 0 && !stack.isEmpty()) {
            if (!simulate) {
                this.removeStack();
                this.markDirtyAndUpdateClient();
            }
            return simulate ? stack.copy() : stack;
        }
        return ItemStack.EMPTY;
    }


    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing != Direction.DOWN)) {
            return opt.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @NotNull
    public ItemStack getStackForRender() {
        return internalStack;
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? internalStack : ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    public int getTickForRender() {
        return ticksExistedClient;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public boolean hasStack() {
        return !this.internalStack.isEmpty();
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot == 0) {
            if (this.internalStack.isEmpty()) {
                if (!simulate) {
                    setStack(stack);
                    this.markDirtyAndUpdateClient();
                }
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (compound.contains("item")) {
            this.internalStack = ItemStack.of(compound.getCompound("item"));
        } else {
            this.internalStack = ItemStack.EMPTY;
        }
        this.bloodStored = compound.getInt("blood_stored");
        this.chargingTicks = compound.getInt("charging_ticks");
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (hasLevel()) handleUpdateTag(pkt.getTag());
    }

    @NotNull
    public ItemStack removeStack() {
        ItemStack stack = this.internalStack;
        this.internalStack = ItemStack.EMPTY;
        return stack;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        if (hasStack()) {
            compound.put("item", this.internalStack.serializeNBT());
        }
        compound.putInt("blood_stored", bloodStored);
        compound.putInt("charging_ticks", chargingTicks);
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
        FluidUtil.getFluidHandler(this.level, this.worldPosition.below(), Direction.UP).ifPresent(handler -> {
            FluidStack drained = handler.drain(new FluidStack(ModFluids.BLOOD.get(), VReference.FOOD_TO_FLUID_BLOOD), IFluidHandler.FluidAction.SIMULATE);
            if (!drained.isEmpty() && drained.getAmount() == VReference.FOOD_TO_FLUID_BLOOD) {
                drained = handler.drain(new FluidStack(ModFluids.BLOOD.get(), VReference.FOOD_TO_FLUID_BLOOD), IFluidHandler.FluidAction.EXECUTE);
                bloodStored += drained.getAmount();
            }
        });
    }

    /**
     * Tries to retrieve a {@link IBloodChargeable} instance from the given stack
     *
     * @return May be null
     */
    @Nullable
    private static IBloodChargeable getChargeItem(@NotNull ItemStack stack) {
        return stack.isEmpty() ? null : (stack.getItem() instanceof IBloodChargeable chargeable ? chargeable : null);
    }

    private void markDirtyAndUpdateClient() {
        if (level != null) {
            super.setChanged();
            BlockState block = this.level.getBlockState(this.worldPosition);
            level.sendBlockUpdated(worldPosition, block, block, 3);
        }
    }

    /**
     * Set the held stack.
     */
    private void setStack(@NotNull ItemStack stack) {
        this.chargingTicks = 0;
        if (this.internalStack.isEmpty()) {
            this.internalStack = stack;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void spawnChargedParticle(Level level, BlockPos blockPos, Random rand) {
        Vec3 pos = Vec3.upFromBottomCenterOf(blockPos, 0.8);
        ModParticles.spawnParticleClient(level, new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), true, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1, new ResourceLocation("minecraft", "glitter_1")), blockPos.getX() + 0.20, blockPos.getY() + 0.65, blockPos.getZ() + 0.20);
        ModParticles.spawnParticleClient(level, new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), true, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1, new ResourceLocation("minecraft", "glitter_1")), blockPos.getX() + 0.80, blockPos.getY() + 0.65, blockPos.getZ() + 0.20);
        ModParticles.spawnParticleClient(level, new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), (int) (4.0F / (rand.nextFloat() * 0.9F + 0.1F)), true, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1, new ResourceLocation("minecraft", "glitter_1")), blockPos.getX() + 0.20, blockPos.getY() + 0.65, blockPos.getZ() + 0.80);
        ModParticles.spawnParticleClient(level, new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), (int) (3.0F / (rand.nextFloat() * 0.6F + 0.4F)), true, pos.x + (1f - rand.nextFloat()) * 0.1, pos.y + (1f - rand.nextFloat()) * 0.2, pos.z + (1f - rand.nextFloat()) * 0.1, new ResourceLocation("minecraft", "glitter_1")), blockPos.getX() + 0.80, blockPos.getY() + 0.65, blockPos.getZ() + 0.80);

    }
}
