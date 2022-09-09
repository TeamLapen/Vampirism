package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.world.biome.VampireForestBiome;
import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
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

    public static final RegistryKey<Biome> VAMPIRE_FOREST_KEY = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, "vampire_forest"));
    public static final RegistryKey<Biome> VAMPIRE_FOREST_HILLS_KEY = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, "vampire_forest_hills"));


    static void registerBiomes(IForgeRegistry<Biome> registry) {
        registry.register(VampireForestBiome.createVampireForest(0.1F, 0.025F).setRegistryName(VAMPIRE_FOREST_KEY.location()));
        registry.register(VampireForestBiome.createVampireForest(0.8f, 0.5f).setRegistryName(VAMPIRE_FOREST_HILLS_KEY.location()));

        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST_KEY.location());
        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST_HILLS_KEY.location());

    }

    /**
     * Only call from main thread / non-parallel event
     */
    static void addBiomesToGeneratorUnsafe() {
        //TODO don't generate hills biome for now. Should be added as a hills variant at some point if supported by Forge
        BiomeManager.addAdditionalOverworldBiomes(VAMPIRE_FOREST_KEY);
        //BiomeManager.addAdditionalOverworldBiomes(VAMPIRE_FOREST_HILLS_KEY);
        BiomeManager.addBiome(net.minecraftforge.common.BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ModBiomes.VAMPIRE_FOREST_KEY, VampirismConfig.COMMON.vampireForestWeight.get()));
//            BiomeManager.addBiome(net.minecraftforge.common.BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ModBiomes.VAMPIRE_FOREST_HILLS_KEY, VampirismConfig.BALANCE.vampireForestHillsWeight.get()));
        BiomeDictionary.addTypes(VAMPIRE_FOREST_KEY, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);

    }

    /**
     * Use only for adding to biome lists
     * <p>
     * Registered in mod constructor
     */
    public static void onBiomeLoadingEventAdditions(BiomeLoadingEvent event) {
        List<MobSpawnInfo.Spawners> monsterList = event.getSpawns().getSpawner(EntityClassification.MONSTER);
        if (monsterList != null && monsterList.stream().anyMatch(spawners -> spawners.type == EntityType.ZOMBIE)) {
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire, VampirismConfig.COMMON.vampireSpawnChance.get(), 1, 3));
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.advanced_vampire, VampirismConfig.COMMON.advancedVampireSpawnChance.get(), 1, 1));
            int hunterChance = VampirismConfig.COMMON.hunterSpawnChance.get();
            if (hunterChance > 0) {
                event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.hunter, hunterChance, 1, 3));
            }
            int advancedHunterChance = VampirismConfig.COMMON.advancedHunterSpawnChance.get();
            if (advancedHunterChance > 0) {
                event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.advanced_hunter, advancedHunterChance, 1, 1));
            }
        }
        Biome.Category cat = event.getCategory();
        if (cat != Biome.Category.NETHER && cat != Biome.Category.THEEND && cat != Biome.Category.OCEAN && cat != Biome.Category.NONE) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, VampirismBiomeFeatures.vampire_dungeon);
        }

        if (VampirismAPI.worldGenRegistry().canStructureBeGeneratedInBiome(ModFeatures.hunter_camp.getRegistryName(), event.getName(), event.getCategory())) {
            event.getGeneration().addStructureStart(VampirismBiomeFeatures.hunter_camp);
        }
    }
}
