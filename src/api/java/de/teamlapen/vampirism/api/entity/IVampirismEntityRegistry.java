package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.datamaps.IConverterEntry;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import de.teamlapen.vampirism.api.VampirismRegistries;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/**
 * Registry for entity blood values and converting handler
 * <br>
 * <br>
 * {@link de.teamlapen.vampirism.api.datamaps.IEntityBlood} can be optioned here as well as converting handler and overlay texture
 * <br>
 * <br>
 * Values are loaded using the following <a href="https://docs.neoforged.net/docs/datamaps/">neoforge datamaps</a>:<br>
 * - {@link VampirismRegistries#ENTITY_BLOOD_MAP}<br>
 */
public interface IVampirismEntityRegistry {

    /**
     * Create a converted creature from the given entity.
     *
     * @apiNote This will not replace the entity in the world. It will only create a new instance of the converted creature.
     * @implSpec This will copy all relevant data from the original entity to the converted entity using {@link  de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler#createFrom(net.minecraft.world.entity.PathfinderMob)}
     * @param entity the entity to convert
     * @return the converted creature or null if the entity cannot be converted
     */
    @Nullable
    IConvertedCreature<?> convert(PathfinderMob entity);

    /**
     * @return A mapping from source entity types (not the converted one), to {@link ResourceLocation} of the overlay texture
     */
    @Unmodifiable
    @NotNull
    Map<EntityType<?>, ResourceLocation> getConvertibleOverlay();

    /**
     * Get a specific overlay texture for an entity
     *
     * @param originalEntity the string values of the original entity's registry name
     * @return the overlay texture or {@code null} if there is none
     */
    @Nullable
    ResourceLocation getConvertibleOverlay(String originalEntity);

    /**
     * Get the {@link de.teamlapen.vampirism.api.datamaps.IEntityBlood} for the given creature.
     *
     * @return the explicit entry or a calculated entry if one exists.
     */
    @Nullable
    IEntityBlood getEntry(PathfinderMob creature);

    /**
     * Get the {@link de.teamlapen.vampirism.api.datamaps.IConverterEntry} for the given creature.
     *
     * @return a value set by a datapack or null if none is set
     */
    @Nullable
    IConverterEntry getConverterEntry(PathfinderMob creature);

    /**
     * Get the {@link de.teamlapen.vampirism.api.datamaps.IEntityBlood} for the given creature.<br>
     * <br>
     * Should no explicit entry exist or one already been calculated, a new entry will be created.<br>
     * <br>
     * New entries are only calculated for entities that fit the requirement to have blood or are not explicitly excluded.<br>
     * Explicitly excluded entities are:<br>
     * - vampires<br>
     * - monster<br>
     * - water creatures<br>
     * - no animals<br>
     * - entities added to the config values of Server#blacklistedBloodEntity<br>
     * <br>
     * @implNote The blood value of entities without an explicit entry is calculated based on the entity's size and the entity's type.
     * @return a new or existing entry
     */
    @NotNull
    IEntityBlood getOrCreateEntry(PathfinderMob creature);
}
