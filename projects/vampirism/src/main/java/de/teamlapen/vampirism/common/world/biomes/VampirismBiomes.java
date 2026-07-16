package de.teamlapen.vampirism.common.world.biomes;

import de.teamlapen.vampirism.api.VEnums;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModEnvironmentAttributes;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.features.VampirismFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;

import java.util.List;
import java.util.Optional;

public class VampirismBiomes {

    private static final BiomeSpecialEffects.Builder VAMPIRE_FOREST_EFFECTS = BiomeSpecialEffectsBuilder
            .create(0x670717)
            .foliageColorOverride(0x1f1f1f)
            .dryFoliageColorOverride(0x383838)
            .grassColorOverride(0x242424);

    public static Biome createVampireForest(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder mobSpawns = new MobSpawnSettings.Builder();

        mobSpawns.creatureGenerationProbability(0.25f);
        mobSpawns.addSpawn(VEnums.VAMPIRE_CATEGORY.getValue(), 80, spawnerData(ModEntities.VAMPIRE.get(), 1, 3));
        mobSpawns.addSpawn(VEnums.VAMPIRE_CATEGORY.getValue(), 27, spawnerData(ModEntities.VAMPIRE_BARON.get(), 1, 1));
        mobSpawns.addSpawn(MobCategory.AMBIENT, 60, spawnerData(ModEntities.BLINDING_BAT.get(), 2, 4));
        mobSpawns.addSpawn(MobCategory.CREATURE, 80, spawnerData(ModEntities.DUMMY_CREATURE.get(), 3, 6));

        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);

        BiomeDefaultFeatures.addDefaultCarversAndLakes(generationSettings);

        addVampireFlower(generationSettings);
        addBushPatch(generationSettings);
        BiomeDefaultFeatures.addBushes(generationSettings);
        BiomeDefaultFeatures.addForestGrass(generationSettings);
        addVampireTrees(generationSettings);
        addWaterSprings(generationSettings);

        addUndergroundVariety(generationSettings);
        BiomeDefaultFeatures.addDefaultOres(generationSettings);
        BiomeDefaultFeatures.addDefaultSoftDisks(generationSettings);

        Biome.BiomeBuilder builder = new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.3f)
                .downfall(0.6f)
                .specialEffects(VAMPIRE_FOREST_EFFECTS.build())
                .mobSpawnSettings(mobSpawns.build())
                .generationSettings(generationSettings.build());

        builder.setAttribute(EnvironmentAttributes.FOG_COLOR, 0x171717)
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x670717)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, 0x131313)
                .setAttribute(ModEnvironmentAttributes.SUN_DAMAGE.get(), false)
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, new AmbientSounds(
                        Optional.empty(),
                        Optional.of(new AmbientMoodSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)),
                        List.of(new AmbientAdditionsSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D))));

        ModSounds.VAMPIRE_FOREST_AMBIENT.asOptional().map(BuiltInRegistries.SOUND_EVENT::wrapAsHolder).ifPresent(soundEvent -> builder.setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(soundEvent)));

        return builder.build();
    }

    public static Biome createVelmorra(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        MobSpawnSettings.Builder mobSpawns = new MobSpawnSettings.Builder();

        mobSpawns.creatureGenerationProbability(0.5f);
        mobSpawns.addSpawn(MobCategory.AMBIENT, 60, spawnerData(ModEntities.BLINDING_BAT.get(), 2, 4));
        mobSpawns.addSpawn(VEnums.VAMPIRE_CATEGORY.getValue(), 60, spawnerData(ModEntities.VAMPIRE.get(), 1, 2));
        mobSpawns.addSpawn(VEnums.VAMPIRE_CATEGORY.getValue(), 27, spawnerData(ModEntities.ADVANCED_VAMPIRE.get(), 1, 2));

        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers);

        BiomeDefaultFeatures.addDefaultCarversAndLakes(generationSettings);

        addVampireFlower(generationSettings);
        addBushPatch(generationSettings);
        BiomeDefaultFeatures.addBushes(generationSettings);
        BiomeDefaultFeatures.addForestGrass(generationSettings);
        addVampireTreesSparse(generationSettings);
        addWaterSprings(generationSettings);

        addUndergroundVariety(generationSettings);
        BiomeDefaultFeatures.addDefaultSoftDisks(generationSettings);

        Biome.BiomeBuilder builder = new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.6f)
                .downfall(0)
                .specialEffects(VAMPIRE_FOREST_EFFECTS
                        .foliageColorOverride(0x101010)
                        .build())
                .mobSpawnSettings(mobSpawns.build())
                .generationSettings(generationSettings.build());

        builder.setAttribute(EnvironmentAttributes.FOG_COLOR, 0x171717)
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x670717)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, 0x131313)
                .setAttribute(ModEnvironmentAttributes.SUN_DAMAGE.get(), false);

        return builder.build();
    }

    public static void addVampireFlower(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.VAMPIRE_FLOWER_PLACED);
    }

    public static void addWaterSprings(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_WATER);
    }

    public static void addVampireTrees(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.VAMPIRE_TREES_PLACED);
    }

    public static void addVampireTreesSparse(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.VAMPIRE_TREES_SPARSE_PLACED);
    }

    public static void addUndergroundVariety(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, OrePlacements.ORE_GRAVEL);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VampirismFeatures.ORE_CURSED_DIRT_PLACED);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, VampirismFeatures.GIANT_DARK_STONE_ROOT_PLACED);
    }

    public static void addBushPatch(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.CURSED_ROOT_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.VAMPIRE_FOREST_GRASS_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.VAMPIRE_FOREST_TALL_GRASS_PLACED);
    }

    private static MobSpawnSettings.SpawnerData spawnerData(EntityType<?> type, int minCount, int maxCount) {
            return new MobSpawnSettings.SpawnerData(type, minCount, maxCount);
    }
}