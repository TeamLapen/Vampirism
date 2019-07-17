/*package de.teamlapen.vampirism.world.old.gen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

*//**
 * Handles Vampirism's world gen
 *//*
public class VampirismWorldGenOLD implements IWorldGenerator { //TODO DELETE CLASS (keep it for reference (how to generate?))
    public VampirismWorldGenOLD() {
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, ChunkGenerator chunkGenerator, AbstractChunkProvider chunkProvider) {

    }
    public static boolean debug = false;
    private static VampirismWorldGenOLD instance;

    public static VampirismWorldGenOLD getInstance() {
        if (instance == null) {
            instance = new VampirismWorldGenOLD();
        }
        return instance;
    }

    public final HunterCampStructureOLD hunterCamp;
    public final VampireDungeonFeatureOLD vampireDungeon;

    private VampirismWorldGenOLD() {
        this.hunterCamp = new HunterCampStructureOLD();
        this.vampireDungeon = new VampireDungeonFeatureOLD();
        ForgeRegistries.BIOMES.
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!Configs.disable_all_worldgen) {
            Dimension dim = world.dimension;
            if (dim.getType() == DimensionType.OVERWORLD || dim.getType() != DimensionType.THE_END && dim.getType() != DimensionType.NETHER && (contains(Configs.worldGenDimensions, dim) || VampirismAPI.isWorldGenEnabledFor(dim))) {
                generateOverworld(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
            }
        }
    }

    public void generateOverworld(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        boolean mapFeatures = world.getWorldInfo().isMapFeaturesEnabled();
        if (mapFeatures) {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8));
            if (!Configs.disable_hunter_camps && hunterCamp.canCampSpawnAt(world, biome, chunkX, chunkZ)) {
                BlockPos pos = new BlockPos(chunkX << 4, 1, chunkZ << 4);

                int tries = 5;
                int max = random.nextInt(3) + 1;
                tries += Math.min(Math.max(biome.decorator.treesPerChunk, 0), 5);
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

    private boolean contains(Dimension[] array, Dimension value) {
        for (Dimension i : array) {
            if (i == value) return true;
        }
        return false;
    }
}*/
