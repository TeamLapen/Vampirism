package de.teamlapen.vampirism.data.reloadlistener;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.world.entity.SundamageRegistry;
import io.netty.handler.codec.DecoderException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SundamageReloadListener implements PreparableReloadListener {

    public static final Identifier SUNDAMAGE_ID = VResourceLocation.mod("sundamage");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DIRECTORY = "vampirism";
    private static final String FILE_NAME = "no_sun_damage.json";
    private static final int PATH_SUFFIX_LENGTH = ".json".length();

    private final RegistryAccess registryAccess;

    public SundamageReloadListener(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull SharedState sharedState, @NotNull Executor exectutor, PreparationBarrier barrier, @NotNull Executor applyExectutor) {
        return CompletableFuture.supplyAsync(() -> load(sharedState.resourceManager())).thenCompose(barrier::wait).thenAcceptAsync(this::apply, applyExectutor);
    }

    private SundamageRegistry.Settings load(@NotNull ResourceManager manager) {
        List<RawFile> files = new ArrayList<>();
        for (Map.Entry<Identifier, List<Resource>> entry : manager.listResourceStacks(DIRECTORY, location -> location.getPath().endsWith(FILE_NAME)).entrySet()) {
            Identifier resourceName = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), entry.getKey().getPath().substring(DIRECTORY.length() + 1, entry.getKey().getPath().length() - PATH_SUFFIX_LENGTH));
            for (Resource resource : entry.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    files.add(RawFile.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(DecoderException::new));
                } catch (Exception e) {
                    LOGGER.error("Could not read single jigsaw pieces file {} from {}", resourceName, resource.sourcePackId(), e);
                }
            }
        }
        return merge(files);
    }

    private SundamageRegistry.Settings merge(List<RawFile> files) {
        Registry<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);
        List<Holder<Biome>> biomeHolder = new ArrayList<>();
        List<HolderSet<Biome>> biomeSets = new ArrayList<>();

        Registry<DimensionType> dimensions = registryAccess.lookupOrThrow(Registries.DIMENSION_TYPE);
        List<Holder<DimensionType>> dimensionHolder = new ArrayList<>();
        List<HolderSet<DimensionType>> dimensionSets = new ArrayList<>();

        Set<ResourceKey<Level>> levels = new HashSet<>();
        Set<ResourceKey<Level>> levelsPositive = new HashSet<>();

        for (RawFile file : files) {
            if (file.replace()) {
                biomeHolder.clear();
                biomeSets.clear();
                dimensionHolder.clear();
                dimensionSets.clear();
                levels.clear();
            }
            for (ExtraCodecs.TagOrElementLocation biome : file.biomes()) {
                if (biome.tag()) {
                    biomeSets.add(biomes.getOrThrow(TagKey.create(Registries.BIOME, biome.id())));
                } else {
                    biomes.get(ResourceKey.create(Registries.BIOME, biome.id())).ifPresent(biomeHolder::add);
                }
            }
            for (ExtraCodecs.TagOrElementLocation dimension : file.dimension()) {
                if (dimension.tag()) {
                    dimensionSets.add(dimensions.getOrThrow(TagKey.create(Registries.DIMENSION_TYPE, dimension.id())));
                } else {
                    dimensions.get(ResourceKey.create(Registries.DIMENSION_TYPE, dimension.id())).ifPresent(dimensionHolder::add);
                }
            }
            levels.addAll(file.levelsNoDamage());
            levelsPositive.addAll(file.levelsDamage());
        }
        biomeSets.add(HolderSet.direct(biomeHolder));
        dimensionSets.add(HolderSet.direct(dimensionHolder));
        return new SundamageRegistry.Settings(new OrHolderSet<>(biomeSets), new OrHolderSet<>(dimensionSets), levels, levelsPositive);
    }

    private void apply(SundamageRegistry.Settings file) {
        VampirismMod.services().sunDamageRegistry().applyData(file);
    }


    public record RawFile(boolean replace, List<ExtraCodecs.TagOrElementLocation> biomes, List<ExtraCodecs.TagOrElementLocation> dimension, List<ResourceKey<Level>> levelsNoDamage, List<ResourceKey<Level>> levelsDamage) {

        private static final Codec<Pair<List<ResourceKey<Level>>, List<ResourceKey<Level>>>> LEVEL_CODEC = RecordCodecBuilder.create(inst1 -> {
            return inst1.group(
                    ResourceKey.codec(Registries.DIMENSION).listOf().optionalFieldOf("no_damage", Collections.emptyList()).forGetter(Pair::getKey),
                    ResourceKey.codec(Registries.DIMENSION).listOf().optionalFieldOf("damage", Collections.emptyList()).forGetter(Pair::getValue)
            ).apply(inst1, Pair::of);
        });

        public static final Codec<RawFile> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        Codec.BOOL.optionalFieldOf("replace", false).forGetter(RawFile::replace),
                        ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("biomes", Collections.emptyList()).forGetter(RawFile::biomes),
                        ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("dimensions", Collections.emptyList()).forGetter(RawFile::dimension),
                        LEVEL_CODEC.optionalFieldOf("levels", Pair.of(Collections.emptyList(), Collections.emptyList())).forGetter(x -> Pair.of(x.levelsNoDamage(), x.levelsDamage()))

                ).apply(inst, (replace, biome, dimensions, levels) -> new RawFile(replace, biome, dimensions, levels.getLeft(), levels.getRight())));

    }
}
