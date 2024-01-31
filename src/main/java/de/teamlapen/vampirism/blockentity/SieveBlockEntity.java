package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.util.FluidTankWithListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.datamaps.FluidBloodConversion;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.blocks.SieveBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

public class SieveBlockEntity extends BlockEntity implements FluidTankWithListener.IFluidTankListener {


    private final @NotNull FluidTankWithListener tank;
    private int cooldownPull = 0;
    private int cooldownProcess = 0;
    private boolean active;

    public SieveBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModTiles.SIEVE.get(), pos, state);
        tank = new FilteringFluidTank(2 * FluidType.BUCKET_VOLUME).setListener(this);
        tank.setDrainable(false);
    }

    public @NotNull FluidTankWithListener getTank() {
        return tank;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("active", isActive());
        return nbt;
    }


    public boolean isActive() {
        return active;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        tank.readFromNBT(tag);
        cooldownProcess = tag.getInt("cooldown_process");
        cooldownPull = tag.getInt("cooldown_pull");
    }

    @Override
    public void onDataPacket(Connection net, @NotNull ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            boolean old = active;
            active = tag.getBoolean("active");
            if (active != old && level != null) {
                this.requestModelDataUpdate();
                this.level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            }
        }
    }

    @Override
    public void onTankContentChanged() {
        this.setActive(!tank.isEmpty(), getBlockState());
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        tank.writeToNBT(compound);
        compound.putInt("cooldown_process", this.cooldownProcess);
        compound.putInt("cooldown_pull", this.cooldownPull);
    }

    public static void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull SieveBlockEntity blockEntity) {
        //Process content
        if (--blockEntity.cooldownProcess < 0) {
            blockEntity.cooldownProcess = 15;
            if (blockEntity.tank.getFluidAmount() > 0) {
                FluidUtil.getFluidHandler(level, pos.below(), Direction.UP).ifPresent(handler -> {
                    blockEntity.tank.setDrainable(true);
                    FluidStack transferred = FluidUtil.tryFluidTransfer(handler, blockEntity.tank, 2 * VReference.FOOD_TO_FLUID_BLOOD, true);
                    blockEntity.tank.setDrainable(false);
                    if (!transferred.isEmpty()) {
                        blockEntity.cooldownProcess = 30;
                        blockEntity.setActive(true, state);
                    }
                });
            } else if (blockEntity.active) {
                blockEntity.setActive(false, state);
            }
        }
        //Pull new content. Cooldown is increased when liquid is filled into the tank (regardless of way)
        if (--blockEntity.cooldownPull < 0) {
            blockEntity.cooldownPull = 10;
            FluidUtil.getFluidHandler(level, pos.above(), Direction.DOWN).ifPresent(handler -> {
                FluidStack transferred = FluidUtil.tryFluidTransfer(blockEntity.tank, handler, 2 * VReference.FOOD_TO_FLUID_BLOOD, true);
            });
        }

    }

    private void setActive(boolean active, @NotNull BlockState blockState) {
        if (this.active != active) {
            this.active = active;
            if (this.level != null && blockState.getBlock() == ModBlocks.BLOOD_SIEVE.get()) {
                this.level.setBlockAndUpdate(getBlockPos(), blockState.setValue(SieveBlock.PROPERTY_ACTIVE, active));
            }
        }
    }

    private class FilteringFluidTank extends FluidTankWithListener {

        private FilteringFluidTank(int capacity) {
            super(capacity);
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            if (!BloodConversionRegistry.hasConversion(resource.getFluid())) {
                return 0;
            }
            FluidStack converted = BloodConversionRegistry.getBloodFromFluid(resource);
            int filled = super.fill(converted, action);
            if (action.execute()) SieveBlockEntity.this.cooldownPull = 10;
            return (int) (filled / BloodConversionRegistry.getBloodValue(resource));
        }
    }
}
