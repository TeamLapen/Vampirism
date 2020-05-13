package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;

/**
 * Interface for the basic vampire mob.
 * Do not implement yourself
 */
public interface IBasicVampire extends IVampireMob, IAdjustableLevel, IVillageCaptureEntity {
    int TYPES = 126;

    /**
     * @return A randomly selected but permanent integer between 0 and {@link IBasicVampire#TYPES} or -1 if not selected yet.
     */
    int getEntityTextureType();
}
