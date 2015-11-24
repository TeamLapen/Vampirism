package de.teamlapen.vampirism;

import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Main class for Vampirism
 */
@Mod(modid = REFERENCE.MODID,name=REFERENCE.NAME,version = REFERENCE.VERSION,acceptedMinecraftVersions = "["+REFERENCE.MINECRAFT_VERSION+"]",dependencies = "required-after:Forge@["+REFERENCE.FORGE_VERSION+",)")
public class VampirismMod {

    @Mod.Instance(value = REFERENCE.MODID)
    public static VampirismMod instance;

    @SidedProxy(clientSide = "de.teamlapen.vampirism.proxy.ClientProxy", serverSide = "de.teamlapen.vampirism.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        proxy.init(event);
        ModPotions.init(event);
        ModBlocks.init(event);
        ModItems.init(event);
        ModBiomes.init(event);
        ModEntities.init(event);

    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        proxy.preInit(event);
        ModPotions.preInit(event);
        ModBlocks.preInit(event);
        ModItems.preInit(event);
        ModBiomes.preInit(event);
        ModEntities.preInit(event);
        ModBlocks.preInitAfterItems();
        ModItems.preInitAfterBlocks();

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit(event);
        ModPotions.postInit(event);
        ModBiomes.postInit(event);

    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event){

    }

}
