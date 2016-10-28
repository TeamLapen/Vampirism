package de.teamlapen.vampirism.api.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Interface for a Potion wrapper. Currently is not designed to be manually created. Will be created when registering something in {@link IBloodPotionRegistry}
 */
public interface IBloodPotionEffect {

    /**
     * @return If both effects can be added to the same blood potion
     */
    boolean canCoexist(@Nonnull IBloodPotionEffect other);

    /**
     * @return The id this effect has been registed with
     */
    String getId();

    /**
     * @return The localized name
     */
    String getLocName(NBTTagCompound properties);

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
     * @param durationMult The duration should be multiplied with this value
     */
    void onActivated(EntityLivingBase hunter, NBTTagCompound propertyNbt, float durationMult);


}
