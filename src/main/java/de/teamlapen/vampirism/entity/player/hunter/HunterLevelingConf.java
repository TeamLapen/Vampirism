package de.teamlapen.vampirism.entity.player.hunter;

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

    public final int TABLE_MIN_LEVEL = 5;
    public final int TABLE_MAX_LEVEL = 14;
    public final int HUNTER_INTEL_COUNT = 9;

    /**
     * Converts hunter level to metadata for hunter intel. Returns -1 if there is no hunter intel for he given level
     *
     * @param level
     * @return
     */
    public int getHunterIntelMetaForLevel(int level) {
        return isLevelValidForTable(level) ? level - TABLE_MIN_LEVEL : -1;
    }

    public int[] getItemRequirementsForTable(int targetLevel) {
        if (!isLevelValidForTable(targetLevel)) {
            throw new IllegalArgumentException("Cannot use the table with the given target level " + targetLevel);
        }
        switch (targetLevel) {
            //fangs,blood,blood_meta,par3
            case 5:
                return new int[]{5, 0, 0, 0};
            case 6:
                return new int[]{0, 1, 0, 0};
            case 7:
                return new int[]{5, 1, 0, 0};
            case 8:
                return new int[]{0, 1, 1, 0};
            case 9:
                return new int[]{5, 1, 1, 0};
            case 10:
                return new int[]{5, 1, 2, 0};
            case 11:
                return new int[]{10, 1, 2, 0};
            case 12:
                return new int[]{10, 1, 3, 0};
            case 13:
                return new int[]{0, 2, 3, 0};
            case 14:
                return new int[]{0, 2, 4, 0};
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
     * Checks if a hunter player can use the hunter table to obtain the target level book
     *
     * @param targetLevel
     * @return
     */
    public boolean isLevelValidForTable(int targetLevel) {
        return targetLevel >= TABLE_MIN_LEVEL && targetLevel <= TABLE_MAX_LEVEL;
    }

    /**
     * Checks if a hunter player can reach the given level using the hunter trainer
     *
     * @param targetLevel
     * @return
     */
    public boolean isLevelValidForTrainer(int targetLevel) {
        return targetLevel >= TABLE_MIN_LEVEL && targetLevel <= TABLE_MAX_LEVEL;
    }
}
