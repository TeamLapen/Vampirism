package de.teamlapen.vampirism.world.biome;

import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class VampireForestBiome {

    public static Biome createVampireForest(float depth, float scale) {
        return VampireForestBiome.createBiomeBuilder(depth, scale, createMobInfoBuilder(), createBiomeAmbienceBuilder()).build();
    }

    public static Biome.Builder createBiomeBuilder(float depth, float scale, MobSpawnInfo.Builder spawnBuilder, BiomeAmbience.Builder ambienceBuilder) {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder().withSurfaceBuilder(SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState())));
        DefaultBiomeFeatures.withCavesAndCanyons(builder); //carver
        VampirismBiomeFeatures.addModdedWaterLake(builder);

        VampirismBiomeFeatures.addVampireFlower(builder);
        DefaultBiomeFeatures.withForestGrass(builder);
        DefaultBiomeFeatures.withDesertDeadBushes(builder);

        DefaultBiomeFeatures.withCommonOverworldBlocks(builder); //stone variants
        DefaultBiomeFeatures.withOverworldOres(builder); //ore
        DefaultBiomeFeatures.withDisks(builder); //disks

        VampirismBiomeFeatures.addVampireTrees(builder);

        VampirismBiomeFeatures.addWaterSprings(builder);
        return new Biome.Builder().precipitation(Biome.RainType.NONE).category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.3F).downfall(0F).setEffects(ambienceBuilder.build()).withMobSpawnSettings(spawnBuilder.build()).withGenerationSettings(builder.build());
    }

    public static MobSpawnInfo.Builder createMobInfoBuilder() {
        MobSpawnInfo.Builder builder = new MobSpawnInfo.Builder();
        builder.withCreatureSpawnProbability(0.25f);
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire, VampirismConfig.BALANCE.mbVampireSpawnChance.get() / 2, 1, 3));
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire_baron, BalanceMobProps.mobProps.VAMPIRE_BARON_SPAWN_CHANCE, 1, 1));
        builder.withSpawner(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(ModEntities.blinding_bat, BalanceMobProps.mobProps.BLINDING_BAT_SPAWN_CHANCE, 2, 4));
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.dummy_creature, BalanceMobProps.mobProps.DUMMY_CREATURE_SPAWN_CHANCE, 3, 6));
        return builder;
    }

    public static BiomeAmbience.Builder createBiomeAmbienceBuilder() {
        return new BiomeAmbience.Builder().setWaterColor(0x7d0000).setWaterFogColor(0x7d0000).setFogColor(0x7d3535).withSkyColor(0x7d3535).withFoliageColor(0x1E1F1F).withGrassColor(0x1E1F1F).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE);
    }
}
