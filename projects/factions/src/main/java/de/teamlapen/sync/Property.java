package de.teamlapen.sync;

import de.teamlapen.sync.api.IStatusProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class Property implements IStatusProvider {

    protected final ResourceLocation key;
    protected final boolean sync;
    private int lastHash;

    public Property(ResourceLocation key, boolean sync) {
        this.key = key;
        this.sync = sync;
    }

    public ResourceLocation key() {
        return key;
    }

    public boolean sync() {
        return sync;
    }

    public boolean hasChanged() {
        return lastHash != getStatus();
    }

    public void store(ValueOutput output, StoreMode mode) {
        this.lastHash = storeValue(output, mode);
    }

    protected abstract int storeValue(ValueOutput output, StoreMode mode);

    public abstract boolean load(ValueInput input, boolean required);

    public enum StoreMode {
        FULL,
        FULL_UPDATE,
        UPDATE
    }
}
