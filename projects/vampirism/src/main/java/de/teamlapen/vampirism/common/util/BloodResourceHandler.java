package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.common.core.ModFluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface BloodResourceHandler extends ResourceHandler<FluidResource> {

    FluidResource bloodResource = FluidResource.of(ModFluids.BLOOD.get());

    @Override
    default int size() {
        return 1;
    }

    @Override
    default @NotNull FluidResource getResource(int index) {
        Objects.checkIndex(index, size());
        return bloodResource;
    }

    @Override
    default long getAmountAsLong(int index) {
        return VReference.FOOD_TO_FLUID_BLOOD * (long) getAmount();
    }

    @Override
    default long getCapacityAsLong(int index, @NotNull FluidResource resource) {
        return VReference.FOOD_TO_FLUID_BLOOD * (long) getCapacity();
    }

    @Override
    default boolean isValid(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return resource.is(ModFluids.BLOOD.get());
    }

    @Override
    default int insert(int index, @NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int currentAmount = getAmount();

        var toAdd = amount / VReference.FOOD_TO_FLUID_BLOOD;
        toAdd = Math.min(toAdd, getCapacity() - currentAmount);

        getJournal().updateSnapshots(transaction);

        return addBlood(toAdd);
    }

    @Override
    default int extract(int index, @NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

        int currentAmount = getAmount();

        var toRemove = amount / VReference.FOOD_TO_FLUID_BLOOD;

        toRemove = Math.min(toRemove, currentAmount);

        getJournal().updateSnapshots(transaction);

        return extractBlood(toRemove);
    }

    int getAmount();

    int addBlood(int amount);

    int extractBlood(int amount);

    int getCapacity();

    SnapshotJournal<Integer> getJournal();
}
