package de.teamlapen.vampirism.world.gen.structures.huntercamp;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.tileentity.TentTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
public abstract class HunterCampPieces extends StructurePiece {
    protected final int x, z;
    protected int y;
    protected final Block baseBlock;

    public static void init(int chunkX, int chunkZ, Biome biomeIn, Random rand, List<StructurePiece> componentsIn) {
        Fireplace hunterCamp = new Fireplace(rand, chunkX * 16 + rand.nextInt(16), 63, chunkZ * 16 + rand.nextInt(16), biomeIn.getSurfaceBuilderConfig().getTop().getBlock());
        componentsIn.add(hunterCamp);
        hunterCamp.buildComponent(hunterCamp, componentsIn, rand);
    }


    @Override
    public boolean func_230383_a_/*addComponentParts*/(ISeedReader worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos blockPos) {
        this.y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
        this.setBoundingBox();

        //fail conditions
        return testPreconditions(worldIn, structureManager);
    }

    public static class Fireplace extends HunterCampPieces {
        boolean specialComponentAdd = false;
        private boolean advanced;

        public Fireplace(Random random, int x, int y, int z, Block baseBlock) {
            super(ModFeatures.hunter_camp_fireplace, 0, x, y, z, baseBlock);
            this.setCoordBaseMode(Direction.Plane.HORIZONTAL.random(random));
        }

        public Fireplace(@SuppressWarnings("unused") TemplateManager templateManager, CompoundNBT nbt) {
            super(ModFeatures.hunter_camp_fireplace, nbt);
            advanced = nbt.getBoolean("advanced");
            specialComponentAdd = nbt.getBoolean("specialComponentAdd");
        }

        @Override
        public boolean func_230383_a_/*addComponentParts*/(ISeedReader worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos blockPos) {
            //preconditions
            if (!super.func_230383_a_/*addComponentParts*/(worldIn, structureManager, chunkGenerator, random, structureBoundingBoxIn, chunkPos, blockPos)) {
                return false;
            }

            //generation
            this.setBlockState(worldIn, VampirismConfig.COMMON.useVanillaCampfire.get() ? Blocks.CAMPFIRE.getDefaultState() : ModBlocks.fire_place.getDefaultState(), 1, 0, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);

            return true;
        }

        @Override
        public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
            //adds 1-4 tent or crafting table elements to the structure (max 1 per direction && max 1 crafting table)
            @Nonnull List<Direction> directions = Lists.newArrayList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
            if (rand.nextInt(4) == 0) {
                //advanced
                listIn.add(getTentComponent(rand, directions, advanced));
                listIn.add(getTentComponent(rand, directions, advanced));
                int i = rand.nextInt(4);
                if (i < 2)
                    listIn.add(getComponent(rand, directions, true));
                if (i < 1)
                    listIn.add(getComponent(rand, directions, true));
                this.advanced = true;
            } else {
                //normal
                listIn.add(getTentComponent(rand, directions, false));
                if (rand.nextInt(2) == 0)
                    listIn.add(getComponent(rand, directions, false));
            }
        }

