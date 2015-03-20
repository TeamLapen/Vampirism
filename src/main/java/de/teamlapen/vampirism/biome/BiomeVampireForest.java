package de.teamlapen.vampirism.biome;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
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
        
        this.topBlock = ModBlocks.cursedEarth;
        this.fillerBlock = Blocks.dirt;
    	this.theBiomeDecorator.treesPerChunk = 6;
		this.theBiomeDecorator.grassPerChunk = 5;
		this.theBiomeDecorator.deadBushPerChunk = 1;
        this.theBiomeDecorator.mushroomsPerChunk = 4;

        // Add the vampire forest flower here
        this.flowers.clear();
        this.addFlower(ModBlocks.vampireFlower, 1, 10);
		
        this.canSpawnLightningBolt();
        this.waterColorMultiplier = 14745518;  // same as swamp
        this.setDisableRain();
	}
	
    /**
     * Normally provides the basic grass color based on the biome temperature and rainfall
     * For the Vampire forest, we want a purple tint
     */
	@Override
    @SideOnly(Side.CLIENT)
    public int getBiomeGrassColor(int p_150558_1_, int p_150558_2_, int p_150558_3_)
    {
//		int grassColor = 0x7A317A; // dark purple
		int grassColor = 0x3D3D52; // Mixture of purple and green
        return getModdedBiomeGrassColor(grassColor);
    }
	
	@Override
    public void decorate(World world, Random rand, int chunk_X, int chunk_Z)
    {
        super.theBiomeDecorator.decorateChunk(world, rand, this, chunk_X, chunk_Z);
        
        for (int j = 0; j < 5; ++j)
        {
            int x = chunk_X + rand.nextInt(16);
            int z = chunk_Z + rand.nextInt(16);
            int y = world.getHeightValue(x, z);
            if (world.getBlock(x, y - 1, z) == ModBlocks.cursedEarth && world.getBlock(x, y, z) == Blocks.air) {
        		world.setBlock(x, y, z, ModBlocks.vampireFlower, 0, 3);
            	Logger.i("BiomeVampireForest", "placed a vampire flower at: " + x + "," + y + "," + z);
            }
        }
    }
}
