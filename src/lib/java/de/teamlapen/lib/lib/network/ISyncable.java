package de.teamlapen.lib.lib.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Provides an interface to sync entities (Subclasses of EntityLiving)
 */
public interface ISyncable {
    /**
     * This method should load all included information. It might contain some or all syncable information.
     **/
    @OnlyIn(Dist.CLIENT)
    void loadUpdateFromNBT(CompoundNBT nbt);

    /**
     * This method is called to get update informations which should be send to the client
     */
    void writeFullUpdateToNBT(CompoundNBT nbt);


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
