package de.teamlapen.vampirism.misc.extension;

import net.minecraft.core.RegistryAccess;

import java.util.List;

public interface ILayeredRegistryAccess {

    void vampirism$setValues(List<RegistryAccess.Frozen> values);
}
