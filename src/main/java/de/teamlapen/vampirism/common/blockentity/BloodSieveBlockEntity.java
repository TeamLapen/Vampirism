package de.teamlapen.vampirism.common.blockentity;

import de.teamlapen.lib.common.blockentities.SingleItemHandler;
import de.teamlapen.lib.common.fluids.ControllableFluidTank;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.common.blocks.BloodSieveBlock;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.util.BloodHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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

    public final ResourceHandler<ItemResource> filterItemHandler;

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
        this.inputFluidInventory = new ControllableFluidTank(CAPACITY, this::setChanged, BloodHelper::isConvertibleToBlood, true, false);
        this.outputFluidInventory = new ControllableFluidTank(CAPACITY, this::setChanged, x -> x.is(ModFluids.BLOOD), false, true);
        this.filterStack = ItemStack.EMPTY;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.inputFluidInventory.deserialize(input.childOrEmpty("input_fluid"));
        this.outputFluidInventory.deserialize(input.childOrEmpty("output_fluid"));
        this.filterStack = input.read(KEY_FILTER_STACK, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.filterCooldownTime = input.getIntOr(KEY_FILTER_COOLDOWN_TIME, -1);
        this.pullCooldownTime = input.getIntOr(KEY_PULL_COOLDOWN_TIME, -1);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.inputFluidInventory.serialize(output.child("input_fluid"));
        this.outputFluidInventory.serialize(output.child("output_fluid"));
        output.store(KEY_FILTER_STACK, ItemStack.OPTIONAL_CODEC, this.filterStack);
        output.putInt(KEY_FILTER_COOLDOWN_TIME, this.filterCooldownTime);
        output.putInt(KEY_PULL_COOLDOWN_TIME, this.pullCooldownTime);
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

        boolean needsChange;

        try (var transaction = Transaction.openRoot()) {
            var moved = ResourceHandlerUtil.move(level.getCapability(Capabilities.Fluid.BLOCK, pos.above(), Direction.DOWN), blockEntity.inputFluidInventory, BloodHelper::isConvertibleToBlood, 100, transaction);
            needsChange = moved != 0;
            transaction.commit();
        }

        try (var transaction = Transaction.openRoot()) {
            var moved = ResourceHandlerUtil.move(blockEntity.outputFluidInventory, level.getCapability(Capabilities.Fluid.BLOCK, pos.below(), Direction.UP), BloodHelper::isConvertibleToBlood, 100, transaction);
            needsChange = moved != 0 || needsChange;
            transaction.commit();
        }

        blockEntity.pullCooldownTime = PULL_DELAY;
        if (needsChange) blockEntity.setChanged();
    }

    public static void filterBlood(BloodSieveBlockEntity blockEntity) {
        if (blockEntity.filterCooldownTime > 0) {
            blockEntity.filterCooldownTime--;
            return;
        }

        if (!blockEntity.inputFluidInventory.isEmpty()) {
            try (var transaction = Transaction.openRoot()) {

                int extracted;
                float conversionRate;
                try (var access = blockEntity.inputFluidInventory.beginAccess()) {
                    FluidResource resource = blockEntity.inputFluidInventory.getResource(0);
                    if (resource.isEmpty()) return;

                    IFluidBloodConversion bloodConversion = BloodHelper.getBloodConversion(resource.getFluid());
                    conversionRate = bloodConversion.conversionRate();

                    int amountAsInt = blockEntity.inputFluidInventory.getAmountAsInt(0);

                    extracted = blockEntity.inputFluidInventory.extract(resource, Math.min(amountAsInt, 50), transaction);
                }

                int blood = (int) Math.ceil(extracted * conversionRate);

                if (blood > 0) {
                    try (var access = blockEntity.outputFluidInventory.beginAccess()) {
                        int inserted = blockEntity.outputFluidInventory.insert(FluidResource.of(ModFluids.BLOOD), blood, transaction);

                        if (inserted > 0) {
                            blockEntity.filterStack.setDamageValue(blockEntity.filterStack.getDamageValue() + inserted);
                            if (blockEntity.filterStack.isBroken()) {
                                blockEntity.filterStack = ItemStack.EMPTY;
                            }
                            blockEntity.updateFilterState(blockEntity.level, blockEntity.worldPosition);
                            transaction.commit();
                        }
                    }
                }
            }
        }

        blockEntity.filterCooldownTime = FILTER_DELAY;
    }

    public void updateFilterState(@Nullable Level level, BlockPos pos) {
        BloodGrinderBlockEntity.updateFilterState(level, pos, BloodSieveBlock.HAS_FILTER, !filterStack.isEmpty() && !filterStack.isBroken());
    }
}
