package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
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
        this.setBlockTextureName(REFERENCE.MODID + ":invisible");
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World p_149731_1_, int p_149731_2_, int p_149731_3_, int p_149731_4_, Vec3 p_149731_5_, Vec3 p_149731_6_) {
        return super.collisionRayTrace(p_149731_1_, p_149731_2_, p_149731_3_, p_149731_4_, p_149731_5_, p_149731_6_);
    }


    @Override
    public int quantityDropped(Random p_149745_1_) {
        return 0;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int meta) {

        int dir = meta & 3;
        int pos = (meta & 12) >> 2;
        int[][] sets = others[pos];
        for (int i = 0; i < 3; i++) {
            int[] p = rotate(sets[i], dir);
            Block b = world.getBlock(x + p[0], y, z + p[1]);
            world.setBlockToAir(x + p[0], y, z + p[1]);
        }
        super.breakBlock(world, x, y, z, p_149749_5_, meta);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_) {
        super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_) {
        return super.getCollisionBoundingBoxFromPool(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_) {
        return super.getSelectedBoundingBoxFromPool(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_) {
        super.setBlockBoundsBasedOnState(p_149719_1_, p_149719_2_, p_149719_3_, p_149719_4_);
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
