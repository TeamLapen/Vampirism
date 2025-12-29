package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IItemCombinerMenu;
import net.minecraft.world.Container;

@Deprecated
public interface IItemCombinerMenuVampirismMock extends IItemCombinerMenu {
    @Override
    default Container getInputSlots() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void setInputSlots(Container inputSlots) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
