package de.teamlapen.lib.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Provides an interface to sync entities
 */
public interface ISyncable  {
    /**
     * This method should load all included information. It might contain some or all synchable information.
     *
     * @param nbt
     */
    @SideOnly(Side.CLIENT)
     void loadUpdateFromNBT(NBTTagCompound nbt);

    /**
     * This method is called to get update informations which should be send to the client
     */
    void writeFullUpdateToNBT(NBTTagCompound nbt);

    interface ISyncableExtendedProperties extends IExtendedEntityProperties,ISyncable{
        /**
         * Returns the entity id of the representing entity
         *
         * @return
         */
        public int getTheEntityID();

        /**
         * Sends a sync packet to the client
         * @param all Whether to send it to all players around or only to the corresponding player
         */
        void sync(boolean all);
    }
}
