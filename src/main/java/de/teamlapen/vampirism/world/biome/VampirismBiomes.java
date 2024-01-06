package de.teamlapen.vampirism.world.biome;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

public class VampirismBiomes {

    public static @NotNull Biome createVampireForest(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        MobSpawnSettings.Builder mobSpawnBuilder = new MobSpawnSettings.Builder();
        mobSpawnBuilder.creatureGenerationProbability(0.25f);
        mobSpawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.VAMPIRE.get(), 80, 1, 3));
        mobSpawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.VAMPIRE_BARON.get(), 27, 1, 1));
        mobSpawnBuilder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(ModEntities.BLINDING_BAT.get(), 60, 2, 4));
        mobSpawnBuilder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.DUMMY_CREATURE.get(), 80, 3, 6));

        BiomeSpecialEffects.Builder biomeSpecialEffectsBuilder = new BiomeSpecialEffects.Builder().waterColor(0x670717).waterFogColor(0x670717).fogColor(0x171717).skyColor(0x131313).foliageColorOverride(0x101010).grassColorOverride(0x101010)
                .ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D))
                .ambientAdditionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D));
        ModSounds.VAMPIRE_FOREST_AMBIENT.asOptional().map(BuiltInRegistries.SOUND_EVENT::wrapAsHolder).ifPresent(soundEvent -> biomeSpecialEffectsBuilder.backgroundMusic(Musics.createGameMusic(soundEvent)));

        return prepareVampireForestBuilder(featureGetter, carverGetter, mobSpawnBuilder, biomeSpecialEffectsBuilder).build();
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

    public static void addWaterSprings(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_WATER);
    }

    public static void addModdedWaterLake(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.LAKES, VampirismFeatures.WATER_LAKE_PLACED);
    }

    public static void addVampireTrees(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismFeatures.VAMPIRE_TREES_PLACED);
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

    public static void addDefaultCarversWithoutLakes(BiomeGenerationSettings.@NotNull Builder builder) {
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND);
        builder.addCarver(GenerationStep.Carving.AIR, Carvers.CANYON);
    }
}