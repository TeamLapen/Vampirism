package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all biome registrations and reference.
 */
public class ModBiomes {
    @ObjectHolder("vampirism:vampire_forest")
    public static final Biome vampire_forest = getNull();
    @ObjectHolder("vampirism:vampire_forest_hills")
    public static final Biome vampire_forest_hills = getNull();
    public static final RegistryKey<Biome> VAMPIRE_FOREST_KEY = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(REFERENCE.MODID, "vampire_forest"));
    public static final RegistryKey<Biome> VAMPIRE_FOREST_HILLS_KEY = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(REFERENCE.MODID, "vampire_forest_hills"));


    static void registerBiomes(IForgeRegistry<Biome> registry) {
        MobSpawnInfo.Builder forestSpawnBuilder = new MobSpawnInfo.Builder();
        forestSpawnBuilder.withCreatureSpawnProbability(0.25f);
        forestSpawnBuilder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire, VampirismConfig.BALANCE.mbVampireSpawnChance.get() / 2, 1, 3));
        forestSpawnBuilder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire_baron, BalanceMobProps.mobProps.VAMPIRE_BARON_SPAWN_CHANCE, 1, 1));
        forestSpawnBuilder.withSpawner(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(ModEntities.blinding_bat, BalanceMobProps.mobProps.BLINDING_BAT_SPAWN_CHANCE, 2, 4));
        forestSpawnBuilder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.dummy_creature, BalanceMobProps.mobProps.DUMMY_CREATURE_SPAWN_CHANCE, 3, 6));

        BiomeAmbience.Builder ambienceBuilder = new BiomeAmbience.Builder().setWaterColor(0x7d0000).setWaterFogColor(0x7d0000).setFogColor(0x7d3535).withSkyColor(0x7d3535).withFoliageColor(0x1E1F1F).withGrassColor(0x1E1F1F).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE);
        registry.register(getVampireForestBuilder(0.1F, 0.025F, forestSpawnBuilder, ambienceBuilder).build().setRegistryName(VAMPIRE_FOREST_KEY.getLocation()));
        registry.register(getVampireForestBuilder(0.8f, 0.5f, forestSpawnBuilder, ambienceBuilder).build().setRegistryName(VAMPIRE_FOREST_HILLS_KEY.getLocation()));

        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST_KEY.getLocation());
        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST_HILLS_KEY.getLocation());
    }

    /**
     * Only call from main thread / non parallel event
     */
    static void addBiomesToGeneratorUnsafe() {
        if (!VampirismConfig.SERVER.disableVampireForestBiomes.get()) {
            List<RegistryKey<Biome>> modList = new ArrayList<>(OverworldBiomeProvider.biomes);
            modList.add(VAMPIRE_FOREST_KEY);
            modList.add(VAMPIRE_FOREST_HILLS_KEY);
            OverworldBiomeProvider.biomes = ImmutableList.copyOf(modList);
            BiomeManager.addBiome(net.minecraftforge.common.BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ModBiomes.VAMPIRE_FOREST_KEY, VampirismConfig.BALANCE.vampireForestWeight.get()));
            BiomeManager.addBiome(net.minecraftforge.common.BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ModBiomes.VAMPIRE_FOREST_HILLS_KEY, VampirismConfig.BALANCE.vampireForestHillsWeight.get()));
        }
    }

    /**
     * Use only for adding to biome lists
     * <p>
     * Registered in mod constructor
     */
    public static void onBiomeLoadingEventAdditions(BiomeLoadingEvent event) {
        List<MobSpawnInfo.Spawners> monsterList = event.getSpawns().getSpawner(EntityClassification.MONSTER);
        if (monsterList != null && monsterList.stream().anyMatch(spawners -> spawners.type == EntityType.ZOMBIE)) {
            event.getSpawns().withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire, VampirismConfig.BALANCE.mbVampireSpawnChance.get() , 1, 2));
            event.getSpawns().withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.advanced_vampire, VampirismConfig.BALANCE.mbAdvancedVampireSpawnChance.get() , 1, 1));
        }
        event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, VampirismBiomeFeatures.vampire_dungeon);
        if (!VampirismConfig.SERVER.disableHunterTentGen.get() && VampirismAPI.worldGenRegistry().canStructureBeGeneratedInBiome(ModFeatures.hunter_camp.getRegistryName(), event.getName(), event.getCategory())) {
            event.getGeneration().withStructure(VampirismBiomeFeatures.hunter_camp);
        }


    }

    private static Biome.Builder getVampireForestBuilder(float depth, float scale, MobSpawnInfo.Builder spawnBuilder, BiomeAmbience.Builder ambienceBuilder) {
        BiomeGenerationSettings.Builder biomeGeneratorSettings = new BiomeGenerationSettings.Builder().withSurfaceBuilder(SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState())));
        DefaultBiomeFeatures.withCavesAndCanyons(biomeGeneratorSettings); //carver
        VampirismBiomeFeatures.addModdedWaterLake(biomeGeneratorSettings);

        VampirismBiomeFeatures.addVampireFlower(biomeGeneratorSettings);
        DefaultBiomeFeatures.withForestGrass(biomeGeneratorSettings);
        DefaultBiomeFeatures.withDesertDeadBushes(biomeGeneratorSettings);

        DefaultBiomeFeatures.withCommonOverworldBlocks(biomeGeneratorSettings); //stone variants
        DefaultBiomeFeatures.withOverworldOres(biomeGeneratorSettings); //ore
        DefaultBiomeFeatures.withDisks(biomeGeneratorSettings); //disks

        VampirismBiomeFeatures.addVampireTrees(biomeGeneratorSettings);

        VampirismBiomeFeatures.addWaterSprings(biomeGeneratorSettings);
        return new Biome.Builder().precipitation(Biome.RainType.RAIN).category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.3F).downfall(0F).setEffects(ambienceBuilder.build()).withMobSpawnSettings(spawnBuilder.copy()).withGenerationSettings(biomeGeneratorSettings.build());
    }
}
