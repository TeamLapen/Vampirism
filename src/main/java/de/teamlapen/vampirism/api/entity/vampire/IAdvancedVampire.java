package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;

import javax.annotation.Nullable;

/**
 * Advanced vampire
 */
public interface IAdvancedVampire extends IVampireMob, IAdjustableLevel {
    int getEyeType();

    @Nullable
    String getTextureName();
}
