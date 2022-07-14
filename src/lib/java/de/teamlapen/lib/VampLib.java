package de.teamlapen.lib;

import de.teamlapen.lib.entity.ClientEntityEventHandler;
import de.teamlapen.lib.entity.EntityEventHandler;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.network.LibraryPacketDispatcher;
import de.teamlapen.lib.proxy.ClientProxy;
import de.teamlapen.lib.proxy.CommonProxy;
import de.teamlapen.lib.proxy.IProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


/**
 * If the package is moved as own mod (probably refactored with a different package name to avoid conflicts) this will be the mod main class.
 */
@Mod(value = LIBREFERENCE.MODID)
public class VampLib {

    public static final AbstractPacketDispatcher dispatcher = new LibraryPacketDispatcher();
    public static boolean inDev = false;
    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public VampLib() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
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
        dispatcher.registerPackets();
    }

    private void processIMC(final InterModProcessEvent event) {
        HelperRegistry.finish();
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler(HelperRegistry.getEventListenerCaps()));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.register(new ClientEntityEventHandler()));//Could register in constructor, just keeping it here for consistency
    }
}
