package de.teamlapen.vampirism.generation;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.generation.structures.GenerateBloodAltar;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.MobProperties;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * 
 * @author WILLIAM
 *
 */
public class WorldGenVampirism implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		switch (world.provider.dimensionId) {
		case -1:
			generateNether(world, random, chunkX * 16, chunkZ * 16);
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
		case 1:
			generateEnd(world, random, chunkX * 16, chunkZ * 16);
		}

	}

	private void generateEnd(World world, Random random, int x, int z) {

	}

	private void generateNether(World world, Random random, int x, int z) {

	}

	private void generateSurface(World world, Random random, int x, int z) {
		addStructures(world, random, x, z);
		addEntities(world, random, x, z);
	}

	private void addEntities(World world, Random random, int x, int z) {
		// parameters are x, y, z, r (radius)
		// returns village if the distance from center to the x, y, z coordinates is < the village radius + r
		int y = world.getHeightValue(x, z); 
		Village v = world.villageCollectionObj.findNearestVillage(x, y, z, 40);
		if (v == null) {
			return;
		}

		int r = v.getVillageRadius();
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(v.getCenter().posX - r, 0, v.getCenter().posZ - r, v.getCenter().posX + r,
				world.getActualHeight(), v.getCenter().posZ + r);

		// Spawn hunters in villages
		int spawnedHunter = world.getEntitiesWithinAABB(EntityVampireHunter.class, box).size();
		Logger.i("Test", "Found village at: " + v.getCenter().posX + " " + v.getCenter().posY 
				+ " " + v.getCenter().posZ + " with " + spawnedHunter + " Hunters");
		// k1 should probably be smaller, all 5 hunters are spawning in a single chunk in the village
		for (int k1 = 1; k1 < 10 && spawnedHunter < MobProperties.vampireHunter_maxPerVillage; k1++) {
			int l1 = v.getCenter().posX + world.rand.nextInt(16) - 8;
			int i2 = v.getCenter().posY + world.rand.nextInt(6) - 3;
			int j2 = v.getCenter().posZ + world.rand.nextInt(16) - 8;

			if (v.isInRange(l1, i2, j2) && this.isValidVampireHunterSpawningLocation(world, l1, i2, j2)) {
				Vec3 pos = Vec3.createVectorHelper(l1, i2, j2);
				Entity e = EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME, world);
				e.setLocationAndAngles(pos.xCoord, pos.yCoord, pos.zCoord, 0.0F, 0.0F);
				if (!((EntityVampireHunter) e).getCanSpawnHere())
					return;
				((EntityVampireHunter) e).isLookingForHome = false;
				((EntityVampireHunter) e).setHomeArea(v.getCenter().posX, v.getCenter().posY, v.getCenter().posZ, r);
				world.spawnEntityInWorld(e);
				Logger.i("HunterSpawn", "Spawned Hunter at: " + pos.xCoord + " " + pos.yCoord + " " + pos.zCoord);

				spawnedHunter++;
			}
		}
	}

	private void addStructures(World world, Random random, int x, int z) {
		int chance = random.nextInt(1000);
		boolean generatedStructure = false; // needed when a 2nd structure is added
		int spawnChance = 0;
		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(x, z);
		
		spawnChance += GenerateBloodAltar.spawnChance; 
		if (generatedStructure == false && chance < spawnChance)
		{
			if (biome == BiomeGenBase.swampland || biome == BiomeGenBase.roofedForest) {
				// Create Blood Altar
				int posX = x + random.nextInt(16);
				int posZ = z + random.nextInt(16);
				// Set y to center of altar
				int posY = world.getHeightValue(posX + 1, posZ + 1); 
				generatedStructure = new GenerateBloodAltar().generate(world, random, posX, posY, posZ);
			}
		}
	}

	private boolean isValidVampireHunterSpawningLocation(World world, int x, int y, int z) {
		if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
			return false;
		} else {
			if (world.getBlock(x, y, z).isNormalCube()) {
				return false;
			}
			if (world.getBlock(x, y + 1, z).isNormalCube()) {
				return false;
			}
			return true;
		}
	}
}
