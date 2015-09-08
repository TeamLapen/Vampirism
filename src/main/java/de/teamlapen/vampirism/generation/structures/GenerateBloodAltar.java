package de.teamlapen.vampirism.generation.structures;

import de.teamlapen.vampirism.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * 
 * @author WILLIAM
 *
 */
public class GenerateBloodAltar extends WorldGenerator {

	@Override
	public boolean generate(World world, Random random, BlockPos pos) {
		if (!locationIsValidSpawn(world, pos) || !locationIsValidSpawn(world, pos.add(2,0,0)) || !locationIsValidSpawn(world, pos.add(2,0,2)) || !locationIsValidSpawn(world, pos.add(0,0,2)))
			return false;

		world.setBlockState(pos,Blocks.obsidian.getDefaultState(),3);
		world.setBlockState(pos.add(1, 0, 0), Blocks.obsidian.getDefaultState(), 3);
		world.setBlockState(pos.add(2, 0, 0), Blocks.obsidian.getDefaultState(), 3);
		world.setBlockState(pos.add(0, 0, 1), Blocks.obsidian.getDefaultState(), 3);
		world.setBlockState(pos.add(0, 0, 2), Blocks.obsidian.getDefaultState(), 3);
		world.setBlockState(pos.add(1, 0, 1), Blocks.obsidian.getDefaultState(), 3);
		world.setBlockState(pos.add(2, 0, 2), Blocks.obsidian.getDefaultState(), 3);
		world.setBlockState(pos.add(1, 0, 2), Blocks.obsidian.getDefaultState(), 3);
		world.setBlockState(pos.add(2, 0, 1), Blocks.obsidian.getDefaultState(), 3);

		world.setBlockState(pos.add(0, 1, 0), Blocks.air.getDefaultState(), 3);
		world.setBlockState(pos.add(1,1,0),Blocks.air.getDefaultState(),3);
		world.setBlockState(pos.add(2,1,0),Blocks.air.getDefaultState(),3);
		world.setBlockState(pos.add(0,1,1),Blocks.air.getDefaultState(),3);

		world.setBlockState(pos.add(1,1,1),ModBlocks.bloodAltar1.getStateFromMeta(1),3);

		world.setBlockState(pos.add(2,1,1),Blocks.air.getDefaultState(),3);
		world.setBlockState(pos.add(0,1,2),Blocks.air.getDefaultState(),3);
		world.setBlockState(pos.add(1,1,2),Blocks.air.getDefaultState(),3);
		world.setBlockState(pos.add(2,1,2),Blocks.air.getDefaultState(),3);
		return true;
	}



	protected Block[] getValidSpawnBlocks() {
		return new Block[] { Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.cobblestone, Blocks.gravel };
	}

	protected boolean locationIsValidSpawn(World world, BlockPos pos) {

		Block checkBlock = world.getBlockState(pos.down()).getBlock();
		Block blockAbove = world.getBlockState(pos).getBlock();
		Block blockBelow = world.getBlockState(pos.down(2)).getBlock();

		for (Block i : getValidSpawnBlocks()) {
			if (blockAbove != Blocks.air) {
				return false;
			}
			if (checkBlock == i) {
				return true;
			} else if (checkBlock == Blocks.snow_layer && blockBelow == i) {
				return true;
			} else if (checkBlock.getMaterial() == Material.plants && blockBelow == i) {
				return true;
			}
		}
		return false;
	}
}
