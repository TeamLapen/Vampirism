package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.util.SingleItemHandler;
import de.teamlapen.lib.lib.util.ControllableFluidTank;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.blocks.BloodSieveBlock;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.util.BloodHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class BloodSieveBlockEntity extends BlockEntity {

    public static final String KEY_INPUT_FLUID = "InputFluid";
    public static final String KEY_OUTPUT_FLUID = "OutputFluid";
    public static final String KEY_FILTER_STACK = "FilterStack";
    public static final String KEY_PULL_COOLDOWN_TIME = "PullCooldown";
    public static final String KEY_FILTER_COOLDOWN_TIME = "FilterCooldown";

    public static final int CAPACITY = FluidType.BUCKET_VOLUME; // Applied for both tanks
    public static final int FILTER_DELAY = 4;
    public static final int PULL_DELAY = 4;

    public final IItemHandler filterItemHandler;

    public final ControllableFluidTank inputFluidInventory;
    public final ControllableFluidTank outputFluidInventory;
    public ItemStack filterStack;
    private int filterCooldownTime = -1;
    private int pullCooldownTime = -1;

    public BloodSieveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOD_SIEVE.get(), pos, state);
        this.filterItemHandler = new SingleItemHandler<>(this, blockEntity -> blockEntity.filterStack, (blockEntity, stack) -> blockEntity.filterStack = stack, BloodGrinderBlockEntity::isFilter, 1, () -> {
            updateFilterState(level, worldPosition);
            setChanged();
        });
        this.inputFluidInventory = new ControllableFluidTank(CAPACITY, BloodHelper::isConvertibleToBlood).setOnFluidChanged(fluid -> setChanged()).setSaveKey(KEY_INPUT_FLUID).setAllowOutput(false);
        this.outputFluidInventory = new ControllableFluidTank(CAPACITY, fluid -> fluid.is(ModFluids.BLOOD)).setOnFluidChanged(fluid -> setChanged()).setSaveKey(KEY_OUTPUT_FLUID).setAllowInput(false);
        this.filterStack = ItemStack.EMPTY;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inputFluidInventory.readFromNBT(registries, tag);
        outputFluidInventory.readFromNBT(registries, tag);
        filterStack = ItemStack.parseOptional(registries, tag.getCompound(KEY_FILTER_STACK));
        filterCooldownTime = tag.getInt(KEY_FILTER_COOLDOWN_TIME);
        pullCooldownTime = tag.getInt(KEY_PULL_COOLDOWN_TIME);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        inputFluidInventory.writeToNBT(registries, tag);
        outputFluidInventory.writeToNBT(registries, tag);
        tag.put(KEY_FILTER_STACK, filterStack.saveOptional(registries));
        tag.putInt(KEY_FILTER_COOLDOWN_TIME, filterCooldownTime);
        tag.putInt(KEY_PULL_COOLDOWN_TIME, pullCooldownTime);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloodSieveBlockEntity blockEntity) {
        tryPullingBlood(level, pos, blockEntity);
        filterBlood(blockEntity);
    }

    public static void tryPullingBlood(Level level, BlockPos pos, BloodSieveBlockEntity blockEntity) {
        if (blockEntity.pullCooldownTime > 0) {
            blockEntity.pullCooldownTime--;
            return;
        }

        boolean[] needsChange = new boolean[] { false };

        FluidUtil.getFluidHandler(level, pos.above(), Direction.DOWN).ifPresent(fluidHandler -> {
            FluidStack moved = FluidUtil.tryFluidTransfer(blockEntity.inputFluidInventory, fluidHandler, 100, true);
            needsChange[0] = !moved.isEmpty();
        });

        FluidUtil.getFluidHandler(level, pos.below(), Direction.UP).ifPresent(fluidHandler -> {
            FluidStack moved = FluidUtil.tryFluidTransfer(fluidHandler, blockEntity.outputFluidInventory, 100, true);
            needsChange[0] = needsChange[0] || !moved.isEmpty();
        });

        blockEntity.pullCooldownTime = PULL_DELAY;
        if (needsChange[0]) blockEntity.setChanged();
    }

    public static void filterBlood(BloodSieveBlockEntity blockEntity) {
        if (blockEntity.filterCooldownTime > 0) {
            blockEntity.filterCooldownTime--;
            return;
        }

        if (!blockEntity.inputFluidInventory.isEmpty()) {
            FluidStack inputFluid = blockEntity.inputFluidInventory.getFluid();
            IFluidBloodConversion bloodConversion = BloodHelper.getBloodConversion(inputFluid);
            float conversionRate = bloodConversion.conversionRate();

            ItemStack filterStack = blockEntity.filterStack;
            int durabilityLeft = filterStack.isDamageableItem() ? (filterStack.getMaxDamage() - filterStack.getDamageValue()) : 0;

            int inputAmount = Math.min(50, inputFluid.getAmount());
            int potentialResult = (int) (inputAmount * conversionRate);

            FluidStack resource1 = new FluidStack(ModFluids.BLOOD.get(), potentialResult);
            int allowedByTank = super.fill(resource1, IFluidHandler.FluidAction.SIMULATE);
            int allowed = Math.min(allowedByTank, durabilityLeft);

            if (allowed > 0) {
                int toDrain = (int) Math.ceil(allowed / conversionRate);
                FluidStack drained = super.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);

                if (!drained.isEmpty()) {
                    int resultBlood = (int) (drained.getAmount() * conversionRate);
                    FluidStack resource = new FluidStack(ModFluids.BLOOD.get(), resultBlood);
                    super.fill(resource, IFluidHandler.FluidAction.EXECUTE);

                    blockEntity.filterStack.setDamageValue(filterStack.getDamageValue() + drained.getAmount());
                    if (blockEntity.filterStack.isBroken()) {
                        blockEntity.filterStack = ItemStack.EMPTY;
                    }
                    blockEntity.updateFilterState(blockEntity.level, blockEntity.worldPosition);

                    blockEntity.setChanged();
                }
            }
        }

        blockEntity.filterCooldownTime = FILTER_DELAY;
    }

    public void updateFilterState(@Nullable Level level, BlockPos pos) {
        BloodGrinderBlockEntity.updateFilterState(level, pos, BloodSieveBlock.HAS_FILTER, !filterStack.isEmpty() && !filterStack.isBroken());
    }
}
