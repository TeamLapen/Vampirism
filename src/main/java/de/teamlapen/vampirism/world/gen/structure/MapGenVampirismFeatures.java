package de.teamlapen.vampirism.world.gen.structure;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
 * TODO consider adding spawns
 */
public class MapGenVampirismFeatures extends MapGenStructure {
    private static final List<Biome> BIOMELIST = Arrays.asList(Biomes.MESA, Biomes.PLAINS, Biomes.TAIGA, Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.COLD_TAIGA);
    /**
     * the minimum distance between scattered features
     */
    private final int minDistanceBetweenScatteredFeatures;
    /**
     * contains possible spawns for scattered features
     */
    private final int SEED_MOD = 96857617;
    /**
     * the maximum distance between scattered features
     */
    private int maxDistanceBetweenScatteredFeatures;

    public MapGenVampirismFeatures() {
        this.maxDistanceBetweenScatteredFeatures = 9;
        this.minDistanceBetweenScatteredFeatures = 2;
    }

    @Override
    public String getStructureName() {
        return "vampirism_feature";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0) {
            chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        if (chunkZ < 0) {
            chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int k = chunkX / this.maxDistanceBetweenScatteredFeatures;
        int l = chunkZ / this.maxDistanceBetweenScatteredFeatures;
        Random random = this.world.setRandomSeed(k, l, SEED_MOD);
        k = k * this.maxDistanceBetweenScatteredFeatures;
        l = l * this.maxDistanceBetweenScatteredFeatures;
        k = k + random.nextInt(this.maxDistanceBetweenScatteredFeatures - 8);
        l = l + random.nextInt(this.maxDistanceBetweenScatteredFeatures - 8);

        if (i == k && j == l) {
            Biome biome = this.world.getBiomeProvider().getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8));

            if (biome == null) {
                return false;
            }

            for (Biome biome1 : BIOMELIST) {
                if (biome == biome1) {
                    return true;
                }
            }
        }

        return false;
    }

    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, this.maxDistanceBetweenScatteredFeatures, 8, SEED_MOD, false, 100, findUnexplored);
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new MapGenVampirismFeatures.Start(this.world, this.rand, chunkX, chunkZ);
    }


    public static class Start extends StructureStart {
        public Start() {
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ) {
            this(worldIn, random, chunkX, chunkZ, worldIn.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
        }

        public Start(World worldIn, Random random, int chunkX, int chunkZ, Biome biomeIn) {
            super(chunkX, chunkZ);
            int i = 5;
            int j = 5;
            ChunkPrimer chunkprimer = new ChunkPrimer();
            ((ChunkGeneratorOverworld) ((ChunkProviderServer) worldIn.getChunkProvider()).chunkGenerator).setBlocksInChunk(chunkX, chunkZ, chunkprimer);
            int k = chunkprimer.findGroundBlockIdx(7, 7);
            int l = chunkprimer.findGroundBlockIdx(7, 7 + j);
            int i1 = chunkprimer.findGroundBlockIdx(7 + i, 7);
            int j1 = chunkprimer.findGroundBlockIdx(7 + i, 7 + j);
            int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));

            VampirismMod.log.bigWarning("TEST", "START");
            VampirismComponentFeaturePiece piece = new VampirismComponentFeaturePiece.VampireHouse1();
            piece.setup(new BlockPos(chunkX << 2, k1, chunkZ << 2));


            this.updateBoundingBox();
        }
    }

}
