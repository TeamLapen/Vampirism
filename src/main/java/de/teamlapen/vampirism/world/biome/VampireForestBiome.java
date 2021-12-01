package de.teamlapen.vampirism.world.biome;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class VampireForestBiome {

    public static Biome createVampireForest(float depth, float scale) {
        return VampireForestBiome.createBiomeBuilder(depth, scale, createMobInfoBuilder(), createBiomeAmbienceBuilder()).build();
    }

    public static Biome.BiomeBuilder createBiomeBuilder(float depth, float scale, MobSpawnSettings.Builder spawnBuilder, BiomeSpecialEffects.Builder ambienceBuilder) {
        //TODO 1.18 surfacebuilder ?  .surfaceBuilder(SurfaceBuilder.DEFAULT.configured(new SurfaceBuilderBaseConfiguration(ModBlocks.cursed_earth.defaultBlockState(), ModBlocks.cursed_earth.defaultBlockState(), ModBlocks.cursed_earth.defaultBlockState()))
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder();
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder); //TODO 1.18 right carver ?
        VampirismBiomeFeatures.addModdedWaterLake(builder);//TODO 1.18 check for right generation / or only water spring (was lake feature)

        VampirismBiomeFeatures.addVampireFlower(builder);
        BiomeDefaultFeatures.addForestGrass(builder);
        BiomeDefaultFeatures.addDesertVegetation(builder);

        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultOres(builder);
        BiomeDefaultFeatures.addDefaultSoftDisks(builder);

        VampirismBiomeFeatures.addVampireTrees(builder);

        VampirismBiomeFeatures.addWaterSprings(builder);
        //TODO 1.18 what happened to .depth(depth).scale(scale) ?
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
        return new BiomeSpecialEffects.Builder().waterColor(0x7d0000).waterFogColor(0x7d0000).fogColor(0x7d3535).skyColor(0x7d3535).foliageColorOverride(0x1E1F1F).grassColorOverride(0x1E1F1F).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS);
    }
}
