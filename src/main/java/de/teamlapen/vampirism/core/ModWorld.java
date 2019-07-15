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
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModWorld {
    public static boolean debug = false;

    public static final FlowersFeature vampire_flower = getNull();
    public static final TreeFeature vampire_tree = getNull();

    public static final Structure<NoFeatureConfig> hunter_camp = getNull();

    public static final SurfaceBuilderConfig vampire_surface = new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState());


    public static void registerFeatures(IForgeRegistry<Feature<?>> registry) {
        registry.register(new VampireForestFlowerFeature(NoFeatureConfig::deserialize).setRegistryName(REFERENCE.MODID, "vampire_flower"));
        registry.register(new TreeFeature(NoFeatureConfig::deserialize, false, 4, Blocks.SPRUCE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState(), false).setRegistryName(REFERENCE.MODID, "vampire_tree"));

        registry.register(new HunterCampStructure(NoFeatureConfig::deserialize).setRegistryName(REFERENCE.MODID, "hunter_camp"));
    }

    public static class StructurePieceTypes {
        public static final IStructurePieceType HUNTERCAMPFIRE = IStructurePieceType.register(HunterCampPieces.Fireplace::new, REFERENCE.MODID + ":huntercampfire");
        public static final IStructurePieceType HUNTERCAMPTENT = IStructurePieceType.register(HunterCampPieces.Tent::new, REFERENCE.MODID + ":huntercamptent");

    }
}
