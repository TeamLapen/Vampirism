package de.teamlapen.vampirism;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.util.REFERENCE;


@Mod(modid=REFERENCE.MODID,name=REFERENCE.NAME,version=REFERENCE.VERSION)
public class VampirismMod {
	
	@Instance(value=REFERENCE.MODID)
	public static VampirismMod instance;
	
	@SidedProxy(clientSide = "de.teamlapen.vampirism.proxy.ClientProxy",serverSide = "de.teamlapen.vampirism.ServerProxy")
	public static IProxy proxy;
	

	@EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.registerEntitys();
        proxy.registerRenderer();
        proxy.registerSounds();
       
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }

}
