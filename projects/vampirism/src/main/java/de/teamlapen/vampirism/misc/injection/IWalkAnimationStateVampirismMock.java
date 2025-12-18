package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IWalkAnimationState;

@Deprecated
public interface IWalkAnimationStateVampirismMock extends IWalkAnimationState {
    @Override
    default float oldSpeed() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void oldSpeed(float speedOld) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
