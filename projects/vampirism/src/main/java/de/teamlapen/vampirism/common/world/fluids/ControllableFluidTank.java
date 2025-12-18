package de.teamlapen.vampirism.common.world.fluids;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.function.Predicate;

public class ControllableFluidTank extends DelegatingResourceHandler<FluidResource> implements ValueIOSerializable {

    private boolean allowInput;
    private boolean allowOutput;

    public ControllableFluidTank(int capacity, Runnable onContentsChanged, Predicate<FluidResource> fluidPredicate, boolean allowInput, boolean allowOutput) {
        super(new FluidStacksResourceHandler(1, capacity) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                onContentsChanged.run();
            }

            @Override
            public boolean isValid(int index, FluidResource resource) {
                return index == 0 && fluidPredicate.test(resource);
            }
        });
        this.allowInput = allowInput;
        this.allowOutput = allowOutput;
    }

    @Override
    public void serialize(ValueOutput output) {
        ((FluidStacksResourceHandler) this.delegate.get()).serialize(output);
    }

    @Override
    public void deserialize(ValueInput input) {
        ((FluidStacksResourceHandler) this.delegate.get()).deserialize(input);
    }

    public AccessTransaction beginAccess() {
        var access = new AccessTransaction(this.allowInput, this.allowOutput);
        this.allowInput = true;
        this.allowOutput = true;
        return access;
    }

    public boolean isEmpty() {
        return this.delegate.get().getAmountAsInt(0) == 0;
    }

    public int getCapacity(FluidResource resource) {
        return super.getCapacityAsInt(0, resource);
    }

    public int getAmount() {
        return getAmountAsInt(0);
    }

    public FluidResource getResource() {
        return getResource(0);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (this.allowOutput) {
            return super.extract(index, resource, amount, transaction);
        } else {
            return 0;
        }
    }

    @Override
    public int extract(FluidResource resource, int amount, TransactionContext transaction) {
        if (this.allowOutput) {
            return super.extract(resource, amount, transaction);
        } else {
            return 0;
        }
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (this.allowInput) {
            return super.insert(index, resource, amount, transaction);
        } else {
            return 0;
        }
    }

    @Override
    public int insert(FluidResource resource, int amount, TransactionContext transaction) {
        if (this.allowInput) {
            return super.insert(resource, amount, transaction);
        } else {
            return 0;
        }
    }

    public class AccessTransaction implements AutoCloseable {

        private final boolean allowInput;
        private final boolean allowOutput;

        public AccessTransaction(boolean allowInput, boolean allowOutput) {
            this.allowInput = allowInput;
            this.allowOutput = allowOutput;
        }

        @Override
        public void close() {
            ControllableFluidTank.this.allowInput = allowInput;
            ControllableFluidTank.this.allowOutput = allowOutput;
        }
    }
}
