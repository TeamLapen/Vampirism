package de.teamlapen.sync.common.storage;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Provides an interface to sync data from the server to client
 */
public interface ISyncable extends ISaveableKey {

    /**
     * This will load all data from the given nbt.
     *
     * @param input the tag might contain all data from {@link #serializeUpdate(ValueOutput, UpdateParams)} or only a subset
     * @implSpec the update component should always be checked against iif components exists. But sub {@link ISyncable} should be called with a {@link net.minecraft.nbt.CompoundTag} anyway, even if it is empty. To allow additional functions.
     * @apiNote This method should only be called on the client side
     **/
    void deserializeUpdate(ValueInput input);

    /**
     * This method writes all syncable data of the object to a new {@link net.minecraft.nbt.CompoundTag}.
     * <p>
     *
     * @apiNote This method should only be called on the server side
     */
    void serializeUpdate(ValueOutput output, UpdateParams params);

    /**
     * A utility method to make the calling of {@link #serializeUpdate(ValueOutput, UpdateParams)} easier
     */
    default void updateToChild(ValueOutput output, UpdateParams params) {
        serializeUpdate(output.child(nbtKey()), params);
    }

    /**
     * A utility method to make the calling of {@link #deserializeUpdate(ValueInput)} easier
     */
    default void updateFromChild(ValueInput input) {
        input.child(nbtKey()).ifPresent(this::deserializeUpdate);
    }
}
