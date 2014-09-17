package de.teamlapen.vampirism.util;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import de.teamlapen.vampirism.entity.EntityVampireHunter;

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

	private void generateEnd(World world, Random random, int i, int j) {

	}

	private void generateNether(World world, Random random, int i, int j) {

	}

	private void generateSurface(World world, Random random, int i, int j) {
		Village v = world.villageCollectionObj.findNearestVillage(i, j, 90, 40);
		if (v == null) {
			return;
		}

		int r = v.getVillageRadius();
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(v.getCenter().posX - r, 0, v.getCenter().posZ - r, v.getCenter().posX + r,
				world.getActualHeight(), v.getCenter().posZ + r);

		// Spawn hunters in villages
		int spawnedHunter = world.getEntitiesWithinAABB(EntityVampireHunter.class, box).size();
		Logger.i("Test", "Found village with " + spawnedHunter);
		for (int k1 = 1; k1 < 100 && spawnedHunter < MobProperties.vampireHunter_maxPerVillage; k1++) {
			int l1 = v.getCenter().posX + world.rand.nextInt(16) - 8;
			int i2 = v.getCenter().posY + world.rand.nextInt(6) - 3;
			int j2 = v.getCenter().posZ + world.rand.nextInt(16) - 8;

			if (v.isInRange(l1, i2, j2) && this.isValidVampireHunterSpawningLocation(world, l1, i2, j2)) {
				Vec3 pos = Vec3.createVectorHelper(l1, i2, j2);
				Entity e = EntityList.createEntityByName(REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME, world);
				e.setLocationAndAngles(pos.xCoord, pos.yCoord, pos.zCoord, 0.0F, 0.0F);
				world.spawnEntityInWorld(e);
				Logger.i("HunterSpawn", "Spawned Hunter");

				spawnedHunter++;

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
