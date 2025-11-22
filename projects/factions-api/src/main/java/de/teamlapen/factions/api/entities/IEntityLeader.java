package de.teamlapen.factions.api.entities;

import de.teamlapen.factions.api.extensions.ILivingEntity;


public interface IEntityLeader extends ILivingEntity {
    /**
     * Call this if an entity stops following this one
     */
    void decreaseFollowerCount();

    /**
     * @return The number of vampires that are following this entity. Don't expect this to be exact.
     */
    int getFollowingCount();

    /**
     * @return The maximum number of entities that are allowed to follow this one
     */
    int getMaxFollowerCount();

    /**
     * Call this if a new entity starts following this one
     *
     * @return If this entity allows more followers
     */
    boolean increaseFollowerCount();

}
