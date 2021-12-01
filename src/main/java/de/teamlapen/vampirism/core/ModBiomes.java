package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.world.biome.VampireForestBiome;
import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

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

    public static final ResourceKey<Biome> VAMPIRE_FOREST_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, "vampire_forest"));
    public static final ResourceKey<Biome> VAMPIRE_FOREST_HILLS_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, "vampire_forest_hills"));

    public static final SurfaceRules.RuleSource cursed_earth = SurfaceRules.state(ModBlocks.cursed_earth.defaultBlockState());


    static void registerBiomes(IForgeRegistry<Biome> registry) {
        registry.register(VampireForestBiome.createVampireForest(0.1F, 0.025F).setRegistryName(VAMPIRE_FOREST_KEY.location()));
        registry.register(VampireForestBiome.createVampireForest(0.8f, 0.5f).setRegistryName(VAMPIRE_FOREST_HILLS_KEY.location()));

        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST_KEY.location());
        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST_HILLS_KEY.location());

        BiomeDictionary.addTypes(VAMPIRE_FOREST_KEY, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
    }

    static void registerSurfaceRules() {
        SurfaceRules.ConditionSource inVampireBiome = SurfaceRules.isBiome(ModBiomes.VAMPIRE_FOREST_KEY);
        SurfaceRules.RuleSource vampireForest = SurfaceRules.ifTrue(inVampireBiome, SurfaceRules.state(ModBlocks.cursed_earth.defaultBlockState()));
        SurfaceRules.RuleSource place = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),SurfaceRules.sequence(vampireForest)))); // TODO 1.18 add to SurfaceRuleData#overworldLike
    }

    /**
     * Only call from main thread / non-parallel event
     */
    static void addBiomesToGeneratorUnsafe() {
        //TODO 1.18 don't generate hills biome for now. Should be added as a hills variant at some point if supported by Forge
        BiomeManager.addAdditionalOverworldBiomes(VAMPIRE_FOREST_KEY);
        //BiomeManager.addAdditionalOverworldBiomes(VAMPIRE_FOREST_HILLS_KEY);
        BiomeManager.addBiome(net.minecraftforge.common.BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ModBiomes.VAMPIRE_FOREST_KEY, VampirismConfig.COMMON.vampireForestWeight.get()));
//            BiomeManager.addBiome(net.minecraftforge.common.BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ModBiomes.VAMPIRE_FOREST_HILLS_KEY, VampirismConfig.BALANCE.vampireForestHillsWeight.get()));

    }

    /**
     * Use only for adding to biome lists
     * <p>
     * Registered in mod constructor
     */
    public static void onBiomeLoadingEventAdditions(BiomeLoadingEvent event) {
        List<MobSpawnSettings.SpawnerData> monsterList = event.getSpawns().getSpawner(MobCategory.MONSTER);
        if (monsterList != null && monsterList.stream().anyMatch(spawners -> spawners.type == EntityType.ZOMBIE)) {
            event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.vampire, VampirismConfig.COMMON.vampireSpawnChance.get(), 1, 3));
            event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.advanced_vampire, VampirismConfig.COMMON.advancedVampireSpawnChance.get(), 1, 1));
            int hunterChance = VampirismConfig.COMMON.hunterSpawnChance.get();
            if (hunterChance > 0) {
                event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.hunter, hunterChance, 1, 3));
            }
            int advancedHunterChance = VampirismConfig.COMMON.advancedHunterSpawnChance.get();
            if (advancedHunterChance > 0) {
                event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.advanced_hunter, advancedHunterChance, 1, 1));
            }
        }
        Biome.BiomeCategory cat = event.getCategory();
        if (cat != Biome.BiomeCategory.NETHER && cat != Biome.BiomeCategory.THEEND && cat != Biome.BiomeCategory.OCEAN && cat != Biome.BiomeCategory.NONE) {
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, VampirismBiomeFeatures.vampire_dungeon_placed);
        }
    }
}
