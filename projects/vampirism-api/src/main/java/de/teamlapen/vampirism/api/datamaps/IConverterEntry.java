package de.teamlapen.vampirism.api.datamaps;

import de.teamlapen.vampirism.api.world.entity.convertible.Converter;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public interface IConverterEntry {

    /**
     * @return The converter for this entity
     */
    Converter converter();

}
