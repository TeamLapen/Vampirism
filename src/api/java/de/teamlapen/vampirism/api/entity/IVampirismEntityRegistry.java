package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

/**
 * Registration of blood values and converting handler for {@link PathfinderMob}'s
 * Adding entries is only possible during init.
 */
public interface IVampirismEntityRegistry {

    /**
     * Register a {@link EntityType} which can be converted using Vampirism's default {@link IConvertingHandler} and thereby being turned into Vampirim's default {@link IConvertedCreature}
     * Requires a blood value to be registered for that creature
     *
     * @param overlay_loc Location of the overlay texture file
     * @deprecated use data driven system
     */
    @Deprecated
    @ThreadSafeAPI
    void addConvertible(EntityType<? extends PathfinderMob> type, ResourceLocation overlay_loc);

    /**
     * Register a {@link EntityType} which can be converted using a default {@link IConvertingHandler} and thereby being turned into Vampirim's default {@link IConvertedCreature}
     * Requires a blood value to be registered for that creature
     *
     * @param helper      Helper instance for the DefaultHandler to specify some values for the converted creature
     * @param overlay_loc Location of the overlay texture file
     *
     * @deprecated use data driven system
     */
    @Deprecated
    @ThreadSafeAPI
    void addConvertible(EntityType<? extends PathfinderMob> type, ResourceLocation overlay_loc, IConvertingHandler.IDefaultHelper helper);

    /**
     * Register a {@link EntityType} which can be converted
     * Requires a blood value to be registered for that creature
     *
     * @param overlay_loc Location of the overlay texture file. Only required if Vampirism's default Converted Creature renderer is used, if you handle that stuff yourself, null is just fine.
     * @param handler     Handles the conversion
     * @deprecated use data driven system
     */
    @Deprecated
    @ThreadSafeAPI
    void addConvertible(EntityType<? extends PathfinderMob> type, ResourceLocation overlay_loc, IConvertingHandler<?> handler);

    /**
     * Registers a custom {@link IExtendedCreatureVampirism} for an entity class
     *
     * @param clazz       The entity class that should use the given constructor
     * @param constructor A 'constructor' that can be used to create the {@link IExtendedCreatureVampirism} object from the entity's object
     * @param <T>         The base class type
     */
    @ThreadSafeAPI
    <T extends PathfinderMob> void addCustomExtendedCreature(Class<? extends T> clazz, Function<T, IExtendedCreatureVampirism> constructor);

    @Nullable
    IConvertedCreature<?> convert(PathfinderMob entity);

    /**
     * @return A map mapping the overlay resource location string to e convertible entity's class
     */
    Map<EntityType<? extends PathfinderMob>, ResourceLocation> getConvertibleOverlay();

    /**
     * @return The custom constructor registered for the given entity's class. Can be null if none is registered
     */
    @Nullable <T extends PathfinderMob> Function<T, IExtendedCreatureVampirism> getCustomExtendedCreatureConstructor(T entity);

    /**
     * @return the existing bitable entry or null if there is none, or they are not yet initialized
     */
    @Nullable
    BiteableEntry getEntry(PathfinderMob creature);

    /**
     * Checks if there exists a bitable entry for the given creature. {@see #getEntry(PathfinderMob)}.
     * If not it tries to automatically calculate a blood value depending on size if applicable and not disabled
     *
     * @return a biteable entry or null if the entity is blacklisted, or they are not yet initialized
     */
    @Nullable
    BiteableEntry getOrCreateEntry(PathfinderMob creature);
}
