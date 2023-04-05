package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.network.ClientboundSundamagePacket;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;


public class SundamageRegistry implements ISundamageRegistry {

    private @Nullable RegistryAccess registryAccess;
    private Settings dataSettings = Settings.EMPTY;
    private final APISettings apiSettings = new APISettings(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    private ConfigSettings configSettings = new ConfigSettings(new HashSet<>(), new HashSet<>(), new HashSet<>());
    private Set<ResourceKey<DimensionType>> noSunDamageDimensions = new HashSet<>();
    private Set<ResourceKey<Biome>> noSunDamageBiomes = new HashSet<>();
    private Set<ResourceKey<Level>> noSunDamageLevels = new HashSet<>();
    private Set<ResourceKey<Level>> sunDamageLevels = new HashSet<>();

    public void reloadSettings() {
        if (this.registryAccess != null) {
            Set<ResourceKey<Biome>> biomes = this.dataSettings.biomesWithout.stream().map(Holder::unwrapKey).flatMap(Optional::stream).collect(Collectors.toSet());
            Set<ResourceKey<DimensionType>> dimensions = this.dataSettings.dimensionWithout.stream().map(Holder::unwrapKey).flatMap(Optional::stream).collect(Collectors.toSet());
            Set<ResourceKey<Level>> noSundamageLevels = new HashSet<>(this.dataSettings.levelsWithoutSunDamage());
            Set<ResourceKey<Level>> sundamageLevels = new HashSet<>(this.dataSettings.levelsWithSunDamage());
            biomes.addAll(this.apiSettings.biomes);
            dimensions.addAll(this.apiSettings.dimensions);
            this.apiSettings.levels.forEach(level -> {
                noSundamageLevels.add(level);
                sundamageLevels.remove(level);
            });
            this.apiSettings.sundamageLevels.forEach(level -> {
                noSundamageLevels.remove(level);
                sundamageLevels.add(level);
            });
            biomes.addAll(this.configSettings.biomes);
            this.configSettings.levels.forEach(level -> {
                noSundamageLevels.add(level);
                sundamageLevels.remove(level);
            });
            this.configSettings.sundamageLevels.forEach(level -> {
                noSundamageLevels.remove(level);
                sundamageLevels.add(level);
            });
            this.noSunDamageBiomes = Collections.unmodifiableSet(biomes);
            this.noSunDamageDimensions = Collections.unmodifiableSet(dimensions);
            this.noSunDamageLevels = Collections.unmodifiableSet(noSundamageLevels);
            this.sunDamageLevels = Collections.unmodifiableSet(sundamageLevels);
            this.updateClients();
        } else {
            this.noSunDamageBiomes = Collections.emptySet();
            this.noSunDamageDimensions = Collections.emptySet();
            this.noSunDamageLevels = Collections.emptySet();
            this.sunDamageLevels = Collections.emptySet();
        }
    }

    public void updateClients() {
        VampirismMod.dispatcher.sendToAll(new ClientboundSundamagePacket(new ArrayList<>(this.noSunDamageDimensions), new ArrayList<>(this.noSunDamageBiomes), new ArrayList<>(this.noSunDamageLevels), new ArrayList<>(this.sunDamageLevels)));
    }

    public void updateClient(ServerPlayer player) {
        VampirismMod.dispatcher.sendTo(new ClientboundSundamagePacket(new ArrayList<>(this.noSunDamageDimensions), new ArrayList<>(this.noSunDamageBiomes), new ArrayList<>(this.noSunDamageLevels), new ArrayList<>(this.sunDamageLevels)), player);
    }

    public void applyData(Settings settings) {
        this.dataSettings = settings;
    }

    public void reloadConfiguration() {
        Set<ResourceKey<Biome>> biomes = VampirismConfig.SERVER.sundamageDisabledBiomes.get().stream().map(ResourceLocation::new).map(s -> ResourceKey.create(Registries.BIOME, s)).collect(Collectors.toUnmodifiableSet());
        Set<ResourceKey<Level>> levels = VampirismConfig.SERVER.sundamageDimensionsOverrideNegative.get().stream().map(ResourceLocation::new).map(s -> ResourceKey.create(Registries.DIMENSION, s)).collect(Collectors.toUnmodifiableSet());
        Set<ResourceKey<Level>> positiveLevels = VampirismConfig.SERVER.sundamageDimensionsOverridePositive.get().stream().map(ResourceLocation::new).map(s -> ResourceKey.create(Registries.DIMENSION, s)).collect(Collectors.toUnmodifiableSet());
        this.configSettings = new ConfigSettings(levels, positiveLevels, biomes);
        reloadSettings();
    }

    @Deprecated
    @Override
    public boolean getSundamageInBiome(ResourceLocation registryName) {
        return !this.noSunDamageBiomes.contains(ResourceKey.create(Registries.BIOME, registryName));
    }

    @Override
    public boolean getSundamageInDim(ResourceKey<Level> dim) {
        if (this.sunDamageLevels.contains(dim)) {
            return true;
        } else if (this.noSunDamageLevels.contains(dim)) {
            return false;
        } else {
            return VampirismConfig.SERVER.sundamageUnknownDimension.get();
        }
    }

    @Override
    public boolean hasSunDamage(@NotNull LevelAccessor levelAccessor, @NotNull BlockPos pos) {
        DimensionType dimensionType = levelAccessor.dimensionType();
        ResourceKey<Level> level = getLevel(levelAccessor);
        if (this.registryAccess != null && this.registryAccess.registry(Registries.DIMENSION_TYPE).flatMap(a -> a.getResourceKey(dimensionType)).filter(key -> this.noSunDamageDimensions.contains(key)).isPresent()) {
            return this.sunDamageLevels.contains(level);
        } else {
            if (this.noSunDamageLevels.contains(level)) {
                return false;
            } else {
                Holder<Biome> biome = levelAccessor.getBiome(pos);
                return biome.unwrapKey().filter(key -> this.noSunDamageBiomes.contains(key)).isEmpty();
            }
        }
    }

    @SafeVarargs
    @Override
    public final void addNoSundamageBiomes(ResourceKey<Biome>... biomes) {
        this.apiSettings.biomes.addAll(Arrays.asList(biomes));
    }

    @Override
    public void addNoSundamageDimensionType(ResourceKey<DimensionType> dimensionType) {
        this.apiSettings.dimensions.add(dimensionType);
    }

    @Override
    public boolean hasBiomeSundamage(ResourceKey<Biome> biome) {
        return false;
    }

    @Override
    public boolean hasDimensionTypeSundamage(ResourceKey<DimensionType> dimensionType) {
        return false;
    }

    private ResourceKey<Level> getLevel(LevelAccessor levelAccessor) {
        return levelAccessor instanceof Level level ? level.dimension() : levelAccessor instanceof WorldGenRegion worldGenRegion ? worldGenRegion.getLevel().dimension() : Level.OVERWORLD;
    }

    @Override
    public boolean isGettingSundamage(LivingEntity entity, LevelAccessor world) {
        return Helper.gettingSundamge(entity, world, null);
    }

    @Deprecated
    @Override
    public void addNoSundamageBiomes(ResourceLocation... biomes) {
        this.apiSettings.biomes.addAll(Arrays.stream(biomes).map(id -> ResourceKey.create(Registries.BIOME, id)).toList());
    }

    @Deprecated
    @Override
    public void specifySundamageForDim(ResourceKey<Level> dimension, boolean sundamage) {
        if (sundamage) {
            apiSettings.sundamageLevels.add(dimension);
            apiSettings.levels.remove(dimension);
        } else {
            apiSettings.levels.add(dimension);
            apiSettings.sundamageLevels.remove(dimension);
        }
    }

    public void applyNetworkData(@NotNull ClientboundSundamagePacket msg) {
        this.noSunDamageBiomes = Set.copyOf(msg.biomes());
        this.noSunDamageDimensions = Set.copyOf(msg.dimensions());
        this.noSunDamageLevels = Set.copyOf(msg.noSunDamageLevels());
        this.sunDamageLevels = Set.copyOf(msg.sunDamageLevels());
    }

    public void initServer(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
        this.reloadSettings();
    }

    public void removeServer() {
        this.registryAccess = null;
        this.reloadSettings();
    }


    public record Settings(HolderSet<Biome> biomesWithout, HolderSet<DimensionType> dimensionWithout, @Unmodifiable Set<ResourceKey<Level>> levelsWithoutSunDamage, @Unmodifiable Set<ResourceKey<Level>> levelsWithSunDamage) {
        private static final Settings EMPTY = new Settings(HolderSet.direct(), HolderSet.direct(), Collections.emptySet(), Collections.emptySet());
    }

    public record ConfigSettings(@Unmodifiable Set<ResourceKey<Level>> levels, @Unmodifiable Set<ResourceKey<Level>> sundamageLevels, @Unmodifiable Set<ResourceKey<Biome>> biomes) {
    }

    public record APISettings(List<ResourceKey<Biome>> biomes, List<ResourceKey<DimensionType>> dimensions, List<ResourceKey<Level>> levels, List<ResourceKey<Level>> sundamageLevels) {
    }
}
