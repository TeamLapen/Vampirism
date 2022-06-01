package de.teamlapen.vampirism.world.biome;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class VampireForestBiome {

    public static Biome createVampireForest(float depth, float scale) {
        return VampireForestBiome.createBiomeBuilder(depth, scale, createMobInfoBuilder(), createBiomeAmbienceBuilder()).build();
    }

    public static Biome.Builder createBiomeBuilder(float depth, float scale, MobSpawnInfo.Builder spawnBuilder, BiomeAmbience.Builder ambienceBuilder) {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder().surfaceBuilder(SurfaceBuilder.DEFAULT.configured(new SurfaceBuilderConfig(ModBlocks.CURSED_GRASS.get().defaultBlockState(), ModBlocks.CURSED_EARTH.get().defaultBlockState(), ModBlocks.CURSED_EARTH.get().defaultBlockState())));

        return new Biome.Builder().precipitation(Biome.RainType.NONE).biomeCategory(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.3F).downfall(0F).specialEffects(ambienceBuilder.build()).mobSpawnSettings(spawnBuilder.build()).generationSettings(builder.build());
    }

    public static void addFeatures(BiomeLoadingEvent event) {
        if (event.getName().equals(ModBiomes.VAMPIRE_FOREST.getId())) {
            BiomeGenerationSettings.Builder builder = event.getGeneration();
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
        }
    }

    public static MobSpawnInfo.Builder createMobInfoBuilder() {
        MobSpawnInfo.Builder builder = new MobSpawnInfo.Builder();
        builder.creatureGenerationProbability(0.25f);
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.VAMPIRE.get(), 35, 1, 3));
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.VAMPIRE_BARON.get(), VampirismConfig.COMMON.baronSpawnChance.get(), 1, 1));
        builder.addSpawn(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(ModEntities.BLINDING_BAT.get(), 60, 2, 4));
        builder.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.DUMMY_CREATURE.get(), 80, 3, 6));
        return builder;
    }

    public static BiomeAmbience.Builder createBiomeAmbienceBuilder() {
        return new BiomeAmbience.Builder()
                .waterColor(0x670717).waterFogColor(0x670717).fogColor(0x171717).skyColor(0x131313)
                .foliageColorOverride(0x101010).grassColorOverride(0x101010).ambientMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)).ambientAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D)).backgroundMusic(BackgroundMusicTracks.createGameMusic(SoundEvents.MUSIC_BIOME_CRIMSON_FOREST));
    }

}
