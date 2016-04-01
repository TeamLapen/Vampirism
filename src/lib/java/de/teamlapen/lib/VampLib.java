package de.teamlapen.lib;

import de.teamlapen.lib.entity.EntityEventHandler;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.util.Logger;
import de.teamlapen.lib.network.LibraryPacketDispatcher;
import de.teamlapen.lib.proxy.IProxy;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * If the package is moved as own mod (probably refactored with a different package name to avoid conflicts) this will be the mod main class.
 */
@Mod(modid = LIBREFERENCE.MODID, name = LIBREFERENCE.NAME, version = LIBREFERENCE.VERSION, acceptedMinecraftVersions = "[1.8.9]", dependencies = "required-after:Forge@[" + LIBREFERENCE.FORGE_VERSION_MIN + ",)")
public class VampLib {
    public final static Logger log = new Logger(LIBREFERENCE.MODID, "de.teamlapen.lib");
    public static boolean inDev = false;
    public static AbstractPacketDispatcher dispatcher = new LibraryPacketDispatcher();

    @SidedProxy(clientSide = "de.teamlapen.lib.proxy.ClientProxy", serverSide = "de.teamlapen.lib.proxy.CommonProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        HelperRegistry.finish();
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler(HelperRegistry.getEventListenerCaps()));
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        checkDevEnv();
        dispatcher.registerPackets();

    }

    private void checkDevEnv() {
        if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            inDev = true;
            log.inDev = true;
        }
    }
}
