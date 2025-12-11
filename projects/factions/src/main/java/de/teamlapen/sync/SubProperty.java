package de.teamlapen.sync;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Supplier;

public class SubProperty<T extends PropertySync> extends Property {

    private final Supplier<T> property;
    private final boolean clientSync;
    private final boolean serverLoad;

    public SubProperty(ResourceLocation key, Supplier<T> property, boolean clientSync, boolean serverLoad) {
        super(key);
        this.property = property;
        this.clientSync = clientSync;
        this.serverLoad = serverLoad;
    }

    @Override
    public boolean hasClientSync() {
        return this.clientSync;
    }

    @Override
    public boolean hasServerLoad() {
        return this.serverLoad;
    }

    @Override
    public boolean loadClient(ValueInput input) {
        return input.child(this.key.toString()).filter(this.property.get()::deserializeUpdate).isPresent();
    }

    @Override
    public void loadServer(ValueInput input) {
        this.property.get().deserialize(input.childOrEmpty(this.key.toString()));
    }

    @Override
    public int getStatus() {
        return this.property.get().getStatus();
    }

    @Override
    protected int storeValue(ValueOutput output, StoreMode mode) {
        ValueOutput child = output.child(this.key.toString());
        switch (mode) {
            case FULL -> property.get().serialize(child);
            case FULL_UPDATE -> property.get().serializeFullUpdate(child);
            case UPDATE -> property.get().serializeUpdate(child);
        }
        return property.get().hashCode();
    }

    public static class Builder<T extends PropertySync> {

        private final PropertyBuilder propertySync;
        private final Supplier<T> property;
        private boolean clientSync = true;
        private boolean serverLoad = true;

        public Builder(PropertyBuilder propertySync, Supplier<T> property) {
            this.propertySync = propertySync;
            this.property = property;
        }

        public Builder<T> disableClientSync() {
            this.clientSync = false;
            return this;
        }

        public Builder<T> disableServerLoad() {
            this.serverLoad = false;
            return this;
        }

        public void register() {
            this.propertySync.register(key -> new SubProperty<>(key, this.property, this.clientSync, this.serverLoad));
        }
    }
}
