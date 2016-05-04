package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;

import javax.annotation.Nullable;

/**
 * Advanced vampire
 */
public interface IAdvancedVampire extends IVampireMob, IAdjustableLevel {
    /**
     * Call this if an entity stops following this one
     */
    void decreaseFollowerCount();

    int getEyeType();

    /**
     * @return The number of vampires that are following this entity. Don't expect this to be exact.
     */
    int getFollowingCount();

    /**
     * @return The maximum number of entities that are allowed to follow this one
     */
    int getMaxFollowerCount();

    @Nullable
    String getTextureName();

    /**
     * Call this if a new entity starts following this one
     * @return If this entity allows more followers
     */
    boolean increaseFollowerCount();
}
