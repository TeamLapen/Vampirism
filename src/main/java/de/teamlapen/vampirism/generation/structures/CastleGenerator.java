package de.teamlapen.vampirism.generation.structures;

import de.teamlapen.vampirism.ModBiomes;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Max on 01.07.2015.
 */
public class CastleGenerator extends WorldGenerator {

	private final int MAX_TRYS=5;
	private final int MAX_CASTLES=2;
	private final int MAX_SIZE=6;
	private final int MIN_SIZE=4;
	private final static String TAG = "CastleGenerator";
	private final List biomes;

	public CastleGenerator() {
		this.biomes = new ArrayList<>();
		biomes.add(ModBiomes.biomeVampireForest);
	}

	private Position[] positions;
	private boolean checked=false;

	public void checkBiome(World world,int posX, int posZ,Random rnd){
		if(!checked){
			this.findPositions(world,rnd);
			if(positions!=null){
				for(int i=0;i<positions.length;i++){

					positions[i]=this.preGeneratePosition(positions[i],world,rnd);
				}
			}
		}
	}
	private void findPositions(World world,Random rnd){
			double phy = rnd.nextDouble() * Math.PI * 2.0D;
			int found=0;
			Position[] foundPos=new Position[MAX_CASTLES];
			for (int i = 0; i < MAX_TRYS&&found<MAX_CASTLES; i++)
			{
				double radius = ( rnd.nextDouble()) * 32 * +5;
				int x = (int)Math.round(Math.cos(phy) * radius);
				int z = (int)Math.round(Math.sin(phy) * radius);
				ChunkPosition chunkposition = world.getWorldChunkManager().findBiomePosition((x << 4) + 8, (z << 4) + 8, 112, this.biomes, rnd);

				if (chunkposition != null)
				{
					int cx = chunkposition.chunkPosX >> 4;
					int cz = chunkposition.chunkPosZ >> 4;
					foundPos[found]=new Position(cx,cz);
					found++;
				}

				phy += (Math.PI * 2D)/ (double)MAX_CASTLES;

				if (i>0&&i % MAX_CASTLES==0)
				{
					phy+=(Math.PI * 2D)/(double)MAX_CASTLES/2;
					radius*=(1+rnd.nextDouble());
				}
			}
			if(found>0) {
				positions = new Position[found];
				for (int j = 0; j < found; j++) {
					positions[j] = foundPos[j];
					Logger.i(TAG, "Found position %d %d", positions[j].chunkXPos, positions[j].chunkZPos);
				}
			}
		checked=true;
	}

	private Position preGeneratePosition(Position position, World world,Random rnd){
		final int TEST_SIZE=10;
		final int D_TEST_SIZE=TEST_SIZE*2;
		Boolean[][] biomes=new Boolean[D_TEST_SIZE][D_TEST_SIZE];
		for(int i=-TEST_SIZE;i<TEST_SIZE;i++){
			for(int j=-TEST_SIZE;j<TEST_SIZE;j++){
				biomes[i+TEST_SIZE][j+TEST_SIZE]=ModBiomes.biomeVampireForest.equals(world.getWorldChunkManager().getBiomeGenAt(position.chunkXPos+i<<4,position.chunkZPos+j<<4));
			}
		}
//		Logger.i(TAG,"Biomes");
//		this.printMatrix(biomes);
		Integer[][] help = new Integer[D_TEST_SIZE][D_TEST_SIZE];
		for(int i=0;i<D_TEST_SIZE;i++){
			help[i][0]=(biomes[i][0])?1:0;
		}
		for(int i=0;i<D_TEST_SIZE;i++){
			help[0][i]=(biomes[0][i])?1:0;
		}
		for(int i=1;i<D_TEST_SIZE;i++){
			for(int j=1;j<D_TEST_SIZE;j++){
				if(biomes[i][j]){
					help[i][j]=Math.min(help[i-1][j],Math.min(help[i][j-1],help[i-1][j-1]))+1;
				}
				else{
					help[i][j]=0;
				}
			}
		}

		int[] max={-1,-1,-1};
		for(int i=2;i<D_TEST_SIZE;i++){
			for(int j=2;j<D_TEST_SIZE;j++){
				if(help[i][j]>max[0]){
					max[0]=help[i][j];
					max[1]=i;
					max[2]=j;
				}
			}
		}
//		Logger.i(TAG,"Help");
//		this.printMatrix(help);
		Logger.i(TAG,"Found area with size %d at coords %d %d",max[0],position.chunkXPos-TEST_SIZE+max[1],position.chunkZPos-TEST_SIZE+max[2]);

		if(max[0]>=MIN_SIZE){
			int sx=MIN_SIZE+rnd.nextInt(Math.min(max[0],MAX_SIZE)-MIN_SIZE+1);
			int sy=MIN_SIZE+rnd.nextInt(Math.min(max[0],MAX_SIZE)-MIN_SIZE+1);
			Position p=new Position(max[1]-max[0]+((max[0]-sx)/2),max[2]-max[0]+((max[0]-sy)/2));
			p.setSize(sx,sy);
			return p;
		}
		return position;
	}

	private void printMatrix(Object[][] objects){
		for(int i=0;i<objects.length;i++){
			System.out.println();
			for(int j=0;j<objects[i].length;j++){
				System.out.print(String.valueOf(objects[i][j]));
			}
		}
	}
	@Override public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
		return false;
	}

	private class Position extends ChunkCoordIntPair{
		private int sizeX;
		private int sizeY;
		public Position(int chunkX, int chunkY) {
			super(chunkX, chunkY);
		}

		public void setSize(int x,int y){
			sizeX=x;
			sizeY=y;
		}

		public int getSizeX() {
			return sizeX;
		}

		public int getSizeY() {
			return sizeY;
		}

		public boolean hasSize(){
			return sizeY>0&&sizeX>0;
		}
	}
}
