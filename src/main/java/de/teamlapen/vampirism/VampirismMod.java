package de.teamlapen.vampirism;

import de.teamlapen.lib.util.IInitListener;
import de.teamlapen.lib.util.Logger;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.FractionRegistry;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModEventHandler;
import de.teamlapen.vampirism.core.VampirismCommand;
import de.teamlapen.vampirism.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;

/**
 * Main class for Vampirism
 */
@Mod(modid = REFERENCE.MODID, name = REFERENCE.NAME, version = REFERENCE.VERSION, acceptedMinecraftVersions = "[1.8.9]", dependencies = "required-after:Forge@[" + REFERENCE.FORGE_VERSION_MIN + ",)", guiFactory = "de.teamlapen.vampirism.client.core.ModGuiFactory")
public class VampirismMod {

    public final static Logger log = new Logger(REFERENCE.MODID, "de.teamlapen.vampirism");
    @Mod.Instance(value = REFERENCE.MODID)
    public static VampirismMod instance;
    @SidedProxy(clientSide = "de.teamlapen.vampirism.proxy.ClientProxy", serverSide = "de.teamlapen.vampirism.proxy.ServerProxy")
    public static IProxy proxy;
    public static boolean inDev=false;

    public static boolean isRealism() {
        return Configs.realism_mode;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        log.t("Test balance value %s",Balance.leveling.TEST_VALUE);
        proxy.onInitStep(IInitListener.Step.INIT, event);

        MinecraftForge.EVENT_BUS.register(new ModEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModPlayerEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModEntityEventHandler());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        checkDevEnv();
        Configs.init(new File(event.getModConfigurationDirectory(),REFERENCE.MODID),inDev);
        Balance.init(new File(event.getModConfigurationDirectory(),REFERENCE.MODID),inDev);

        proxy.onInitStep(IInitListener.Step.PRE_INIT, event);

        //Check VampirismApi
        if(REFERENCE.HIGHEST_HUNTER_LEVEL!= VampirismAPI.getHighestHunterLevel()||REFERENCE.HIGHEST_VAMPIRE_LEVEL!=VampirismAPI.getHighestVampireLevel()){
            log.e("Vampirism","There seems to be a problem with Vampirism's API");
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.onInitStep(IInitListener.Step.POST_INIT, event);
        FractionRegistry.finish();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event){
        event.registerServerCommand(new VampirismCommand());
    }

    private void checkDevEnv(){
        if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            inDev = true;
            log.inDev = true;
        }
    }

}
