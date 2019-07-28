package de.teamlapen.vampirism.world.gen.structures;

import com.google.common.collect.Lists;

import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModWorld;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

/**
 * @author cheaterpaul
 */
public abstract class HunterCampPieces extends StructurePiece {
    int x, y, z;
    public static class Fireplace extends HunterCampPieces {
        public Fireplace(Random random, int x, int y, int z) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPFIRE, 0);
            this.setCoordBaseMode(Direction.Plane.HORIZONTAL.random(random));
            this.x = x * 16 + random.nextInt(16);
            this.y = y;
            this.z = z * 16 + random.nextInt(16);
            this.boundingBox = new MutableBoundingBox(this.x, this.y, this.z, x + 2, y + 2, z + 2);
        }

        public Fireplace(TemplateManager templateManager, CompoundNBT nbt) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPFIRE, nbt);
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos) {
            y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
            this.boundingBox = new MutableBoundingBox(x, y, z, x + 2, y + 2, z + 2);
            this.setBlockState(worldIn, ModBlocks.fire_place.getDefaultState(), 1, 0, 1, structureBoundingBoxIn);
            return true;
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
        }

        @Override
        public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
            List<Integer> list = Lists.newArrayList(0, 1, 2, 3);
            int i = rand.nextInt(8);
            listIn.add(new Tent(rand, x, y, z, Direction.byHorizontalIndex(list.remove(rand.nextInt(list.size())))));
            if (i < 4)
                listIn.add(new Tent(rand, x, y, z, Direction.byHorizontalIndex(list.remove(rand.nextInt(list.size())))));
            if (i > 3)
                listIn.add(new Tent(rand, x, y, z, Direction.byHorizontalIndex(list.remove(rand.nextInt(list.size())))));
            if (i < 1)
                listIn.add(new Tent(rand, x, y, z, Direction.byHorizontalIndex(list.remove(rand.nextInt(list.size())))));
        }
    }

    public static class Tent extends HunterCampPieces {
        Direction direction;
        int mirror = 0;
        boolean generated;

        public Tent(Random random, int x, int y, int z, Direction direction) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPTENT, 1);
            this.setCoordBaseMode(direction);
            this.direction = direction;
            this.x = x;
            this.y = y;
            this.z = z;
            setBoundingBox(direction);
        }

        public Tent(TemplateManager templateManager, CompoundNBT nbt) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPTENT, nbt);
            direction = Direction.byHorizontalIndex(nbt.getInt("dir"));
            mirror = nbt.getInt("mirror");
            generated = nbt.getBoolean("generated");
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos) {
            y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
            setBoundingBox(direction);
            Direction dir = direction == Direction.SOUTH || direction == Direction.WEST ? direction.getOpposite() : direction;
            int nul = this.direction.getHorizontalIndex() % 4;
            int eins = (this.direction.getHorizontalIndex() + 1) % 4;
            int zwei = (this.direction.getHorizontalIndex() + 2) % 4;
            int drei = (this.direction.getHorizontalIndex() + 3) % 4;

            boolean positiveAxisDirection = this.direction.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            boolean xAxis = this.direction.getAxis() == Direction.Axis.X;


            //you should have a goddamn reason to edit this
            if (!generated) {
                if (mirror == 0 ? (mirror = randomIn.nextInt(2) + 1) == 1 : mirror == 1) {
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, positiveAxisDirection ? eins : xAxis ? nul : zwei), 2, 0, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, !positiveAxisDirection ? drei : xAxis ? zwei : nul), 1, 0, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, positiveAxisDirection ? drei : xAxis ? zwei : nul), 1, 0, 1, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, !positiveAxisDirection ? eins : xAxis ? nul : zwei), 2, 0, 1, structureBoundingBoxIn);
                } else {
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, !positiveAxisDirection ? drei : xAxis ? zwei : nul), 0, 0, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, positiveAxisDirection ? eins : xAxis ? nul : zwei), 1, 0, 0, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir.getOpposite()).with(TentBlock.POSITION, !positiveAxisDirection ? eins : xAxis ? nul : zwei), 1, 0, 1, structureBoundingBoxIn);
                    this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, dir).with(TentBlock.POSITION, positiveAxisDirection ? drei : xAxis ? zwei : nul), 0, 0, 1, structureBoundingBoxIn);
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

        public CraftingTable(int x, int y, int z, Direction direction) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPTABLE, 2);
            this.x = x;
            this.y = y;
            this.z = z;
            this.setCoordBaseMode(direction);
            this.direction = direction;
            setBoundingBox(direction);
        }

        public CraftingTable(TemplateManager templateManager, CompoundNBT compoundNBT) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPTABLE, compoundNBT);
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos) {
            y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
            this.boundingBox = new MutableBoundingBox(x, y, z, x + 2, y + 2, z + 2);
            this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 1, 0, 1, structureBoundingBoxIn);
            return true;
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

    public HunterCampPieces(IStructurePieceType pieceType, int pieceIndex) {
        super(pieceType, pieceIndex);
    }

    public HunterCampPieces(IStructurePieceType pieceType, CompoundNBT tagCompound) {
        super(pieceType, tagCompound);
        this.x = tagCompound.getInt("x");
        this.y = tagCompound.getInt("y");
        this.z = tagCompound.getInt("z");
    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound) {
        tagCompound.putInt("x", x);
        tagCompound.putInt("y", y);
        tagCompound.putInt("z", z);
    }
}
