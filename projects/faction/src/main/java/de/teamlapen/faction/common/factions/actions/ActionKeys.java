package de.teamlapen.faction.common.factions.actions;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public enum ActionKeys implements StringRepresentable {
    ACTION_1(49), // GLFW.GLFW_KEY_1
    ACTION_2(50), // GLFW.GLFW_KEY_2
    ACTION_3(51), // GLFW.GLFW_KEY_3
    ACTION_4,
    ACTION_5,
    ACTION_6,
    ACTION_7,
    ACTION_8,
    ACTION_9;

    public static final Codec<ActionKeys> CODEC = Codec.INT.xmap(x -> ActionKeys.values()[x], ActionKeys::ordinal);
    public static final Codec<ActionKeys> STRING_CODEC = StringRepresentable.fromEnum(ActionKeys::values);

    private final OptionalInt defaultKey;

    ActionKeys() {
        this(OptionalInt.empty());
    }

    ActionKeys(int defaultKey) {
        this(OptionalInt.of(defaultKey));
    }

    ActionKeys(OptionalInt defaultKey) {
        this.defaultKey = defaultKey;
    }

    public OptionalInt getDefaultKey() {
        return defaultKey;
    }

    @Override
    public String getSerializedName() {
        return name();
    }
}
