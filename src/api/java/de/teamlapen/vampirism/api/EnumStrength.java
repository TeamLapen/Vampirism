package de.teamlapen.vampirism.api;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Can be used to determine which strength/tier something is of. e.g. used for holy water and garlic
 */
public enum EnumStrength implements StringRepresentable {
    NONE("none",0),
    WEAK("weak", 1),
    MEDIUM("medium", 2),
    STRONG("strong", 3);

    public static @NotNull EnumStrength getFromStrength(int strength) {
        for (EnumStrength s : values()) {
            if (s.strength == strength) {
                return s;
            }
        }
        return NONE;
    }

    public static EnumStrength byName(String name) {
        for (EnumStrength s : values()) {
            if (s.name.equals(name)) {
                return s;
            }
        }
        return NONE;
    }

    private final String name;
    final int strength;

    EnumStrength(String name, int strength) {
        this.name = name;
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    /**
     * If this strength is stronger than the given one.
     */
    public boolean isStrongerThan(@NotNull EnumStrength compare) {
        return this.strength > compare.strength;
    }


    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
