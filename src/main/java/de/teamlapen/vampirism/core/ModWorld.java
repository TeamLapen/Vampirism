package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SRGNAMES;
import de.teamlapen.vampirism.world.gen.features.VampireForestFlowerFeature;
import de.teamlapen.vampirism.world.gen.structures.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.structures.HunterCampStructure;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModWorld {
    public static boolean debug = false;
    private static final Logger LOGGER = LogManager.getLogger();

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

    static void modifyVillageSize(GenerationSettings settings) {

        if (!VampirismConfig.SERVER.villageModify.get()) {
            LOGGER.trace("Not modifying village");
            return;
        }
        try {
            ObfuscationReflectionHelper.setPrivateValue(GenerationSettings.class, settings, VampirismConfig.SERVER.villageDistance.get(), SRGNAMES.GenerationSettings_villageDistance);
        } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            LOGGER.error("Could not modify field 'villageDistance' in GenerationSettings", e);
        }


        try {
            ObfuscationReflectionHelper.setPrivateValue(GenerationSettings.class, settings, VampirismConfig.SERVER.villageSeparation.get(), SRGNAMES.GenerationSettings_villageSeparation);
        } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            LOGGER.error("Could not modify field for villageSeparation in GenerationSettings", e);
        }


        LOGGER.debug("Modified MapGenVillage fields.");

    }
}
