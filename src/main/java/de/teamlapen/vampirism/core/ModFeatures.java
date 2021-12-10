package de.teamlapen.vampirism.core;


import com.google.common.collect.Lists;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.ConfigurableStructureSeparationSettings;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;

public class ModFeatures {
    //features
    public static final VampireDungeonFeature vampire_dungeon = new VampireDungeonFeature(NoneFeatureConfiguration.CODEC);
    //structures
    public static final StructureFeature<NoneFeatureConfiguration> hunter_camp = new HunterCampFeature(NoneFeatureConfiguration.CODEC);
    //structure pieces
    public static final StructurePieceType hunter_camp_fireplace = StructurePieceType.setPieceId(HunterCampPieces.Fireplace::new, REFERENCE.MODID + ":hunter_camp_fireplace");
    public static final StructurePieceType hunter_camp_tent = StructurePieceType.setPieceId(HunterCampPieces.Tent::new, REFERENCE.MODID + ":hunter_camp_tent");
    public static final StructurePieceType hunter_camp_special = StructurePieceType.setPieceId(HunterCampPieces.SpecialBlock::new, REFERENCE.MODID + ":hunter_camp_craftingtable");
    //structure processor
    public static final StructureProcessorType<RandomStructureProcessor> random_selector = StructureProcessorType.register(REFERENCE.MODID + ":random_selector", RandomStructureProcessor.CODEC);
    public static final StructureProcessorType<BiomeTopBlockProcessor> biome_based = StructureProcessorType.register(REFERENCE.MODID + ":biome_based", BiomeTopBlockProcessor.CODEC);

    static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.register(vampire_dungeon.setRegistryName(REFERENCE.MODID, "vampire_dungeon"));
    }

    static void registerStructures(IForgeRegistry<StructureFeature<?>> registry) {
        StructureFeature.STEP.put(hunter_camp, GenerationStep.Decoration.SURFACE_STRUCTURES);
        StructureFeature.STRUCTURES_REGISTRY.put(REFERENCE.MODID + ":hunter_camp", hunter_camp);
        registry.register(hunter_camp.setRegistryName(REFERENCE.MODID, "hunter_camp"));
    }

    static void registerIgnoredBiomesForStructure() {
        VampirismAPI.worldGenRegistry().removeStructureFromBiomeCategories(hunter_camp.getRegistryName(), Lists.newArrayList(Biome.BiomeCategory.OCEAN, Biome.BiomeCategory.THEEND, Biome.BiomeCategory.NETHER, Biome.BiomeCategory.BEACH, Biome.BiomeCategory.ICY, Biome.BiomeCategory.RIVER, Biome.BiomeCategory.JUNGLE));
        VampirismAPI.worldGenRegistry().removeStructureFromBiomes(hunter_camp.getRegistryName(), Lists.newArrayList(ModBiomes.VAMPIRE_FOREST_KEY.location()));
    }

    public static void addStructureSeparationSettings(ResourceKey<Level> dimension, Map<StructureFeature<?>, StructureFeatureConfiguration> settings) {
        if (dimension == Level.OVERWORLD) {
            settings.putIfAbsent(ModFeatures.hunter_camp, new ConfigurableStructureSeparationSettings(VampirismConfig.COMMON.hunterTentDistance, VampirismConfig.COMMON.hunterTentSeparation, 14357719));
        }
    }

}
