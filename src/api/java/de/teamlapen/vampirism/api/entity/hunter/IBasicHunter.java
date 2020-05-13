package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.vampire.IBasicVampire;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Interface for basic hunter mob
 * Do not implement
 */
public interface IBasicHunter extends IHunterMob, IAdjustableLevel, IVillageCaptureEntity {
    boolean isLookingForHome();

    void makeNormalHunter();

    void makeVillageHunter(AxisAlignedBB box);

    int TYPES = 126;

    /**
     * @return A randomly selected but permanent integer between 0 and {@link IBasicVampire#TYPES} or -1 if not selected yet.
     */
    int getEntityTextureType();

}
