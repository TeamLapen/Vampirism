package de.teamlapen.vampirism.api.world.entity.vampire;

import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.faction.api.world.entities.ICustomizationHolder;

/**
 * Interface for the basic vampire mob.
 * Do not implement yourself
 */
public interface IBasicVampire extends IVampireMob, IAdjustableLevel, IVillageCaptureEntity, ICustomizationHolder {
    int TYPES = 126;

    /**
     * @return A randomly selected but permanent integer between 0 and {@link IBasicVampire#TYPES} or -1 if not selected yet.
     */
    @Override
    int getEntityTextureType();
}
