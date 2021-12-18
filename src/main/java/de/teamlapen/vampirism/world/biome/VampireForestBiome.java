package de.teamlapen.vampirism.world.biome;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;

public class VampireForestBiome {

    public static Biome createVampireForest() {
        return VampireForestBiome.createBiomeBuilder(createMobInfoBuilder(), createBiomeAmbienceBuilder()).build();
    }

    public static Biome.BiomeBuilder createBiomeBuilder(MobSpawnSettings.Builder spawnBuilder, BiomeSpecialEffects.Builder ambienceBuilder) {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder();
        VampirismBiomeFeatures.addDefaultCarversWithoutLakes(builder);
        VampirismBiomeFeatures.addModdedWaterLake(builder);

        VampirismBiomeFeatures.addVampireFlower(builder);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VampirismBiomeFeatures.forest_grass_placed);

        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultOres(builder);
        BiomeDefaultFeatures.addDefaultSoftDisks(builder);

        VampirismBiomeFeatures.addVampireTrees(builder);

        VampirismBiomeFeatures.addWaterSprings(builder);
        return new Biome.BiomeBuilder().precipitation(Biome.Precipitation.NONE).biomeCategory(Biome.BiomeCategory.FOREST).temperature(0.3F).downfall(0F).specialEffects(ambienceBuilder.build()).mobSpawnSettings(spawnBuilder.build()).generationSettings(builder.build());
    }

    public static MobSpawnSettings.Builder createMobInfoBuilder() {
        MobSpawnSettings.Builder builder = new MobSpawnSettings.Builder();
        builder.creatureGenerationProbability(0.25f);
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.vampire, 35, 1, 3));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.vampire_baron, VampirismConfig.COMMON.baronSpawnChance.get(), 1, 1));
        builder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(ModEntities.blinding_bat, 60, 2, 4));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(ModEntities.dummy_creature, 80, 3, 6));
        return builder;
    }

    public static BiomeSpecialEffects.Builder createBiomeAmbienceBuilder() {
        return new BiomeSpecialEffects.Builder().waterColor(0x7d0000).waterFogColor(0x7d0000).fogColor(0x7d3535).skyColor(0x7d3535).foliageColorOverride(0x1E1F1F).grassColorOverride(0x2c2132).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS);
    }
}
