package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Advanced vampire
 */
public interface IAdvancedVampire extends IVampireMob, IAdjustableLevel, IVillageCaptureEntity, IEntityLeader {

    int getEyeType();

    @Nullable
    String getTextureName();

}
