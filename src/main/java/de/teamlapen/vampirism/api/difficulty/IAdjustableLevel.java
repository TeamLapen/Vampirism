package de.teamlapen.vampirism.api.difficulty;

/**
 * If a entity implements this the level will automatically be set on world join.
 * 0 is the minimum level
 */
public interface IAdjustableLevel {
    /**
     * Should be -1 if the level has not been set yet
     *
     * @return The current level
     */
    int getLevel();

    /**
     * Set the level
     *
     * @param level
     */
    void setLevel(int level);

    /**
     * @return Maximal possible level
     */
    int getMaxLevel();

    /**
     * Calculate a (random) level under consideration of the given difficulty
     * If result is <0 the entity is not spawned
     *
     * @param d
     * @return Can be over max level. Will be capped.
     */
    int suggestLevel(Difficulty d);
}
