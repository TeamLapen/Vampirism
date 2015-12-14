package de.teamlapen.vampirism;

import de.teamlapen.lib.util.Logger;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
@Mod(modid = REFERENCE.MODID,name=REFERENCE.NAME,version = REFERENCE.VERSION,acceptedMinecraftVersions = "[1.8]",dependencies = "required-after:Forge@["+REFERENCE.FORGE_VERSION_MIN+",)",guiFactory = "de.teamlapen.vampirism.client.core.ModGuiFactory")
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
        ModPotions.init(event);
        ModBlocks.init(event);
        ModItems.init(event);
        ModBiomes.init(event);
        ModEntities.init(event);
        proxy.init(event);

        Object mod_event_handler=new ModEventHandler();
        MinecraftForge.EVENT_BUS.register(mod_event_handler);
        FMLCommonHandler.instance().bus().register(mod_event_handler);

        Object mod_player_event_handler=new ModPlayerEventHandler();
        MinecraftForge.EVENT_BUS.register(mod_player_event_handler);
        FMLCommonHandler.instance().bus().register(mod_player_event_handler);

        Object mod_entity_event_handler=new ModEntityEventHandler();
        MinecraftForge.EVENT_BUS.register(mod_entity_event_handler);
        FMLCommonHandler.instance().bus().register(mod_entity_event_handler);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        checkDevEnv();
        Configs.init(new File(event.getModConfigurationDirectory(),REFERENCE.MODID),inDev);
        Balance.init(new File(event.getModConfigurationDirectory(),REFERENCE.MODID),inDev);

        ModPotions.preInit(event);
        ModBlocks.preInit(event);
        ModItems.preInit(event);
        ModBiomes.preInit(event);
        ModEntities.preInit(event);
        ModBlocks.preInitAfterItems();
        ModItems.preInitAfterBlocks();
        proxy.preInit(event);

        //Check VampirismApi
        if(REFERENCE.HIGHEST_HUNTER_LEVEL!= VampirismAPI.getHighestHunterLevel()||REFERENCE.HIGHEST_VAMPIRE_LEVEL!=VampirismAPI.getHighestVampireLevel()){
            log.e("Vampirism","There seems to be a problem with Vampirism's API");
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        ModPotions.postInit(event);
        ModBiomes.postInit(event);
        proxy.postInit(event);
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
