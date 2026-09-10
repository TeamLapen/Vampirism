package de.teamlapen.vampirism.common.world.entity;

import com.google.common.collect.ImmutableSet;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.ISundamageRegistry;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModEnvironmentAttributes;
import de.teamlapen.vampirism.common.tags.ModBiomeTags;
import de.teamlapen.vampirism.common.tags.ModDimensionTypeTags;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class SundamageRegistry implements ISundamageRegistry {


    private final Set<ResourceKey<DimensionType>> apiDimensionTypes = new HashSet<>();
    private final Set<ResourceKey<Level>> apiDimensions = new HashSet<>();
    private final Set<ResourceKey<Biome>> apiBiomes = new HashSet<>();

    private Set<ResourceKey<DimensionType>> noDamageDimensionType = Set.of();
    private Set<ResourceKey<Level>> noDamageDimensions = Set.of();
    private Set<ResourceKey<Biome>> noDamageBiome = Set.of();

    private boolean init = false;

    @Override
    public boolean hasSunDamage(LevelAccessor levelAccessor, BlockPos pos) {
        var dimensionType = levelAccessor.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE).getResourceKey(levelAccessor.dimensionType()).orElseThrow();
        if (noDamageDimensionType.contains(dimensionType)) {
            return false;
        }
        ResourceKey<Level> level = getLevel(levelAccessor);
        if (noDamageDimensions.contains(level)) {
            return false;
        }
        Holder<Biome> biome = levelAccessor.getBiome(pos);
        if (noDamageBiome.contains(biome.getKey())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isGettingSundamage(LivingEntity entity, LevelAccessor world) {
        BlockPos position = entity.blockPosition();
        return checkEntitySundamageProperties(entity)
                && world instanceof Level level
                && hasSunDamage(level, position)
                && checkDimensionProperties(level, position);
    }

    private boolean checkEntitySundamageProperties(LivingEntity entity) {
        return !entity.isSpectator()
                && !entity.hasEffect(ModEffects.AURA_OF_DARKNESS)
                && !entity.isInRain();
    }

    private boolean checkDimensionProperties(Level level, BlockPos pos) {
        return level.environmentAttributes().getValue(ModEnvironmentAttributes.SUN_DAMAGE.get(), pos)
                && Helper.isDay(level, pos);
    }

    @SubscribeEvent
    public void onResourceManagerReload(TagsUpdatedEvent resourceManager) {
        if (init) {
            load(resourceManager.getRegistries());
        }
    }

    @SubscribeEvent
    public void onResourceManagerReload(LevelEvent.Load loadEvent) {
        this.init = true;
        load(loadEvent.getLevel().registryAccess());
    }

    @SubscribeEvent
    public void onResourceManagerReload(LevelEvent.Unload unloadEvent) {
        this.init = false;
    }

    @SafeVarargs
    @Override
    public final void addNoSundamageBiomes(ResourceKey<Biome>... biomes) {
        this.apiBiomes.addAll(Arrays.asList(biomes));
    }

    @SafeVarargs
    @Override
    public final void addNoSundamageDimensionTypes(ResourceKey<DimensionType>... dimensionTypes) {
        this.apiDimensionTypes.addAll(Arrays.asList(dimensionTypes));
    }

    @SafeVarargs
    @Override
    public final void addNoSundamageDimension(ResourceKey<Level>... dimensionTypes) {
        this.apiDimensions.addAll(Arrays.asList(dimensionTypes));
    }

    @Override
    public void reload() {
        if (init) {
            load(VampirismMod.proxy.registryAccess());
        }
    }

    private void load(RegistryAccess registries) {
        this.noDamageDimensionType = set(registries, Registries.DIMENSION_TYPE, ModDimensionTypeTags.HAS_NO_SUNDAMAGE, this.apiDimensionTypes, null);
        this.noDamageDimensions = set(registries, Registries.DIMENSION, null, this.apiDimensions, ModConfig.server().noSundamageBiomes);
        this.noDamageBiome = set(registries, Registries.BIOME, ModBiomeTags.HAS_NO_SUNDAMAGE, this.apiBiomes, ModConfig.server().noSundamageBiomes);
    }

    //<editor-fold desc="Helper">
    private <T> Set<ResourceKey<T>> set(RegistryAccess registries, ResourceKey<Registry<T>> registry, @Nullable TagKey<T> tag, Set<ResourceKey<T>> set, @Nullable ModConfigSpec.ConfigValue<List<? extends String>> configValue) {
        var dimensionBuilder = ImmutableSet.<ResourceKey<T>>builder();
        dimensionBuilder.addAll(set);
        if (tag != null) {
            registries.getOrThrow(tag).stream().map(IHolderExtension::getKey).filter(Objects::nonNull).forEach(dimensionBuilder::add);
        }
        if (configValue != null) {
            configValue.get().stream().map(x -> ResourceKey.create(registry, Identifier.parse(x))).forEach(set::add);
        }
        return dimensionBuilder.build();
    }

    @SuppressWarnings("deprecation")
    private ResourceKey<Level> getLevel(LevelAccessor levelAccessor) {
        return levelAccessor instanceof Level level ? level.dimension() : levelAccessor instanceof WorldGenRegion worldGenRegion ? worldGenRegion.getLevel().dimension() : Level.OVERWORLD;
    }
    //</editor-fold>

}
