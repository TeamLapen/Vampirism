package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBiomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomePlains;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Handles Vampirism's world gen
 */
public class VampirismWorldGen implements IWorldGenerator {
    private final WorldGenHunterCamp hunterCamp;

    public VampirismWorldGen() {
        this.hunterCamp = new WorldGenHunterCamp();
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
        boolean generatedStructure = false;
        boolean mapFeatures = world.getWorldInfo().isMapFeaturesEnabled();
        Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8));
        if (!generatedStructure && mapFeatures && !ModBiomes.vampireForest.getRegistryName().equals(biome.getRegistryName())) {
            int chance = random.nextInt(1000);
            int trees = biome.theBiomeDecorator.treesPerChunk;
            float bh = biome.getBaseHeight() + biome.getHeightVariation();
            float prop = 1;
            prop += Math.min(trees, 8);
            prop += bh * 3;

            if (biome instanceof BiomePlains) prop *= 0.8F;

            if (world.getWorldType().equals(WorldType.FLAT)) {
                prop = 0.2F;
            }
            if (chance < Balance.general.HUNTER_CAMP_SPAWN_CHANCE * prop) {
                BlockPos pos = new BlockPos((chunkX << 4), 0, (chunkZ << 4));
                pos = world.getHeight(pos);
                float temp = biome.getFloatTemperature(pos);
                if (hunterCamp.isValidTemperature(temp)) {
                    generatedStructure = hunterCamp.generate(world, random, pos.up());
                }
            }
        }


    }
}
