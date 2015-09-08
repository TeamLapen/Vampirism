package de.teamlapen.vampirism.generation;

import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.ModBiomes;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.generation.castle.CastleGenerator;
import de.teamlapen.vampirism.generation.structures.GenerateBloodAltar;
import de.teamlapen.vampirism.generation.structures.GenerateHunterCamp;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.villages.VillageVampire;
import de.teamlapen.vampirism.villages.VillageVampireData;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenPlains;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

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
	 * @param blockPos pos
	 */
	private void addEntities(World world, Random random, BlockPos blockPos) {
		if(world.getWorldInfo().getTerrainType()== WorldType.FLAT)return;
		// Added try/catch block to resolve issue #15
		try {
			blockPos=world.getHorizon(blockPos);
			Village v = world.villageCollectionObj.getNearestVillage(blockPos,0);
			if (v == null) {
				return;
			}
			VillageVampire vv = VillageVampireData.get(world).getVillageVampire(v);
			if (vv == null) return;
			int spawnedHunter = world.getEntitiesWithinAABB(EntityVampireHunter.class, vv.getBoundingBox()).size();
			for (Entity e : Helper.spawnEntityInVillage(v, random.nextInt(3)- spawnedHunter, REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME, world)) {
				((EntityVampireHunter) e).setVillageArea(blockPos, v.getVillageRadius());
			}
		} catch (Exception e) {
			// If an exception occurs, it is likely a bug in minecraft, but we don't need to crash so we will return
			return;
		}
	}

	private void addStructures(World world, Random random, BlockPos blockPos) {
		int chance = random.nextInt(1000);
		boolean generatedStructure = false; // needed when a 2nd structure is
											// added
		BiomeGenBase biome = world.getBiomeGenForCoords(blockPos);

		if (!generatedStructure && chance < BALANCE.ALTAR_1_SPAWN_CHANCE) {
			if (biome == BiomeGenBase.swampland || biome == BiomeGenBase.roofedForest) {

				blockPos=world.getHorizon(blockPos.add(random.nextInt(16)-8+1,0,random.nextInt(16)-8+1));
				generatedStructure = generateBloodAltar.generate(world, random, blockPos);
			}
		}
		chance = random.nextInt(1000);
		int trees = biome.theBiomeDecorator.treesPerChunk;
		float bh = biome.maxHeight;
		float prop = 1;
		if (trees > 2 && trees < 11) {
			prop += trees;
		}
		prop += bh * 5;

		if (biome instanceof BiomeGenPlains) prop *= 0.7F;

		if (world.getWorldInfo().getTerrainType().equals(WorldType.FLAT)) {
			prop = 0.2F;
		}
		if (!generatedStructure && !Configs.disable_hunter && chance < BALANCE.HUNTER_CAMP_SPAWN_CHANCE * prop && !biome.equals(ModBiomes.biomeVampireForest)) {
			blockPos=world.getHorizon(blockPos.add(random.nextInt(16)-8,0,random.nextInt(16)-8));
			if (world.getBlockState(blockPos.down()).getBlock().getMaterial() == Material.leaves) {
				blockPos=blockPos.down();
				while ((world.getBlockState(blockPos).getBlock().getMaterial() == Material.leaves || world.isAirBlock(blockPos)) && blockPos.getY() > 50) {
					blockPos=blockPos.down();
				}
			}

			float temp = biome.getFloatTemperature(blockPos);
			if (generateHunterCamp.isValidTemperature(temp)) {
				generatedStructure = generateHunterCamp.generate(world, random, blockPos);
			}

		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.getDimensionId()) {
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
		if(world.provider.getDimensionId()==VampirismMod.castleDimensionId){
			castleGenerator.checkBiome(world,chunkX,chunkZ,random,true);
		}
	}

	private void generateEnd(World world, Random random, int cx, int cz) {

	}

	private void generateNether(World world, Random random, int cx, int cz) {

	}

	private void generateSurface(World world, Random random, int cx, int cz) {
		BlockPos center=new BlockPos((cx<<4)+8,0,((cz<<4)+8));
		addEntities(world, random, center);
		if (world.getWorldInfo().isMapFeaturesEnabled()) {
			castleGenerator.checkBiome(world, cx,cz, random, false);
			addStructures(world, random, center);
		}

	}
}
