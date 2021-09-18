package de.teamlapen.vampirism.core;


import com.google.common.collect.Lists;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.mixin.DimensionStructureSettingsAccessor;
import de.teamlapen.vampirism.util.ConfigurableStructureSeparationSettings;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.features.VampirismLakeFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampStructure;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ModFeatures {
    //features
    public static final VampireDungeonFeature vampire_dungeon = new VampireDungeonFeature(NoneFeatureConfiguration.CODEC);
    public static final VampirismLakeFeature mod_lake = new VampirismLakeFeature(BlockStateConfiguration.CODEC);
    //structures
    public static final StructureFeature<NoneFeatureConfiguration> hunter_camp = new HunterCampStructure(NoneFeatureConfiguration.CODEC/*deserialize*/);
    //structure pieces
    public static final StructurePieceType hunter_camp_fireplace = StructurePieceType.setPieceId(HunterCampPieces.Fireplace::new, REFERENCE.MODID + ":hunter_camp_fireplace");
    public static final StructurePieceType hunter_camp_tent = StructurePieceType.setPieceId(HunterCampPieces.Tent::new, REFERENCE.MODID + ":hunter_camp_tent");
    public static final StructurePieceType hunter_camp_special = StructurePieceType.setPieceId(HunterCampPieces.SpecialBlock::new, REFERENCE.MODID + ":hunter_camp_craftingtable");
    //structure proccesor
    public static final StructureProcessorType<RandomStructureProcessor> random_selector = StructureProcessorType.register/*register*/(REFERENCE.MODID + ":random_selector", RandomStructureProcessor.CODEC);
    public static final StructureProcessorType<BiomeTopBlockProcessor> biome_based = StructureProcessorType.register/*register*/(REFERENCE.MODID + ":biome_based", BiomeTopBlockProcessor.CODEC);
    private static final Logger LOGGER = LogManager.getLogger();

    static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.register(vampire_dungeon.setRegistryName(REFERENCE.MODID, "vampire_dungeon"));
        registry.register(mod_lake.setRegistryName(REFERENCE.MODID, "mod_lake"));
    }

    static void registerStructures(IForgeRegistry<StructureFeature<?>> registry) {
        StructureFeature.STEP.put(hunter_camp, GenerationStep.Decoration.SURFACE_STRUCTURES);
        StructureFeature.STRUCTURES_REGISTRY.put(REFERENCE.MODID + ":hunter_camp", hunter_camp);
        registry.register(hunter_camp.setRegistryName(REFERENCE.MODID, "hunter_camp"));
    }

    static void registerIgnoredBiomesForStructures() {
        VampirismAPI.worldGenRegistry().removeStructureFromBiomeCategories(hunter_camp.getRegistryName(), Lists.newArrayList(Biome.BiomeCategory.OCEAN, Biome.BiomeCategory.THEEND, Biome.BiomeCategory.NETHER, Biome.BiomeCategory.BEACH, Biome.BiomeCategory.ICY, Biome.BiomeCategory.RIVER, Biome.BiomeCategory.JUNGLE));
        VampirismAPI.worldGenRegistry().removeStructureFromBiomes(hunter_camp.getRegistryName(), Lists.newArrayList(ModBiomes.VAMPIRE_FOREST_KEY.location()));
        VampirismAPI.worldGenRegistry().removeStructureFromBiomes(hunter_camp.getRegistryName(), Lists.newArrayList(ModBiomes.VAMPIRE_FOREST_HILLS_KEY.location()));

    }


    /**
     * Not safe to run in parallel with other mods
     */
    public static void registerStructureSeparation() {
        //https://github.com/MinecraftForge/MinecraftForge/pull/7232
        //https://github.com/MinecraftForge/MinecraftForge/pull/7331
        StructureSettings settings = NoiseGeneratorSettings.bootstrap().structureSettings();
        //Copy/Overwrite
        Map<StructureFeature<?>, StructureFeatureConfiguration> structureSettingsMapOverworld = new HashMap<>(settings.structureConfig()); //TODO 1.17 check if any PR has been accepted
        addStructureSeparationSettings(structureSettingsMapOverworld);
        if (VampirismConfig.COMMON.villageModify.get()) {
            LOGGER.info("Replacing vanilla village structure separation settings for the overworld dimension preset");
            structureSettingsMapOverworld.put(StructureFeature.VILLAGE, new ConfigurableStructureSeparationSettings(VampirismConfig.COMMON.villageDistance, VampirismConfig.COMMON.villageSeparation, StructureSettings.DEFAULTS.get(StructureFeature.VILLAGE).salt()));
        } else {
            LOGGER.trace("Not modifying village");
        }
        ((DimensionStructureSettingsAccessor) settings).setStructureSeparation_vampirism(structureSettingsMapOverworld);
    }

    /**
     * Make sure a given world (that is being loaded) has our structure separation settings.
     * Datapack worlds might generate without them otherwise.
     */
    public static void checkWorldStructureSeparation(ResourceKey<Level> dimension, boolean flatWorld, StructureSettings settings) {
        if (dimension.compareTo(Level.OVERWORLD) != 0 || flatWorld) return;
        if (!VampirismConfig.COMMON.enforceTentGeneration.get()) return;
        //Copy/Overwrite
        Map<StructureFeature<?>, StructureFeatureConfiguration> structureSettings = new HashMap<>(settings.structureConfig());
        if (!structureSettings.containsKey(hunter_camp)) {
            LOGGER.info("Cannot find hunter camp configuration for loaded world -> Adding");
            int dist = VampirismConfig.COMMON.hunterTentDistance.get();
            int sep = VampirismConfig.COMMON.hunterTentSeparation.get();
            if (dist <= sep) {
                LOGGER.warn("Hunter tent distance must be larger than separation. Adjusting");
                dist = sep + 1;
            }
            structureSettings.put(hunter_camp, new StructureFeatureConfiguration(dist, sep, 14357719));
        }
        ((DimensionStructureSettingsAccessor) settings).setStructureSeparation_vampirism(structureSettings);
    }

    private static void addStructureSeparationSettings(Map<StructureFeature<?>, StructureFeatureConfiguration> settings) {
        settings.put(hunter_camp, new ConfigurableStructureSeparationSettings(VampirismConfig.COMMON.hunterTentDistance, VampirismConfig.COMMON.hunterTentSeparation, 14357719));

    }
}
