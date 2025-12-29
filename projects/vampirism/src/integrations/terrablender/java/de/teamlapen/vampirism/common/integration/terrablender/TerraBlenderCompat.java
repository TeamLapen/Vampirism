package de.teamlapen.vampirism.common.integration.terrablender;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.integration.ITerraBlenderBiomeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * When TerraBlender is installed we use it to add our biomes to the overworld, instead of hacking it into the overworld preset. This is more compatible with other mods.
 * <br>
 * #registerBiomeProviderIfPresentUnsafe() is called during common setup.
 * The hack code in VampirismWorldGen is called during LoadComplete and can therefore check #arreBiomesAddedViaTerraBlender
 */
@EventBusSubscriber
public class TerraBlenderCompat  {

    private static final String MOD_ID = "terrablender";

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void registerBiomeProviderIfPresentUnsafe(FMLCommonSetupEvent event) {
        if (!ModConfig.common().addVampireForestToOverworld.get()) {
            return;
        }
        if (ModList.get().isLoaded(MOD_ID)) {
            TerraBlenderRegistration.registerRegions();
            TerraBlenderRegistration.registerSurfaceRules();
            LOGGER.info("TerraBlender is installed. Using it to add vampire Forest to overworld.");
            VampirismMod.integrations().useTerraBlender(new Provider(true));
        }
    }

    private record Provider(boolean isUsingTerraBlender) implements ITerraBlenderBiomeProvider {
    }
}