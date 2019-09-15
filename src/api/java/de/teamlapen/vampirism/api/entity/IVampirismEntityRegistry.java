package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

/**
 * Registration of blood values and converting handler for {@link CreatureEntity}'s
 * Adding entries is only possible during init.
 */
public interface IVampirismEntityRegistry {

    /**
     * Register a {@link EntityType} which can be converted using Vampirism's default {@link IConvertingHandler} and thereby being turned into Vampirim's default {@link IConvertedCreature}
     * Requires a blood value to be registered for that creature
     *
     * @param type
     * @param overlay_loc Location of the overlay texture file
     */
    @ThreadSafeAPI
    void addConvertible(EntityType<? extends CreatureEntity> type, Class<? extends CreatureEntity> clazz, ResourceLocation overlay_loc);

    /**
     * Register a {@link EntityType} which can be converted using a default {@link IConvertingHandler} and thereby being turned into Vampirim's default {@link IConvertedCreature}
     * Requires a blood value to be registered for that creature
     *
     * @param type
     * @param helper      Helper instance for the DefaultHandler to specify some values for the converted creature
     * @param overlay_loc Location of the overlay texture file
     */
    @ThreadSafeAPI
    void addConvertible(EntityType<? extends CreatureEntity> type, Class<? extends CreatureEntity> clazz, ResourceLocation overlay_loc, IConvertingHandler.IDefaultHelper helper);

    /**
     * Register a {@link EntityType} which can be converted
     * Requires a blood value to be registered for that creature
     *
     * @param type
     * @param overlay_loc Location of the overlay texture file. Only required if Vampirism's default Converted Creature renderer is used, if you handle that stuff yourself, null is just fine.
     * @param handler     Handles the conversion
     */
    @ThreadSafeAPI
    void addConvertible(EntityType<? extends CreatureEntity> type, Class<? extends CreatureEntity> clazz, ResourceLocation overlay_loc, IConvertingHandler handler);

    /**
     * Registers a custom {@link IExtendedCreatureVampirism} for a entity class
     *
     * @param clazz       The entity class that should use the given constructor
     * @param constructor A 'constructor' that can be used to create the {@link IExtendedCreatureVampirism} object from the entity's object
     * @param <T>         The base class type
     */
    @ThreadSafeAPI
    <T extends CreatureEntity> void addCustomExtendedCreature(Class<? extends T> clazz, Function<T, IExtendedCreatureVampirism> constructor);

    @Nullable
    IConvertedCreature convert(CreatureEntity entity);

    /**
     * @return A map mapping the overlay resource location string to e convertible entity's class
     */
    @OnlyIn(Dist.CLIENT)
    Map<Class<? extends CreatureEntity>, ResourceLocation> getConvertibleOverlay();

    /**
     * @return The custom constructor registered for the given entity's class. Can be null if none is registered
     */
    @Nullable
    <T extends CreatureEntity> Function<T, IExtendedCreatureVampirism> getCustomExtendedCreatureConstructor(T entity);

    /**
     * Check the biteable entry for the given creature.
     * If it is null, it tries to automatically calculate a blood value depending on size if applicable and not disabled
     * (and store it for that entity registration)
     * <p>
     * Returns null otherwise
     */
    @Nullable
    BiteableEntry getEntry(CreatureEntity creature);
}
