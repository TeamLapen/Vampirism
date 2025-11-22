package de.teamlapen.sync.api;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface ISyncable {

    void sync();

    void serializeFullUpdate(ValueOutput output);

    default void serializeUpdate(ValueOutput output) {
        serializeFullUpdate(output);
    }

    boolean deserializeUpdate(ValueInput input);
}
