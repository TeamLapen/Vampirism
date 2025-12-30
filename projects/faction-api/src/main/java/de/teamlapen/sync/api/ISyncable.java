package de.teamlapen.sync.api;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface ISyncable {

    /**
     * Sync this object with the client
     */
    void sync();

    /**
     * Serialize all properties that can be synced.
     * <p>
     * This is usually called initially to send all data to the client
     */
    void serializeFullUpdate(ValueOutput output);

    /**
     * Serialize only properties that have changed since the last sync.
     * <p>
     * This is usually called by {@link #sync()} to send only the changed data to the clients
     */
    default void serializeUpdate(ValueOutput output) {
        serializeFullUpdate(output);
    }

    /**
     * Deserialize properties that have changed since the last sync.
     * <p>
     * This called with the data serialized by {@link #serializeUpdate(ValueOutput)} or {@link #serializeFullUpdate(ValueOutput)}
     * @implSpec the deserializer should not expect that all possible data is present
     */
    boolean deserializeUpdate(ValueInput input);
}
