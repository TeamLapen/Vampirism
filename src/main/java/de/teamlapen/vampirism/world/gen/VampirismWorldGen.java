package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Handles Vampirism's world gen
 */
public class VampirismWorldGen implements IWorldGenerator {
    public static boolean debug = false;
    private final WorldGenHunterCamp hunterCamp;
    private final WorldGenVampireDungeon vampireDungeon;

    public VampirismWorldGen() {
        this.hunterCamp = new WorldGenHunterCamp();
        this.vampireDungeon = new WorldGenVampireDungeon();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        switch (world.provider.getDimension()) {
            case -1:
                generateNether(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                break;
            case 0:
                generateOverworld(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                break;
            case 1:
                generateEnd(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                break;
            default:
                break;
        }
    }


    public void generateEnd(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

    }

    public void generateNether(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

    }

    public void generateOverworld(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        boolean mapFeatures = world.getWorldInfo().isMapFeaturesEnabled();
        if (mapFeatures) {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8));
            if (hunterCamp.canCampSpawnAt(world, biome, chunkX, chunkZ)) {
                BlockPos pos = new BlockPos(chunkX << 4, 1, chunkZ << 4);

                int tries = 5;
                int max = random.nextInt(3) + 1;
                tries += Math.min(Math.max(biome.theBiomeDecorator.treesPerChunk, 0), 5);
                tries += 10 * (biome.getHeightVariation());
                if (Biomes.ROOFED_FOREST.equals(biome)) tries += 4;
                if (debug) VampirismMod.log.i("WorldGen", "Trying to generate camp at %s with %d tries", pos, tries);
                for (int j = 0; j < max; j++) {
                    for (int i = 0; i < tries; i++) {
                        if (hunterCamp.generate(world, random, pos)) {
                            break;
                        }
                    }
                }


            }
            for (int j2 = 0; j2 < 10; ++j2) {
                BlockPos pos = new BlockPos((chunkX << 4), 0, (chunkZ << 4));
                int i3 = random.nextInt(16) + 8;
                int l3 = random.nextInt(256);
                int l1 = random.nextInt(16) + 8;
                (vampireDungeon).generate(world, random, pos.add(i3, l3, l1));
            }


        }


    }
}
