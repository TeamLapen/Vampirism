package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IFoodData;

public interface IFoodDataMock extends IFoodData {
    @Override
    default float getExhaustionLevel() {
        return 0;
    }

    @Override
    default void setExhaustionLevel(float exhaustionLevel) {

    }
}
