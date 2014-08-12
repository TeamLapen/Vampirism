package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.common.registry.EntityRegistry;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.*;

public abstract class CommonProxy implements IProxy{
	
	public void registerEntitys(){
		int id=0;
		
		//Registration of vampire hunter
		EntityRegistry.registerModEntity(EntityVampireHunter.class, "VampireHunter", id, VampirismMod.instance, 80, 1, true);
		EntityRegistry.addSpawn(EntityVampireHunter.class, 2, 0, 1, EnumCreatureType.monster, BiomeGenBase.getBiomeGenArray());
		id++;
		
		//Registration of vampire
		EntityRegistry.registerModEntity(EntityVampire.class, "Vampire", id, VampirismMod.instance,80, 1, true);
		EntityRegistry.addSpawn(EntityVampire.class, 2, 0, 1, EnumCreatureType.monster, BiomeGenBase.getBiomeGenArray());
		id++;
	}

}
