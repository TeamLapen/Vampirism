package de.teamlapen.sync.common.storage;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface ISyncableSaveData extends IDefaultSaveble, IStateSyncable {

    @Override
    default void deserializeUpdate(ValueInput input) {
        deserialize(input);
    }

    @Override
    default void serializeUpdateInternal(ValueOutput output, UpdateParams params) {
        serialize(output);
    }
}
