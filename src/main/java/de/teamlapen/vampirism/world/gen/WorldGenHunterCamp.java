package de.teamlapen.vampirism.world.gen;

import de.teamlapen.vampirism.items.ItemTent;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * Generate hunter camps
 * TODO generate larger camps for hunter v2
 */
public class WorldGenHunterCamp extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        IBlockState ground = worldIn.getBlockState(position.down());
        if (ground.getBlock().isSideSolid(ground, worldIn, position.down(), EnumFacing.UP)) {
            EnumFacing facing = EnumFacing.getHorizontal(rand.nextInt(EnumFacing.HORIZONTALS.length));

            BlockPos tl = worldIn.getHeight(position.offset(facing).offset(facing.rotateYCCW()));
            BlockPos bl = worldIn.getHeight(position.offset(facing.rotateYCCW()));
            BlockPos tr = worldIn.getHeight(position.offset(facing));
            if (Math.abs(tl.getY() - position.getY()) < 2 && Math.abs(bl.getY() - position.getY()) < 2 && Math.abs(tr.getY() - position.getY()) < 2) {
                tl = new BlockPos(tl.getX(), position.getY(), tl.getZ());
                bl = new BlockPos(bl.getX(), position.getY(), bl.getZ());
                tr = new BlockPos(tr.getX(), position.getY(), tr.getZ());
                if (!checkGround(worldIn, tl.down(), ground)) return false;
                if (!checkGround(worldIn, bl.down(), ground)) return false;
                if (!checkGround(worldIn, tr.down(), ground)) return false;
                ItemTent.placeAt(worldIn, position, facing, true, true);
                BlockPos entrance1 = position.offset(facing.getOpposite());
                BlockPos entrance2 = position.offset(facing.getOpposite()).offset(facing.rotateYCCW());
                worldIn.setBlockToAir(entrance1);
                worldIn.setBlockToAir(entrance2);
                if (rand.nextInt(3) == 0) {
                    worldIn.setBlockState(worldIn.getHeight(entrance1.offset(facing.getOpposite())), Blocks.CRAFTING_TABLE.getDefaultState());
                }
                if (rand.nextInt(3) == 0) {
                    worldIn.setBlockState(worldIn.getHeight(entrance2.offset(facing.getOpposite())), Blocks.TORCH.getDefaultState());
                }


            }
            return true;
        }

        return false;
    }

    public boolean isValidTemperature(float t) {
        return t < 1.5F && t > 0.1F;
    }

    private boolean checkGround(World worldIn, BlockPos nw, IBlockState ground) {
        Material m = worldIn.getBlockState(nw).getMaterial();
        if (m.isLiquid()) return false;
        if (m.isReplaceable()) {
            worldIn.setBlockState(nw, ground);
            return true;
        }
        return worldIn.isSideSolid(nw, EnumFacing.UP, false);
    }
}
