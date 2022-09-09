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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Handles all biome registrations and reference.
 */
public class ModBiomes {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, REFERENCE.MODID);

    public static final RegistryKey<Biome> VAMPIRE_FOREST_KEY = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, "vampire_forest"));
    public static final RegistryKey<Biome> VAMPIRE_FOREST_HILLS_KEY = RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, "vampire_forest_hills"));

    public static final RegistryObject<Biome> VAMPIRE_FOREST = BIOMES.register(VAMPIRE_FOREST_KEY.location().getPath(), () -> VampireForestBiome.createVampireForest(0.1F, 0.055F));
    public static final RegistryObject<Biome> VAMPIRE_FOREST_HILLS = BIOMES.register(VAMPIRE_FOREST_HILLS_KEY.location().getPath(), () -> VampireForestBiome.createVampireForest(0.8f, 0.5f));

    static void registerBiomes(IEventBus bus) {
        BIOMES.register(bus);

        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST_KEY.location());
        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST_HILLS_KEY.location());
    }

    /**
     * Only call from main thread / non parallel event
     */
    static void addBiomesToGeneratorUnsafe() {
        //TODO don't generate hills biome for now. Should be added as a hills variant at some point if supported by Forge
        BiomeManager.addAdditionalOverworldBiomes(VAMPIRE_FOREST_KEY);
//        BiomeManager.addAdditionalOverworldBiomes(VAMPIRE_FOREST_HILLS_KEY);
        BiomeDictionary.addTypes(VAMPIRE_FOREST_KEY, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        BiomeManager.addBiome(net.minecraftforge.common.BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ModBiomes.VAMPIRE_FOREST_KEY, VampirismConfig.COMMON.vampireForestWeight.get()));
//            BiomeManager.addBiome(net.minecraftforge.common.BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ModBiomes.VAMPIRE_FOREST_HILLS_KEY, VampirismConfig.COMMON.vampireForestWeight.get()));
    }

    /**
     * Use only for adding to biome lists
     * <p>
     * Registered in mod constructor
     */
    public static void onBiomeLoadingEventAdditions(BiomeLoadingEvent event) {
        List<MobSpawnInfo.Spawners> monsterList = event.getSpawns().getSpawner(EntityClassification.MONSTER);
        if (monsterList != null && monsterList.stream().anyMatch(spawners -> spawners.type == EntityType.ZOMBIE)) {
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.VAMPIRE.get(), VampirismConfig.COMMON.vampireSpawnChance.get(), 1, 3));
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.ADVANCED_VAMPIRE.get(), VampirismConfig.COMMON.advancedVampireSpawnChance.get(), 1, 1));
            int hunterChance = VampirismConfig.COMMON.hunterSpawnChance.get();
            if (hunterChance > 0) {
                event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.HUNTER.get(), hunterChance, 1, 3));
            }
            int advancedHunterChance = VampirismConfig.COMMON.advancedHunterSpawnChance.get();
            if (advancedHunterChance > 0) {
                event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.ADVANCED_HUNTER.get(), advancedHunterChance, 1, 1));
            }
        }
        Biome.Category cat = event.getCategory();
        if (cat != Biome.Category.NETHER && cat != Biome.Category.THEEND && cat != Biome.Category.OCEAN && cat != Biome.Category.NONE) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, VampirismBiomeFeatures.vampire_dungeon);
        }

        if (VampirismAPI.worldGenRegistry().canStructureBeGeneratedInBiome(ModFeatures.HUNTER_CAMP.get().getRegistryName(), event.getName(), event.getCategory())) {
            event.getGeneration().addStructureStart(VampirismBiomeFeatures.hunter_camp);
        }
    }
}
