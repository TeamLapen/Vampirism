package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ILayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;

import java.util.List;

@Deprecated
public interface ILayeredRegistryAccessVampirismMock extends ILayeredRegistryAccess {

    @Override
    default void vampirism$setValues(List<RegistryAccess.Frozen> values) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
