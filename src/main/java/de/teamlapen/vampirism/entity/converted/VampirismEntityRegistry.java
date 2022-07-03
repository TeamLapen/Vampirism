package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
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
    private final Map<EntityType<? extends PathfinderMob>, IConvertingHandler<?>> convertibles = new ConcurrentHashMap<>();
    @Nonnull
    private final Map<EntityType<? extends PathfinderMob>, ResourceLocation> convertibleOverlay = new ConcurrentHashMap<>();
    @Nonnull
    private final Map<ResourceLocation, Integer> bloodValues = Maps.newHashMap();
    /**
     * Stores custom extended creature constructors after {@link InterModEnqueueEvent}
     */
    private final Map<Class<? extends PathfinderMob>, Function<? extends PathfinderMob,IExtendedCreatureVampirism>> extendedCreatureConstructors = new ConcurrentHashMap<>();
    private int bloodMultiplier = 100;
    private Function<IConvertingHandler.IDefaultHelper, IConvertingHandler<?>> defaultConvertingHandlerCreator;

    /**
     * denies convertible addition after {@link InterModProcessEvent}
     */
    private boolean finished = false;

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends PathfinderMob> type, ResourceLocation overlayLocation) {
        addConvertible(type, overlayLocation, new DefaultConvertingHandler<>(null));
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends PathfinderMob> type, ResourceLocation overlay_loc, IConvertingHandler.IDefaultHelper helper) {
        addConvertible(type, overlay_loc, defaultConvertingHandlerCreator.apply(helper));
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends PathfinderMob> type, ResourceLocation overlay_loc, @Nonnull IConvertingHandler<?> handler) {
        if (finished) throw new IllegalStateException("Register convertibles during InterModEnqueueEvent");
        convertibles.put(type, handler);

        if (FMLEnvironment.dist.isClient() && overlay_loc != null) {
            convertibleOverlay.put(type, overlay_loc);
        }
    }

    @Override
    @ThreadSafeAPI
    public <T extends PathfinderMob> void addCustomExtendedCreature(Class<? extends T> clazz, Function<T, IExtendedCreatureVampirism> constructor) {
        if (finished) throw new IllegalStateException("Register extended creatures during InterModEnqueueEvent");
        extendedCreatureConstructors.put(clazz, constructor);
    }

    public void applyNewResources(Map<ResourceLocation, Integer> valuesIn, int multiplier) {
        this.bloodValues.putAll(valuesIn);
        this.bloodMultiplier = multiplier;
        Map<ResourceLocation, BiteableEntry> biteables = Maps.newHashMap();
        Set<ResourceLocation> blacklist = Sets.newHashSet();
        float bloodValueMultiplier = bloodMultiplier / 10F;
        final IConvertingHandler<?> defaultHandler = defaultConvertingHandlerCreator.apply(null);
        for (Map.Entry<EntityType<? extends PathfinderMob>, IConvertingHandler<?>> entry : convertibles.entrySet()) {
            ResourceLocation id = RegUtil.id(entry.getKey());
            if (id == null) {
                LOGGER.warn("Cannot register convertible {} since there is no EntityString for it", entry.getKey());
                continue;
            }
            Integer blood = valuesIn.remove(id);
            if (blood == null) {
                LOGGER.warn("Missing blood value for convertible creature {} ({})", entry.getKey().getDescription(), id);
                continue;
            }
            blood = Math.round(blood * bloodValueMultiplier);
            LOGGER.debug("Registering convertible {} with blood {} and handler {}", entry.getKey().getDescription().getString(), blood, entry.getValue().getClass().getName());
            BiteableEntry biteEntry = new BiteableEntry(blood, (entry.getValue() == null ? defaultHandler : entry.getValue()));
            biteables.put(id, biteEntry);
        }
        LOGGER.info("Registered {} convertibles", biteables.size());
        for (Map.Entry<ResourceLocation, Integer> entry : valuesIn.entrySet()) {
            int blood = Math.abs(Math.round(entry.getValue() * bloodValueMultiplier));
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
    public IConvertedCreature<?> convert(PathfinderMob entity) {
        BiteableEntry b = biteableEntryManager.get(entity);
        if (b != null && b.convertingHandler != null) {
            return ((IConvertingHandler<PathfinderMob>) b.convertingHandler).createFrom(entity);
        }
        LOGGER.warn("Failed to find convertible entry for {}", entity);
        return null;
    }

    public void finishRegistration() {
        finished = true;
    }

    public int getBloodMultiplier() {
        return bloodMultiplier;
    }

    @Nonnull
    public Map<ResourceLocation, Integer> getBloodValues() {
        return bloodValues;
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public Map<EntityType<? extends PathfinderMob>, ResourceLocation> getConvertibleOverlay() {
        return convertibleOverlay;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PathfinderMob> Function<T, IExtendedCreatureVampirism> getCustomExtendedCreatureConstructor(T entity) {
        return (Function<T, IExtendedCreatureVampirism>) extendedCreatureConstructors.get(entity.getClass());
    }

    @Nullable
    @Override
    public BiteableEntry getEntry(PathfinderMob creature) {
        return biteableEntryManager.get(creature);
    }

    /**
     * Set the creator for Vampirism's default converting handler
     * FOR INTERNAL USAGE ONLY
     */
    public void setDefaultConvertingHandlerCreator(Function<IConvertingHandler.IDefaultHelper, IConvertingHandler<?>> creator) {
        defaultConvertingHandlerCreator = creator;
    }
}