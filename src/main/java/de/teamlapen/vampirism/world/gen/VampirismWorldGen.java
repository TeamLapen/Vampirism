package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBiomes;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenPlains;
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
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        switch (world.provider.getDimensionId()) {
            case -1:
                generateNether(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                break;
            case 0:
                generateOverworld(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                break;
            case 1:
                generateEnd(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
                break;
        }
    }

    public void generateEnd(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

    }

    public void generateNether(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

    }

    public void generateOverworld(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        boolean generatedStructure = false;
        boolean mapFeatures = world.getWorldInfo().isMapFeaturesEnabled();
        BiomeGenBase biome = world.getBiomeGenForCoords(new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8));

        if (!generatedStructure && mapFeatures && biome.biomeID != ModBiomes.vampireForest.biomeID) {
            int chance = random.nextInt(1000);
            int trees = biome.theBiomeDecorator.treesPerChunk;
            float bh = biome.maxHeight;
            float prop = 1;
            if (trees > 2 && trees < 11) {
                prop += trees;
            }
            prop += bh * 5;

            if (biome instanceof BiomeGenPlains) prop *= 0.7F;

            if (world.getWorldType().equals(WorldType.FLAT)) {
                prop = 0.2F;
            }
            if (chance < Balance.general.HUNTER_CAMP_SPAWN_CHANCE * prop) {
                BlockPos pos = new BlockPos((chunkX << 4) + random.nextInt(16), 0, (chunkZ << 4) + random.nextInt(16));
                pos = world.getHeight(pos);
                Material material;
                while (((material = world.getBlockState(pos).getBlock().getMaterial()) == Material.leaves || material == Material.plants || world.isAirBlock(pos)) && pos.getY() > 50) {
                    pos = pos.down();
                }
                float temp = biome.getFloatTemperature(pos);
                if (hunterCamp.isValidTemperature(temp)) {
                    generatedStructure = hunterCamp.generate(world, random, pos.up());
                }
            }
        }


    }
}
