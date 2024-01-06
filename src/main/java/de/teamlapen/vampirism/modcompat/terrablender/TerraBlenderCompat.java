package de.teamlapen.vampirism.modcompat.terrablender;

import de.teamlapen.vampirism.config.VampirismConfig;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * When TerraBlender is installed we use it to add our biomes to the overworld, instead of hacking it into the overworld preset. This is more compatible with other mods.
 * <br>
 * #registerBiomeProviderIfPresentUnsafe() is called during common setup.
 * The hack code in VampirismWorldGen is called during LoadComplete and can therefore check #arreBiomesAddedViaTerraBlender
 */
public class TerraBlenderCompat {

    private static final String MOD_ID = "terrablender";

    private static boolean isUsingTerraBlender = false;
    private static final Logger LOGGER = LogManager.getLogger();

    public static void registerBiomeProviderIfPresentUnsafe() {
        if (!VampirismConfig.COMMON.addVampireForestToOverworld.get()) {
            return;
        }
        if (ModList.get().isLoaded(MOD_ID)) {
            TerraBlenderRegistration.registerRegions();
            TerraBlenderRegistration.registerSurfaceRules();
            LOGGER.info("TerraBlender is installed. Using it to add vampire Forest to overworld.");
            isUsingTerraBlender = true;
        }
    }

    public static boolean areBiomesAddedViaTerraBlender() {
        return isUsingTerraBlender;
    }
}