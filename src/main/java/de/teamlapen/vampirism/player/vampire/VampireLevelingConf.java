package de.teamlapen.vampirism.player.vampire;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;
import java.util.Map;

/**
 * Stores values/information about the leveling of vampires. Might be replaced by an actually configurable object at some point
 * All levels here are target levels, not the levels the player currently is on
 */
public class VampireLevelingConf {
    private static final VampireLevelingConf instance = new VampireLevelingConf();

    public static VampireLevelingConf getInstance() {
        return instance;
    }

    /**
     * Minimum target level that can be reached using the inspiration altar
     */
    private final int INSPIRATION_MIN_LEVEL;
    /**
     * Maximum target level
     */
    private final int INSPIRATION_MAX_LEVEL;
    private final int INFUSION_MAX_LEVEL;
    private final int INFUSION_MIN_LEVEL;
    /**
     * Maps <target level> to requirements
     */
    private final Map<Integer, AltarInfusionRequirements> altarInfusionRequirementsHashMap;

    private VampireLevelingConf() {
        INSPIRATION_MIN_LEVEL = 2;
        INSPIRATION_MAX_LEVEL = 4;
        INFUSION_MIN_LEVEL = 5;
        INFUSION_MAX_LEVEL = 14;
        altarInfusionRequirementsHashMap = Maps.newHashMap();
        altarInfusionRequirementsHashMap.put(5, new AltarInfusionRequirements(0, 0, 5, 1));
        altarInfusionRequirementsHashMap.put(6, new AltarInfusionRequirements(0, 1, 5, 1));
        altarInfusionRequirementsHashMap.put(7, new AltarInfusionRequirements(0, 1, 10, 1));
        altarInfusionRequirementsHashMap.put(8, new AltarInfusionRequirements(1, 1, 10, 1));
        altarInfusionRequirementsHashMap.put(9, new AltarInfusionRequirements(1, 1, 10, 1));
        altarInfusionRequirementsHashMap.put(10, new AltarInfusionRequirements(2, 1, 15, 1));
        altarInfusionRequirementsHashMap.put(11, new AltarInfusionRequirements(2, 1, 15, 1));
        altarInfusionRequirementsHashMap.put(12, new AltarInfusionRequirements(3, 1, 20, 1));
        altarInfusionRequirementsHashMap.put(13, new AltarInfusionRequirements(3, 2, 20, 1));
        altarInfusionRequirementsHashMap.put(14, new AltarInfusionRequirements(4, 2, 25, 1));

        assert altarInfusionRequirementsHashMap.size() == INFUSION_MAX_LEVEL - INFUSION_MIN_LEVEL + 1;
    }

    @NotNull
    public AltarInfusionRequirements getAltarInfusionRequirements(int targetLevel) {
        if (!altarInfusionRequirementsHashMap.containsKey(targetLevel)) {
            throw new IllegalArgumentException("Level " + targetLevel + " cannot be reached with an altar of infusion");
        }
        return altarInfusionRequirementsHashMap.get(targetLevel);
    }

    public int getRequiredBloodForAltarInspiration(int targetLevel) {
        if (!isLevelValidForAltarInspiration(targetLevel)) return -1;
        return 40 + (targetLevel - INSPIRATION_MIN_LEVEL) * 30;
    }

    public int getRequiredStructureLevelAltarInfusion(int targetLevel) {
        int t = (targetLevel - 4) / 2;
        return (int) (8 + (54 - 8) * t / 5f);
    }

    public boolean isLevelValidForAltarInfusion(int targetLevel) {
        return targetLevel >= INFUSION_MIN_LEVEL && targetLevel <= INFUSION_MAX_LEVEL;

    }

    public boolean isLevelValidForAltarInspiration(int targetLevel) {
        return targetLevel >= INSPIRATION_MIN_LEVEL && targetLevel <= INSPIRATION_MAX_LEVEL;
    }

    public record AltarInfusionRequirements(int pureBloodLevel, int blood, int heart, int vampireBook) {

    }


}
