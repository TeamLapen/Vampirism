package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class SupplierRequirement implements TaskRequirement<Boolean> {
    private final @Nonnull Supplier<Boolean> supplier;

    public SupplierRequirement(@Nonnull Supplier<Boolean> supplier) {
        this.supplier = supplier;
    }

    @Nonnull
    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }

    @Nonnull
    @Override
    public Boolean getStat() {
        return this.supplier.get();
    }

    @Override
    public int getAmount() {
        return this.supplier.get().compareTo(false);
    }
}
