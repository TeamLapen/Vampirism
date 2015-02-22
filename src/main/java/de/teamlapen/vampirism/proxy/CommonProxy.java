package de.teamlapen.vampirism.proxy;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

import cpw.mods.fml.common.registry.EntityRegistry;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.VampireEntityEventHandler;
import de.teamlapen.vampirism.entity.player.VampirePlayerEventHandler;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

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

	@Override
	public void registerEntitys() {
		BiomeGenBase[] allBiomes = Iterators.toArray(Iterators.filter(Iterators.forArray(BiomeGenBase.getBiomeGenArray()), Predicates.notNull()),
				BiomeGenBase.class);

		// Registration of vampire hunter
		Logger.i("EntityRegister", "Adding "+REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME+" with spawn probe of "+BALANCE.VAMPIRE_HUNTER_SPAWN_PROBE);
		EntityRegistry.registerGlobalEntityID(EntityVampireHunter.class, REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME,
				EntityRegistry.findGlobalUniqueEntityId(), 0x666D68, 0x52E9E9);
		EntityRegistry.addSpawn(EntityVampireHunter.class, BALANCE.VAMPIRE_HUNTER_SPAWN_PROBE, 1, 2, EnumCreatureType.creature, allBiomes);

		// Registration of vampire
		Logger.i("EntityRegister", "Adding "+REFERENCE.ENTITY.VAMPIRE_NAME+" with spawn probe of "+BALANCE.VAMPIRE_SPAWN_PROBE);
		EntityRegistry.registerGlobalEntityID(EntityVampire.class, REFERENCE.ENTITY.VAMPIRE_NAME, EntityRegistry.findGlobalUniqueEntityId(),
				0x54B8DD, 0x34898D);
		EntityRegistry.addSpawn(EntityVampire.class, BALANCE.VAMPIRE_SPAWN_PROBE, 1, 3, EnumCreatureType.monster, allBiomes);
	}

	@Override
	public void registerSubscriptions() {
		MinecraftForge.EVENT_BUS.register(new VampirePlayerEventHandler());
		MinecraftForge.EVENT_BUS.register(new VampireEntityEventHandler());
	}

}
