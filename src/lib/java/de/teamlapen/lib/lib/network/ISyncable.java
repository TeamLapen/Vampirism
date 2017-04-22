package de.teamlapen.lib.lib.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Provides an interface to sync entities
 */
public interface ISyncable {
    /**
     * This method should load all included information. It might contain some or all syncable information.
     **/
    @SideOnly(Side.CLIENT)
    void loadUpdateFromNBT(NBTTagCompound nbt);

    /**
     * This method is called to get update informations which should be send to the client
     */
    void writeFullUpdateToNBT(NBTTagCompound nbt);


    /**
     * Interface for {@link Capability} implementations, which should be syncable
     */
    interface ISyncableEntityCapabilityInst extends ISyncable {

        /**
         * @return A unique location for this capability. Probably best to use the one it is registered with.
         */
        ResourceLocation getCapKey();

        /***
         * @return the entity id of the representing entity
         */
        int getTheEntityID();
    }
}
