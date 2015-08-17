package de.teamlapen.vampirism.generation.structures;

import de.teamlapen.vampirism.item.ItemTent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * Generates hunter camps
 */
public class GenerateHunterCamp extends WorldGenerator {
    private final int[] rotX = {0, 1, 1, 0};
    private final int[] rotZ = {0, 0, 1, 1};
    private final int[][][] entrance = {{{0, -1}, {1, -1}}, {{1, 0}, {1, 1}}, {{0, 1}, {-1, 1}}, {{-1, 0}, {-1, -1}}};

    @Override
    public boolean generate(World world, Random rand, int posX, int h, int posZ) {
        int h1 = world.getHeightValue(posX + 1, posZ + 1);
        int h2 = world.getHeightValue(posX, posZ + 1);
        int h3 = world.getHeightValue(posX + 1, posZ);
        Block b = world.getBlock(posX, h - 1, posZ);
        if (!b.isNormalCube()) return false;
        if (Math.abs(h - h1) < 2 && Math.abs(h - h2) < 2 && Math.abs(h - h3) < 2) {
            if (!checkGround(world, posX, h - 1, posZ)) return false;
            if (!checkGround(world, posX + 1, h - 1, posZ)) return false;
            if (!checkGround(world, posX, h - 1, posZ + 1)) return false;
            if (!checkGround(world, posX + 1, h - 1, posZ + 1)) return false;
            int dir = rand.nextInt(4);
            posX += +rotX[dir];
            posZ += rotZ[dir];
            ItemTent.placeAt(world, posX, h, posZ, dir, true);
            for (int[] pos : entrance[dir]) {
                world.setBlockToAir(posX + pos[0], h, posZ + pos[1]);
                world.setBlockToAir(posX + pos[0], h + 1, posZ + pos[1]);
            }
            if (rand.nextInt(3) == 0) {
                int r = rand.nextInt(2);
                int x = posX + entrance[dir][r][0] * 2;
                int z = posZ + entrance[dir][r][1] * 2;
                int y = world.getHeightValue(x, z);
                world.setBlock(x, y, z, Blocks.crafting_table);
            }
            return true;
        }

        return false;
    }

    private boolean checkGround(World w, int x, int y, int z) {
        Block b = w.getBlock(x, y, z);
        Material m = b.getMaterial();
        if (m.isLiquid()) {
            return false;
        }
        if (m == Material.air || m == Material.vine) {
            w.setBlock(x, y, z, w.getBlock(x, y - 1, z));
            return true;
        }
        return b.isNormalCube();
    }

    public boolean isValidTemperature(float t) {
        return t < 1.5F && t > 0.1F;
    }
}
