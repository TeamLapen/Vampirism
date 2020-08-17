package de.teamlapen.vampirism.core;


import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.features.VampireForestFlowerFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampStructure;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class ModFeatures {
    //features
    public static final FlowersFeature vampire_flower = new VampireForestFlowerFeature(NoFeatureConfig::deserialize);
    public static final TreeFeature vampire_tree = (TreeFeature) new TreeFeature(NoFeatureConfig::deserialize, false, 4, Blocks.SPRUCE_LOG.getDefaultState(), ModBlocks.bloody_spruce_leaves.getDefaultState(), false).setSapling(ModBlocks.bloody_spruce_sapling);
    public static final TreeFeature vampire_tree_red = (TreeFeature) new TreeFeature(NoFeatureConfig::deserialize, false, 4, ModBlocks.bloody_spruce_log.getDefaultState(), ModBlocks.bloody_spruce_leaves_red.getDefaultState(), false).setSapling(ModBlocks.bloody_spruce_sapling);
    public static final VampireDungeonFeature vampire_dungeon = new VampireDungeonFeature(NoFeatureConfig::deserialize);
    //structures
    public static final Structure<NoFeatureConfig> hunter_camp = new HunterCampStructure();
    //structurepieces
    public static final IStructurePieceType hunter_camp_fireplace = IStructurePieceType.register(HunterCampPieces.Fireplace::new, REFERENCE.MODID + ":hunter_camp_fireplace");
    public static final IStructurePieceType hunter_camp_tent = IStructurePieceType.register(HunterCampPieces.Tent::new, REFERENCE.MODID + ":hunter_camp_tent");
    public static final IStructurePieceType hunter_camp_special = IStructurePieceType.register(HunterCampPieces.SpecialBlock::new, REFERENCE.MODID + ":hunter_camp_craftingtable");

    public static final IStructureProcessorType random_selector = IStructureProcessorType.register(REFERENCE.MODID + ":random_selector", RandomStructureProcessor::new);
    public static final IStructureProcessorType biome_based = IStructureProcessorType.register(REFERENCE.MODID + ":biome_based", BiomeTopBlockProcessor::new);


    static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.register(vampire_flower.setRegistryName(REFERENCE.MODID, "vampire_flower"));
        registry.register(vampire_tree.setRegistryName(REFERENCE.MODID, "vampire_tree"));
        registry.register(vampire_tree_red.setRegistryName(REFERENCE.MODID, "vampire_tree_red"));

        registry.register(hunter_camp.setRegistryName(REFERENCE.MODID, "hunter_camp"));
        registry.register(vampire_dungeon.setRegistryName(REFERENCE.MODID,"vampire_dungeon"));
    }

    static void registerIgnoredBiomesForStructures() {
        VampirismAPI.worldGenRegistry().removeStructureFromBiomeCategories(hunter_camp.getRegistryName(), Lists.newArrayList(BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.END, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.COLD, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.JUNGLE));
    }
}
