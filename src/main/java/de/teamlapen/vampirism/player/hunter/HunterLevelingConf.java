package de.teamlapen.vampirism.player.hunter;

import de.teamlapen.vampirism.VampirismMod;

/**
 * Stores values/informations about the leveling of hunters. Might be replaced by an actually configurable object at some point
 * All levels here are target levels, not the levels the player currently is on
 */
public class HunterLevelingConf {
    private static HunterLevelingConf instance;

    public static HunterLevelingConf instance() {
        if (instance == null) {
            instance = new HunterLevelingConf();
        }
        return instance;
    }

    public final int BASIC_HUNTER_MIN_LEVEL = 2;
    public final int BASIC_HUNTER_MAX_LEVEL = 4;
    public final int TABLE_MIN_LEVEL = 5;
    public final int TABLE_MAX_LEVEL = 14;
    public final int HUNTER_INTEL_COUNT = 10;

    /**
     * Converts hunter level to metadata for hunter intel. Returns -1 if there is no hunter intel for he given level
     *
     * @param level
     * @return
     */
    public int getHunterIntelMetaForLevel(int level) {
        return isLevelValidForTable(level) ? level - TABLE_MIN_LEVEL : -1;
    }

    /**
     * @throws IllegalArgumentException If the altar cannot be used at that level
     */
    public int[] getItemRequirementsForTable(int targetLevel) {
        if (!isLevelValidForTable(targetLevel)) {
            throw new IllegalArgumentException("Cannot use the table with the given target level " + targetLevel);
        }
        switch (targetLevel) {
            //fangs,blood,blood_meta,vampire book
            case 5:
                return new int[]{10, 0, 0, 1};
            case 6:
                return new int[]{0, 1, 0, 1};
            case 7:
                return new int[]{10, 1, 0, 1};
            case 8:
                return new int[]{0, 1, 1, 1};
            case 9:
                return new int[]{15, 1, 1, 1};
            case 10:
                return new int[]{20, 1, 2, 1};
            case 11:
                return new int[]{20, 1, 2, 1};
            case 12:
                return new int[]{20, 1, 3, 1};
            case 13:
                return new int[]{25, 2, 3, 1};
            case 14:
                return new int[]{25, 2, 4, 1};
            default:
                return null;//Should never be reached
        }
    }

    public int[] getItemRequirementsForTrainer(int targetLevel) {
        if (!isLevelValidForTrainer(targetLevel)) {
            throw new IllegalArgumentException("Cannot use the trainer with the given target level " + targetLevel);
        }
        switch (targetLevel) {
            //iron,gold
            case 5:
                return new int[]{5, 0};
            case 6:
                return new int[]{10, 0};
            case 7:
                return new int[]{15, 0};
            case 8:
                return new int[]{40, 0};
            case 9:
                return new int[]{20, 10};
            case 10:
                return new int[]{20, 20};
            case 11:
                return new int[]{20, 10};
            case 12:
                return new int[]{30, 10};
            case 13:
                return new int[]{40, 20};
            case 14:
                return new int[]{40, 40};
            default:
                VampirismMod.log.w("HunterLevelingConf", "Something is wrong with the hunter levels");
        }
        return null;//Should never be reached
    }

    /**
     * @param meta
     * @return the hunter level that can be reached with this hunter intel metadata
     */
    public int getLevelForHunterIntelMeta(int meta) {
        return Math.min(meta + TABLE_MIN_LEVEL, TABLE_MAX_LEVEL);
    }

    /**
     * @throws IllegalArgumentException If the basic hunter cannot be used at that level
     */
    public int getVampireBloodCountForBasicHunter(int targetLevel) {
        if (!isLevelValidForBasicHunter(targetLevel)) {
            throw new IllegalArgumentException("Cannot use the table with the given target level " + targetLevel);
        }
        switch (targetLevel) {
            case 2:
                return 1;
            case 3:
                return 5;
            case 4:
                return 12;
            default:
                return 100000;
        }
    }

    /**
     * Checks if a hunter player can be trained by an basic hunter to reach the target level
     */
    public boolean isLevelValidForBasicHunter(int targetLevel) {
        return targetLevel >= BASIC_HUNTER_MIN_LEVEL && targetLevel <= BASIC_HUNTER_MAX_LEVEL;
    }

    /**
     * Checks if a hunter player can use the hunter table to obtain the target level book
     */
    public boolean isLevelValidForTable(int targetLevel) {
        return targetLevel >= TABLE_MIN_LEVEL && targetLevel <= TABLE_MAX_LEVEL;
    }

    /**
     * Checks if a hunter player can reach the given level using the hunter trainer
     */
    public boolean isLevelValidForTrainer(int targetLevel) {
        return targetLevel >= TABLE_MIN_LEVEL && targetLevel <= TABLE_MAX_LEVEL;
    }
}
