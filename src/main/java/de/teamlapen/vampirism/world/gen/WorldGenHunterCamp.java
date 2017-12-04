package de.teamlapen.vampirism.world.gen;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.hunter.EntityAdvancedHunter;
import de.teamlapen.vampirism.items.ItemTent;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.Random;

/**
 * Generate hunter camps
 */
public class WorldGenHunterCamp extends WorldGenerator {


    public boolean canCampSpawnAt(World world, Biome biome, int chunkX, int chunkZ) {
        int distance = Balance.general.HUNTER_CAMP_DENSITY;
        //Check Biome
        if (ModBiomes.vampireForest.getRegistryName().equals(biome.getRegistryName())) {
            return false;
        }

        //Check temperature
        BlockPos pos = new BlockPos((chunkX << 4), 0, (chunkZ << 4));
        pos = world.getHeight(pos);
        float t = biome.getTemperature(pos);
        if (t > 1.5F || t < 0.1F) return false;

        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0) {
            chunkX -= distance - 1;
        }

        if (chunkZ < 0) {
            chunkZ -= distance - 1;
        }

        int k = chunkX / distance;
        int l = chunkZ / distance;
        Random random = world.setRandomSeed(k, l, 10387312);
        k = k * distance;
        l = l * distance;
        k = k + random.nextInt(distance - 2);
        l = l + random.nextInt(distance - 2);

        if (i == k && j == l) {
            return world.getVillageCollection().getNearestVillage(world.getHeight(new BlockPos(i << 4, 0, j << 4)), 20) == null;

        }

        return false;
    }

    /**
     * @param worldIn
     * @param rand
     * @param position Should be (0/height/0) of the chunk
     * @return
     */
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (worldIn.getBiomeForCoordsBody(position).getHeightVariation() < 0.3 && rand.nextInt(6) == 0) {
            int r = rand.nextInt(2);
            int r1 = rand.nextInt(2);
            int r2 = rand.nextInt(2);
            int r3 = rand.nextInt(2);
            BlockPos center = findSolidPos(worldIn, position.add(8, 0, 8));
            BlockPos pos1 = findSolidPos(worldIn, center.add(4 + r, 5, r1 - 1));
            BlockPos pos2 = findSolidPos(worldIn, center.add(-4 - r1, 5, r2 - 1));
            BlockPos pos3 = findSolidPos(worldIn, center.add(r2 - 1, 5, -4 - r3));
            BlockPos pos4 = findSolidPos(worldIn, center.add(r3 - 1, 5, 4 + r));
            int dif = Math.abs(center.getY() - pos1.getY()) + Math.abs(center.getY() - pos2.getY()) + Math.abs(center.getY() - pos3.getY()) + Math.abs(center.getY() - pos4.getY());


            boolean place = dif < 8 && placeFire(worldIn, findSolidPos(worldIn, center));
            if (place) {
                placeTent(worldIn, rand, pos1, EnumFacing.EAST);
                placeTent(worldIn, rand, pos2, EnumFacing.WEST);
                placeTent(worldIn, rand, pos3, EnumFacing.NORTH);
                placeTent(worldIn, rand, pos4, EnumFacing.SOUTH);
                EntityAdvancedHunter hunter = new EntityAdvancedHunter(worldIn);
                AxisAlignedBB box = new AxisAlignedBB(center.add(-7, 0, -10), center.add(7, 1, 7));
                UtilLib.spawnEntityInWorld(worldIn, box, hunter, 8);
                hunter.setCampArea(box.grow(4, 5, 4));
                if (VampirismWorldGen.debug)
                    VampirismMod.log.i("HunterCamp", "Generated advanced hunter camp at %s", center);
                return true;
            }
            return false;
        } else {
            BlockPos pos = position.add(rand.nextInt(16), 0, rand.nextInt(16));
            boolean flag = placeTent(worldIn, rand, findSolidPos(worldIn, pos), EnumFacing.getHorizontal(rand.nextInt(EnumFacing.HORIZONTALS.length)));
            if (flag && VampirismWorldGen.debug)
                VampirismMod.log.i("HunterCamp", "Generated normal hunter camp at %s", pos);
            return flag;
        }
    }

    private boolean checkGroundAndPos(World worldIn, BlockPos position, IBlockState ground) {
        if (worldIn.getBlockState(position).getMaterial().isLiquid()) return false;
        IBlockState b = worldIn.getBlockState(position.down());
        if (b.getMaterial().isLiquid() || b.getBlock() instanceof IFluidBlock) return false;
        if (ground != null && b.getMaterial().isReplaceable()) {
            worldIn.setBlockState(position.down(), ground);
            return true;
        }
        return worldIn.isSideSolid(position.down(), EnumFacing.UP, false);
    }

    private BlockPos findSolidPos(World world, BlockPos position) {
        Material material;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(world.getHeight(position).up(30));
        while (((material = world.getBlockState(pos).getMaterial()) == Material.LEAVES || material == Material.PLANTS || world.isAirBlock(pos)) && pos.getY() > 50) {
            pos.move(EnumFacing.DOWN);
        }
        return pos.up();
    }


    private boolean placeFire(World worldIn, BlockPos position) {
        if (checkGroundAndPos(worldIn, position, null)) {
            setBlockAndNotifyAdequately(worldIn, position, ModBlocks.fire_place.getDefaultState());
            return true;
        }
        return false;
    }

    private boolean placeTent(World worldIn, Random rand, BlockPos position, EnumFacing facing) {

        IBlockState ground = worldIn.getBlockState(position.down());
        if (ground.getBlock().isSideSolid(ground, worldIn, position.down(), EnumFacing.UP)) {


            BlockPos tl = worldIn.getHeight(position.offset(facing).offset(facing.rotateYCCW()));
            BlockPos bl = worldIn.getHeight(position.offset(facing.rotateYCCW()));
            BlockPos tr = worldIn.getHeight(position.offset(facing));
            if (Math.abs(tl.getY() - position.getY()) < 2 && Math.abs(bl.getY() - position.getY()) < 2 && Math.abs(tr.getY() - position.getY()) < 2) {
                tl = new BlockPos(tl.getX(), position.getY(), tl.getZ());
                bl = new BlockPos(bl.getX(), position.getY(), bl.getZ());
                tr = new BlockPos(tr.getX(), position.getY(), tr.getZ());
                if (!checkGroundAndPos(worldIn, tl, ground)) return false;
                if (!checkGroundAndPos(worldIn, bl, ground)) return false;
                if (!checkGroundAndPos(worldIn, tr, ground)) return false;
                ItemTent.placeAt(worldIn, position, facing, true, true);
                BlockPos entrance1 = position.offset(facing.getOpposite());
                BlockPos entrance2 = position.offset(facing.getOpposite()).offset(facing.rotateYCCW());
                worldIn.setBlockToAir(entrance1);
                worldIn.setBlockToAir(entrance2);
                if (rand.nextInt(3) == 0) {
                    this.setBlockAndNotifyAdequately(worldIn, worldIn.getHeight(entrance1.offset(facing.getOpposite())), Blocks.CRAFTING_TABLE.getDefaultState());
                }
                if (rand.nextInt(3) == 0) {
                    this.setBlockAndNotifyAdequately(worldIn, worldIn.getHeight(entrance2.offset(facing.getOpposite())), Blocks.TORCH.getDefaultState());
                }

                return true;

            }

        }

        return false;
    }
}
