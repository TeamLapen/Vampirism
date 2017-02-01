package de.teamlapen.vampirism.api;

/**
 * Can be used to determine which strength/tier something is of. e.g. used for holy water and garlic
 */
public enum EnumStrength {
    NONE(0), WEAK(1), MEDIUM(2), STRONG(3);

    public static EnumStrength getFromStrenght(int strength) {
        for (EnumStrength s : values()) {
            if (s.strength == strength) {
                return s;
            }
        }
        return NONE;
    }
    final int strength;

    EnumStrength(int strength) {
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    /**
     * If this strength is stronger than the given one.
     */
    public boolean isStrongerThan(EnumStrength compare) {
        return this.strength > compare.strength;
    }
}
