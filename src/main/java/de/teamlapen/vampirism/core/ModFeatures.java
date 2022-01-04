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
import de.teamlapen.vampirism.world.gen.treedecorator.TrunkCursedVineTreeDecorator;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ModFeatures {
    //features
    public static final VampireDungeonFeature vampire_dungeon = new VampireDungeonFeature(NoFeatureConfig.CODEC);
    public static final VampirismLakeFeature mod_lake = new VampirismLakeFeature(BlockStateFeatureConfig.CODEC);
    //structures
    public static final Structure<NoFeatureConfig> hunter_camp = new HunterCampStructure(NoFeatureConfig.CODEC/*deserialize*/);
    //structure pieces
    public static final IStructurePieceType hunter_camp_fireplace = IStructurePieceType.setPieceId(HunterCampPieces.Fireplace::new, REFERENCE.MODID + ":hunter_camp_fireplace");
    public static final IStructurePieceType hunter_camp_tent = IStructurePieceType.setPieceId(HunterCampPieces.Tent::new, REFERENCE.MODID + ":hunter_camp_tent");
    public static final IStructurePieceType hunter_camp_special = IStructurePieceType.setPieceId(HunterCampPieces.SpecialBlock::new, REFERENCE.MODID + ":hunter_camp_craftingtable");
    //structure proccesor
    public static final IStructureProcessorType<RandomStructureProcessor> random_selector = IStructureProcessorType.register/*register*/(REFERENCE.MODID + ":random_selector", RandomStructureProcessor.CODEC);
    public static final IStructureProcessorType<BiomeTopBlockProcessor> biome_based = IStructureProcessorType.register/*register*/(REFERENCE.MODID + ":biome_based", BiomeTopBlockProcessor.CODEC);
    private static final Logger LOGGER = LogManager.getLogger();

    public static final TreeDecoratorType<TrunkCursedVineTreeDecorator> trunk_cursed_vine = new TreeDecoratorType<>(TrunkCursedVineTreeDecorator.CODEC);

    static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.register(vampire_dungeon.setRegistryName(REFERENCE.MODID, "vampire_dungeon"));
        registry.register(mod_lake.setRegistryName(REFERENCE.MODID, "mod_lake"));
    }

    static void registerStructures(IForgeRegistry<Structure<?>> registry) {
        Structure.STEP.put(hunter_camp, GenerationStage.Decoration.SURFACE_STRUCTURES);
        Structure.STRUCTURES_REGISTRY.put(REFERENCE.MODID + ":hunter_camp", hunter_camp);
        registry.register(hunter_camp.setRegistryName(REFERENCE.MODID, "hunter_camp"));
    }

    static void registerTreeDecoratorTypes(IForgeRegistry<TreeDecoratorType<?>> registry) {
        registry.register(trunk_cursed_vine.setRegistryName(REFERENCE.MODID, "trunk_cursed_vine"));
    }

    static void registerIgnoredBiomesForStructures() {
        VampirismAPI.worldGenRegistry().removeStructureFromBiomeCategories(hunter_camp.getRegistryName(), Lists.newArrayList(Biome.Category.OCEAN, Biome.Category.THEEND, Biome.Category.NETHER, Biome.Category.BEACH, Biome.Category.ICY, Biome.Category.RIVER, Biome.Category.JUNGLE));
        VampirismAPI.worldGenRegistry().removeStructureFromBiomes(hunter_camp.getRegistryName(), Lists.newArrayList(ModBiomes.VAMPIRE_FOREST_KEY.location()));
        VampirismAPI.worldGenRegistry().removeStructureFromBiomes(hunter_camp.getRegistryName(), Lists.newArrayList(ModBiomes.VAMPIRE_FOREST_HILLS_KEY.location()));

    }


    /**
     * Not safe to run in parallel with other mods
     */
    public static void registerStructureSeparation() {
        //https://github.com/MinecraftForge/MinecraftForge/pull/7232
        //https://github.com/MinecraftForge/MinecraftForge/pull/7331
        DimensionStructuresSettings settings = DimensionSettings.bootstrap().structureSettings();
        //Copy/Overwrite
        Map<Structure<?>, StructureSeparationSettings> structureSettingsMapOverworld = new HashMap<>(settings.structureConfig()); //TODO 1.17 check if any PR has been accepted
        addStructureSeparationSettings(structureSettingsMapOverworld);
        if (VampirismConfig.COMMON.villageModify.get()) {
            LOGGER.info("Replacing vanilla village structure separation settings for the overworld dimension preset");
            structureSettingsMapOverworld.put(Structure.VILLAGE, new ConfigurableStructureSeparationSettings(VampirismConfig.COMMON.villageDistance, VampirismConfig.COMMON.villageSeparation, DimensionStructuresSettings.DEFAULTS.get(Structure.VILLAGE).salt()));
        } else {
            LOGGER.trace("Not modifying village");
        }
        ((DimensionStructureSettingsAccessor) settings).setStructureSeparation_vampirism(structureSettingsMapOverworld);
    }

    /**
     * Make sure a given world (that is being loaded) has our structure separation settings.
     * Datapack worlds might generate without them otherwise.
     *
     * @param settings
     */
    public static void checkWorldStructureSeparation(RegistryKey<World> dimension, boolean flatWorld, DimensionStructuresSettings settings) {
        if (dimension.compareTo(World.OVERWORLD) != 0 || flatWorld) return;
        if (!VampirismConfig.COMMON.enforceTentGeneration.get()) return;
        //Copy/Overwrite
        Map<Structure<?>, StructureSeparationSettings> structureSettings = new HashMap<>(settings.structureConfig());
        if (!structureSettings.containsKey(hunter_camp)) {
            LOGGER.info("Cannot find hunter camp configuration for loaded world -> Adding");
            int dist = VampirismConfig.COMMON.hunterTentDistance.get();
            int sep = VampirismConfig.COMMON.hunterTentSeparation.get();
            if (dist <= sep) {
                LOGGER.warn("Hunter tent distance must be larger than separation. Adjusting");
                dist = sep + 1;
            }
            structureSettings.put(hunter_camp, new StructureSeparationSettings(dist, sep, 14357719));
        }
        ((DimensionStructureSettingsAccessor) settings).setStructureSeparation_vampirism(structureSettings);
    }

    private static void addStructureSeparationSettings(Map<Structure<?>, StructureSeparationSettings> settings) {
        settings.put(hunter_camp, new ConfigurableStructureSeparationSettings(VampirismConfig.COMMON.hunterTentDistance, VampirismConfig.COMMON.hunterTentSeparation, 14357719));

    }
}
