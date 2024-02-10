package de.teamlapen.vampirism.api.extensions;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface IEntity {


    /**
     * Get the entity that this interface represents
     *
     * @return The entity
     */
    @NotNull
    Entity asEntity();
}
