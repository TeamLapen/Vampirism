package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.common.registry.EntityRegistry;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.VampireHunter;

public abstract class CommonProxy implements IProxy{
	
	public void registerEntitys(){
		int id=0;
		EntityRegistry.registerModEntity(VampireHunter.class, "Vampire Hunter", id, VampirismMod.instance,80, 1	, true);
		id++;
		EntityRegistry.addSpawn(VampireHunter.class, 2, 0, 1, EnumCreatureType.monster, BiomeGenBase.getBiomeGenArray());
	}

}
