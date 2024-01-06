package de.teamlapen.lib.lib.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * Provides an interface to sync entities (Subclasses of EntityLiving)
 */
public interface ISyncable {
    /**
     * This method should load all included information. It might contain some or all syncable information.
     **/
    void loadUpdateFromNBT(CompoundTag nbt);

    /**
     * This method is called to get update information which should be sent to the client
     */
    CompoundTag writeFullUpdateToNBT();

    /**
     * Interface for {@link net.neoforged.neoforge.attachment.AttachmentType} implementations, which should be syncable
     */
    interface ISyncableAttachment extends ISyncable {

        /**
         * @return A unique location for this capability. Probably best to use the one it is registered with.
         */
        ResourceLocation getAttachmentKey();

        /***
         * @return the entity id of the representing entity
         */
        int getTheEntityID();

        CompoundTag writeToNBT();

        void loadFromNBT(CompoundTag nbt);
    }
}
