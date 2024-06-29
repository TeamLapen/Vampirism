package de.teamlapen.lib.lib.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Provides an interface to sync data from the server to client
 */
public interface ISyncable extends INBTObject {

    /**
     * This will load all data from the given nbt.
     *
     * @param nbt the tag might contain all data from {@link #serializeUpdateNBT()} or only a subset
     * @implSpec the update component should always be checked against iif components exists. But sub {@link de.teamlapen.lib.lib.storage.ISyncable} should be called with a {@link net.minecraft.nbt.CompoundTag} anyway, even if it is empty. To allow additional functions.
     * @apiNote This method should only be called on the client side
     **/
    void deserializeUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt);

    /**
     * This method writes all syncable data of the object to a new {@link net.minecraft.nbt.CompoundTag}.
     * <p>
     *
     * @apiNote This method should only be called on the server side
     */
    @NotNull
    CompoundTag serializeUpdateNBT(HolderLookup.@NotNull Provider provider);

}
