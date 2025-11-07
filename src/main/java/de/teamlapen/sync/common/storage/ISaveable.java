package de.teamlapen.sync.common.storage;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/**
 * Interface to highlight that this object can be saved to CompoundTags
 * <p>
 *
 * @apiNote All members should only be called on the server side
 */
public interface ISaveable extends ValueIOSerializable, ISaveableKey {

    @Override
    void serialize(ValueOutput output);

    @Override
    void deserialize(ValueInput input);
}
