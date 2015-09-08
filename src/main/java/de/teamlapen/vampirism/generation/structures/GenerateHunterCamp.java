package de.teamlapen.vampirism.generation.structures;

import de.teamlapen.vampirism.item.ItemTent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
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
    public boolean generate(World world, Random rand, BlockPos blockPos) {
        int h=blockPos.getY();
        int h1 = world.getHorizon(blockPos.add(1,0,1)).getY();
        int h2 = world.getHorizon(blockPos.add(0,0,1)).getY();
        int h3 = world.getHorizon(blockPos.add(1, 0, 0)).getY();
        Block b = world.getBlockState(blockPos.down()).getBlock();
        if (!b.isNormalCube()) return false;
        if (Math.abs( - h1) < 2 && Math.abs(h - h2) < 2 && Math.abs(h - h3) < 2) {
            if (!checkGround(world, blockPos.add(0, -1, 0))) return false;
            if (!checkGround(world, blockPos.add(1,-1,0))) return false;
            if (!checkGround(world, blockPos.add(0, -1, 1))) return false;
            if (!checkGround(world, blockPos.add(1,-1,1))) return false;
            int dir = rand.nextInt(4);
            blockPos.add(rotX[dir], 0, rotZ[dir]);

            ItemTent.placeAt(world, blockPos, dir, true,true);
            for (int[] pos : entrance[dir]) {
                world.setBlockToAir(blockPos.add(pos[0],0,pos[1]));
                world.setBlockToAir(blockPos.add(pos[0],1,pos[1]));

            }
            if (rand.nextInt(3) == 0) {
                int r = rand.nextInt(2);
                BlockPos craftPos=world.getHorizon(blockPos.add(entrance[dir][r][0] * 2, 0, entrance[dir][r][1] * 2));

                world.setBlockState(craftPos, Blocks.crafting_table.getDefaultState());
            }
            if (rand.nextInt(3) == 0) {
                int r = rand.nextInt(2);
                BlockPos craftPos=world.getHorizon(blockPos.add(entrance[dir][r][0] * 2, 0, entrance[dir][r][1] * 2));

                world.setBlockState(craftPos, Blocks.torch.getDefaultState());
            }
            return true;
        }

        return false;
    }


    private boolean checkGround(World w, BlockPos pos) {
        Block b = w.getBlockState(pos).getBlock();
        Material m = b.getMaterial();
        if (m.isLiquid()) {
            return false;
        }
        if (m == Material.air || m == Material.vine) {
            w.setBlockState(pos, w.getBlockState(pos.down()));
            return true;
        }
        return b.isNormalCube();
    }

    public boolean isValidTemperature(float t) {
        return t < 1.5F && t > 0.1F;
    }
}
