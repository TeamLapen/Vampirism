package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;

/**
 * Advanced vampire hunter
 */
public interface IAdvancedHunter extends IHunterMob, IAdjustableLevel, IVillageCaptureEntity {
    int getHunterType();

    @Nullable
    String getTextureName();

    boolean isLookingForHome();

    void setCampArea(AxisAlignedBB box);

}
