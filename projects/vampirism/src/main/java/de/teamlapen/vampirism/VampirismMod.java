package de.teamlapen.vampirism;

import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.common.CommonServices;
import de.teamlapen.vampirism.common.items.crossbow.CrossbowArrowHandler;
import de.teamlapen.vampirism.common.proxy.IProxy;
import de.teamlapen.vampirism.server.proxy.ServerProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForgeMod;

/**
 * Main class for Vampirism
 */
@Mod(REFERENCE.MODID)
public class VampirismMod {

    public static final IProxy proxy = FMLEnvironment.getDist() == Dist.CLIENT ? VampirismModClient.getProxy() : new ServerProxy();
    public static boolean inDev = false;
    public static boolean inDataGen = false;
    private static CommonServices SERVICES;


    public VampirismMod(IEventBus modEventBus, ModContainer modContainer) {
        checkEnv();

        modEventBus.addListener(this::processIMC);

        ShapedRecipePattern.setCraftingSize(4, 4);
        NeoForgeMod.enableMergedAttributeTooltips();

        SERVICES = new CommonServices(modContainer);
        SERVICES.register(modEventBus);
    }

    public static CommonServices services() {
        return SERVICES;
    }

    private void checkEnv() {
        String launchTarget = System.getProperty("vampirism_target");
        inDev = !FMLEnvironment.isProduction();
        if (launchTarget != null && launchTarget.contains("data")) {
            inDataGen = true;
        }
    }

    private void processIMC(final InterModProcessEvent event) {
        CrossbowArrowHandler.collectCrossbowArrows();
    }

}
