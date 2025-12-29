package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ISlot;
import net.minecraft.world.Container;

@Deprecated
public interface ISlotVampirismMock extends ISlot {

    @Override
    default void setContainer(Container container) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
