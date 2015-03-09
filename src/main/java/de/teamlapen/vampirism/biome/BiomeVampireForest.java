package de.teamlapen.vampirism.biome;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.entity.EntityGhost;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeVampireForest extends BiomeGenBase {
	public final static String name = "vampireForest";

	@SuppressWarnings("unchecked")
	public BiomeVampireForest(int id) {
		super(id);

        this.spawnableCreatureList.clear();
		this.spawnableCreatureList.add(new SpawnListEntry(EntityGhost.class, 5, 2, 10));        
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        
        this.topBlock = Blocks.grass;
        this.fillerBlock = ModBlocks.cursedEarth;
    	this.theBiomeDecorator.treesPerChunk = 6;
		this.theBiomeDecorator.grassPerChunk = 5;
		this.theBiomeDecorator.deadBushPerChunk = 1;
		this.theBiomeDecorator.bigMushroomsPerChunk = 1;
		
        this.canSpawnLightningBolt();
        this.waterColorMultiplier = 14745518;  // same as swamp
        this.setDisableRain();
//        this.setColor(14745518);
	}
}
