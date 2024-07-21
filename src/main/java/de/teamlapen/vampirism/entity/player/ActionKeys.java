package de.teamlapen.vampirism.entity.player;

import java.util.OptionalInt;

public enum ActionKeys {
    ACTION_1(49), // GLFW.GLFW_KEY_1
    ACTION_2(50), // GLFW.GLFW_KEY_2
    ACTION_3(51), // GLFW.GLFW_KEY_3
    ACTION_4,
    ACTION_5,
    ACTION_6,
    ACTION_7,
    ACTION_8,
    ACTION_9;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final OptionalInt defaultKey;

    ActionKeys() {
        this(OptionalInt.empty());
    }

    ActionKeys(int defaultKey) {
        this(OptionalInt.of(defaultKey));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    ActionKeys(OptionalInt defaultKey) {
        this.defaultKey = defaultKey;
    }

    public OptionalInt getDefaultKey() {
        return defaultKey;
    }
}
