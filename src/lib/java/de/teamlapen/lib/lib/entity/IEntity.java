package de.teamlapen.lib.lib.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;

public interface IEntity {

    Entity asEntity();

    default RegistryAccess registryAccess() {
        return asEntity().registryAccess();
    }
}
