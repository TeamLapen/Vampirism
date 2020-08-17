package de.teamlapen.vampirism.core;


import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampStructure;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class ModFeatures {
    //features
    public static final BlockClusterFeatureConfig vampire_flower = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.vampire_orchid.getDefaultState()), new SimpleBlockPlacer()).tries(64).build();
    public static final VampireDungeonFeature vampire_dungeon = new VampireDungeonFeature(NoFeatureConfig::deserialize);
    //feature config
    public static final TreeFeatureConfig vampire_tree = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()), new SimpleBlockStateProvider(ModBlocks.vampire_spruce_leaves.getDefaultState()), new BlobFoliagePlacer(2, 0)).baseHeight(4).heightRandA(2).foliageHeight(3).ignoreVines().setSapling(ModBlocks.bloody_spruce_sapling).build();
    public static final TreeFeatureConfig vampire_tree_red = new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.bloody_spruce_log.getDefaultState()), new SimpleBlockStateProvider(ModBlocks.bloody_spruce_leaves.getDefaultState()), new BlobFoliagePlacer(2, 0)).baseHeight(4).heightRandA(2).foliageHeight(3).ignoreVines().setSapling(ModBlocks.bloody_spruce_sapling).build();
    //structures
    public static final Structure<NoFeatureConfig> hunter_camp = new HunterCampStructure();
    //structurepieces
    public static final IStructurePieceType hunter_camp_fireplace = IStructurePieceType.register(HunterCampPieces.Fireplace::new, REFERENCE.MODID + ":hunter_camp_fireplace");
    public static final IStructurePieceType hunter_camp_tent = IStructurePieceType.register(HunterCampPieces.Tent::new, REFERENCE.MODID + ":hunter_camp_tent");
    public static final IStructurePieceType hunter_camp_special = IStructurePieceType.register(HunterCampPieces.SpecialBlock::new, REFERENCE.MODID + ":hunter_camp_craftingtable");

    public static final IStructureProcessorType random_selector = IStructureProcessorType.register(REFERENCE.MODID + ":random_selector", RandomStructureProcessor::new);
    public static final IStructureProcessorType biome_based = IStructureProcessorType.register(REFERENCE.MODID + ":biome_based", BiomeTopBlockProcessor::new);

    static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.register(hunter_camp.setRegistryName(REFERENCE.MODID, "hunter_camp"));
        registry.register(vampire_dungeon.setRegistryName(REFERENCE.MODID, "vampire_dungeon"));
    }

    static void registerIgnoredBiomesForStructures() {
        VampirismAPI.worldGenRegistry().removeStructureFromBiomeCategories(hunter_camp.getRegistryName(), Lists.newArrayList(BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.END, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.COLD, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.JUNGLE));
    }
}
