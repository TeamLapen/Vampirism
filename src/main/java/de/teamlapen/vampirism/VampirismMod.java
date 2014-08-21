package de.teamlapen.vampirism;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import de.teamlapen.vampirism.client.gui.VampireHudOverlay;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.ModBlocks;
import de.teamlapen.vampirism.util.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.WorldGenVampirism;


@Mod(modid=REFERENCE.MODID,name=REFERENCE.NAME,version=REFERENCE.VERSION)
public class VampirismMod {
	
	@Instance(value=REFERENCE.MODID)
	public static VampirismMod instance;
	
	@SidedProxy(clientSide = "de.teamlapen.vampirism.proxy.ClientProxy",serverSide = "de.teamlapen.vampirism.ServerProxy")
	public static IProxy proxy;
	

	@EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		ModItems.init();
		ModBlocks.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.registerEntitys();
        proxy.registerRenderer();
        proxy.registerSounds();
        GameRegistry.registerWorldGenerator(new WorldGenVampirism(), 1000);
       
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.registerSubscriptions();
    	
    }
    
    
    @EventHandler
    public void onServerStart(FMLServerStartingEvent e){
    	e.registerServerCommand(new TestCommand()); //Keep there until final
    	
    }

}
