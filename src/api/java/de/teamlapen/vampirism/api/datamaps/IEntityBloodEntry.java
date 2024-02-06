package de.teamlapen.vampirism.api.datamaps;

import de.teamlapen.vampirism.api.entity.convertible.Converter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Blood and converter extension for entities.
 * <br>
 * <br>
 * This interface is used as <a href="https://docs.neoforged.net/docs/datamaps/">neoforge datamap</a> entry for {@link de.teamlapen.vampirism.api.VampirismRegistries#ENTITY_BLOOD_VALUES}
 */
public interface IEntityBloodEntry {

    /**
     * @return The amount of blood this entity has
     */
    int blood();

    /**
     * @return The converter data for this entity
     */
    @Nullable
    IConverterEntry converter();

    /**
     * @return If this entity can be converted into a vampiric creature
     */
    boolean canBeConverted();

    /**
     * Converter entry for {@link IEntityBloodEntry}
     */
    interface IConverterEntry {

        /**
         * @return The converter for this entity
         */
        Converter converter();

        /**
         * @return The texture overlay for the converted entity
         */
        Optional<ResourceLocation> overlay();
    }
}
