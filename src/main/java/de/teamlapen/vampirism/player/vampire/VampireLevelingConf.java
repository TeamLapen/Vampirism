package de.teamlapen.vampirism.player.vampire;

/**
 * Stores values/informations about the leveling of vampires. Might be replaced by an actually configurable object at some point
 * All levels here are target levels, not the levels the player currently is on
 */
public class VampireLevelingConf {
    private static VampireLevelingConf instance = new VampireLevelingConf();

    public static VampireLevelingConf getInstance() {
        return instance;
    }

    /**
     * Minimum target level that can be reached using the inspiration altar
     */
    private final int INSPIRATION_MIN_LEVEL = 2;
    private final int INSPIRATION_MAX_LEVEL = 4;

    private VampireLevelingConf() {
    }

    public int getRequiredBloodForAltarInspiration(int targetLevel) {
        if (!isLevelValidForAltarInspiration(targetLevel)) return -1;
        return 40 + (targetLevel - INSPIRATION_MIN_LEVEL) * 30;
    }

    public boolean isLevelValidForAltarInspiration(int targetLevel) {
        return targetLevel >= INSPIRATION_MIN_LEVEL && targetLevel <= INSPIRATION_MAX_LEVEL;
    }


}
