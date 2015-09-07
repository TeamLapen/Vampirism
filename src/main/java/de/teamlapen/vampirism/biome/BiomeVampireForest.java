package de.teamlapen.vampirism.biome;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
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


		this.topBlock = ModBlocks.cursedEarth.getDefaultState();
		this.fillerBlock = ModBlocks.cursedEarth.getDefaultState();
		this.theBiomeDecorator.treesPerChunk = 5;
		this.theBiomeDecorator.grassPerChunk = 4;
		this.theBiomeDecorator.deadBushPerChunk = 4;
		this.worldGeneratorTrees=new WorldGenTrees(false,4,1,0,false);

		// Add the vampire forest flower here
		this.flowers.clear();
		this.addFlower(ModBlocks.vampireFlower.getStateFromMeta(1), 10);

		this.canSpawnLightningBolt();
		//this.waterColorMultiplier = 14745518; // same as swamp
		this.waterColorMultiplier = 0xFF2505;
	}

	@Override
	public void decorate(World world, Random rand, BlockPos pos) {
		CastlePositionData data = CastlePositionData.get(world);
		if (!data.isPosAt(pos.getX(), pos.getZ())) {
			super.theBiomeDecorator.decorate(world, rand, this, pos);

			for (int j = 0; j < 3; ++j) {
				pos=world.getPrecipitationHeight(pos.add(rand.nextInt(16),0,rand.nextInt(16)));
				if (world.getBlockState(pos.down()).getBlock() == ModBlocks.cursedEarth && world.getBlockState(pos).getBlock().isAir(world,pos)) {
					world.setBlockState(pos, ModBlocks.vampireFlower.getStateFromMeta(0), 3);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getGrassColorAtPos(BlockPos p_180627_1_) {
		// int grassColor = 0x7A317A; // dark purple
		int grassColor = 0x1E1F1F; // Mixture of purple and green
		return grassColor;
	}


	@Override public int getSkyColorByTemp(float p_76731_1_) {
		return 0xA33641;
	}

	@Override
	public int getFoliageColorAtPos(BlockPos p_180625_1_) {
		return 0x1E1F1F;
	}
}
