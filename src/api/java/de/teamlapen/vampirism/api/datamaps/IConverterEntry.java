package de.teamlapen.vampirism.api.datamaps;

import de.teamlapen.vampirism.api.entity.convertible.Converter;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IConverterEntry {

    /**
     * @return The converter for this entity
     */
    Converter converter();

    /**
     * @return The texture overlay for the converted entity
     */
    Optional<ResourceLocation> overlay();
}
