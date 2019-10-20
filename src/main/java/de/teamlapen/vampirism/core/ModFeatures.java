package de.teamlapen.vampirism.core;


import com.google.common.collect.Lists;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.features.VampireForestFlowerFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampStructure;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.IForgeRegistry;

public class ModFeatures {
    //features
    public static final FlowersFeature vampire_flower = new VampireForestFlowerFeature(NoFeatureConfig::deserialize);
    public static final TreeFeature vampire_tree = new TreeFeature(NoFeatureConfig::deserialize, false, 4, Blocks.SPRUCE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState(), false);
    //structures
    public static final Structure<NoFeatureConfig> hunter_camp = new HunterCampStructure();
    //structurepieces
    public static final IStructurePieceType hunter_camp_fireplace = IStructurePieceType.register(HunterCampPieces.Fireplace::new, REFERENCE.MODID + ":hunter_camp_fireplace");
    public static final IStructurePieceType hunter_camp_tent = IStructurePieceType.register(HunterCampPieces.Tent::new, REFERENCE.MODID + ":hunter_camp_tent");
    public static final IStructurePieceType hunter_camp_special = IStructurePieceType.register(HunterCampPieces.SpecialBlock::new, REFERENCE.MODID + ":hunter_camp_craftingtable");


    static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.register(vampire_flower.setRegistryName(REFERENCE.MODID, "vampire_flower"));
        registry.register(vampire_tree.setRegistryName(REFERENCE.MODID, "vampire_tree"));

        registry.register(hunter_camp.setRegistryName(REFERENCE.MODID, "hunter_camp"));
    }

    static void registerIgnoredBiomesForStructures() {
        VampirismAPI.worldGenRegistry().removeStructureFromBiomeCategories(hunter_camp.getRegistryName(), Lists.newArrayList(Biome.Category.OCEAN, Biome.Category.THEEND, Biome.Category.NETHER, Biome.Category.ICY, Biome.Category.BEACH, Biome.Category.RIVER, Biome.Category.SWAMP, Biome.Category.JUNGLE));
    }
}
