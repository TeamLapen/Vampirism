package de.teamlapen.vampirism.api;

/**
 * Represents garlic strength
 */
public enum EnumGarlicStrength {
    NONE(0), WEAK(1), MEDIUM(2), STRONG(3);
    final int strength;

    EnumGarlicStrength(int strength) {
        this.strength = strength;
    }

    /**
     * If this strength is stronger than the given one.
     */
    public boolean isStrongerThan(EnumGarlicStrength compare) {
        return this.strength > compare.strength;
    }
}
