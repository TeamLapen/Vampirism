package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.data.reloadlistener.ConvertiblesReloadListener;
import de.teamlapen.vampirism.entity.converted.converter.DefaultConverter;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VampirismEntityRegistry implements IVampirismEntityRegistry {
    /**
     * Filled after ResourceManager reload (after 1 tick in game)
     * Stores biteable values
     */
    @NotNull
    public static final BiteableEntryManager biteableEntryManager = new BiteableEntryManager();
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Used to store convertible handlers after {@link net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent}
     */
    @NotNull
    private final Map<EntityType<? extends PathfinderMob>, IConvertingHandler<?>> convertibles = new HashMap<>();
    @NotNull
    protected final Map<EntityType<? extends PathfinderMob>, ResourceLocation> convertibleOverlay = new HashMap<>();
    protected final Map<String, ResourceLocation> convertibleIdOverlay = new HashMap<>();

    @NotNull
    private final Map<EntityType<? extends PathfinderMob>, IConvertingHandler<?>> convertiblesAPI = new ConcurrentHashMap<>();
    @NotNull
    protected final Map<EntityType<? extends PathfinderMob>, ResourceLocation> convertibleOverlayAPI = new ConcurrentHashMap<>();
    /**
     * Stores custom extended creature constructors after {@link net.neoforged.fml.event.lifecycle.InterModEnqueueEvent}
     */
    private final Map<Class<? extends PathfinderMob>, Function<? extends PathfinderMob, IExtendedCreatureVampirism>> extendedCreatureConstructors = new ConcurrentHashMap<>();
    private final BiFunction<IConvertingHandler.IDefaultHelper, @Nullable ResourceLocation, IConvertingHandler<?>> defaultConvertingHandlerCreator;

    /**
     * denies convertible addition after {@link net.neoforged.fml.event.lifecycle.InterModProcessEvent}
     */
    private boolean finished = false;

    public VampirismEntityRegistry(BiFunction<IConvertingHandler.IDefaultHelper, @Nullable ResourceLocation, IConvertingHandler<?>> creator) {
        this.defaultConvertingHandlerCreator = creator;
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends PathfinderMob> type, ResourceLocation overlayLocation) {
        addConvertible(type, overlayLocation, new DefaultConvertingHandler<>(null, overlayLocation));
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends PathfinderMob> type, ResourceLocation overlay_loc, IConvertingHandler.IDefaultHelper helper) {
        addConvertible(type, overlay_loc, defaultConvertingHandlerCreator.apply(helper, overlay_loc));
    }

    @Override
    @ThreadSafeAPI
    public void addConvertible(EntityType<? extends PathfinderMob> type, @Deprecated @Nullable ResourceLocation overlay_loc, @NotNull IConvertingHandler<?> handler) {
        if (finished) throw new IllegalStateException("Register convertibles during InterModEnqueueEvent");
        convertiblesAPI.put(type, handler);
        convertibleOverlayAPI.put(type, overlay_loc);
    }

    @Override
    @ThreadSafeAPI
    public <T extends PathfinderMob> void addCustomExtendedCreature(Class<? extends T> clazz, Function<T, IExtendedCreatureVampirism> constructor) {
        if (finished) throw new IllegalStateException("Register extended creatures during InterModEnqueueEvent");
        extendedCreatureConstructors.put(clazz, constructor);
    }

    public void applyNewResources(@NotNull Map<ResourceLocation, Float> valuesIn) {
        Map<ResourceLocation, Float> values = Maps.newHashMap(valuesIn);
        Map<ResourceLocation, BiteableEntry> biteables = Maps.newHashMap();
        Set<ResourceLocation> blacklist = Sets.newHashSet();
        final IConvertingHandler<?> defaultHandler = defaultConvertingHandlerCreator.apply(null, null);
        for (Map.Entry<EntityType<? extends PathfinderMob>, IConvertingHandler<?>> entry : convertibles.entrySet()) {
            ResourceLocation id = RegUtil.id(entry.getKey());
            if (id == null) {
                LOGGER.warn("Cannot register convertible {} since there is no EntityString for it", entry.getKey());
                continue;
            }
            Float bloodF = values.remove(id);
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
        for (Map.Entry<ResourceLocation, Float> entry : values.entrySet()) {
            if (!RegUtil.has(BuiltInRegistries.ENTITY_TYPE, entry.getKey())) continue;
            int blood = Math.abs(Math.round(entry.getValue()));
            if (blood == 0) {
                blacklist.add(entry.getKey());
            } else {
                biteables.put(entry.getKey(), new BiteableEntry(blood));
            }
        }
        biteableEntryManager.setNewBiteables(biteables, blacklist);
        VampirismMod.proxy.applyConvertibleOverlays(convertibleOverlay);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public IConvertedCreature<?> convert(@NotNull PathfinderMob entity) {
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

    @NotNull
    @Override
    public Map<EntityType<? extends PathfinderMob>, ResourceLocation> getConvertibleOverlay() {
        return convertibleOverlay;
    }

    @Override
    public @Nullable ResourceLocation getConvertibleOverlay(String sourceEntity) {
        return this.convertibleIdOverlay.get(sourceEntity);
    }

    @Unmodifiable
    public Map<EntityType<? extends PathfinderMob>, IConvertingHandler<?>> getConvertibles() {
        return Collections.unmodifiableMap(convertibles);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PathfinderMob> Function<T, IExtendedCreatureVampirism> getCustomExtendedCreatureConstructor(@NotNull T entity) {
        return (Function<T, IExtendedCreatureVampirism>) extendedCreatureConstructors.get(entity.getClass());
    }

    @Nullable
    @Override
    public BiteableEntry getEntry(@NotNull PathfinderMob creature) {
        return biteableEntryManager.get(creature);
    }

    @Override
    public @Nullable BiteableEntry getOrCreateEntry(PathfinderMob creature) {
        return biteableEntryManager.getOrCalculate(creature);
    }

    public void applyDataConvertibles(Map<EntityType<? extends PathfinderMob>, ConvertiblesReloadListener.EntityEntry> entries) {
        this.convertibles.clear();
        this.convertibleOverlay.clear();
        this.convertibles.putAll(this.convertiblesAPI);
        this.convertibleOverlay.putAll(this.convertibleOverlayAPI);
        entries.forEach((type, entry) -> {
            Optional<IConvertingHandler<?>> handler = entry.converter().map(c -> c.createHandler(entry.overlay().orElse(null)));
            this.convertibles.put(type, handler.orElseGet(() -> new DefaultConverter().createHandler(entry.overlay().orElse(null))));
            entry.overlay().ifPresent(overlay -> this.convertibleOverlay.put(type, overlay));
        });
    }

    public record DatapackHelper(ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier attributes) implements IConvertingHandler.IDefaultHelper {

        @Override
        public Map<Attribute, Pair<FloatProvider, Double>> getAttributeModifier() {
            return this.attributes.attributeModifier();
        }
    }
}