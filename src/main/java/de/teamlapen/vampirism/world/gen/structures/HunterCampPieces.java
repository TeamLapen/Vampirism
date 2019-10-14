package de.teamlapen.vampirism.world.gen.structures;

import com.google.common.collect.Lists;

import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModWorldFeatures;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
public abstract class HunterCampPieces extends StructurePiece {
    protected int x, y, z;
    protected Block baseBlock;

    public static void init(int chunkX, int chunkZ, Biome biomeIn, Random rand, List<StructurePiece> components) {
        Fireplace hunterCamp = new Fireplace(rand, chunkX * 16 + rand.nextInt(16), 63, chunkZ * 16 + rand.nextInt(16), biomeIn.getSurfaceBuilderConfig().getTop().getBlock());
        components.add(hunterCamp);
        hunterCamp.buildComponent(hunterCamp, components, rand);
    }

    public static class Fireplace extends HunterCampPieces {
        boolean craftingTableAdded = false;

        public Fireplace(Random random, int x, int y, int z, Block baseBlock) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPFIRE, 0, x, y, z, baseBlock);
            this.setCoordBaseMode(Direction.Plane.HORIZONTAL.random(random));
        }

        public Fireplace(TemplateManager templateManager, CompoundNBT nbt) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPFIRE, nbt);
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {
            //preconditions
            if (!super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, chunkPosIn)) return false;

            //generation
            this.setBlockState(worldIn, ModBlocks.fire_place.getDefaultState(), 1, 0, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
            return true;
        }

        @Override
        public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
            //adds 1-4 tent or crafting table elements to the structure (max 1 per direction && max 1 crafting table)
            @Nonnull List<Direction> directions = Lists.newArrayList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
            int i = rand.nextInt(8);
            listIn.add(getComponent(rand, directions));
            if (i < 4)
                listIn.add(getComponent(rand, directions));
            if (i < 2)
                listIn.add(getComponent(rand, directions));
            if (i < 1)
                listIn.add(getComponent(rand, directions));
        }

        /**
         * @throws IllegalArgumentException if direction size == 0
         */
        private StructurePiece getComponent(Random rand, List<Direction> directions) {
            @Nonnull Direction direction = directions.remove(rand.nextInt(directions.size()));
            //blockpos at center of the 3x3 component
            int x = this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);
            int z = this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);

            //make sure a crafting table is only generated once
            if (!craftingTableAdded && rand.nextInt(6) == 0) {
                craftingTableAdded = true;
                return new CraftingTable(x, y, z, direction, baseBlock);
            }
            return new Tent(x, y, z, direction, baseBlock);
        }
    }

    public static class Tent extends HunterCampPieces {
        private final Direction direction;
        private int mirror;

        public Tent(int x, int y, int z, Direction direction, Block baseBlock) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPTENT, 1, x, y, z, baseBlock);
            this.setCoordBaseMode(direction);
            this.direction = direction;
        }

        public Tent(TemplateManager templateManager, CompoundNBT nbt) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPTENT, nbt);
            direction = Direction.byHorizontalIndex(nbt.getInt("direction"));
            mirror = nbt.getInt("mirror");
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {
            //preconditions
            if (!super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, chunkPosIn)) return false;
            if (Math.abs(this.y - worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0), this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0))) > 1)
                return false;

            //helper variable for tent blockstates
            Direction dir = direction == Direction.SOUTH || direction == Direction.WEST ? direction.getOpposite() : direction;
            int nul = this.direction.getHorizontalIndex() % 4;
            int eins = (this.direction.getHorizontalIndex() + 1) % 4;
            int zwei = (this.direction.getHorizontalIndex() + 2) % 4;
            int drei = (this.direction.getHorizontalIndex() + 3) % 4;
            boolean positiveAxisDirection = this.direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
            boolean xAxis = this.direction.getAxis().equals(Direction.Axis.X);
            int a = positiveAxisDirection ? eins : xAxis ? nul : zwei;
            int b = !positiveAxisDirection ? drei : xAxis ? zwei : nul;
            int c = positiveAxisDirection ? drei : xAxis ? zwei : nul;
            int d = !positiveAxisDirection ? eins : xAxis ? nul : zwei;

            //generation
            if (mirror == 0 ? (mirror = randomIn.nextInt(2) + 1) == 1 : mirror == 1) {
                //additional preconditions
                if (worldIn.getBlockState(new BlockPos(x + 1, y - 1, z - 1)).getMaterial().isLiquid()) return false;
                if (worldIn.getBlockState(new BlockPos(x, y - 1, z - 1)).getMaterial().isLiquid()) return false;
                if (worldIn.getBlockState(new BlockPos(x + 1, y - 1, z)).getMaterial().isLiquid()) return false;

                //generation
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, a), 2, 0, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, b), 1, 0, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent_main.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, c), 1, 0, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, d), 2, 0, 1, structureBoundingBoxIn);

                //generate floor
                BlockPos pos1 = new BlockPos(x + 1, y - 1, z - 1);
                if (worldIn.getBlockState(pos1).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, baseBlock.getDefaultState(), 2, -1, 0, structureBoundingBoxIn);
                BlockPos pos2 = new BlockPos(x, y - 1, z - 1);
                if (worldIn.getBlockState(pos2).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 0, structureBoundingBoxIn);
                BlockPos pos3 = new BlockPos(x, y - 1, z);
                if (worldIn.getBlockState(pos3).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 1, structureBoundingBoxIn);
                BlockPos pos4 = new BlockPos(x + 1, y - 1, z);
                if (worldIn.getBlockState(pos4).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, baseBlock.getDefaultState(), 2, -1, 1, structureBoundingBoxIn);

                BlockState air = Blocks.AIR.getDefaultState();
                //generate air towards fireplace
                this.setBlockState(worldIn, air, 1, 0, -1, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 2, 0, -1, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 1, 1, -1, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 2, 1, -1, structureBoundingBoxIn);
                //generate air above
                this.setBlockState(worldIn, air, 2, 1, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 1, 1, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 1, 1, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 2, 1, 1, structureBoundingBoxIn);
            } else {
                //additional preconditions
                if (worldIn.getBlockState(new BlockPos(x - 1, y - 1, z - 1)).getMaterial().isLiquid()) return false;
                if (worldIn.getBlockState(new BlockPos(x, y - 1, z - 1)).getMaterial().isLiquid()) return false;
                if (worldIn.getBlockState(new BlockPos(x - 1, y - 1, z)).getMaterial().isLiquid()) return false;

                //generation
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, b), 0, 0, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, a), 1, 0, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent_main.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, d), 1, 0, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, c), 0, 0, 1, structureBoundingBoxIn);

                //generate floor
                BlockPos pos1 = new BlockPos(x - 1, y - 1, z - 1);
                if (worldIn.getBlockState(pos1).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, baseBlock.getDefaultState(), 0, -1, 0, structureBoundingBoxIn);
                BlockPos pos2 = new BlockPos(x, y - 1, z - 1);
                if (worldIn.getBlockState(pos2).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 0, structureBoundingBoxIn);
                BlockPos pos3 = new BlockPos(x, y - 1, z);
                if (worldIn.getBlockState(pos3).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 1, structureBoundingBoxIn);
                BlockPos pos4 = new BlockPos(x - 1, y - 1, z);
                if (worldIn.getBlockState(pos4).getMaterial().isReplaceable())
                    this.setBlockState(worldIn, baseBlock.getDefaultState(), 0, -1, 1, structureBoundingBoxIn);

                //generate air
                BlockState air = Blocks.AIR.getDefaultState();
                //generate air towards fireplace
                this.setBlockState(worldIn, air, 1, 0, -1, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 0, 0, -1, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 1, 1, -1, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 0, 1, -1, structureBoundingBoxIn);
                //generate air above
                this.setBlockState(worldIn, air, 0, 1, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 1, 1, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 1, 1, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, air, 0, 1, 1, structureBoundingBoxIn);
            }
            return true;
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            tagCompound.putInt("direction", direction.getHorizontalIndex());
            tagCompound.putInt("mirror", mirror);
            super.readAdditional(tagCompound);
        }
    }

    public static class CraftingTable extends HunterCampPieces {
        private Direction direction;

        public CraftingTable(int x, int y, int z, Direction direction, Block baseBlocks) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPTABLE, 2, x, y, z, baseBlocks);
            this.setCoordBaseMode(direction);
            this.direction = direction;
        }

        public CraftingTable(TemplateManager templateManager, CompoundNBT compoundNBT) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPTABLE, compoundNBT);
            direction = Direction.byHorizontalIndex(compoundNBT.getInt("dir"));
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {
            //preconditions
            if (!super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, chunkPosIn)) return false;
            if (Math.abs(this.y - worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0), this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0))) > 1)
                return false;

            //generation
            this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 1, 0, 1, structureBoundingBoxIn);
            return true;
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putInt("dir", direction.getHorizontalIndex());
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

    @Override
    public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPosIn) {
        this.y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
        this.setBoundingBox();

        //fail conditions
        if (this.y < 63) return false;
        if (worldIn.getBlockState(new BlockPos(x, y - 1, z)).getMaterial().isLiquid()) return false;
        return true;
    }
}
