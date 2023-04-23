package de.teamlapen.vampirism.entity.player.hunter;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.HunterIntelItem;
import org.jetbrains.annotations.Range;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public class HunterLeveling {

    private static final BasicHunterRequirement LEVEL_2 = new BasicHunterRequirement(2, 1);
    private static final BasicHunterRequirement LEVEL_3 = new BasicHunterRequirement(3, 5);
    private static final BasicHunterRequirement LEVEL_4 = new BasicHunterRequirement(4, 12);
    private static final HunterTrainerRequirement LEVEL_5 = new HunterTrainerRequirement(5, 5, 0, new HunterTableRequirement(0, 10, 0, 0, 1, ModItems.HUNTER_INTEL_0));
    private static final HunterTrainerRequirement LEVEL_6 = new HunterTrainerRequirement(6, 10, 0, new HunterTableRequirement(0, 0, 1, 0, 1, ModItems.HUNTER_INTEL_1));
    private static final HunterTrainerRequirement LEVEL_7 = new HunterTrainerRequirement(7, 15, 0, new HunterTableRequirement(0, 10, 1, 0, 1, ModItems.HUNTER_INTEL_2));
    private static final HunterTrainerRequirement LEVEL_8 = new HunterTrainerRequirement(8, 40, 0, new HunterTableRequirement(1, 0, 1, 1, 1, ModItems.HUNTER_INTEL_3));
    private static final HunterTrainerRequirement LEVEL_9 = new HunterTrainerRequirement(9, 20, 10, new HunterTableRequirement(1, 15, 1, 1, 1, ModItems.HUNTER_INTEL_4));
    private static final HunterTrainerRequirement LEVEL_10 = new HunterTrainerRequirement(10, 20, 20, new HunterTableRequirement(2, 20, 1, 2, 1, ModItems.HUNTER_INTEL_5));
    private static final HunterTrainerRequirement LEVEL_11 = new HunterTrainerRequirement(11, 20, 10, new HunterTableRequirement(2, 20, 1, 2, 1, ModItems.HUNTER_INTEL_6));
    private static final HunterTrainerRequirement LEVEL_12 = new HunterTrainerRequirement(12, 30, 10, new HunterTableRequirement(3, 20, 1, 3, 1, ModItems.HUNTER_INTEL_7));
    private static final HunterTrainerRequirement LEVEL_13 = new HunterTrainerRequirement(13, 40, 20, new HunterTableRequirement(3, 25, 2, 3, 1, ModItems.HUNTER_INTEL_8));
    private static final HunterTrainerRequirement LEVEL_14 = new HunterTrainerRequirement(14, 40, 40, new HunterTableRequirement(3, 25, 2, 4, 1, ModItems.HUNTER_INTEL_9));

    private static final HunterLevelRequirement[] LEVEL_REQUIREMENTS = {null, null, LEVEL_2, LEVEL_3, LEVEL_4, LEVEL_5, LEVEL_6, LEVEL_7, LEVEL_8, LEVEL_9, LEVEL_10, LEVEL_11, LEVEL_12, LEVEL_13, LEVEL_14};

    public static Optional<HunterLevelRequirement> getLevelRequirement(@Range(from = 2, to = REFERENCE.HIGHEST_HUNTER_LEVEL) int targetLevel) {
        return Optional.ofNullable(LEVEL_REQUIREMENTS[targetLevel]);
    }

    public static Optional<HunterTrainerRequirement> getTrainerRequirement(@Range(from = 2, to = REFERENCE.HIGHEST_HUNTER_LEVEL) int targetLevel) {
        return Optional.ofNullable(LEVEL_REQUIREMENTS[targetLevel]).filter(HunterTrainerRequirement.class::isInstance).map(HunterTrainerRequirement.class::cast);
    }

    public static Optional<BasicHunterRequirement> getBasicHunterRequirement(@Range(from = 2, to = REFERENCE.HIGHEST_HUNTER_LEVEL) int targetLevel) {
        return Optional.ofNullable(LEVEL_REQUIREMENTS[targetLevel]).filter(BasicHunterRequirement.class::isInstance).map(BasicHunterRequirement.class::cast);
    }

    public static Collection<BasicHunterRequirement> getBasicHunterRequirements() {
        return Arrays.stream(LEVEL_REQUIREMENTS).filter(BasicHunterRequirement.class::isInstance).map(BasicHunterRequirement.class::cast).toList();
    }

    public static Collection<HunterTrainerRequirement> getTrainerRequirements() {
        return Arrays.stream(LEVEL_REQUIREMENTS).filter(HunterTrainerRequirement.class::isInstance).map(HunterTrainerRequirement.class::cast).toList();
    }

    public interface HunterLevelRequirement {
        int targetLevel();
    }

    public record HunterTableRequirement(int requiredTableTier, int bookQuantity, int vampireFangQuantity, int pureBloodQuantity, @Range(from = 0, to = 4) int pureBloodLevel, int vampireBookQuantity, Supplier<HunterIntelItem> resultIntelItem) {

        public HunterTableRequirement(int requiredTableTier, int fangs, int blood, int pureBloodLevel, int vampireBook, Supplier<HunterIntelItem> intel) {
            this(requiredTableTier, 1, fangs, blood, pureBloodLevel, vampireBook, intel);
        }
    }

    public record HunterTrainerRequirement(int targetLevel, int ironQuantity, int goldQuantity, HunterTableRequirement tableRequirement) implements HunterLevelRequirement {
    }

    public record BasicHunterRequirement(int targetLevel, int vampireBloodAmount) implements HunterLevelRequirement {
    }
}
