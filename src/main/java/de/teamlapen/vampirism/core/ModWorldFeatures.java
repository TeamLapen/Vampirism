package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.gen.features.VampireForestFlowerFeature;
import de.teamlapen.vampirism.world.gen.structures.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.structures.HunterCampStructure;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.registries.IForgeRegistry;

public class ModWorldFeatures {
    public static final FlowersFeature vampire_flower = new VampireForestFlowerFeature(NoFeatureConfig::deserialize);
    public static final TreeFeature vampire_tree = new TreeFeature(NoFeatureConfig::deserialize, false, 4, Blocks.SPRUCE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState(), false);

    public static final Structure<NoFeatureConfig> hunter_camp = new HunterCampStructure(NoFeatureConfig::deserialize);


    static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.register(vampire_flower.setRegistryName(REFERENCE.MODID, "vampire_flower"));
        registry.register(vampire_tree.setRegistryName(REFERENCE.MODID, "vampire_tree"));

        registry.register(hunter_camp.setRegistryName(REFERENCE.MODID, "hunter_camp"));
    }

    public static class StructurePieceTypes {
        public static final IStructurePieceType HUNTERCAMPFIRE = IStructurePieceType.register(HunterCampPieces.Fireplace::new, REFERENCE.MODID + ":huntercampfire");
        public static final IStructurePieceType HUNTERCAMPTENT = IStructurePieceType.register(HunterCampPieces.Tent::new, REFERENCE.MODID + ":huntercamptent");
        public static final IStructurePieceType HUNTERCAMPTABLE = IStructurePieceType.register(HunterCampPieces.CraftingTable::new, REFERENCE.MODID + ":huntercamptable");

    }
}
