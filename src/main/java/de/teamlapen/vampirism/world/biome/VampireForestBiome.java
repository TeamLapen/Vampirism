package de.teamlapen.vampirism.world.biome;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class VampireForestBiome {

    public static Biome createVampireForest(float depth, float scale) {
        return VampireForestBiome.createBiomeBuilder(depth, scale, createMobInfoBuilder(), createBiomeAmbienceBuilder()).build();
    }

    public static Biome.Builder createBiomeBuilder(float depth, float scale, MobSpawnInfo.Builder spawnBuilder, BiomeAmbience.Builder ambienceBuilder) {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder().surfaceBuilder(SurfaceBuilder.DEFAULT.configured(new SurfaceBuilderConfig(ModBlocks.cursed_grass.defaultBlockState(), ModBlocks.cursed_earth.defaultBlockState(), ModBlocks.cursed_earth.defaultBlockState())));
        DefaultBiomeFeatures.addDefaultCarvers(builder); //carver
        VampirismBiomeFeatures.addModdedWaterLake(builder);

        VampirismBiomeFeatures.addVampireFlower(builder);
        VampirismBiomeFeatures.addBushPatch(builder);
        DefaultBiomeFeatures.addForestGrass(builder);

        VampirismBiomeFeatures.addUndergroundVariety(builder);
        DefaultBiomeFeatures.addDefaultOres(builder); //ore
        DefaultBiomeFeatures.addDefaultSoftDisks(builder); //disks
        VampirismBiomeFeatures.addDarkStoneSoftDisk(builder); //disks

        VampirismBiomeFeatures.addVampireTrees(builder);

        VampirismBiomeFeatures.addWaterSprings(builder);
        return new Biome.Builder().precipitation(Biome.RainType.NONE).biomeCategory(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.3F).downfall(0F).specialEffects(ambienceBuilder.build()).mobSpawnSettings(spawnBuilder.build()).generationSettings(builder.build());
    }

    public static MobSpawnInfo.Builder createMobInfoBuilder() {
        MobSpawnInfo.Builder builder = new MobSpawnInfo.Builder();
        builder.creatureGenerationProbability(0.25f);
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire, 35, 1, 3));
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire_baron, VampirismConfig.COMMON.baronSpawnChance.get(), 1, 1));
        builder.addSpawn(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(ModEntities.blinding_bat, 60, 2, 4));
        builder.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.dummy_creature, 80, 3, 6));
        return builder;
    }

    public static BiomeAmbience.Builder createBiomeAmbienceBuilder() {
        return new BiomeAmbience.Builder()
                .waterColor(0x670717).waterFogColor(0x670717).fogColor(0x171717).skyColor(0x131313)
                .foliageColorOverride(0x101010).grassColorOverride(0x101010).ambientMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D)).backgroundMusic(BackgroundMusicTracks.createGameMusic(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST));
    }

}
