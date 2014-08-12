package de.teamlapen.vampirism.proxy;


import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.common.registry.EntityRegistry;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.util.Logger;

public abstract class CommonProxy implements IProxy{
	private static int startEntityId=10;
	
	public void registerEntitys(){
		int id=0;
		
		BiomeGenBase[] allBiomes = Iterators.toArray(Iterators.filter(Iterators.forArray(BiomeGenBase.getBiomeGenArray()),
				Predicates.notNull()), BiomeGenBase.class);
		
		//Registration of vampire hunter
		EntityRegistry.registerModEntity(EntityVampireHunter.class, "VampireHunter", id, VampirismMod.instance, 80, 1, true);
		EntityRegistry.addSpawn(EntityVampireHunter.class, 2, 0, 1, EnumCreatureType.monster, allBiomes);	
		addEntityMapping(EntityVampireHunter.class);
		id++;
		
		//Registration of vampire
		EntityRegistry.registerModEntity(EntityVampire.class, "Vampire", id, VampirismMod.instance,80, 1, true);
		EntityRegistry.addSpawn(EntityVampire.class, 2, 0, 1, EnumCreatureType.monster, allBiomes);
		addEntityMapping(EntityVampire.class);
		id++;
	}
	
	@SuppressWarnings("unchecked")
	public static void addEntityMapping(Class<? extends Entity> entity){
		Logger.i("RegisterEntitys", "Adding Mapping for "+entity.getName());
		int id = getUniqueEntityId();
	    EntityList.addMapping(EntityVampireHunter.class, "VampireHunter", id);
	    EntityList.entityEggs.put(id, new EntityList.EntityEggInfo(id, 0, 50));
	}
	
	public static int getUniqueEntityId()
	   {
	      do
	      {
	         startEntityId++;
	      }
	      while(EntityList.getStringFromID(startEntityId) !=null);
	      
	      return startEntityId;
	   }

}
