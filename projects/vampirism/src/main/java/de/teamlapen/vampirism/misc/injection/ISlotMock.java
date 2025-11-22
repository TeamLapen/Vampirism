package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ISlot;
import net.minecraft.world.Container;

public interface ISlotMock extends ISlot {

    @Override
    default void setContainer(Container container) {

    }
}
