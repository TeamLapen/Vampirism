package de.teamlapen.vampirism.api.entity.player.vampire;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Blood stats similar to FoodStats for vampire players
 */
public interface IBloodStats {
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

    @SideOnly(Side.CLIENT)
    int getPrevBloodLevel();

    /**
     * @return If the player could use blood
     */
    boolean needsBlood();
}
