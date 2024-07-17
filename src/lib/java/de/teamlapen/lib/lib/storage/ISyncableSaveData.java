package de.teamlapen.lib.lib.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface ISyncableSaveData extends IDefaultSavable, IStateSyncable {

    @Override
    default void deserializeUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        deserializeNBT(provider, nbt);
    }

    @Override
    @NotNull
    default CompoundTag serializeUpdateNBTInternal(HolderLookup.@NotNull Provider provider, UpdateParams params) {
        return serializeNBT(provider);
    }
}
