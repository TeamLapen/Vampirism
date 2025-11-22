package de.teamlapen.sync;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Supplier;

public class SubProperty<T extends PropertySync> extends Property {

    private final Supplier<T> property;

    public SubProperty(ResourceLocation key, boolean sync, Supplier<T> property) {
        super(key, sync);
        this.property = property;
    }

    @Override
    public int getStatus() {
        return this.property.get().getStatus();
    }

    @Override
    protected int storeValue(ValueOutput output, StoreMode mode) {
        ValueOutput child = output.child(key.toString());
        switch (mode) {
            case FULL -> property.get().serialize(child);
            case FULL_UPDATE -> property.get().serializeFullUpdate(child);
            case UPDATE -> property.get().serializeUpdate(child);
        }
        return property.get().hashCode();
    }

    @Override
    public boolean load(ValueInput input, boolean required) {
        return input.child(key.toString()).filter(this.property.get()::deserializeUpdate).isPresent();
    }
}
