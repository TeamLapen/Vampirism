package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SRGNAMES;
import de.teamlapen.vampirism.world.gen.structures.HunterCampPieces;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ObjectHolder(REFERENCE.MODID)
public class ModWorld {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean debug = false;

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

    public static class StructurePieceTypes {
        public static final IStructurePieceType HUNTERCAMPFIRE = IStructurePieceType.register(HunterCampPieces.Fireplace::new, REFERENCE.MODID + ":huntercampfire");
        public static final IStructurePieceType HUNTERCAMPTENT = IStructurePieceType.register(HunterCampPieces.Tent::new, REFERENCE.MODID + ":huntercamptent");
    }
}
