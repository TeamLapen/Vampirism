package de.teamlapen.vampirism.biome.features;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.biome.VampirismBiome;
import de.teamlapen.vampirism.biome.config.HunterTentConfig;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.hunter.EntityAdvancedHunter;
import de.teamlapen.vampirism.items.ItemTent;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.fluids.IFluidBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Random;

/**
 * Generate hunter camps
 */
public class FeatureHunterCamp extends Feature<HunterTentConfig> {

    private static final Logger LOGGER = LogManager.getLogger(FeatureHunterCamp.class);
    private IBlockState campfire_blockstate;


    public FeatureHunterCamp() {
        super();
        campfire_blockstate = ModBlocks.fire_place.getDefaultState();
    }

    /**
     * @param worldIn
     * @param rand
     * @param position Should be (0/height/0) of the chunk
     * @return
     */
    @Override
    public boolean place(IWorld worldIn, IChunkGenerator generator, Random rand, BlockPos position, HunterTentConfig config) {
        if (worldIn.getWorld().getBiome(position).getScale() < 0.3 && rand.nextInt(6) == 0) {
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
                EntityAdvancedHunter hunter = new EntityAdvancedHunter(worldIn.getWorld());
                AxisAlignedBB box = new AxisAlignedBB(center.add(-7, 0, -10), center.add(7, 1, 7));
                UtilLib.spawnEntityInWorld(worldIn.getWorld(), box, hunter, 8, Collections.emptyList());
                hunter.setCampArea(box.grow(4, 5, 4));
                if (VampirismBiome.debug)
                    LOGGER.info("Generated advanced hunter camp at {}", center);
                return true;
            }
            return false;
        } else {
            BlockPos pos = position.add(rand.nextInt(16), 0, rand.nextInt(16));
            boolean flag = placeTent(worldIn, rand, findSolidPos(worldIn, pos), EnumFacing.byHorizontalIndex(rand.nextInt(4)));
            if (flag && VampirismBiome.debug)
                LOGGER.info("Generated normal hunter camp at {}", pos);
            return flag;
        }
    }

    public void setCampfireBlockstate(IBlockState state) {
        this.campfire_blockstate = state;
    }

    boolean canCampSpawnAt(World world, Biome biome, int chunkX, int chunkZ) { //TODO 1.13 why isn't this used anymore
        int distance = Math.max(1, Balance.general.HUNTER_CAMP_DENSITY);
        //Check Biome
        if (ModBiomes.vampireForest.getRegistryName().equals(biome.getRegistryName())) {
            return false;
        }

        //Check temperature
        BlockPos pos = new BlockPos((chunkX << 4), 0, (chunkZ << 4));
        pos = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos);
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
        SharedSeedRandom random = new SharedSeedRandom();
        random.setFeatureSeed(world.getSeed(), chunkX, chunkZ);
        k = k * distance;
        l = l * distance;
        k = k + random.nextInt(distance - 2);
        l = l + random.nextInt(distance - 2);

        //return world.getVillageCollection().getNearestVillage(world.getHeight(new BlockPos(i << 4, 0, j << 4)), 25) == null; //Useless as village collection is not updated on world gen
        return i == k && j == l;

    }

    private boolean checkGroundAndPos(IWorld worldIn, BlockPos position, IBlockState ground) {
        if (worldIn.getBlockState(position).getMaterial().isLiquid()) return false;
        IBlockState b = worldIn.getBlockState(position.down());
        if (b.getMaterial().isLiquid() || b.getBlock() instanceof IFluidBlock) return false;
        if (ground != null && b.getMaterial().isReplaceable()) {
            worldIn.setBlockState(position.down(), ground, 2);
            return true;
        }
        return worldIn.getWorld().isTopSolid(position.down());
    }

    private BlockPos findSolidPos(IWorld world, BlockPos position) {
        Material material;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position).up(30));
        while (((material = world.getBlockState(pos).getMaterial()) == Material.LEAVES || material == Material.PLANTS || world.isAirBlock(pos)) && pos.getY() > 50) {
            pos.move(EnumFacing.DOWN);
        }
        return pos.up();
    }


    private boolean placeFire(IWorld worldIn, BlockPos position) {
        if (checkGroundAndPos(worldIn, position, null)) {
            setBlockState(worldIn, position, campfire_blockstate);
            return true;
        }
        return false;
    }

    private boolean placeTent(IWorld worldIn, Random rand, BlockPos position, EnumFacing facing) {

        IBlockState ground = worldIn.getBlockState(position.down());
        if (ground.isTopSolid()) {

            BlockPos tl = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position.offset(facing).offset(facing.rotateYCCW()));
            BlockPos bl = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position.offset(facing.rotateYCCW()));
            BlockPos tr = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position.offset(facing));
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
                worldIn.removeBlock(entrance1);
                worldIn.removeBlock(entrance2);
                if (rand.nextInt(3) == 0) {
                    this.setBlockState(worldIn, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, entrance1.offset(facing.getOpposite())), Blocks.CRAFTING_TABLE.getDefaultState());
                }
                if (rand.nextInt(3) == 0) {
                    this.setBlockState(worldIn, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, entrance2.offset(facing.getOpposite())), Blocks.TORCH.getDefaultState());
                }

                return true;

            }

        }

        return false;
    }
}
