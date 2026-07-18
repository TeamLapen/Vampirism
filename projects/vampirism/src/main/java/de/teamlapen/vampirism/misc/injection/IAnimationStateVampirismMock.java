package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IAnimationState;

@Deprecated
public interface IAnimationStateVampirismMock extends IAnimationState {
    @Override
    default int vampirism$startTick()  {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
