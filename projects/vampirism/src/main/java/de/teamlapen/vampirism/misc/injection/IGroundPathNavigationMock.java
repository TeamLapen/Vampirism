package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IGroundPathNavigation;

public interface IGroundPathNavigationMock extends IGroundPathNavigation {
    @Override
    default boolean getAvoidSun() {
        return false;
    }
}
