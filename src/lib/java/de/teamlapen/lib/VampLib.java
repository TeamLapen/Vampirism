package de.teamlapen.lib;

import de.teamlapen.lib.client.VampLibClient;
import de.teamlapen.lib.entity.EntityEventHandler;
import de.teamlapen.lib.network.LibraryPacketDispatcher;
import de.teamlapen.lib.proxy.CommonProxy;
import de.teamlapen.lib.proxy.IProxy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;


/**
 * If the package is moved as own mod (probably refactored with a different package name to avoid conflicts) this will be the mod main class.
 */
@Mod(value = LIBREFERENCE.MODID)
public class VampLib {

    public static boolean inDev = false;
    public static final IProxy proxy = FMLLoader.getDist() == Dist.CLIENT ? VampLibClient.getProxy() : new CommonProxy();

    public VampLib(IEventBus modBus) {
        modBus.addListener(this::preInit);
        modBus.addListener(this::enqueueIMC);
        modBus.addListener(this::processIMC);
        modBus.register(new LibraryPacketDispatcher());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.register(VampLibClient.class);
        }
    }

    private void checkDevEnv() {
        String launchTarget = System.getenv().get("target");
        if (launchTarget != null && launchTarget.contains("dev")) {
            inDev = true;
        }
    }

    @SuppressWarnings("EmptyMethod")
    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void preInit(final FMLCommonSetupEvent event) {
        checkDevEnv();
    }

    private void processIMC(final InterModProcessEvent event) {
        HelperRegistry.finish();
        NeoForge.EVENT_BUS.register(new EntityEventHandler(HelperRegistry.getEventListenerCaps()));
    }
}
