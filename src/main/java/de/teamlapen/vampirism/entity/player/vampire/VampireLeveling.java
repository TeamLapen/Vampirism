package de.teamlapen.vampirism.entity.player.vampire;

import de.teamlapen.vampirism.REFERENCE;
import org.jetbrains.annotations.Range;

import java.util.Arrays;
import java.util.Optional;

public class VampireLeveling {

    private static final AltarInspirationRequirement LEVEL_2 = new AltarInspirationRequirement(2, 40);
    private static final AltarInspirationRequirement LEVEL_3 = new AltarInspirationRequirement(3, 70);
    private static final AltarInspirationRequirement LEVEL_4 = new AltarInspirationRequirement(4, 100);
    private static final AltarInfusionRequirements LEVEL_5 = new AltarInfusionRequirements(5, 0, 0, 5, 1);
    private static final AltarInfusionRequirements LEVEL_6 = new AltarInfusionRequirements(6, 0, 1, 5, 1);
    private static final AltarInfusionRequirements LEVEL_7 = new AltarInfusionRequirements(7, 0, 1, 10, 1);
    private static final AltarInfusionRequirements LEVEL_8 = new AltarInfusionRequirements(8, 1, 1, 10, 1);
    private static final AltarInfusionRequirements LEVEL_9 = new AltarInfusionRequirements(9, 1, 1, 10, 1);
    private static final AltarInfusionRequirements LEVEL_10 = new AltarInfusionRequirements(10, 2, 1, 15, 1);
    private static final AltarInfusionRequirements LEVEL_11 = new AltarInfusionRequirements(11, 2, 1, 15, 1);
    private static final AltarInfusionRequirements LEVEL_12 = new AltarInfusionRequirements(12, 3, 1, 20, 1);
    private static final AltarInfusionRequirements LEVEL_13 = new AltarInfusionRequirements(13, 3, 2, 20, 1);
    private static final AltarInfusionRequirements LEVEL_14 = new AltarInfusionRequirements(14, 4, 2, 25, 1);

    private static final VampireLevelRequirement[] LEVEL_REQUIREMENTS = {null, null, LEVEL_2, LEVEL_3, LEVEL_4, LEVEL_5, LEVEL_6, LEVEL_7, LEVEL_8, LEVEL_9, LEVEL_10, LEVEL_11, LEVEL_12, LEVEL_13, LEVEL_14};

    public static Optional<VampireLevelRequirement> getLevelRequirement(@Range(from = 2, to = REFERENCE.HIGHEST_HUNTER_LEVEL) int targetLevel) {
        return Optional.ofNullable(LEVEL_REQUIREMENTS[targetLevel]);
    }

    public static Optional<AltarInfusionRequirements> getInfusionRequirement(@Range(from = 2, to = REFERENCE.HIGHEST_HUNTER_LEVEL) int targetLevel) {
        return Optional.ofNullable(LEVEL_REQUIREMENTS[targetLevel]).filter(AltarInfusionRequirements.class::isInstance).map(AltarInfusionRequirements.class::cast);
    }

    public static Optional<AltarInspirationRequirement> getInspirationRequirement(@Range(from = 2, to = REFERENCE.HIGHEST_HUNTER_LEVEL) int targetLevel) {
        return Optional.ofNullable(LEVEL_REQUIREMENTS[targetLevel]).filter(AltarInspirationRequirement.class::isInstance).map(AltarInspirationRequirement.class::cast);
    }

    public static AltarInfusionRequirements[] getInfusionRequirements() {
        return Arrays.stream(LEVEL_REQUIREMENTS).filter(AltarInfusionRequirements.class::isInstance).map(AltarInfusionRequirements.class::cast).toArray(AltarInfusionRequirements[]::new);
    }

    public static AltarInspirationRequirement[] getInspirationRequirements() {
        return Arrays.stream(LEVEL_REQUIREMENTS).filter(AltarInspirationRequirement.class::isInstance).map(AltarInspirationRequirement.class::cast).toArray(AltarInspirationRequirement[]::new);
    }

    public interface VampireLevelRequirement {
        @Range(from = 2, to = REFERENCE.HIGHEST_VAMPIRE_LEVEL)
        int targetLevel();
    }

    public record AltarInfusionRequirements(int targetLevel, int pureBloodLevel, int pureBloodQuantity, int humanHeartQuantity, int vampireBookQuantity) implements VampireLevelRequirement {

        public int getRequiredStructurePoints() {
            int t = (this.targetLevel() - 4) / 2;
            return (int) (8 + (54 - 8) * t / 5f);
        }
    }

    public record AltarInspirationRequirement(int targetLevel, int bloodAmount) implements VampireLevelRequirement {

    }
}
