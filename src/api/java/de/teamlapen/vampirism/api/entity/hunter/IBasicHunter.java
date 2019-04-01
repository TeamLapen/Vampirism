package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Interface for basic
 */
public interface IBasicHunter extends IHunterMob, IAdjustableLevel, IVillageCaptureEntity {
    boolean isLookingForHome();

    void makeCampHunter(AxisAlignedBB box);

    void makeNormalHunter();

    void makeVillageHunter(AxisAlignedBB box);

}
