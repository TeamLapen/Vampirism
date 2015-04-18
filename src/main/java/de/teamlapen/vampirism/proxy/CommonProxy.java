package de.teamlapen.vampirism.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import de.teamlapen.vampirism.ModBiomes;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.EntityDeadMob;
import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.EntityVampireLord;
import de.teamlapen.vampirism.entity.EntityVampireMinion;
import de.teamlapen.vampirism.entity.VampireEntityEventHandler;
import de.teamlapen.vampirism.entity.player.VampirePlayerEventHandler;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.villages.VillageVampireData;

public abstract class CommonProxy implements IProxy {

	/**
	 * Removes the stored data from map and returns it
	 * 
	 * @param name
	 * @return
	 */
	public static NBTTagCompound getEntityData(String name) {
		return extendedEntityData.remove(name);
	}

	/**
	 * Stores entity data
	 * 
	 * @param name
	 * @param compound
	 */
	public static void storeEntityData(String name, NBTTagCompound compound) {
		extendedEntityData.put(name, compound);
	}

	private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();
	
	private int modEntityId=0;

	@Override
	public void registerEntitys() {
		//Create a array of all biomes except hell and end
		int entityId=0;
		
		BiomeGenBase[] allBiomes =BiomeGenBase.getBiomeGenArray();
		allBiomes[9]=null;
		allBiomes[8]=null;
		BiomeGenBase[] biomes = Iterators.toArray(Iterators.filter(Iterators.forArray(allBiomes), Predicates.notNull()),
				BiomeGenBase.class);
		
		registerEntity(EntityVampireHunter.class,REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME,BALANCE.VAMPIRE_HUNTER_SPAWN_PROBE,1,2,EnumCreatureType.creature,biomes);
		registerEntity(EntityVampire.class,REFERENCE.ENTITY.VAMPIRE_NAME,BALANCE.VAMPIRE_SPAWN_PROBE,1,3,EnumCreatureType.monster,biomes);
		registerEntity(EntityVampireLord.class,REFERENCE.ENTITY.VAMPIRE_LORD_NAME,BALANCE.VAMPIRE_LORD_SPAWN_PROBE,1,1,EnumCreatureType.monster,ModBiomes.biomeVampireForest);
		registerEntity(EntityVampireMinion.class,REFERENCE.ENTITY.VAMPIRE_MINION_NAME,false);
		registerEntity(EntityDeadMob.class,REFERENCE.ENTITY.DEAD_MOB_NAME,false);
		registerEntity(EntityDracula.class,REFERENCE.ENTITY.DRACULA_NAME,false);
		registerEntity(EntityGhost.class,REFERENCE.ENTITY.GHOST_NAME,5,1,2,EnumCreatureType.monster,ModBiomes.biomeVampireForest);
		registerEntity(EntityBlindingBat.class,REFERENCE.ENTITY.BLINDING_BAT_NAME,false);

	}
	private void registerEntity(Class<? extends Entity> clazz,String name,boolean useGlobal){

		Logger.i("EntityRegister", "Adding "+name+"("+clazz.getSimpleName()+")"+(useGlobal?" with global id":"with mod id"));
		if(useGlobal){
			EntityRegistry.registerGlobalEntityID(clazz, name, EntityRegistry.findGlobalUniqueEntityId(),calculateColor(name) ,calculateColor(name+"2"));
		}
		else{
			name=name.replace("vampirism.", "");
			EntityRegistry.registerModEntity(clazz, name, modEntityId++, VampirismMod.instance, 80, 1, true);
		}

	}
	
	private int calculateColor(String n){
		int hash=n.hashCode();
		while(hash>0xFFFFFF){
			hash=(int)((float)hash/50F);
		}
		return hash;
	}
	
	/**
	 * Registers the Entity and it's spawn
	 * @param clazz Class
	 * @param name Name
	 * @param probe WeightedProbe
	 * @param min Min group size
	 * @param max Max group size
	 * @param type CreatureType
	 * @param biomes Biomes
	 */
	private void registerEntity(Class<? extends EntityLiving> clazz,String name,int probe,int min,int max,EnumCreatureType type,BiomeGenBase... biomes){
		this.registerEntity(clazz, name,true);
		Logger.i("EntityRegister", "Adding spawn with probe of "+probe);
		EntityRegistry.addSpawn(clazz, probe, min, max, type, biomes);
	}
	@Override
	public void registerSubscriptions() {
		MinecraftForge.EVENT_BUS.register(new VampirePlayerEventHandler());
		MinecraftForge.EVENT_BUS.register(new VampireEntityEventHandler());
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event){
		//Loading VillageVampireData
		FMLCommonHandler.instance().bus().register(VillageVampireData.get(event.world));//Not sure if this is the right position or if it could lead to a memory leak
	}
	
	

}
