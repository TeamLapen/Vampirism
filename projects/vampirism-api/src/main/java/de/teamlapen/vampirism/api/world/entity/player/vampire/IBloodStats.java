package de.teamlapen.vampirism.api.world.entity.player.vampire;


import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

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
     * @return If the player could use blood
     */
    boolean needsBlood();
}
