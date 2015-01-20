package de.teamlapen.vampirism.generation.structures;

import java.util.Random;

import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * 
 * @author WILLIAM
 *
 */
public class GenerateBloodAltar extends WorldGenerator {
	public static int spawnChance = 25;           // chance n/1000

	protected Block[] GetValidSpawnBlocks()	{
			return new Block[] { Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.cobblestone, Blocks.gravel };
	}

	public boolean LocationIsValidSpawn(World world, int x, int y, int z) {

		Block checkBlock = world.getBlock(x, y - 1, z);
		Block blockAbove = world.getBlock(x, y , z);
		Block blockBelow = world.getBlock(x, y - 2, z);

		for (Block i : GetValidSpawnBlocks())
		{
			if (blockAbove != Blocks.air)
			{
				return false;
			}
			if (checkBlock == i)
			{
				return true;
			}
			else if (checkBlock == Blocks.snow_layer && blockBelow == i)
			{
				return true;
			}
			else if (checkBlock.getMaterial() == Material.plants && blockBelow == i)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z)
	{
		if(!LocationIsValidSpawn(world, x, y, z) || !LocationIsValidSpawn(world, x + 2, y, z) || !LocationIsValidSpawn(world, x + 2, y, z + 2) || !LocationIsValidSpawn(world, x, y, z + 2))
			return false;

		Logger.i("GenerateBloodAltar", "Spawned Blood Altar at: " + x + " " + y + " " + z);
		world.setBlock(x + 0, y + 0, z + 0, Blocks.obsidian, 0, 3);
		world.setBlock(x + 1, y + 0, z + 0, Blocks.obsidian, 0, 3);
		world.setBlock(x + 2, y + 0, z + 0, Blocks.obsidian, 0, 3);
		world.setBlock(x + 0, y + 0, z + 1, Blocks.obsidian, 0, 3);
		world.setBlock(x + 1, y + 0, z + 1, Blocks.obsidian, 0, 3);
		world.setBlock(x + 2, y + 0, z + 1, Blocks.obsidian, 0, 3);
		world.setBlock(x + 0, y + 0, z + 2, Blocks.obsidian, 0, 3);
		world.setBlock(x + 1, y + 0, z + 2, Blocks.obsidian, 0, 3);
		world.setBlock(x + 2, y + 0, z + 2, Blocks.obsidian, 0, 3);
		world.setBlock(x + 0, y + 1, z + 0, Blocks.air, 0, 3);
		world.setBlock(x + 1, y + 1, z + 0, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 1, z + 0, Blocks.air, 0, 3);
		world.setBlock(x + 0, y + 1, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 1, y + 1, z + 1, ModBlocks.bloodAltar, 1, 3);
		world.setBlock(x + 2, y + 1, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 0, y + 1, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 1, y + 1, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 1, z + 2, Blocks.air, 0, 3);
		
		return true;
	}
}
