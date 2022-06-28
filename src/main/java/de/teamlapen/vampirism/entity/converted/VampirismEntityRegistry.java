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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
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
    /**
     * Filled after ResourceManager reload (after 1 tick in game)
     * Stores biteable entries
     */
    @Nonnull
    public static final BiteableEntryManager biteableEntryManager = new BiteableEntryManager();
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Used to store convertible handlers after {@link FMLCommonSetupEvent}
     */
    @Nonnull
    private final Map<EntityType<? extends CreatureEntity>, IConvertingHandler> convertibles = new ConcurrentHashMap<>();
    @Nonnull
    private final Map<EntityType<? extends CreatureEntity>, ResourceLocation> convertibleOverlay = new ConcurrentHashMap<>();
    /**
     * Stores custom extended creature constructors after {@link InterModEnqueueEvent}
     */
    private final Map<Class<? extends CreatureEntity>, Function> extendedCreatureConstructors = new ConcurrentHashMap<>();
    private Function<IConvertingHandler.IDefaultHelper, IConvertingHandler> defaultConvertingHandlerCreator;

    /**
     * denies convertible addition after {@link InterModProcessEvent}
     */
    private boolean finished = false;

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends CreatureEntity> type, ResourceLocation overlayLocation) {
        addConvertible(type, overlayLocation, new DefaultConvertingHandler(null));
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends CreatureEntity> type, ResourceLocation overlay_loc, IConvertingHandler.IDefaultHelper helper) {
        addConvertible(type, overlay_loc, defaultConvertingHandlerCreator.apply(helper));
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends CreatureEntity> type, ResourceLocation overlay_loc, @Nonnull IConvertingHandler handler) {
        if (finished) throw new IllegalStateException("Register convertibles during InterModEnqueueEvent");
        convertibles.put(type, handler);

        if (FMLEnvironment.dist.isClient() && overlay_loc != null) {
            convertibleOverlay.put(type, overlay_loc);
        }
    }

    @Override
    @ThreadSafeAPI
    public <T extends CreatureEntity> void addCustomExtendedCreature(Class<? extends T> clazz, Function<T, IExtendedCreatureVampirism> constructor) {
        if (finished) throw new IllegalStateException("Register extended creatures during InterModEnqueueEvent");
        extendedCreatureConstructors.put(clazz, constructor);
    }

    public void applyNewResources(Map<ResourceLocation, Float> valuesIn) {
        Map<ResourceLocation, BiteableEntry> biteables = Maps.newHashMap();
        Set<ResourceLocation> blacklist = Sets.newHashSet();
        final IConvertingHandler defaultHandler = defaultConvertingHandlerCreator.apply(null);
        for (Map.Entry<EntityType<? extends CreatureEntity>, IConvertingHandler> entry : convertibles.entrySet()) {
            ResourceLocation id = entry.getKey().getRegistryName();
            if (id == null) {
                LOGGER.warn("Cannot register convertible {} since there is no EntityString for it", entry.getKey());
                continue;
            }
            Float bloodF = valuesIn.get(id);
            if (bloodF == null) {
                LOGGER.warn("Missing blood value for convertible creature {} ({})", entry.getKey().getDescription(), id);
                continue;
            }
            int blood = Math.round(bloodF);
            LOGGER.debug("Registering convertible {} with blood {} and handler {}", entry.getKey().getDescription().getString(), blood, entry.getValue().getClass().getName());
            BiteableEntry biteEntry = new BiteableEntry(blood, (entry.getValue() == null ? defaultHandler : entry.getValue()));
            biteables.put(id, biteEntry);
        }
        LOGGER.info("Registered {} convertibles", biteables.size());
        for (Map.Entry<ResourceLocation, Float> entry : valuesIn.entrySet()) {
            int blood = Math.abs(Math.round(entry.getValue()));
            if (blood == 0) {
                blacklist.add(entry.getKey());
            } else {
                biteables.put(entry.getKey(), new BiteableEntry(blood));
            }
        }
        biteableEntryManager.setNewBiteables(biteables, blacklist);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public IConvertedCreature convert(CreatureEntity entity) {
        BiteableEntry b = biteableEntryManager.get(entity);
        if (b != null && b.convertingHandler != null) {
            return b.convertingHandler.createFrom(entity);
        }
        LOGGER.warn("Failed to find convertible entry for {}", entity);
        return null;
    }

    public void finishRegistration() {
        finished = true;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public Map<EntityType<? extends CreatureEntity>, ResourceLocation> getConvertibleOverlay() {
        return convertibleOverlay;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends CreatureEntity> Function<T, IExtendedCreatureVampirism> getCustomExtendedCreatureConstructor(T entity) {
        return extendedCreatureConstructors.get(entity.getClass());
    }

    @Nullable
    @Override
    public BiteableEntry getEntry(CreatureEntity creature) {
        return biteableEntryManager.get(creature);
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