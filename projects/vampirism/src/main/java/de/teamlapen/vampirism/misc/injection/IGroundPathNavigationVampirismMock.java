package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IGroundPathNavigation;

@Deprecated
public interface IGroundPathNavigationVampirismMock extends IGroundPathNavigation {
    @Override
    default boolean getAvoidSun() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
