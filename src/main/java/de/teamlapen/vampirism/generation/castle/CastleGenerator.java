package de.teamlapen.vampirism.generation.castle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.teamlapen.vampirism.ModBiomes;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * World generator for vampire castles in vampire biomes.
 * Manages finding, optimizing, pregenerating and generating of the castle (-position)
 *
 * @author Maxanier
 */
public class CastleGenerator extends WorldGenerator {

	private final static String TAG = "CastleGenerator";
	private static HashMap<String, BuildingTile> tileMap;
	private final int MAX_TRYS = 128;
	private final int MAX_CASTLES = 3;
	private final int MAX_SIZE = 6;
	private final int MIN_SIZE = 4;
	private final List biomes;
	private String[][] tiles;
	private int[][][] houseDirs;
	/**
	 * Used to check if multiple pregeneration calls run at the same time (due to multithreading). I'm pretty sure that this will never happen and the code won't work if it does). Generates a error message is this is the case.
	 */
	private boolean pregenerating;

	public CastleGenerator() {
		this.biomes = new ArrayList();
		biomes.add(ModBiomes.biomeVampireForest);
	}

	/**
	 * Loads all tiles from the jar
	 */
	public static void loadTiles() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		tileMap = new HashMap<String, BuildingTile>();
		loadTile("wall", gson, tileMap);
		loadTile("flatDirt", gson, tileMap);
		loadTile("house1", gson, tileMap);
		loadTile("house2", gson, tileMap);
		loadTile("stables", gson, tileMap);
		loadTile("castlell", gson, tileMap);
		loadTile("castlelr", gson, tileMap);
		loadTile("castleur", gson, tileMap);
		loadTile("castleul", gson, tileMap);
		loadTile("blacksmith", gson, tileMap);
		loadTile("grave", gson, tileMap);
		loadTile("pasture", gson, tileMap);
		loadTile("entrancel", gson, tileMap);
		loadTile("entrancer", gson, tileMap);
		loadTile("path", gson, tileMap);
	}

	/**
	 * Loads the tile and puts it to the given map if successful
	 *
	 * @param name
	 * @param gson
	 * @param addTo
	 */
	private static void loadTile(String name, Gson gson, Map<String, BuildingTile> addTo) {
		BuildingTile tile = loadTile(name, gson);
		if (tile != null)
			addTo.put(name, tile);
	}

	/**
	 * Loads a tile's json from the mod jar
	 *
	 * @param name
	 * @param gson
	 * @return
	 */
	private static BuildingTile loadTile(String name, Gson gson) {
		BuildingTile tile = null;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(VampirismMod.class.getResourceAsStream("/assets/vampirism/building_tiles/" + name + ".json"));
			tile = gson.fromJson(inputStreamReader, BuildingTile.class);
			tile.finishLoading();
			inputStreamReader.close();
		} catch (IOException e) {
			Logger.e(TAG, e, "Failed to load tile %s", name);
		} catch (NullPointerException e) {
			Logger.e(TAG, e, "Did not find tile %s", name);
		}
		return tile;
	}

	/**
	 * Should be called for every biome.
	 * Generates and optimizes positions if they was not generated before.
	 * Also (pre) generates the castle stuff when the given chunk is within a position
	 *
	 * @param world
	 * @param chunkX
	 * @param chunkZ
	 * @param rnd
	 * @param castleWorld Whether it is the castle dimension or the overworld
	 */
	public void checkBiome(World world, int chunkX, int chunkZ, Random rnd, boolean castleWorld) {
		CastlePositionData data = CastlePositionData.get(world);
		if (!data.checked) {
			if (castleWorld) {
				CastlePositionData.Position p = new CastlePositionData.Position(0, 0);
				p.setSize(6, 6);
				data.positions.add(p);
			} else {

				data.positions.addAll(this.findPositions(world, rnd));

				if (data.positions.size() > 0) {
					ListIterator<CastlePositionData.Position> iterator = data.positions.listIterator();
					while (iterator.hasNext()) {
						CastlePositionData.Position pos = iterator.next();
						CastlePositionData.Position pos2 = this.optimizePosition(pos, world, rnd);
						if (!pos2.equals(pos)) {
							pos = pos2;
							iterator.set(pos2);
						}
					}

				}
			}
			data.checked = true;
			data.markDirty();
		}
		if (data.positions.size() > 0) {
			for (CastlePositionData.Position p : data.positions) {

				if (p.isChunkInPosition(chunkX, chunkZ)) {
					if (!p.hasTiles()) {
						preGeneratePosition(p, world, rnd);
						data.markDirty();
					}
					String s = p.getTileAt(chunkX - p.chunkXPos, chunkZ - p.chunkZPos);
					String[] param = s.split(",");
					int height = p.getHeight();
					if (height == -1) {
						height = getAverageHeight(world.getChunkFromChunkCoords(chunkX, chunkZ));
						p.setHeight(height);
					}
					for (int i = (chunkX << 4); i < (chunkX << 4) + 16; i++) {
						for (int j = (chunkZ << 4); j < (chunkZ << 4) + 16; j++) {
							for (int k = height; k < height + 20; k++) {
								world.setBlockToAir(i, k, j);
							}
						}
					}
					for (int i = (chunkX << 4); i < (chunkX << 4) + 16; i++) {
						for (int j = (chunkZ << 4); j < (chunkZ << 4) + 16; j++) {
							for (int k = height - 1; k > height - 10; k--) {
								world.setBlock(i, k, j, ModBlocks.cursedEarth);
							}
						}
					}
					for (int i = 0; i < param.length; i += 2) {
						int rotation = Integer.parseInt(param[i]);
						BuildingTile tile = tileMap.get(param[i + 1]);
						if (tile != null) {
							tile.build(chunkX, chunkZ, world, height, rotation);
						}
					}
					p.markGenerated(chunkX, chunkZ);
					data.markDirty();
					break;
				}
			}
		}
	}

	/**
	 * Looks for positions in the world
	 *
	 * @param world
	 * @param rnd
	 * @return
	 */
	private List<CastlePositionData.Position> findPositions(World world, Random rnd) {
		Logger.d(TAG, "Looking for Positions");
		long t = System.currentTimeMillis();
		double phy = rnd.nextDouble() * Math.PI * 2.0D;
		List<CastlePositionData.Position> foundPos = new LinkedList<CastlePositionData.Position>();

		double radius = (rnd.nextDouble()) * 32 + 5;
		for (int i = 0; i < MAX_TRYS && foundPos.size() < MAX_CASTLES; i++) {
			int x = (int) Math.round(Math.cos(phy) * radius);
			int z = (int) Math.round(Math.sin(phy) * radius);
			ChunkPosition chunkposition = world.getWorldChunkManager().findBiomePosition((x << 4) + 8, (z << 4) + 8, 112, this.biomes, rnd);

			if (chunkposition != null) {
				int cx = chunkposition.chunkPosX >> 4;
				int cz = chunkposition.chunkPosZ >> 4;
				foundPos.add(new CastlePositionData.Position(cx, cz));
				Logger.d(TAG, "Found position %d %d", cx, cz);
				//Increase the counter to avoid mutliple castles in one spot
				i += (8 - (i % 8));
			}

			phy += (Math.PI / 4F);

			if (i > 0 && i % 8 == 0) {
				phy += (Math.PI / 8F);
				radius *= (1.5D + rnd.nextDouble());
			}
		}

		if (foundPos.size() == 0) {
			VampirismMod.vampireCastleFail = true;
			Logger.w(TAG, "Did not find any positions");
		}
		Logger.d(TAG, "Looking for positions took %s ms", System.currentTimeMillis() - t);
		return foundPos;
	}

	/**
	 * Optimizes the given position by choosing a large area only containing the vampire biome, setting a useable size and adjusting the position
	 * Finds the largest area with this algorithm http://www.geeksforgeeks.org/maximum-size-sub-matrix-with-all-1s-in-a-binary-matrix/
	 *
	 * @param position
	 * @param world
	 * @param rnd
	 * @return
	 */
	private CastlePositionData.Position optimizePosition(CastlePositionData.Position position, World world, Random rnd) {
		Logger.d(TAG, "Optimizing Position");
		long t = System.currentTimeMillis();
		final int TEST_SIZE = 10;
		final int D_TEST_SIZE = TEST_SIZE * 2;
		Boolean[][] biomes = new Boolean[D_TEST_SIZE][D_TEST_SIZE];
		for (int i = -TEST_SIZE; i < TEST_SIZE; i++) {
			for (int j = -TEST_SIZE; j < TEST_SIZE; j++) {
				biomes[i + TEST_SIZE][j + TEST_SIZE] = ModBiomes.biomeVampireForest.equals(world.getWorldChunkManager().getBiomeGenAt(position.chunkXPos + i << 4, position.chunkZPos + j << 4));
			}
		}
		//		Logger.i(TAG,"Biomes");
		//		this.printMatrix(biomes);
		Integer[][] help = new Integer[D_TEST_SIZE][D_TEST_SIZE];
		for (int i = 0; i < D_TEST_SIZE; i++) {
			help[i][0] = (biomes[i][0]) ? 1 : 0;
		}
		for (int i = 0; i < D_TEST_SIZE; i++) {
			help[0][i] = (biomes[0][i]) ? 1 : 0;
		}
		for (int i = 1; i < D_TEST_SIZE; i++) {
			for (int j = 1; j < D_TEST_SIZE; j++) {
				if (biomes[i][j]) {
					help[i][j] = Math.min(help[i - 1][j], Math.min(help[i][j - 1], help[i - 1][j - 1])) + 1;
				} else {
					help[i][j] = 0;
				}
			}
		}

		int[] max = { -1, -1, -1 };
		for (int i = 2; i < D_TEST_SIZE; i++) {
			for (int j = 2; j < D_TEST_SIZE; j++) {
				if (help[i][j] > max[0]) {
					max[0] = help[i][j];
					max[1] = i;
					max[2] = j;
				}
			}
		}
		//		Logger.i(TAG,"Help");
		//		this.printMatrix(help);

		Logger.d(TAG, "Optimizing position took %s ms", System.currentTimeMillis() - t);
		if (max[0] >= MIN_SIZE) {
			int highcx = position.chunkXPos - TEST_SIZE + max[1];
			int highcz = position.chunkZPos - TEST_SIZE + max[2];
			int lowcx = highcx - max[0];
			int lowcz = highcz - max[0];
			Logger.d(TAG, "Found fitting area with size %d at coords %d %d (%d %d)", max[0], lowcx, lowcz, highcx, highcz);
			int sx = MIN_SIZE + rnd.nextInt(Math.min(max[0], MAX_SIZE) - MIN_SIZE + 1);
			int sz = MIN_SIZE + rnd.nextInt(Math.min(max[0], MAX_SIZE) - MIN_SIZE + 1);
			CastlePositionData.Position p = new CastlePositionData.Position(lowcx + ((max[0] - sx) / 2), lowcz + ((max[0] - sz) / 2));
			p.setSize(sx, sz);
			return p;
		}

		return position;
	}

	/**
	 * Pregenerates the given position by (randomly) selecting tiles for it
	 *
	 * @param position
	 * @param world
	 * @param rnd
	 */
	private void preGeneratePosition(CastlePositionData.Position position, World world, Random rnd) {
		if (position.hasSize()) {
			Logger.d(TAG, "Pregenerating position %s", position);
			if (pregenerating) {
				Logger.e(TAG, "Already pregenerating. Did not expect that.");
				return;
			}
			pregenerating = true;
			int sx = position.getSizeX();
			int sz = position.getSizeZ();
			tiles = new String[sx][sz];
			houseDirs = new int[sx][sz][];

			//Add ground
			for (int x = 0; x < sx; x++) {
				for (int z = 0; z < sz; z++) {
					tiles[x][z] = "0,flatDirt";
				}
			}

			//Add path from entrance to the castle
			for (int z = sz - 1; z > sz / 2; z--) {
				addPathAndDir(sx / 2, z, 3);
				addPathAndDir(sx / 2 - 1, z, 1);
			}

			//Create walls
			for (int x = 0; x < sx; x++) {
				tiles[x][0] += ",2,wall";
			}
			for (int x = 0; x < sx; x++) {
				if (x == sx / 2) {
					tiles[x][sz - 1] += ",0,entrancer";
				} else if (x == sx / 2 - 1) {
					tiles[x][sz - 1] += ",0,entrancel";
				} else {
					tiles[x][sz - 1] += ",0,wall";
				}
			}
			for (int z = 0; z < sz; z++) {
				tiles[0][z] += ",1,wall";
			}
			for (int z = 0; z < sz; z++) {
				tiles[sx - 1][z] += ",3,wall";
			}

			/**
			 * Upper castle x (middle)
			 */
			int ucx = sx / 2;

			/**
			 * Upper castle z (middle)
			 */
			int ucz = sz / 2;
			//Place castle
			tiles[ucx][ucz] = "0,flatDirt,0,castlelr,2,path,1,path";
			tiles[ucx - 1][ucz] = "0,flatDirt,0,castlell,2,path,3,path";
			tiles[ucx - 1][ucz - 1] = "0,flatDirt,0,castleul,0,path,3,path";
			tiles[ucx][ucz - 1] = "0,flatDirt,0,castleur,0,path,1,path";

			//Place paths around the castle
			addPathAndDir(ucx, ucz - 2, 2);
			addPathAndDir(ucx - 1, ucz - 2, 2);
			addPathAndDir(ucx, ucz + 1, 0);
			addPathAndDir(ucx - 1, ucz + 1, 0);

			addPathAndDir(ucx - 2, ucz, 1);
			addPathAndDir(ucx - 2, ucz - 1, 1);
			addPathAndDir(ucx + 1, ucz, 3);
			addPathAndDir(ucx + 1, ucz - 1, 3);

			//Add corner paths (coordinate system x to the right, z to the bottom)

			//Upper left
			boolean ulcorner = false;
			if (rnd.nextBoolean()) {
				addPathAndDir(ucx - 2, ucz - 2, 2);
				addPathAndDir(ucx - 2, ucz - 1, 0);
				if (ucx - 3 >= 0) {
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx - 3, ucz - 2, 1);
						addPathAndDir(ucx - 2, ucz - 2, 3);
						ulcorner = true;
					}
				}
			}
			if (rnd.nextBoolean()) {
				addPathAndDir(ucx - 1, ucz - 2, 3);
				addPathAndDir(ucx - 2, ucz - 2, 1);

				if (ucz - 3 >= 0) {
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx - 2, ucz - 3, 2);
						addPathAndDir(ucx - 2, ucz - 2, 0);
						ulcorner = true;
					}
				}
			}
			if (ulcorner && ucz - 3 >= 0 && ucx - 3 >= 0) {
				if (rnd.nextBoolean()) {
					addPathAndDir(ucx - 3, ucz - 3, 1);
					addPathAndDir(ucx - 2, ucz - 3, 3);
				} else {
					addPathAndDir(ucx - 3, ucz - 3, 2);
					addPathAndDir(ucx - 3, ucz - 2, 0);
				}
			}

			boolean urcorner = false;
			if (rnd.nextBoolean()) {
				addPathAndDir(ucx + 1, ucz - 2, 3);
				addPathAndDir(ucx, ucz - 2, 1);
				if (ucz - 3 >= 0) {
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx + 1, ucz - 3, 2);
						addPathAndDir(ucx + 1, ucz - 2, 0);
						urcorner = true;
					}
				}
			}
			if (rnd.nextBoolean()) {
				addPathAndDir(ucx + 1, ucz - 2, 2);
				addPathAndDir(ucx + 1, ucz - 1, 0);
				if (ucx + 2 < sx) {
					addPathAndDir(ucx + 2, ucz - 2, 3);
					addPathAndDir(ucx + 1, ucz - 2, 1);
					urcorner = true;
				}
			}
			if (urcorner && ucx + 2 < sx && ucz - 3 >= 0) {
				if (rnd.nextBoolean()) {
					addPathAndDir(ucx + 1, ucz - 3, 1);
					addPathAndDir(ucx + 2, ucz - 3, 3);
				} else {
					addPathAndDir(ucx + 2, ucz - 2, 0);
					addPathAndDir(ucx + 2, ucz - 3, 2);
				}
			}

			boolean lrcorner = false;
			if (rnd.nextBoolean()) {
				addPathAndDir(ucx + 1, ucz, 2);
				addPathAndDir(ucx + 1, ucz + 1, 0);
				if (ucx + 2 < sx) {
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx + 2, ucz + 1, 3);
						addPathAndDir(ucx + 1, ucz + 1, 1);
						lrcorner = true;
					}
				}
			}
			if (rnd.nextBoolean()) {
				addPathAndDir(ucx + 1, ucz + 1, 3);
				addPathAndDir(ucx, ucz + 1, 1);
				if (ucz + 2 < sz) {
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx + 1, ucz + 2, 0);
						addPathAndDir(ucx + 1, ucz + 1, 2);
						lrcorner = true;
					}
				}
			}

			if (lrcorner && ucx + 2 < sx && ucz + 2 < sz) {
				if (rnd.nextBoolean()) {
					addPathAndDir(ucx + 2, ucz + 2, 0);
					addPathAndDir(ucx + 2, ucz + 1, 2);
				} else {
					addPathAndDir(ucx + 2, ucz + 2, 3);
					addPathAndDir(ucx + 1, ucz + 2, 1);
				}
			}

			boolean llcorner = false;

			if (rnd.nextBoolean()) {
				addPathAndDir(ucx - 1, ucz + 1, 3);
				addPathAndDir(ucx - 2, ucz + 1, 1);
				if (ucz + 2 < sz) {
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx + 1, ucz + 2, 0);
						addPathAndDir(ucx + 1, ucz + 1, 2);
						llcorner = true;
					}
				}

			}
			if (rnd.nextBoolean()) {
				addPathAndDir(ucx - 2, ucz + 1, 0);
				addPathAndDir(ucx - 2, ucz, 2);
				if (ucx - 3 >= 0) {
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx - 3, ucz + 1, 1);
						addPathAndDir(ucx - 2, ucz + 1, 3);
						llcorner = true;
					}
				}

			}
			if (llcorner && ucx - 3 >= 0 && ucz + 2 < sz) {
				if (rnd.nextBoolean()) {
					addPathAndDir(ucx - 3, ucz + 2, 0);
					addPathAndDir(ucx - 3, ucz + 1, 2);
				} else {
					addPathAndDir(ucx - 3, ucz + 2, 1);
					addPathAndDir(ucx - 2, ucz + 2, 3);
				}
			}

			//Middle right/left paths
			if (rnd.nextBoolean()) {
				addPathAndDir(ucx + 1, ucz, 0);
				addPathAndDir(ucx + 1, ucz - 1, 2);
				if (ucx + 2 < sx) {
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx + 2, ucz - 1, 3);
						addPathAndDir(ucx + 1, ucz - 1, 1);
					}
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx + 2, ucz, 3);
						addPathAndDir(ucx + 1, ucz, 1);
					}
				}
			}

			if (rnd.nextBoolean()) {
				addPathAndDir(ucx - 2, ucz, 0);
				addPathAndDir(ucx - 2, ucz - 1, 2);
				if (ucx - 3 >= 0) {
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx - 3, ucz - 1, 1);
						addPathAndDir(ucx - 2, ucz - 1, 3);
					}
					if (rnd.nextBoolean()) {
						addPathAndDir(ucx - 3, ucz, 1);
						addPathAndDir(ucx - 2, ucz, 3);
					}
				}
			}

			//Place houses
			boolean large = sx >= 6 && sz >= 6;
			for (int x = 0; x < sx; x++) {
				for (int z = 0; z < sz; z++) {
					int[] dirs = houseDirs[x][z];
					if (dirs != null) {
						if (large && x == 0 || x == 5 || z == 0 || z == 5) {
							tiles[x][z] += getRandomNonHouse(rnd, dirs, true);
						} else {
							tiles[x][z] += getRandomHouse(rnd, dirs, true);
						}

					}

				}
			}

			position.setMainCastle(new ChunkCoordIntPair(position.chunkXPos + sx / 2 - 1, position.chunkZPos + sz / 2 - 1),
					new ChunkCoordIntPair(position.chunkXPos + sx / 2, position.chunkZPos + sz / 2));

			position.setTiles(tiles);
			houseDirs = null;
			tiles = null;
			pregenerating = false;

		}
	}

	/**
	 * Adds a path tile to tiles as well as the dir to dirs
	 *
	 * @param x
	 * @param z
	 * @param dir
	 */
	private void addPathAndDir(int x, int z, int dir) {
		tiles[x][z] += ("," + dir + ",path");

		int[] d = houseDirs[x][z];
		if (d == null) {
			d = new int[] { dir };
			houseDirs[x][z] = d;
			return;
		} else {
			houseDirs[x][z] = ArrayUtils.add(d, dir);
		}
	}

	/**
	 * Returns a random house tile String with a random direction from the gives dirs array.
	 * Tiny chance that a 'NonHouse' is returned.
	 *
	 * @param rnd
	 * @param dirs
	 * @return
	 */
	private String getRandomHouse(Random rnd, int[] dirs, boolean allowNonHouse) {
		int dir = dirs[rnd.nextInt(dirs.length)];
		int type = rnd.nextInt(allowNonHouse ? 7 : 6);
		String s;
		switch (type) {
		case 0:
		case 1:
			s = "house1";
			break;
		case 2:
		case 3:
			s = "house2";
			break;
		case 4:
			s = "blacksmith";
			break;
		case 6:
			return getRandomNonHouse(rnd, dirs, false);
		default:
			return "";
		}
		return "," + dir + "," + s;
	}

	private String getRandomNonHouse(Random rnd, int[] dirs, boolean allowHouse) {
		int dir = dirs[rnd.nextInt(dirs.length)];
		int type = rnd.nextInt(allowHouse ? 6 : 5);
		String s;
		switch (type) {
		case 0:
			s = "grave";
			break;
		case 1:
			s = "stables";
			break;
		case 2:
			s = "pasture";
			break;
		case 3:
			s = "stables";
			break;
		case 5:
			return getRandomHouse(rnd, dirs, false);
		default:
			return "";
		}
		return "," + dir + "," + s;
	}

	private int getAverageHeight(Chunk chunk) {
		int[] map = chunk.heightMap;
		int sum = 0;
		for (int i = 0; i < map.length; i++) {
			sum += map[i];
		}
		return MathHelper.floor_float(sum / (float) map.length);
	}

	private void printMatrix(Object[][] objects) {
		for (int i = 0; i < objects.length; i++) {
			System.out.println();
			for (int j = 0; j < objects[i].length; j++) {
				System.out.print(String.valueOf(objects[i][j]));
			}
		}
	}

	/**
	 * Unused
	 *
	 * @param p_76484_1_
	 * @param p_76484_2_
	 * @param p_76484_3_
	 * @param p_76484_4_
	 * @param p_76484_5_
	 * @return
	 */
	@Override public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
		return false;
	}

}
