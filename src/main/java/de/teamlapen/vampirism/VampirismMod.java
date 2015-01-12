package de.teamlapen.vampirism;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import de.teamlapen.vampirism.generation.WorldGenVampirism;
import de.teamlapen.vampirism.network.BloodAltarPacket;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.network.SpawnParticlePacket;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.util.ModBlocks;
import de.teamlapen.vampirism.util.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.villages.VillageDensity;

@Mod(modid = REFERENCE.MODID, name = REFERENCE.NAME, version = REFERENCE.VERSION)
public class VampirismMod {

	@Instance(value = REFERENCE.MODID)
	public static VampirismMod instance;

	@SidedProxy(clientSide = "de.teamlapen.vampirism.proxy.ClientProxy", serverSide = "de.teamlapen.vampirism.ServerProxy")
	public static IProxy proxy;

	public static SimpleNetworkWrapper modChannel;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerEntitys();
		proxy.registerRenderer();
		proxy.registerSounds();
		GameRegistry.registerWorldGenerator(new WorldGenVampirism(), 1000);
		proxy.registerSubscriptions();
		VillageDensity.init();
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent e) {
		e.registerServerCommand(new TestCommand()); // Keep there until final

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ModItems.init();
		ModBlocks.init();
		proxy.registerKeyBindings();
		setupNetwork();
		VillageDensity.preInit(event);
	}

	private void setupNetwork() {
		modChannel = NetworkRegistry.INSTANCE.newSimpleChannel(REFERENCE.MODID);
		int id = 0;
		modChannel.registerMessage(InputEventPacket.Handler.class, InputEventPacket.class, id++, Side.SERVER);
		modChannel.registerMessage(SpawnParticlePacket.Handler.class, SpawnParticlePacket.class, id++, Side.CLIENT);
		modChannel.registerMessage(BloodAltarPacket.Handler.class, BloodAltarPacket.class, id++, Side.CLIENT);
	}

}
