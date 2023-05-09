package de.teamlapen.vampirism.data.reloadlistener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.data.reloadlistener.bloodvalues.BloodValueBuilder;
import de.teamlapen.vampirism.data.reloadlistener.bloodvalues.BloodValueReader;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ConvertiblesReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final String PATH_SUFFIX = ".json";
    private static final int PATH_SUFFIX_LENGTH = PATH_SUFFIX.length();
    private static final String DIRECTORY = "vampirism/convertibles";
    private final BloodValueReader bloodValueReader;

    public ConvertiblesReloadListener(BloodValueReader bloodValueReader) {
        this.bloodValueReader = bloodValueReader;
    }

    public @NotNull CompletableFuture<Pair<Map<EntityType<? extends PathfinderMob>, EntityEntry>, Map<String, BloodValueBuilder>>> prepare(@NotNull ResourceManager manager, Executor executor) {
        var convertibles = CompletableFuture.supplyAsync(() -> load(manager), executor);
        var bloodValues = bloodValueReader.prepare(manager, executor);
        return convertibles.thenCombine(bloodValues, Pair::of);
    }

    public void apply(Pair<Map<EntityType<? extends PathfinderMob>, EntityEntry>, Map<String, BloodValueBuilder>> entries) {
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).applyDataConvertibles(entries.getKey());
        this.bloodValueReader.load(entries.getValue());
    }

    private Map<EntityType<? extends PathfinderMob>, EntityEntry> load(ResourceManager manager) {
        IForgeRegistry<EntityType<?>> entityTypes = ForgeRegistries.ENTITY_TYPES;
        Map<EntityType<? extends PathfinderMob>, EntityEntry> values = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks(DIRECTORY, file -> file.getPath().endsWith(PATH_SUFFIX)).entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            String s = resourceLocation.getPath();
            String substring = s.substring(DIRECTORY.length() + 1, s.length() - PATH_SUFFIX_LENGTH);
            ResourceLocation resourceName = new ResourceLocation(resourceLocation.getNamespace(), substring);
            ResourceLocation entityName = new ResourceLocation(substring.replace('/', ':'));
            for (Resource resource : entry.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonElement = GSON.fromJson(reader, JsonElement.class);
                    EntityEntry entityEntry = EntityEntry.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, jsonElement)).getOrThrow(false, LOGGER::error);
                    EntityType<? extends PathfinderMob> entity = (EntityType<? extends PathfinderMob>) entityTypes.getValue(entityName);
                    values.put(entity, entityEntry);
                } catch (Exception e) {
                    LOGGER.error("Couldn't read {} converting file from {}", resourceName, resource.sourcePackId(), e);
                }
            }
        }
        return values;
    }

    public record EntityEntry(Optional<Converter> converter, Optional<ResourceLocation> overlay) {

        public static final Codec<EntityEntry> CODEC = RecordCodecBuilder.create(inst -> {
            return inst.group(
                    Converter.CODEC.optionalFieldOf("handler").forGetter(i -> i.converter),
                    ResourceLocation.CODEC.optionalFieldOf("overlay").forGetter(i -> i.overlay)
            ).apply(inst, EntityEntry::new);
        });

        public EntityEntry(Converter converter, ResourceLocation overlay) {
            this(Optional.of(converter), Optional.of(overlay));
        }

        public EntityEntry(@Nullable ResourceLocation overlay) {
            this(Optional.empty(), Optional.ofNullable(overlay));
        }

        public EntityEntry(Converter converter) {
            this(Optional.of(converter), Optional.empty());
        }

        public record Attributes(FloatProvider damageProvider, FloatProvider knockBackResistanceProvider, FloatProvider maxHealthProvider, FloatProvider convertedSpeedProvider) {
            public static Attributes DEFAULT = new Attributes(ConstantFloat.of(1.3f), ConstantFloat.of(1.3f), ConstantFloat.of(1.5f), ConstantFloat.of(1.2f));

            public static final Codec<Attributes> CODEC = RecordCodecBuilder.create(inst -> {
                return inst.group(
                        FloatProvider.CODEC.optionalFieldOf("damage_multiplikator", ConstantFloat.of(1f)).forGetter(Attributes::damageProvider),
                        FloatProvider.CODEC.optionalFieldOf("knockback_resistance_multiplikator", ConstantFloat.of(1f)).forGetter(Attributes::knockBackResistanceProvider),
                        FloatProvider.CODEC.optionalFieldOf("max_health_multiplikator", ConstantFloat.of(1f)).forGetter(Attributes::maxHealthProvider),
                        FloatProvider.CODEC.optionalFieldOf("converted_speed_multiplikator", ConstantFloat.of(1f)).forGetter(Attributes::convertedSpeedProvider)
                ).apply(inst, Attributes::new);
            });

        }
    }
}
