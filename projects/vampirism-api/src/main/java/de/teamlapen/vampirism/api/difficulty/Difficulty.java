package de.teamlapen.vampirism.api.difficulty;

import org.jetbrains.annotations.NotNull;

import static net.minecraft.util.Mth.clamp;

/**
 * Represents a calculated difficulty level.
 * The used difficulty levels are in percentage of the max reachable level.
 * So a level 5 in a faction which has a max level of 10 would be represented by 5/10*100=50
 *
 * @param minPercLevel Percentage between 0 and 100
 */
public record Difficulty(int minPercLevel, int maxPercLevel, int avgPercLevel) {

    public Difficulty {
        maxPercLevel = clamp(maxPercLevel, 0, 100);
        minPercLevel = clamp(minPercLevel, 0, maxPercLevel);
        avgPercLevel = clamp(avgPercLevel, minPercLevel, maxPercLevel);
    }

    public boolean isZero() {
        return (this.minPercLevel == 0 && this.maxPercLevel == 0);
    }

    @Override
    public @NotNull String toString() {
        return "Difficulty: min_" + minPercLevel + " max_" + maxPercLevel + " avg_" + avgPercLevel;
    }
}
