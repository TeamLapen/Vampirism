package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IFoodData;

@Deprecated
public interface IFoodDataVampirismMock extends IFoodData {
    @Override
    default float getExhaustionLevel() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void setExhaustionLevel(float exhaustionLevel) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
