package de.teamlapen.sync.common.storage;

import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.ApiStatus;

/**
 * Some objects should not sync all available date every time.
 * This interface only writes the data if {@link #needsUpdate()} returns true.
 */
public interface IStateSyncable extends ISyncable {

    void serializeUpdateInternal(ValueOutput output, UpdateParams params);

    /**
     * @deprecated Calling is safe, but use {@link #serializeUpdateInternal(ValueOutput, UpdateParams)} to save data.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @ApiStatus.Internal
    default void serializeUpdate(ValueOutput output, UpdateParams params) {
        if (params.ignoreChanges()) {
            serializeUpdateInternal(output, params);
        } else {
            if (needsUpdate()) {
                serializeUpdateInternal(output, params);
                updateSend();
            }
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
