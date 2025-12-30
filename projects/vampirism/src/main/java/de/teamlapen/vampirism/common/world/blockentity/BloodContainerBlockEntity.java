package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.faction.common.world.blockentity.NetworkedBlockEntity;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.util.BloodHelper;
import de.teamlapen.vampirism.common.world.fluids.ControllableFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class BloodContainerBlockEntity extends NetworkedBlockEntity {

    public static final int CAPACITY = FluidType.BUCKET_VOLUME * 5;

    public final ControllableFluidTank fluidInventory;

    public BloodContainerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BLOOD_CONTAINER.get(), pos, blockState);
        this.fluidInventory = new ControllableFluidTank(CAPACITY, this::setChanged, fluid -> fluid.is(ModFluids.BLOOD) || BloodHelper.isConvertibleToBlood(fluid), true, true);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fluidInventory.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.fluidInventory.serialize(output);
    }

    public FluidStack getFluid() {
        FluidResource resource = this.fluidInventory.getResource();
        return resource.toStack(this.fluidInventory.getAmount());
    }

    public void setFluid(FluidStack fluid) {
        try (var transaction = Transaction.openRoot()) {
            FluidResource resource = this.fluidInventory.getResource();
            if (!resource.isEmpty()) {
                int amount = this.fluidInventory.getAmount();
                this.fluidInventory.extract(resource, amount, transaction);
            }
            if (!fluid.isEmpty()) {
                this.fluidInventory.insert(FluidResource.of(fluid), fluidInventory.getAmount(), transaction);
            }
            transaction.commit();
        }
    }
}
