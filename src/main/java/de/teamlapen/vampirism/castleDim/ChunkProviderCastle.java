package de.teamlapen.vampirism.castleDim;

import de.teamlapen.vampirism.ModBlocks;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS;

/**
 * Chunkprovider for the castle world.
 * Creates a flat world with a limited size and an entrance area
 */
public class ChunkProviderCastle implements IChunkProvider {
	protected World worldObj;
	protected Random rand;
	public final static int MAX_Y_HEIGHT=35;

	public ChunkProviderCastle(World world, long seed) {
		worldObj = world;
		rand = new Random(seed);
	}

	@Override public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {
		BlockFalling.fallInstantly = true;
		int k = p_73153_2_ * 16;
		int l = p_73153_3_ * 16;
		BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(new BlockPos(k + 16,0, l + 16));
		this.rand.setSeed(this.worldObj.getSeed());
		long i1 = this.rand.nextLong() / 2L * 2L + 1L;
		long j1 = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long) p_73153_2_ * i1 + (long) p_73153_3_ * j1 ^ this.worldObj.getSeed());
		boolean flag = false;

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_, flag));

		//biomegenbase.decorate(this.worldObj, this.rand, k, l);
		if (TerrainGen.populate(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_, flag, ANIMALS)) {
			SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomegenbase, k + 8, l + 8, 16, 16, this.rand);
		}

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_, flag));

		BlockFalling.fallInstantly = false;
	}

	@Override
	public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_) {
		return false;
	}

	@Override public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
		return true;
	}

	@Override public boolean unloadQueuedChunks() {
		return false;
	}

	@Override public boolean canSave() {
		return true;
	}

	@Override public String makeString() {
		return "providerVampirismCastle";
	}

	@Override
	public List func_177458_a(EnumCreatureType p_177458_1_, BlockPos p_177458_2_) {
		return null;
	}

	@Override
	public BlockPos getStrongholdGen(World worldIn, String p_180513_2_, BlockPos p_180513_3_) {
		return null;
	}


	@Override public int getLoadedChunkCount() {
		return 0;
	}

	@Override
	public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_) {

	}

	@Override public void saveExtraData() {

	}

	@Override public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
		return false;
	}

	@Override public Chunk provideChunk(int p_73154_1_, int p_73154_2_) {
		this.rand.setSeed((long) p_73154_1_ * 341873128712L + (long) p_73154_2_ * 132897987541L);
		ChunkPrimer primer=new ChunkPrimer();
		BiomeGenBase[] biomesForGeneration;
		//biomesForGeneration=this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, p_73154_1_ * 16, p_73154_2_ * 16, 16, 16);
		this.generateTerrain(p_73154_1_, p_73154_2_, primer);
		Chunk chunk = new Chunk(this.worldObj, primer, p_73154_1_, p_73154_2_);
		//		byte[] abyte = chunk.getBiomeArray();
		//
		//		for (int k = 0; k < abyte.length; ++k)
		//		{
		//			abyte[k] = (byte)biomesForGeneration[k].biomeID;
		//		}

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public Chunk provideChunk(BlockPos blockPosIn) {
		return null;
	}

	protected void generateTerrain(int chunkX, int chunkZ,ChunkPrimer primer) {
		if (chunkZ == 6) {
			if (chunkX == 2)
				createEntranceChunk(primer, true);
			if (chunkX == 3)
				createEntranceChunk(primer, false);
		}
		if (chunkX > 5 || chunkX < 0 || chunkZ > 5 || chunkZ < 0)
			return;
		int y = 2;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int i = x * 128 * 16 | z * 128 | y;
				primer.setBlockState(i,Blocks.bedrock.getDefaultState());
			}
		}
		for (y += 1; y <=MAX_Y_HEIGHT; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					int i = x * 128 * 16 | z * 128 | y;
					primer.setBlockState(i,ModBlocks.cursedEarth.getDefaultState());
				}
			}
		}
	}

	/**
	 * Creates a half of the entrance area

	 */
	protected void createEntranceChunk(ChunkPrimer primer, boolean left) {
		int y = MAX_Y_HEIGHT;
		int x = 0;
		int z = 0;
		int i = 0;
		for (x = 0; x < 9; x++) {
			for (z = 0; z < 10; z++) {
				i = (left ? 15 - x : x) * 128 * 16 | z * 128 | y;
				primer.setBlockState(i,Blocks.obsidian.getDefaultState());
			}
		}
		for (x = 0; x < 2; x++) {
			z = 9;
			for (y = MAX_Y_HEIGHT; y < MAX_Y_HEIGHT+4; y++) {
				i = (left ? 15 - x : x) * 128 * 16 | z * 128 | y;
				primer.setBlockState(i,Blocks.bedrock.getDefaultState());
			}
		}
		y = MAX_Y_HEIGHT+1;
		x = 0;
		i = (left ? 15 - x : x) * 128 * 16 | z * 128 | y;
		primer.setBlockState(i,ModBlocks.castlePortal.getDefaultState());
		y = MAX_Y_HEIGHT+2;
		i = (left ? 15 - x : x) * 128 * 16 | z * 128 | y;
		primer.setBlockState(i,ModBlocks.castlePortal.getDefaultState());
		z = 10;
		y = MAX_Y_HEIGHT+1;
		i = (left ? 15 - x : x) * 128 * 16 | z * 128 | y;
		primer.setBlockState(i,Blocks.bedrock.getDefaultState());
		y = MAX_Y_HEIGHT+2;
		i = (left ? 15 - x : x) * 128 * 16 | z * 128 | y;
		primer.setBlockState(i,Blocks.bedrock.getDefaultState());
	}

	/**
	 * Checks if the given entity is in the main area and is thereby allowed to build
	 * @param e
	 * @return
	 */
	public static boolean allowedToBuildHere(Entity e){
		int cX=e.chunkCoordX;
		int cZ=e.chunkCoordZ;
		if(cX>0&&cX<6&&cZ>0&&cZ<6)return true;
		return false;
	}

}
