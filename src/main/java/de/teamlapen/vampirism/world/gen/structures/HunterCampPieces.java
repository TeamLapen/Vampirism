package de.teamlapen.vampirism.world.gen.structures;

import com.google.common.collect.Lists;

import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModWorldFeatures;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public abstract class HunterCampPieces extends StructurePiece {
    protected int x, y, z;
    protected final Block baseBlock;

    public HunterCampPieces(IStructurePieceType structurePieceType, int part, int x, int y, int z, Block baseBlock) {
        super(structurePieceType, part);
        this.baseBlock = baseBlock;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public HunterCampPieces(IStructurePieceType structurePieceType, CompoundNBT nbt) {
        super(structurePieceType, nbt);
        this.baseBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("baseBlock")));
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.z = nbt.getInt("z");
    }

    public static class Fireplace extends HunterCampPieces {
        boolean craftinTable = false;

        public Fireplace(Random random, int x, int y, int z, Block baseBlock) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPFIRE, 0, x * 16 + random.nextInt(16), y, z * 16 + random.nextInt(16), baseBlock);
            this.setCoordBaseMode(Direction.Plane.HORIZONTAL.random(random));
            this.boundingBox = new MutableBoundingBox(this.x, this.y, this.z, x + 2, y + 2, z + 2);
        }

        public Fireplace(TemplateManager templateManager, CompoundNBT nbt) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPFIRE, nbt);
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, @Nonnull Random randomIn, @Nonnull MutableBoundingBox structureBoundingBoxIn, @Nonnull ChunkPos chunkPos) {
            this.y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
            this.boundingBox = new MutableBoundingBox(x, y, z, x + 2, y + 2, z + 2);

            this.setBlockState(worldIn, baseBlock.getDefaultState(), 0, -1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 0, -1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 0, -1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 2, -1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 2, -1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 2, -1, 2, structureBoundingBoxIn);

            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 0, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 0, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, ModBlocks.fire_place.getDefaultState(), 1, 0, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 0, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 0, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 0, 2, structureBoundingBoxIn);

            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 2, structureBoundingBoxIn);

            return true;
        }

        @Override
        public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
            List<Integer> directions = Lists.newArrayList(0, 1, 2, 3);
            int i = rand.nextInt(8);
            listIn.add(new Tent(rand, x, y, z, Direction.byHorizontalIndex(directions.remove(rand.nextInt(directions.size()))), baseBlock));
            if (i < 4)
                listIn.add(getComponent(rand, directions));
            if (i < 2)
                listIn.add(getComponent(rand, directions));
            if (i < 1)
                listIn.add(getComponent(rand, directions));
        }

        private StructurePiece getComponent(Random rand, List<Integer> directions) {
            if (rand.nextInt(6) == 0 && !craftinTable) {
                craftinTable = true;
                return new CraftingTable(x, y, z, Direction.byHorizontalIndex(directions.remove(rand.nextInt(directions.size()))), baseBlock);
            }
            return new Tent(rand, x, y, z, Direction.byHorizontalIndex(directions.remove(rand.nextInt(directions.size()))), baseBlock);
        }
    }

    public static class Tent extends HunterCampPieces {
        private Direction direction;
        private int mirror = 0;
        private boolean generated;

        public Tent(Random random, int x, int y, int z, Direction direction, Block baseBlock) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPTENT, 1, x, y, z, baseBlock);
            this.setCoordBaseMode(direction);
            this.direction = direction;
            setBoundingBox(direction);
        }

        public Tent(TemplateManager templateManager, CompoundNBT nbt) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPTENT, nbt);
            direction = Direction.byHorizontalIndex(nbt.getInt("dir"));
            mirror = nbt.getInt("mirror");
            generated = nbt.getBoolean("generated");
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, @Nonnull Random randomIn, @Nonnull MutableBoundingBox structureBoundingBoxIn, @Nonnull ChunkPos chunkPos) {
            y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
            setBoundingBox(direction);

            Direction dir = direction == Direction.SOUTH || direction == Direction.WEST ? direction.getOpposite() : direction;
            int nul = this.direction.getHorizontalIndex() % 4;
            int eins = (this.direction.getHorizontalIndex() + 1) % 4;
            int zwei = (this.direction.getHorizontalIndex() + 2) % 4;
            int drei = (this.direction.getHorizontalIndex() + 3) % 4;

            boolean positiveAxisDirection = this.direction.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            boolean xAxis = this.direction.getAxis() == Direction.Axis.X;


            if (!generated) {
                if (mirror == 0 ? (mirror = randomIn.nextInt(2) + 1) == 1 : mirror == 1) {
                    if (worldIn.getBlockState(new BlockPos(x + 2, y - 1, z)).isAir(worldIn, new BlockPos(x + 2, y - 1, z)))

                        this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, positiveAxisDirection ? eins : xAxis ? nul : zwei), 2, 0, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, !positiveAxisDirection ? drei : xAxis ? zwei : nul), 1, 0, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent_main.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, positiveAxisDirection ? drei : xAxis ? zwei : nul), 1, 0, 1, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, !positiveAxisDirection ? eins : xAxis ? nul : zwei), 2, 0, 1, structureBoundingBoxIn);

                    this.setBlockState(worldIn, this.baseBlock.getDefaultState(), 2, -1, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, this.baseBlock.getDefaultState(), 1, -1, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, this.baseBlock.getDefaultState(), 1, -1, 1, structureBoundingBoxIn);
                    this.setBlockState(worldIn, this.baseBlock.getDefaultState(), 2, -1, 1, structureBoundingBoxIn);

                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 1, structureBoundingBoxIn);

                } else {
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, !positiveAxisDirection ? drei : xAxis ? zwei : nul), 0, 0, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, positiveAxisDirection ? eins : xAxis ? nul : zwei), 1, 0, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent_main.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, !positiveAxisDirection ? eins : xAxis ? nul : zwei), 1, 0, 1, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, positiveAxisDirection ? drei : xAxis ? zwei : nul), 0, 0, 1, structureBoundingBoxIn);

                    this.setBlockState(worldIn, this.baseBlock.getDefaultState(), 0, -1, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, this.baseBlock.getDefaultState(), 1, -1, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, this.baseBlock.getDefaultState(), 1, -1, 1, structureBoundingBoxIn);
                    this.setBlockState(worldIn, this.baseBlock.getDefaultState(), 0, -1, 1, structureBoundingBoxIn);

                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 1, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 1, 1, structureBoundingBoxIn);
                }
                generated = true;
            }

            return true;
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            tagCompound.putInt("dir", direction.getHorizontalIndex());
            tagCompound.putInt("mirror", mirror);
            tagCompound.putBoolean("generated", generated);
            super.readAdditional(tagCompound);
        }
    }

    public static class CraftingTable extends HunterCampPieces {
        private Direction direction;

        public CraftingTable(int x, int y, int z, Direction direction, Block baseBlocks) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPTABLE, 2, x, y, z, baseBlocks);
            this.setCoordBaseMode(direction);
            this.direction = direction;
            this.setBoundingBox(direction);
        }

        public CraftingTable(TemplateManager templateManager, CompoundNBT compoundNBT) {
            super(ModWorldFeatures.StructurePieceTypes.HUNTERCAMPTABLE, compoundNBT);
            direction = Direction.byHorizontalIndex(compoundNBT.getInt("dir"));
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
            this.y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
            this.setBoundingBox(this.direction);

            this.setBlockState(worldIn, baseBlock.getDefaultState(), 0, -1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 0, -1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 0, -1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 1, -1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 2, -1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 2, -1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, baseBlock.getDefaultState(), 2, -1, 2, structureBoundingBoxIn);

            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 0, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 0, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 1, 0, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 0, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 0, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 0, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 0, 2, structureBoundingBoxIn);

            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 1, 2, structureBoundingBoxIn);
            return true;
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putInt("dir", direction.getHorizontalIndex());
        }
    }

    protected void setBoundingBox(Direction direction) {
        switch (direction) {
            case NORTH:
                this.boundingBox = new MutableBoundingBox(x, y, z - 3, x + 2, y + 1, z - 1);
                break;
            case SOUTH:
                this.boundingBox = new MutableBoundingBox(x, y, z + 3, x + 2, y + 1, z + 5);
                break;
            case WEST:
                this.boundingBox = new MutableBoundingBox(x - 3, y, z, x - 1, y + 1, z + 2);
                break;
            case EAST:
                this.boundingBox = new MutableBoundingBox(x + 3, y, z, x + 5, y + 1, z + 2);
        }
    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound) {
        tagCompound.putInt("x", x);
        tagCompound.putInt("y", y);
        tagCompound.putInt("z", z);
        tagCompound.putString("baseBlock", this.baseBlock.getRegistryName().toString());
    }
}
