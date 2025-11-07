package de.teamlapen.sync.common.storage;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface IDefaultSaveble extends ISaveable {

    default void saveToChild(ValueOutput output) {
        serialize(output.child(nbtKey()));
    }

    default void loadFromChild(ValueInput input) {
        input.child(nbtKey()).ifPresent(this::deserialize);
    }
}
