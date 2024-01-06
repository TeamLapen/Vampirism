package de.teamlapen.vampirism.data.reloadlistener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.data.reloadlistener.bloodvalues.BloodValueBuilder;
import de.teamlapen.vampirism.data.reloadlistener.bloodvalues.BloodValueReader;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import java.util.stream.Collectors;

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
        Registry<EntityType<?>> entityTypes = BuiltInRegistries.ENTITY_TYPE;
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
                    EntityType<? extends PathfinderMob> entity = (EntityType<? extends PathfinderMob>) entityTypes.get(entityName);
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

        public record ConvertingAttributeModifier(Map<Attribute, com.mojang.datafixers.util.Pair<FloatProvider, Double>> attributeModifier) {
            public static ConvertingAttributeModifier DEFAULT = new ConvertingAttributeModifier(
                    Map.of(
                            Attributes.ATTACK_DAMAGE, com.mojang.datafixers.util.Pair.of(ConstantFloat.of(1.3f), (double) BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG),
                            Attributes.KNOCKBACK_RESISTANCE, com.mojang.datafixers.util.Pair.of(ConstantFloat.of(1.3f), (double) BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_KNOCKBACK_RESISTANCE),
                            Attributes.MAX_HEALTH, com.mojang.datafixers.util.Pair.of(ConstantFloat.of(1.5f), (double) BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_HEALTH),
                            Attributes.MOVEMENT_SPEED, com.mojang.datafixers.util.Pair.of(ConstantFloat.of(1.2f), (double) BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_SPEED))
            );

            public ConvertingAttributeModifier(List<com.mojang.datafixers.util.Pair<Attribute, com.mojang.datafixers.util.Pair<FloatProvider, Double>>> values) {
                this(values.stream().collect(Collectors.toMap(com.mojang.datafixers.util.Pair::getFirst, com.mojang.datafixers.util.Pair::getSecond, (a, b) -> b)));
            }

            private static final Codec<com.mojang.datafixers.util.Pair<Attribute, com.mojang.datafixers.util.Pair<FloatProvider, Double>>> CODEC_PAIR = RecordCodecBuilder.create(inst -> {
                return inst.group(
                        BuiltInRegistries.ATTRIBUTE.byNameCodec().fieldOf("attribute").forGetter(com.mojang.datafixers.util.Pair::getFirst),
                        FloatProvider.CODEC.fieldOf("modifier").forGetter(s -> s.getSecond().getFirst()),
                        Codec.DOUBLE.optionalFieldOf("fallback_base", 1d).forGetter(s -> s.getSecond().getSecond())
                ).apply(inst, ((attribute, floatProvider, aDouble) -> com.mojang.datafixers.util.Pair.of(attribute, com.mojang.datafixers.util.Pair.of(floatProvider, aDouble))));
            });
            public static final Codec<ConvertingAttributeModifier> CODEC = CODEC_PAIR.listOf().xmap(
                    ConvertingAttributeModifier::new,
                    x -> x.attributeModifier.entrySet().stream().map(s -> com.mojang.datafixers.util.Pair.of(s.getKey(), com.mojang.datafixers.util.Pair.of(s.getValue().getFirst(), s.getValue().getSecond()))).collect(Collectors.toList())
            );

            public com.mojang.datafixers.util.Pair<FloatProvider, Double> modifier(Attribute attribute) {
                return attributeModifier.get(attribute);
            }

        }
    }
}
