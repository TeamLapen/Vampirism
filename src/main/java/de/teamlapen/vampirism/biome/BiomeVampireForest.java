package de.teamlapen.vampirism.biome;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * 
 * @author WILLIAM
 *
 *         For vampirism versions around 0.5 you can find the biome at -1050,88,677 at seed -8086880447615324958
 */
public class BiomeVampireForest extends BiomeGenBase {
	public final static String name = "vampireForest";

	@SuppressWarnings("unchecked")
	public BiomeVampireForest(int id) {
		super(id);

		this.spawnableCreatureList.clear();
		this.spawnableMonsterList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableMonsterList.add(new SpawnListEntry(EntityGhost.class, 10, 2, 3));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityVampireBaron.class, 1, 1, 1));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityBlindingBat.class, 3, 4, 8));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityVampire.class, 10,2,3));
		this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityDummyBittenAnimal.class,10,3,5));


		this.topBlock = ModBlocks.cursedEarth;
		this.fillerBlock = ModBlocks.cursedEarth;
		this.theBiomeDecorator.treesPerChunk = 5;
		this.theBiomeDecorator.grassPerChunk = 4;
		this.theBiomeDecorator.deadBushPerChunk = 4;
		this.worldGeneratorTrees=new WorldGenTrees(false,4,1,0,false);

		// Add the vampire forest flower here
		this.flowers.clear();
		this.addFlower(ModBlocks.vampireFlower, 1, 10);

		this.canSpawnLightningBolt();
		//this.waterColorMultiplier = 14745518; // same as swamp
		this.waterColorMultiplier = 0xFF2505;
	}

	@Override
	public void decorate(World world, Random rand, int coordX, int coordZ) {
		CastlePositionData data = CastlePositionData.get(world);
		if (!data.isPosAt(coordX, coordZ)) {
			super.theBiomeDecorator.decorateChunk(world, rand, this, coordX, coordZ);

			for (int j = 0; j < 3; ++j) {
				int x = coordX + rand.nextInt(16);
				int z = coordZ + rand.nextInt(16);
				int y = world.getHeightValue(x, z);
				if (world.getBlock(x, y - 1, z) == ModBlocks.cursedEarth && world.getBlock(x, y, z) == Blocks.air) {
					world.setBlock(x, y, z, ModBlocks.vampireFlower, 0, 3);
				}
			}
		}
	}

	/**
	 * Normally provides the basic grass color based on the biome temperature and rainfall For the Vampire forest, we want a purple tint
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public int getBiomeGrassColor(int p_150558_1_, int p_150558_2_, int p_150558_3_) {
		// int grassColor = 0x7A317A; // dark purple
		int grassColor = 0x1E1F1F; // Mixture of purple and green
		return grassColor;
	}

	@Override public int getSkyColorByTemp(float p_76731_1_) {
		return 0xA33641;
	}

	@Override public int getBiomeFoliageColor(int p_150571_1_, int p_150571_2_, int p_150571_3_) {
		return 0x1E1F1F;
	}
}
