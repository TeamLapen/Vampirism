package de.teamlapen.lib.lib.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Some objects should not sync all available date every time.
 * This interface only writes the data if {@link #needsUpdate()} returns true.
 */
public interface IStateSyncable extends ISyncable {

    @NotNull
    CompoundTag serializeUpdateNBTInternal(HolderLookup.@NotNull Provider provider, UpdateParams params);

    /**
     * @deprecated Calling is safe, but use {@link #serializeUpdateNBTInternal(HolderLookup.Provider, UpdateParams)} to save data.
     */
    @Deprecated
    @NotNull
    default CompoundTag serializeUpdateNBT(HolderLookup.@NotNull Provider provider, UpdateParams params) {
        if (params.ignoreChanges()) {
            return serializeUpdateNBTInternal(provider, params);
        } else {
            if (needsUpdate()) {
                var update = serializeUpdateNBTInternal(provider, params);
                updateSend();
                return update;
            }
            return new CompoundTag();
        }
    }

    /**
     * If this object has pending changes that should be synced
     */
    default boolean needsUpdate() {
        return false;
    }

    /**
     * Called after the data was written to the nbt to indicate that the data was sent
     */
    default void updateSend() {

    }

}
