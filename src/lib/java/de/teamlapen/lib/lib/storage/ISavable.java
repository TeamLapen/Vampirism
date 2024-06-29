package de.teamlapen.lib.lib.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

/**
 * Interface to highlight that this object can be saved to CompoundTags
 * <p>
 *
 * @apiNote All members should only be called on the server side
 */
public interface ISavable extends INBTSerializable<CompoundTag>, INBTObject {

    @NotNull
    @Override
    CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider);

    @Override
    void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt);
}
