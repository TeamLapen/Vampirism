package de.teamlapen.vampirism.common.world.biomes;

import de.teamlapen.vampirism.api.VEnums;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.features.VampirismFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.*;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class VampirismBiomes {

    public static @NotNull Biome createVampireForest(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        MobSpawnSettings.Builder mobSpawnBuilder = new MobSpawnSettings.Builder();
        mobSpawnBuilder.creatureGenerationProbability(0.25f);
        mobSpawnBuilder.addSpawn(VEnums.VAMPIRE_CATEGORY.getValue(), 80, new MobSpawnSettings.SpawnerData(ModEntities.VAMPIRE.get(), 1, 3));
        mobSpawnBuilder.addSpawn(VEnums.VAMPIRE_CATEGORY.getValue(), 27, new MobSpawnSettings.SpawnerData(ModEntities.VAMPIRE_BARON.get(), 1, 1));
        mobSpawnBuilder.addSpawn(MobCategory.AMBIENT, 60, new MobSpawnSettings.SpawnerData(ModEntities.BLINDING_BAT.get(), 2, 4));
        mobSpawnBuilder.addSpawn(MobCategory.CREATURE, 80, new MobSpawnSettings.SpawnerData(ModEntities.DUMMY_CREATURE.get(), 3, 6));

        BiomeSpecialEffects.Builder biomeSpecialEffectsBuilder = new BiomeSpecialEffects.Builder().waterColor(0x670717).foliageColorOverride(0x101010).grassColorOverride(0x101010);

        var builder = prepareVampireForestBuilder(featureGetter, carverGetter, mobSpawnBuilder, biomeSpecialEffectsBuilder)
                .setAttribute(EnvironmentAttributes.FOG_COLOR, 0x171717)
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x670717)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, 0x131313)
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, new AmbientSounds(
                        Optional.empty(),
                        Optional.of(new AmbientMoodSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)),
                        List.of(new AmbientAdditionsSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D))))
                ;

        ModSounds.VAMPIRE_FOREST_AMBIENT.asOptional().map(BuiltInRegistries.SOUND_EVENT::wrapAsHolder).ifPresent(soundEvent -> builder.setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(soundEvent)));

        return builder.build();
    }

    public static Biome.@NotNull BiomeBuilder prepareVampireForestBuilder(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter, MobSpawnSettings.@NotNull Builder spawnBuilder, BiomeSpecialEffects.@NotNull Builder ambienceBuilder) {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder); //carver
        addModdedWaterLake(builder);

        addVampireFlower(builder);
        addBushPatch(builder);
        BiomeDefaultFeatures.addForestGrass(builder);

        addUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultOres(builder); //ore
        BiomeDefaultFeatures.addDefaultSoftDisks(builder); //disks

        addVampireTrees(builder);

        addWaterSprings(builder);
        return new Biome.BiomeBuilder().hasPrecipitation(false).temperature(0.3F).downfall(0F).specialEffects(ambienceBuilder.build()).mobSpawnSettings(spawnBuilder.build()).generationSettings(builder.build());
    }


    public static void addVampireFlower(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.VAMPIRE_FLOWER_PLACED);
    }

    public static Biome createVelmorra(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> configuredCarvers) {

        var mobSettings = new MobSpawnSettings.Builder()
                .creatureGenerationProbability(0.25f)
                .addSpawn(MobCategory.AMBIENT, 60, new MobSpawnSettings.SpawnerData(ModEntities.BLINDING_BAT.get(), 2, 4));

        var specialEffects = new BiomeSpecialEffects.Builder()
                .waterColor(0x670717)
                .foliageColorOverride(0x101010)
                .grassColorOverride(0x101010);

        var generation = new BiomeGenerationSettings.Builder(placedFeatures, configuredCarvers);
        BiomeDefaultFeatures.addDefaultCarversAndLakes(generation);

        addVampireFlower(generation);
        addBushPatch(generation);
        BiomeDefaultFeatures.addForestGrass(generation);

        addUndergroundVariety(generation);

        BiomeDefaultFeatures.addDefaultSoftDisks(generation);

        addVampireTreesSparse(generation);

        addWaterSprings(generation);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.6f)
                .downfall(0)
                .specialEffects(specialEffects.build())
                .mobSpawnSettings(mobSettings.build())
                .generationSettings(generation.build())
                .build();
    }

    public static void addWaterSprings(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_WATER);
    }

    public static void addModdedWaterLake(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.LAKES, VampirismFeatures.WATER_LAKE_PLACED);
    }

    public static void addVampireTrees(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.VAMPIRE_TREES_PLACED);
    }

    public static void addVampireTreesSparse(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.VAMPIRE_TREES_SPARSE_PLACED);
    }

    public static void addUndergroundVariety(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_GRAVEL);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VampirismFeatures.ORE_CURSED_DIRT_PLACED);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VampirismFeatures.ORE_DARK_STONE_PLACED);
    }

    public static void addBushPatch(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.CURSED_ROOT_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.FOREST_GRASS_PLACED);
    }

}