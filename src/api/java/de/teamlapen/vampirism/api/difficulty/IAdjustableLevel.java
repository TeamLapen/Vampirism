package de.teamlapen.vampirism.api.difficulty;

/**
 * If an entity implements this the level will automatically be set on world join.
 * 0 is the minimum level
 */
public interface IAdjustableLevel {
    /**
     * Should be -1 if the level has not been set yet
     *
     * @return The current level
     */
    int getEntityLevel();

    /**
     * Set the level
     */
    void setEntityLevel(int level);

    /**
     * @return Maximal possible level
     */
    int getMaxEntityLevel();

    /**
     * Calculate a (random) level under consideration of the given difficulty
     * If result is smaller than zero the entity is not spawned
     *
     * @return Can be over max level. Will be capped.
     */
    int suggestEntityLevel(Difficulty d);
}
