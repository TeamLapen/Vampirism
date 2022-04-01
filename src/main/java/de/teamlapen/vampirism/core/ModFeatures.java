package de.teamlapen.vampirism.core;


import com.google.common.collect.Lists;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.IForgeRegistry;

public class ModFeatures {
    //features
    public static final VampireDungeonFeature vampire_dungeon = new VampireDungeonFeature(NoneFeatureConfiguration.CODEC);
    //structures
    public static final StructureFeature<NoneFeatureConfiguration> hunter_camp = new HunterCampFeature(NoneFeatureConfiguration.CODEC);
    //structure pieces
    public static final StructurePieceType hunter_camp_fireplace = setPieceId(HunterCampPieces.Fireplace::new, "hunter_camp_fireplace");
    public static final StructurePieceType hunter_camp_tent = setPieceId(HunterCampPieces.Tent::new, "hunter_camp_tent");
    public static final StructurePieceType hunter_camp_special = setPieceId(HunterCampPieces.SpecialBlock::new,  "hunter_camp_craftingtable");
    //structure processor
    public static final StructureProcessorType<RandomStructureProcessor> random_selector = StructureProcessorType.register(REFERENCE.MODID + ":random_selector", RandomStructureProcessor.CODEC);
    public static final StructureProcessorType<BiomeTopBlockProcessor> biome_based = StructureProcessorType.register(REFERENCE.MODID + ":biome_based", BiomeTopBlockProcessor.CODEC);

    private static StructurePieceType setPieceId(StructurePieceType.ContextlessType  p_210159_, String p_210160_) {
        return Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(REFERENCE.MODID, p_210160_), p_210159_);
    }

    static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.register(vampire_dungeon.setRegistryName(REFERENCE.MODID, "vampire_dungeon"));
    }

    static void registerStructures(IForgeRegistry<StructureFeature<?>> registry) {
        StructureFeature.STEP.put(hunter_camp, GenerationStep.Decoration.SURFACE_STRUCTURES);
        registry.register(hunter_camp.setRegistryName(REFERENCE.MODID, "hunter_camp"));
    }

    static void registerIgnoredBiomesForStructure() {
        VampirismAPI.worldGenRegistry().removeStructureFromBiomeCategories(hunter_camp.getRegistryName(), Lists.newArrayList(Biome.BiomeCategory.OCEAN, Biome.BiomeCategory.THEEND, Biome.BiomeCategory.NETHER, Biome.BiomeCategory.BEACH, Biome.BiomeCategory.ICY, Biome.BiomeCategory.RIVER, Biome.BiomeCategory.JUNGLE));
        VampirismAPI.worldGenRegistry().removeStructureFromBiomes(hunter_camp.getRegistryName(), Lists.newArrayList(ModBiomes.VAMPIRE_FOREST.location()));
    }


}
