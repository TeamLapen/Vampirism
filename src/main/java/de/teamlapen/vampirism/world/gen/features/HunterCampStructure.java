package de.teamlapen.vampirism.world.gen.features;

import com.mojang.datafixers.Dynamic;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.items.TentItem;
import de.teamlapen.vampirism.world.gen.biome.VampirismBiome;
import de.teamlapen.vampirism.world.gen.features.config.HunterTentConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.fluids.IFluidBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Random;
import java.util.function.Function;

/**
 * Generate hunter camps
 */
public class HunterCampStructure extends ScatteredStructure<NoFeatureConfig> {

    private static final Logger LOGGER = LogManager.getLogger(HunterCampStructure.class);
    private BlockState campfire_blockstate;


    public HunterCampStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> function) {
        super(function);
        campfire_blockstate = ModBlocks.fire_place.getDefaultState();
    }

    @Override
    public String getStructureName() {
        return "HunterCamp";
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public IStartFactory getStartFactory() {
        return HunterCampStructure.Start::new;
    }

    @Override
    protected int getSeedModifier() {
        return 0;
    }

    public static class Start extends StructureStart {
        public Start(Structure<?> p_i51341_1_, int chunkX, int chunkZ, Biome biomeIn, MutableBoundingBox boundsIn, int referenceIn, long seed) {
            super(p_i51341_1_, chunkX, chunkZ, biomeIn, boundsIn, referenceIn, seed);
        }

        @Override
        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {

        }
    }

    /**
     * @param worldIn
     * @param rand
     * @param position Should be (0/height/0) of the chunk
     * @return
     */
    @Override
    public boolean place(IWorld worldIn, ChunkGenerator generator, Random rand, BlockPos position, HunterTentConfig config) {
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
                placeTent(worldIn, rand, pos1, Direction.EAST);
                placeTent(worldIn, rand, pos2, Direction.WEST);
                placeTent(worldIn, rand, pos3, Direction.NORTH);
                placeTent(worldIn, rand, pos4, Direction.SOUTH);
                AdvancedHunterEntity hunter = new AdvancedHunterEntity(worldIn.getWorld());
                AxisAlignedBB box = new AxisAlignedBB(center.add(-7, 0, -10), center.add(7, 1, 7));
                UtilLib.spawnEntityInWorld(worldIn.getWorld(), box, hunter, 8, Collections.emptyList(), SpawnReason.CHUNK_GENERATION);
                hunter.setCampArea(box.grow(4, 5, 4));
                if (VampirismBiome.debug)
                    LOGGER.info("Generated advanced hunter camp at {}", center);
                return true;
            }
            return false;
        } else {
            BlockPos pos = position.add(rand.nextInt(16), 0, rand.nextInt(16));
            boolean flag = placeTent(worldIn, rand, findSolidPos(worldIn, pos), Direction.byHorizontalIndex(rand.nextInt(4)));
            if (flag && VampirismBiome.debug)
                LOGGER.info("Generated normal hunter camp at {}", pos);
            return flag;
        }
    }

    public void setCampfireBlockstate(BlockState state) {
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

    private boolean checkGroundAndPos(IWorld worldIn, BlockPos position, BlockState ground) {
        if (worldIn.getBlockState(position).getMaterial().isLiquid()) return false;
        BlockState b = worldIn.getBlockState(position.down());
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
            pos.move(Direction.DOWN);
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

    private boolean placeTent(IWorld worldIn, Random rand, BlockPos position, Direction facing) {

        BlockState ground = worldIn.getBlockState(position.down());
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
                TentItem.placeAt(worldIn, position, facing, true, true);
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
