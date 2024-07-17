package de.teamlapen.lib.lib.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface IDefaultSavable extends ISavable {

    default void saveToCompound(HolderLookup.@NotNull Provider provider, CompoundTag compoundTag) {
        compoundTag.put(nbtKey(), serializeNBT(provider));
    }

    default void loadFromCompound(HolderLookup.@NotNull Provider provider, CompoundTag compoundTag) {
        deserializeNBT(provider, compoundTag.getCompound(nbtKey()));
    }
}
