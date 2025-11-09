package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IWalkAnimationState;

public interface IWalkAnimationStateMock extends IWalkAnimationState {
    @Override
    default float oldSpeed() {
        return 0;
    }

    @Override
    default void oldSpeed(float speedOld) {

    }
}
