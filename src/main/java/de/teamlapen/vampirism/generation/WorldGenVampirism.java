package de.teamlapen.vampirism.generation;

import cpw.mods.fml.common.IWorldGenerator;
import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.ModBiomes;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.generation.castle.CastleGenerator;
import de.teamlapen.vampirism.generation.structures.GenerateBloodAltar;
import de.teamlapen.vampirism.generation.structures.GenerateHunterCamp;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.villages.VillageVampire;
import de.teamlapen.vampirism.villages.VillageVampireData;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenPlains;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

/**
 * 
 * @author WILLIAM
 *
 */
public class WorldGenVampirism implements IWorldGenerator {

	public final static CastleGenerator castleGenerator=new CastleGenerator();
	private final GenerateBloodAltar generateBloodAltar;
	private final GenerateHunterCamp generateHunterCamp;

	public WorldGenVampirism() {
		generateBloodAltar = new GenerateBloodAltar();
		generateHunterCamp = new GenerateHunterCamp();
	}

	/**
	 * Generates entities
	 * 
	 * @param world
	 *            World
	 * @param random
	 *            Random
	 * @param x
	 *            xCoord
	 * @param z
	 *            ZCoord
	 */
	private void addEntities(World world, Random random, int x, int z) {
		if(world.provider.terrainType== WorldType.FLAT)return;
		// Added try/catch block to resolve issue #15
		try {
			int y = world.getHeightValue(x, z);
			Village v = world.villageCollectionObj.findNearestVillage(x, y, z, 0);
			if (v == null) {
				return;
			}
			VillageVampire vv = VillageVampireData.get(world).getVillageVampire(v);
			if (vv == null) return;
			int spawnedHunter = world.getEntitiesWithinAABB(EntityVampireHunter.class, vv.getBoundingBox()).size();
			for (Entity e : Helper.spawnEntityInVillage(v, random.nextInt(3)- spawnedHunter, REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME, world)) {
				((EntityVampireHunter) e).setVillageArea(v.getCenter().posX, v.getCenter().posY, v.getCenter().posZ, v.getVillageRadius());
			}
		} catch (Exception e) {
			// If an exception occurs, it is likely a bug in minecraft, but we don't need to crash so we will return
			return;
		}
	}

	private void addStructures(World world, Random random, int x, int z) {
		int chance = random.nextInt(1000);
		boolean generatedStructure = false; // needed when a 2nd structure is
											// added
		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(x, z);

		if (!generatedStructure && chance < BALANCE.ALTAR_1_SPAWN_CHANCE) {
			if (biome == BiomeGenBase.swampland || biome == BiomeGenBase.roofedForest) {
				// Create Blood Altar
				int posX = x + random.nextInt(16);
				int posZ = z + random.nextInt(16);
				// Set y to center of altar
				int posY = world.getHeightValue(posX + 1, posZ + 1);
				generatedStructure = generateBloodAltar.generate(world, random, posX, posY, posZ);
			}
		}
		chance = random.nextInt(1000);
		int trees = biome.theBiomeDecorator.treesPerChunk;
		float bh = biome.heightVariation;
		float prop = 1;
		if (trees > 2 && trees < 11) {
			prop += trees;
		}
		prop += bh * 5;

		if (biome instanceof BiomeGenPlains) prop *= 0.7F;

		if (world.provider.terrainType.equals(WorldType.FLAT)) {
			prop = 0.2F;
		}
		Logger.t("Chance %d /%s", chance, BALANCE.HUNTER_CAMP_SPAWN_CHANCE * prop);
		if (!generatedStructure && !Configs.disable_hunter && chance < BALANCE.HUNTER_CAMP_SPAWN_CHANCE * prop && !biome.equals(ModBiomes.biomeVampireForest)) {
			int posX = x + random.nextInt(16);
			int posZ = z + random.nextInt(16);
			int posY = world.getHeightValue(x, z);
			if (world.getBlock(x, posY - 1, z).getMaterial() == Material.leaves) {
				posY--;
				while ((world.getBlock(x, posY, z).getMaterial() == Material.leaves || world.isAirBlock(x, posY, z)) && posY > 50) {
					posY--;
				}
			}

			float temp = biome.getFloatTemperature(posX, posY, posZ);
			if (generateHunterCamp.isValidTemperature(temp)) {
				generatedStructure = generateHunterCamp.generate(world, random, posX, posY, posZ);
			}

		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		switch (world.provider.dimensionId) {
		case -1:
			generateNether(world, random, chunkX, chunkZ);
			break;
		case 0:
			generateSurface(world, random, chunkX, chunkZ);
			break;
		case 1:
			generateEnd(world, random, chunkX, chunkZ);
			break;
		}
		if(world.provider.dimensionId==VampirismMod.castleDimensionId){
			castleGenerator.checkBiome(world,chunkX,chunkZ,random,true);
		}

	}

	private void generateEnd(World world, Random random, int x, int z) {

	}

	private void generateNether(World world, Random random, int x, int z) {

	}

	private void generateSurface(World world, Random random, int x, int z) {
		addEntities(world, random, x << 4 + 8, z << 4 + 8);
		if (world.getWorldInfo().isMapFeaturesEnabled()) {
			castleGenerator.checkBiome(world, x, z, random, false);
			addStructures(world, random, x << 4, z << 4);
		}

	}
}
