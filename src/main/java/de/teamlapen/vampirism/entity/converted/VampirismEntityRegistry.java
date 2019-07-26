package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class VampirismEntityRegistry implements IVampirismEntityRegistry {


    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Only available after InterModProcessEvent
     * Stores biteable entries
     */
    private static @Nullable
    BiteableEntryManager biteableEntryManager;

    /**
     * Only available after InterModProcessEvent
     * <p>
     * Biteable entries are stored here
     */
    public static BiteableEntryManager getBiteableEntryManager() {
        return biteableEntryManager;
    }

    /**
     * Used to store blood values during init
     */
    private final Map<ResourceLocation, Integer> bloodValues = new ConcurrentHashMap<>();
    /**
     * Used to store overriding values during init. Will override entries in {@link #bloodValues} after init
     */
    private final Map<ResourceLocation, Integer> overridingValues = new ConcurrentHashMap<>();
    /**
     * Used to store convertible handlers during init
     */
    private final Map<EntityType<? extends CreatureEntity>, IConvertingHandler> convertibles = new ConcurrentHashMap<>();
    private final Map<Class<? extends CreatureEntity>, String> convertibleOverlay = new ConcurrentHashMap<>();
    /**
     * Stores custom extended creature constructors
     */
    private final Map<Class<? extends CreatureEntity>, Function> extendedCreatureConstructors = new ConcurrentHashMap<>();
    private boolean finished = false;
    private Function<IConvertingHandler.IDefaultHelper, IConvertingHandler> defaultConvertingHandlerCreator;

    @Override
    @ThreadSafeAPI
    public void addBloodValue(ResourceLocation entityId, int value) {
        if (finished) throw new IllegalStateException("Register blood values during InterModEnqueueEvent");
        bloodValues.put(entityId, value);
    }

    @Override
    @ThreadSafeAPI
    public void addBloodValues(Map<ResourceLocation, Integer> values) {
        if (finished) throw new IllegalStateException("Register blood values during InterModEnqueueEvent");
        bloodValues.putAll(values);
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends CreatureEntity> type, Class<? extends CreatureEntity> clazz, String overlay_loc) {
        addConvertible(type, clazz, overlay_loc, new DefaultConvertingHandler(null));
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends CreatureEntity> type, Class<? extends CreatureEntity> clazz, String overlay_loc, IConvertingHandler.IDefaultHelper helper) {
        addConvertible(type, clazz, overlay_loc, defaultConvertingHandlerCreator.apply(helper));
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends CreatureEntity> type, Class<? extends CreatureEntity> clazz, String overlay_loc, @Nonnull IConvertingHandler handler) {
        if (finished) throw new IllegalStateException("Register convertibles during InterModEnqueueEvent");
        convertibles.put(type, handler);

        if (FMLEnvironment.dist.isClient() && overlay_loc != null) {
            convertibleOverlay.put(clazz, overlay_loc);
        }
    }

    @Override
    @ThreadSafeAPI
    public <T extends CreatureEntity> void addCustomExtendedCreature(Class<? extends T> clazz, Function<T, IExtendedCreatureVampirism> constructor) {
        if (finished) throw new IllegalStateException("Register extended creatures during InterModEnqueueEvent");
        extendedCreatureConstructors.put(clazz, constructor);
    }

    @Override
    public
    @Nullable
    IConvertedCreature convert(CreatureEntity entity) {
        ResourceLocation id = new ResourceLocation(entity.getEntityString());
        BiteableEntry b = biteableEntryManager.get(id);
        if (b != null && b.convertingHandler != null) {
            return b.convertingHandler.createFrom(entity);
        }
        LOGGER.warn("Failed to find convertible entry for {}", entity);
        return null;
    }

    /**
     * Finished registration
     * Should be called during post init
     */
    public void finishRegistration() {
        if (finished) return;
        Map<ResourceLocation, BiteableEntry> biteables = Maps.newHashMap();
        Set<ResourceLocation> blacklist = Sets.newHashSet();
        bloodValues.putAll(overridingValues);
        float bloodValueMultiplier = 1;
        Integer i = bloodValues.get(new ResourceLocation("multiplier"));
        if (i != null) {
            bloodValueMultiplier = i / 10F;
        }
        final IConvertingHandler defaultHandler = defaultConvertingHandlerCreator.apply(null);
        for (Map.Entry<EntityType<? extends CreatureEntity>, IConvertingHandler> entry : convertibles.entrySet()) {
            ResourceLocation id = entry.getKey().getRegistryName();
            if (id == null) {
                LOGGER.warn("Cannot register convertible {} since there is no EntityString for it", entry.getKey());
                continue;
            }
            Integer blood = bloodValues.remove(id);
            if (blood == null) {
                LOGGER.warn("Missing blood value for convertible creature {} ({})", entry.getKey().getName(), id);
                continue;
            }
            blood = Math.round(blood * bloodValueMultiplier);
            LOGGER.info(" Registering convertible {} with blood {} and handler {}", entry.getKey().getName().getFormattedText(), blood, entry.getValue().getClass().getName());
            BiteableEntry biteEntry = new BiteableEntry(blood, (entry.getValue() == null ? defaultHandler : entry.getValue()));
            biteables.put(id, biteEntry);
        }
        for (Map.Entry<ResourceLocation, Integer> entry : bloodValues.entrySet()) {
            int blood = Math.abs(Math.round(entry.getValue() * bloodValueMultiplier));
            if (blood == 0) {
                blacklist.add(entry.getKey());
            } else {
                biteables.put(entry.getKey(), new BiteableEntry(blood));
            }
        }
        bloodValues.clear();
        convertibles.clear();
        biteableEntryManager = new BiteableEntryManager(biteables, blacklist);
        finished = true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Map<Class<? extends CreatureEntity>, String> getConvertibleOverlay() {
        return convertibleOverlay;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends CreatureEntity> Function<T, IExtendedCreatureVampirism> getCustomExtendedCreatureConstructor(T entity) {
        return extendedCreatureConstructors.get(entity.getClass());
    }

    @Nullable
    @Override
    public BiteableEntry getEntry(CreatureEntity creature) {
        return getEntry(creature.getType().getRegistryName());
    }

    @Nullable
    @Override
    public BiteableEntry getEntry(ResourceLocation entity_id) {
        return biteableEntryManager == null ? null : biteableEntryManager.get(entity_id);
    }

    @Override
    @ThreadSafeAPI
    public void overrideBloodValues(Map<ResourceLocation, Integer> values) {
        overridingValues.putAll(values);
    }

    /**
     * Set the creator for Vampirism's default converting handler
     * FOR INTERNAL USAGE ONLY
     *
     * @param
     */
    public void setDefaultConvertingHandlerCreator(Function<IConvertingHandler.IDefaultHelper, IConvertingHandler> creator) {
        defaultConvertingHandlerCreator = creator;
    }
}