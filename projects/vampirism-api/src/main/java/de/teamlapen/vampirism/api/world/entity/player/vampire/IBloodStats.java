package de.teamlapen.vampirism.api.world.entity.player.vampire;


import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.NotNull;

/**
 * Blood stats similar to FoodStats for vampire players
 */
public interface IBloodStats extends ResourceHandler<FluidResource> {
    float LOW_SATURATION = 0.3F;
    float MEDIUM_SATURATION = 0.7F;
    float HIGH_SATURATION = 1.0F;


    /**
     * @return The current blood level
     */
    int getBloodLevel();

    /**
     * @return The maximum amount of blood
     */
    int getMaxBlood();

    int getPrevBloodLevel();

    /**
     * Adds blood and blood saturation to the player
     *
     * @param amount             The blood to add. Only what fits into the blood bar is added, the rest is returned
     * @param saturationModifier Similar to the food saturation modifier. The added saturation is {@code amount * saturationModifier * 2},
     *                           see {@link FoodConstants#saturationByModifier(int, float)}. Unlike vanilla, the saturation may exceed the blood bar
     * @return The blood that did not fit into the blood bar
     */
    int addBlood(int amount, float saturationModifier);

    /**
     * {@link FoodProperties#saturation()} holds the absolute saturation value, while {@link #addBlood(int, float)} expects a modifier.
     *
     * @return The saturation modifier the given food properties were built with
     */
    static float saturationModifier(FoodProperties foodProperties) {
        return foodProperties.nutrition() > 0 ? foodProperties.saturation() / (foodProperties.nutrition() * 2F) : 0F;
    }

    /**
     * @return If the player could use blood
     */
    boolean needsBlood();
}
