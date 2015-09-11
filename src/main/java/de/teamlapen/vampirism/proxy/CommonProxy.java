package de.teamlapen.vampirism.proxy;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import de.teamlapen.vampirism.*;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.convertible.BiteableRegistry;
import de.teamlapen.vampirism.entity.convertible.EntityConvertedCreature;
import de.teamlapen.vampirism.entity.convertible.EntityConvertedSheep;
import de.teamlapen.vampirism.entity.convertible.EntityConvertedVillager;
import de.teamlapen.vampirism.entity.minions.EntityRemoteVampireMinion;
import de.teamlapen.vampirism.entity.minions.EntitySaveableVampireMinion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.VampirePlayerEventHandler;
import de.teamlapen.vampirism.item.ItemSpawnEgg;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.TickRunnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class CommonProxy implements IProxy {

	/**
	 * List of entity names which should be spawnable
	 */
	public static final List<String> spawnableEntityNames = new ArrayList<String>();
	private List<TickRunnable> clientRunnables = new ArrayList<TickRunnable>();
	private List<TickRunnable> serverRunnables = new ArrayList<TickRunnable>();
	private int modEntityId = 0;


	@Override
	public void preInit() {
		ModPotion.preInit();
		ModBlocks.preInit();
		ModItems.preInit();

		ModBiomes.preInit();
		ModBlocks.registerRecipes();
		ModItems.registerRecipes();
	}

	@Override
	public void init() {
		registerEntitys();
		registerSubscriptions();
	}

	@Override
	public void postInit() {

	}

	@Override public void onTick(TickEvent event) {
		if (event instanceof TickEvent.ServerTickEvent) {
			WorldServer server = MinecraftServer.getServer().worldServerForDimension(0);

			if (server.areAllPlayersAsleep() && event.phase.equals(TickEvent.Phase.START)) {
				Logger.i("ServerProxy", "All players are asleep");
				if (server.playerEntities.size() > 0) {// Should always be the case, but better check
					if (VampirePlayer.get(((EntityPlayer) server.playerEntities.get(0))).sleepingCoffin) {
						Logger.i("CommonProxy", "All players are sleeping in a coffin ->waking them up");
						// Set time to next night
						long i = server.getWorldTime() + 24000L;
						server.setWorldTime(i - i % 24000L - 11000L);

						wakeAllPlayers(server);
					} else {
						Logger.i("CommonProxy", "All players are sleeping in a bed");
					}
				}

			}
			if (VampirismMod.potionFail && event.phase.equals(TickEvent.Phase.END) && MinecraftServer.getServer().getTickCounter() % 200 == 0) {
				MinecraftServer.getServer().getConfigurationManager()
						.sendChatMsg(new ChatComponentText("There was a SEVERE error adding Vampirism's potions, please check and change the configured IDs of " + ModPotion.checkPotions()));
			}
			
			Iterator<TickRunnable> iterator = serverRunnables.iterator();
			while (iterator.hasNext()) {
				TickRunnable run = iterator.next();
				if (!run.shouldContinue()) {
					iterator.remove();
				} else {
					run.onTick();
				}
			}
			onServerTick((TickEvent.ServerTickEvent) event);
		} else if (event instanceof TickEvent.ClientTickEvent) {
			Iterator<TickRunnable> iterator = clientRunnables.iterator();
			while (iterator.hasNext()) {
				TickRunnable run = iterator.next();
				if (!run.shouldContinue()) {
					iterator.remove();
				} else {
					run.onTick();
				}
			}
			onClientTick((TickEvent.ClientTickEvent) event);
		}
	}

	protected void addTickRunnable(TickRunnable run, boolean client) {
		List<TickRunnable> list = client ? clientRunnables : serverRunnables;
		if (list.size() > 100) {
			Logger.w("CommonProxy", "There are over 100 runnables in %s list. Deleting them.", client ? "client" : "server");
		}
		list.add(run);
	}

	public abstract void onClientTick(TickEvent.ClientTickEvent event);

	public abstract void onServerTick(TickEvent.ServerTickEvent event);

	private void registerEntity(Class<? extends Entity> clazz, String name, boolean egg) {

		Logger.d("EntityRegister", "Adding " + name + "(" + clazz.getSimpleName() + ") with mod id %d", modEntityId);
		EntityRegistry.registerModEntity(clazz, name.replace("vampirism.", ""), modEntityId++, VampirismMod.instance, 80, 1, true);
		if (egg) {
			spawnableEntityNames.add(name);
		}

	}

	/**
	 * Registers the entity and add a spawn entry for it
	 *
	 * @param clazz
	 * @param name
	 * @param probe
	 * @param min
	 * @param max
	 * @param type
	 * @param biomes
	 */
	private void registerEntity(Class<? extends EntityLiving> clazz, String name, int probe, int min, int max, EnumCreatureType type, BiomeGenBase... biomes) {
		this.registerEntity(clazz, name, true);
		Logger.d("EntityRegister", "Adding spawn with probe of " + probe);
		EntityRegistry.addSpawn(clazz, probe, min, max, type, biomes);
	}

	public void registerEntitys() {
		// Create a array of all biomes except hell and end
		BiomeGenBase[] allBiomes = BiomeGenBase.getBiomeGenArray();
		allBiomes = Arrays.copyOf(allBiomes, allBiomes.length);
		allBiomes[9] = null;
		allBiomes[8] = null;
		BiomeGenBase[] allBiomesNoVampire = Arrays.copyOf(allBiomes, allBiomes.length);
		int vId = ModBiomes.biomeVampireForest.biomeID;
		if (vId > 0 && vId < allBiomes.length) {
			allBiomesNoVampire[vId] = null;
		}
		BiomeGenBase[] biomes = Iterators.toArray(Iterators.filter(Iterators.forArray(allBiomes), Predicates.notNull()), BiomeGenBase.class);
		allBiomesNoVampire = Iterators.toArray(Iterators.filter(Iterators.forArray(allBiomesNoVampire), Predicates.notNull()), BiomeGenBase.class);
		registerEntity(EntityVampireHunter.class, REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME, true);
		registerEntity(EntityVampire.class, REFERENCE.ENTITY.VAMPIRE_NAME, BALANCE.VAMPIRE_SPAWN_PROBE, 1, 3, EnumCreatureType.MONSTER, allBiomesNoVampire);
		registerEntity(EntityVampireBaron.class, REFERENCE.ENTITY.VAMPIRE_BARON, true);
		EntityList.stringToClassMapping.put("vampirism.vampireLord", EntityVampireBaron.class);
		registerEntity(EntitySaveableVampireMinion.class, REFERENCE.ENTITY.VAMPIRE_MINION_SAVEABLE_NAME, false);
		registerEntity(EntityRemoteVampireMinion.class, REFERENCE.ENTITY.VAMPIRE_MINION_REMOTE_NAME, false);
		registerEntity(EntityDeadMob.class, REFERENCE.ENTITY.DEAD_MOB_NAME, false);
		registerEntity(EntityDracula.class, REFERENCE.ENTITY.DRACULA_NAME, false);
		registerEntity(EntityGhost.class, REFERENCE.ENTITY.GHOST_NAME,true);
		registerEntity(EntityBlindingBat.class, REFERENCE.ENTITY.BLINDING_BAT_NAME, false);
		registerEntity(EntityDummyBittenAnimal.class,REFERENCE.ENTITY.DUMMY_CREATURE,false);
		registerEntity(EntityPortalGuard.class, REFERENCE.ENTITY.PORTAL_GUARD, false);
		registerEntity(EntityConvertedCreature.class, REFERENCE.ENTITY.CONVERTED_CREATURE, false);
		registerEntity(EntityConvertedVillager.class, REFERENCE.ENTITY.CONVERTED_VILLAGER, false);
		registerEntity(EntityConvertedSheep.class, REFERENCE.ENTITY.CONVERTED_SHEEP, false);
		ModItems.spawn_egg= new ItemSpawnEgg(spawnableEntityNames);
		GameRegistry.registerItem(ModItems.spawn_egg, ItemSpawnEgg.name);
		OreDictionary.registerOre("mobEgg", ModItems.spawn_egg);

		registerConvertibles();

	}

	private void registerConvertibles() {
		String base = REFERENCE.MODID + ":textures/entity/vanilla/%sOverlay.png";
		BiteableRegistry.addConvertible(EntityCow.class, String.format(base, "cow"));
		BiteableRegistry.addConvertible(EntityPig.class, String.format(base, "pig"));
		BiteableRegistry.addConvertible(EntityOcelot.class, String.format(base, "cat"));
		BiteableRegistry.addConvertible(EntityHorse.class, String.format(base, "horse"));
		BiteableRegistry.addConvertible(EntitySheep.class, String.format(base, "sheep"), new EntityConvertedSheep.ConvertingSheepHandler());
		BiteableRegistry.addConvertible(EntityVillager.class, String.format(base, "villager"), new EntityConvertedVillager.VillagerConvertingHandler());
	}
	public void registerSubscriptions() {
		Object playerHandler = new VampirePlayerEventHandler();
		MinecraftForge.EVENT_BUS.register(playerHandler);
		FMLCommonHandler.instance().bus().register(playerHandler);
		MinecraftForge.EVENT_BUS.register(new VampireEntityEventHandler());
		Object handler = new VampirismEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
	}

	private void wakeAllPlayers(WorldServer server) {
		@SuppressWarnings("rawtypes") Iterator iterator = server.playerEntities.iterator();

		while (iterator.hasNext()) {
			EntityPlayerMP p = (EntityPlayerMP) iterator.next();
			VampirePlayer.get(p).wakeUpPlayer(true, false, false, true);

		}
	}
}