        /**
         * @throws IllegalArgumentException if direction size == 0
         */
        private StructurePiece getComponent(Random rand, List<Direction> directions, boolean advanced) {
            @Nonnull Direction direction = directions.remove(rand.nextInt(directions.size()));
            //blockpos at center of the 3x3 component
            int x = this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);
            int z = this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);

            //make sure a crafting table is only generated once
            if (!specialComponentAdd && rand.nextInt(2) == 0) {
                specialComponentAdd = true;
                return new SpecialBlock(x, y, z, direction, baseBlock, advanced);
            }
            return new Tent(x, y, z, direction, baseBlock, advanced);
        }

        /**
         * @throws IllegalArgumentException if direction size == 0
         */
        private StructurePiece getTentComponent(Random rand, List<Direction> directions, boolean advanced) {
            @Nonnull Direction direction = directions.remove(rand.nextInt(directions.size()));
            //blockpos at center of the 3x3 component
            int x = this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);
            int z = this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);
            return new Tent(x, y, z, direction, baseBlock, advanced);
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putBoolean("advanced", this.advanced);
            tagCompound.putBoolean("specialComponentAdd", this.specialComponentAdd);
        }
    }

    public static class Tent extends HunterCampPieces {
        private final Direction direction;
        private int mirror;
        int xDiff;
        int xCenter;
        private final boolean advanced;

        public Tent(int x, int y, int z, Direction direction, Block baseBlock, boolean advanced) {
            super(ModFeatures.hunter_camp_tent, 1, x, y, z, baseBlock);
            this.setCoordBaseMode(direction);
            this.direction = direction;
            this.advanced = advanced;
        }

        public Tent(@SuppressWarnings("unused") TemplateManager templateManager, CompoundNBT nbt) {
            super(ModFeatures.hunter_camp_tent, nbt);
            direction = Direction.byHorizontalIndex(nbt.getInt("direction"));
            mirror = nbt.getInt("mirror");
            advanced = nbt.getBoolean("advanced");
        }

        @Override
        public boolean func_230383_a_/*addComponentParts*/(ISeedReader worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos blockPos) {
            //set helper variables
            if (mirror == 0 ? (mirror = random.nextInt(2) + 1) == 1 : mirror == 1) {
                xDiff = 2;
                xCenter = this.x + 1;
            } else {
                xDiff = 0;
                xCenter = this.x - 1;
            }

            //preconditions
            if (!super.func_230383_a_/*addComponentParts*/(worldIn, structureManager, chunkGenerator, random, structureBoundingBoxIn, chunkPos, blockPos))
                return false;

            //helper variable for tent blockstates
            Direction dir = direction == Direction.SOUTH || direction == Direction.WEST ? direction.getOpposite() : direction;
            int nul = this.direction.getHorizontalIndex() % 4;
            int one = (this.direction.getHorizontalIndex() + 1) % 4;
            int two = (this.direction.getHorizontalIndex() + 2) % 4;
            int three = (this.direction.getHorizontalIndex() + 3) % 4;
            boolean positiveAxisDirection = this.direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
            boolean xAxis = this.direction.getAxis().equals(Direction.Axis.X);
            int a = positiveAxisDirection ? one : xAxis ? nul : two;
            int b = !positiveAxisDirection ? three : xAxis ? two : nul;
            int c = positiveAxisDirection ? three : xAxis ? two : nul;
            int d = !positiveAxisDirection ? one : xAxis ? nul : two;

            //----------------------generation---------------------

            //generation of tent blocks
            if (mirror == 1) {
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, a), xDiff, 0, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, b), 1, 0, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent_main.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, c), 1, 0, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, d), xDiff, 0, 1, structureBoundingBoxIn);
            } else {
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, b), xDiff, 0, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, a), 1, 0, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent_main.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, d), 1, 0, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, c), xDiff, 0, 1, structureBoundingBoxIn);
            }

            TileEntity tile = worldIn.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof TentTileEntity) {
                ((TentTileEntity) tile).setSpawn(true);
                if (this.advanced) {
                    ((TentTileEntity) tile).setAdvanced(true);
                }
            }

            //generate floor
            BlockPos pos1 = new BlockPos(xCenter, y - 1, z - 1);
            if (worldIn.getBlockState(pos1).getMaterial().isReplaceable())
                this.setBlockState(worldIn, baseBlock.getDefaultState(), xDiff, -1, 0, structureBoundingBoxIn);
            BlockPos pos2 = new BlockPos(x, y - 1, z - 1);
            if (worldIn.getBlockState(pos2).getMaterial().isReplaceable())
                this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 0, structureBoundingBoxIn);
            BlockPos pos3 = new BlockPos(x, y - 1, z);
            if (worldIn.getBlockState(pos3).getMaterial().isReplaceable())
                this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 1, structureBoundingBoxIn);
            BlockPos pos4 = new BlockPos(xCenter, y - 1, z);
            if (worldIn.getBlockState(pos4).getMaterial().isReplaceable())
                this.setBlockState(worldIn, baseBlock.getDefaultState(), xDiff, -1, 1, structureBoundingBoxIn);

            //generate air
            BlockState air = Blocks.AIR.getDefaultState();
            //generate air towards fireplace
            this.setBlockState(worldIn, air, 1, 0, -1, structureBoundingBoxIn);
            this.setBlockState(worldIn, air, xDiff, 0, -1, structureBoundingBoxIn);
            this.setBlockState(worldIn, air, 1, 1, -1, structureBoundingBoxIn);
            this.setBlockState(worldIn, air, xDiff, 1, -1, structureBoundingBoxIn);
            //generate air above
            this.setBlockState(worldIn, air, xDiff, 1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, air, 1, 1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, air, 1, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, air, xDiff, 1, 1, structureBoundingBoxIn);

            //replace top level dirt with grass
            if (Tags.Blocks.DIRT.func_230235_a_/*contains*/(worldIn.getBlockState(new BlockPos(x, y - 1, z - 2)).getBlock())) {
                this.setBlockState(worldIn, Blocks.GRASS_BLOCK.getDefaultState(), 1, -1, -1, structureBoundingBoxIn);
            }
            if (Tags.Blocks.DIRT.func_230235_a_/*contains*/(worldIn.getBlockState(new BlockPos(xCenter, y - 1, z - 2)).getBlock())) {
                this.setBlockState(worldIn, Blocks.GRASS_BLOCK.getDefaultState(), xDiff, -1, -1, structureBoundingBoxIn);
            }

            return true;
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            tagCompound.putInt("direction", this.direction.getHorizontalIndex());
            tagCompound.putInt("mirror", this.mirror);
            tagCompound.putBoolean("advanced", this.advanced);
            super.readAdditional(tagCompound);
        }

        @Override
        protected boolean testPreconditions(ISeedReader worldIn, StructureManager manager) {
            return super.testPreconditions(worldIn, manager)
                    && !worldIn.getBlockState(new BlockPos(xCenter, y - 1, z - 1)).getMaterial().isLiquid()
                    && !worldIn.getBlockState(new BlockPos(x, y - 1, z - 1)).getMaterial().isLiquid()
                    && !worldIn.getBlockState(new BlockPos(xCenter, y - 1, z)).getMaterial().isLiquid()
                    //distance to campfire block
                    && (Math.abs(this.y - worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0), this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0))) < 2);
        }
    }

    protected void setBoundingBox() {
        this.boundingBox = new MutableBoundingBox(this.x - 1, this.y, this.z - 1, this.x + 1, this.y + 2, this.z + 1);
    }

    public HunterCampPieces(IStructurePieceType structurePieceType, int part, int x, int y, int z, Block baseBlock) {
        super(structurePieceType, part);
        this.baseBlock = baseBlock;
        this.x = x;
        this.y = y;
        this.z = z;
        this.setBoundingBox();
    }

    public HunterCampPieces(IStructurePieceType structurePieceType, CompoundNBT nbt) {
        super(structurePieceType, nbt);
        this.baseBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("baseBlock")));
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.z = nbt.getInt("z");
    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound) {
        tagCompound.putInt("x", x);
        tagCompound.putInt("y", y);
        tagCompound.putInt("z", z);
        //noinspection ConstantConditions
        tagCompound.putString("baseBlock", this.baseBlock.getRegistryName().toString());
    }

    public static class SpecialBlock extends HunterCampPieces {
        private final Direction direction;
        private final boolean advanced;

        public SpecialBlock(int x, int y, int z, Direction direction, Block baseBlocks, boolean advanced) {
            super(ModFeatures.hunter_camp_special, 2, x, y, z, baseBlocks);
            this.setCoordBaseMode(direction);
            this.direction = direction;
            this.advanced = advanced;
        }

        public SpecialBlock(@SuppressWarnings("unused") TemplateManager templateManager, CompoundNBT compoundNBT) {
            super(ModFeatures.hunter_camp_special, compoundNBT);
            this.direction = Direction.byHorizontalIndex(compoundNBT.getInt("dir"));
            this.advanced = compoundNBT.getBoolean("advanced");
        }

        @Override
        public boolean func_230383_a_/*addComponentParts*/(ISeedReader worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos blockPos) {
            //preconditions
            if (!super.func_230383_a_/*addComponentParts*/(worldIn, structureManager, chunkGenerator, random, structureBoundingBoxIn, chunkPos, blockPos))
                return false;

            //generation
            if (advanced) {
                if (!worldIn.getBlockState(new BlockPos(this.x + 1, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.x + 1, this.z) - 1, z)).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, ModBlocks.weapon_table.getDefaultState(), 2, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.x + 1, this.z) - y, 1, structureBoundingBoxIn);
                if (!worldIn.getBlockState(new BlockPos(this.x - 1, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.x - 1, this.z) - 1, z)).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 0, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.x - 1, this.z) - y, 1, structureBoundingBoxIn);
            } else {
                this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 1, 0, 1, structureBoundingBoxIn);
            }
            return true;
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putInt("dir", this.direction.getHorizontalIndex());
            tagCompound.putBoolean("advanced", this.advanced);
        }


        @Override
        protected boolean testPreconditions(ISeedReader worldIn, StructureManager manager) {
            return super.testPreconditions(worldIn, manager)
                    && (Math.abs(this.y - worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0), this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0))) < 3);
        }
    }

    protected boolean testPreconditions(ISeedReader worldIn, StructureManager manager) {
        return this.y >= 63
                && !worldIn.getBlockState(new BlockPos(x, y - 1, z)).getMaterial().isLiquid()
                && manager.func_235010_a_(new BlockPos(x, y, z), true, Structure.field_236381_q_).isValid();
    }
}
