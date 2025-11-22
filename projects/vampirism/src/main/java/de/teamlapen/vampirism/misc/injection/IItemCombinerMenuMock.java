package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IItemCombinerMenu;
import net.minecraft.world.Container;

public interface IItemCombinerMenuMock extends IItemCombinerMenu {
    @Override
    default Container getInputSlots() {
        return null;
    }

    @Override
    default void setInputSlots(Container inputSlots) {

    }
}
