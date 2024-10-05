package de.teamlapen.vampirism.api.difficulty;

import org.jetbrains.annotations.NotNull;

import static net.minecraft.util.Mth.clamp;

/**
 * Represents a calculated difficulty level.
 * The used difficulty levels are in percentage of the max reachable level.
 * So a level 5 in a faction which has a max level of 10 would be represented by 5/10*100=50
 */
public class Difficulty {

    /**
     * Percentage between 0 and 100
     */
    public final int minPercLevel, maxPercLevel, avgPercLevel;

    public Difficulty(int mil, int mal, int al) {
        mal = clamp(mal, 0, 100);
        mil = clamp(mil, 0, mal);
        al = clamp(al, mil, mal);
        this.minPercLevel = mil;
        this.maxPercLevel = mal;
        this.avgPercLevel = al;
    }

    public boolean isZero() {
        return (minPercLevel == 0 && maxPercLevel == 0);
    }

    @Override
    public @NotNull String toString() {
        return "Difficulty: min_" + minPercLevel + " max_" + maxPercLevel + " avg_" + avgPercLevel;
    }
}
