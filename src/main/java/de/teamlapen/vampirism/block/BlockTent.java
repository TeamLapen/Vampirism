package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Basic tent block. Mainly placeholder for the tent rendered for {@link BlockMainTent}
 */
public class BlockTent extends BasicBlock {


    private final int[][][] others = {{{1, 0}, {1, 1}, {0, 1}}, {{0, 1}, {-1, 1}, {-1, 0}}, {{0, -1}, {-1, -1}, {-1, 0}}, {{1, 0}, {1, -1}, {0, -1}}};
    public static final String name = "tent";
    public BlockTent() {
        super(Material.cloth, name);
        this.setCreativeTab(null);
    }


    @Override
    public int quantityDropped(Random p_149745_1_) {
        return 0;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        int dir = meta & 3;
        int rpos = (meta & 12) >> 2;
        int[][] sets = others[rpos];
        for (int i = 0; i < 3; i++) {
            int[] p = rotate(sets[i], dir);
            Block b = worldIn.getBlockState(pos.add(p[0],0,p[1])).getBlock();
           worldIn.setBlockToAir(pos.add(p[0],0,p[1]));
        }
        super.breakBlock(worldIn, pos, state);
    }


    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    

    private int[] rotate(int[] set, int dir) {
        if (dir == 0) {
            return new int[]{set[0], set[1]};
        } else if (dir == 1) {
            return new int[]{-set[1], set[0]};
        } else if (dir == 2) {
            return new int[]{-set[0], -set[1]};
        } else {
            return new int[]{set[1], -set[0]};
        }
    }
}
