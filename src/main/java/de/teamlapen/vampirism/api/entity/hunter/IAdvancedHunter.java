package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;

/**
 * Advanced vampire hunter
 */
public interface IAdvancedHunter extends IHunterMob, IAdjustableLevel {
    int getHunterType();

    @Nullable
    String getTextureName();

    boolean isLookingForHome();

    void setCampArea(AxisAlignedBB box);

}
