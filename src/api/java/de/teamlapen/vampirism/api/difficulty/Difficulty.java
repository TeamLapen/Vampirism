package de.teamlapen.vampirism.api.difficulty;

/**
 * Represents a calculated difficulty level.
 * The used difficulty levels are in percentage of the max reachable level.
 * So a level 5 in a faction which has a max level of 10 would be represented by 5/10*100=50
 */
public record Difficulty(int minPercLevel, int maxPercLevel, int avgPercLevel) {

    public boolean isZero() {
        return (this.minPercLevel == 0 && this.maxPercLevel == 0);
    }

}
