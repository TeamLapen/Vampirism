package de.teamlapen.vampirism.api.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

/**
 * Interface for a Potion wrapper. Currently is not designed to be manually created. Will be created when registering something in {@link IBloodPotionRegistry}
 */
public interface IBloodPotionEffect {

    /**
     * @return The id this effect has been registed with
     */
    String getId();

    /**
     * @return The localized name
     */
    String getLocName(int duration, int amplifier);

    /**
     * Randomly selects duration and amplifier etc
     *
     * @param rng
     * @return A nbt tag containing all properties which can be used to store the effect with the item
     */
    NBTTagCompound getRandomProperties(Random rng);

    boolean isBad();

    /**
     * Called when this effect is activated
     * @param propertyNbt The nbt tag created in {@link IBloodPotionEffect#getRandomProperties(Random)}
     */
    void onActivated(EntityLivingBase hunter, NBTTagCompound propertyNbt);


}
