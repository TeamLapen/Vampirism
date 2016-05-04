package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Interface for basic
 */
public interface IBasicHunter extends IHunterMob, IAdjustableLevel {
    boolean isLookingForHome();

    void setCampArea(AxisAlignedBB box);
}
