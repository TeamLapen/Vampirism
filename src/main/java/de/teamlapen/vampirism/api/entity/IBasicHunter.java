package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.difficulty.IAdjustableLevel;
import net.minecraft.util.AxisAlignedBB;

/**
 * Interface for basic
 */
public interface IBasicHunter extends IHunter, IAdjustableLevel {
    boolean isLookingForHome();

    void setCampArea(AxisAlignedBB box);
}
