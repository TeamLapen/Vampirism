package de.teamlapen.vampirism.world.gen.structure.huntercamp;

import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blockentity.TentBlockEntity;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class HunterCampPieces extends StructurePiece {
    public static void addStartPieces(@NotNull StructurePiecesBuilder structurePiecesBuilder, @NotNull RandomSource random, BlockPos pos) {
        Fireplace hunterCamp = new Fireplace(random, pos);
        structurePiecesBuilder.addPiece(hunterCamp);
        hunterCamp.addChildren(hunterCamp, structurePiecesBuilder, random);
    }

    protected final BlockPos pos;

    public HunterCampPieces(@NotNull StructurePieceType structurePieceType, int part, BlockPos pos) {
        super(structurePieceType, part, new BoundingBox(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
        this.pos = pos;
    }

    public HunterCampPieces(@NotNull StructurePieceType structurePieceType, @NotNull CompoundTag nbt) {
        super(structurePieceType, nbt);
        this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag tagCompound) {
        tagCompound.putInt("x", this.pos.getX());
        tagCompound.putInt("y", this.pos.getY());
        tagCompound.putInt("z", this.pos.getZ());
    }

    protected boolean testPreconditions(@NotNull WorldGenLevel worldIn, @NotNull StructureManager manager, @NotNull ChunkPos chunkPos) {
        if (!VampirismConfig.COMMON.enableHunterTentGeneration.get()) return false;
        return UtilLib.getStructureStartAt(worldIn.getLevel(), this.pos, StructureTags.VILLAGE).isEmpty();
    }

    public static class Fireplace extends HunterCampPieces {
        boolean specialComponentAdd = false;
        private boolean advanced;

        public Fireplace(@NotNull RandomSource random, BlockPos pos) {
            super(ModStructures.HUNTER_CAMP_FIREPLACE.get(), 0, pos);
            this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
        }

        public Fireplace(@NotNull CompoundTag nbt) {
            super(ModStructures.HUNTER_CAMP_FIREPLACE.get(), nbt);
            this.advanced = nbt.getBoolean("advanced");
            this.specialComponentAdd = nbt.getBoolean("specialComponentAdd");
        }

        @Override
        public void addChildren(@NotNull StructurePiece componentInt, @NotNull StructurePieceAccessor listIn, @NotNull RandomSource rand) {
            //adds 1-4 tent or crafting table elements to the structure (max 1 per direction && max 1 crafting table)
            @NotNull List<Direction> directions = Lists.newArrayList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
            if (rand.nextInt(3) == 0) {
                this.advanced = true;
                //advanced
                listIn.addPiece(getTentComponent(rand, directions, true));
                listIn.addPiece(getTentComponent(rand, directions, false));
                int i = rand.nextInt(4);
                if (i < 2) {
                    listIn.addPiece(getComponent(rand, directions, true));
                }
                if (i < 1) {
                    listIn.addPiece(getComponent(rand, directions, true));
                }
            } else {
                //normal
                listIn.addPiece(getTentComponent(rand, directions, false));
                if (rand.nextInt(2) == 0) {
                    listIn.addPiece(getComponent(rand, directions, false));
                }
            }
        }

        @Override
        public void postProcess(@NotNull WorldGenLevel worldIn, @NotNull StructureManager structureManager, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, @NotNull BoundingBox structureBoundingBoxIn, @NotNull ChunkPos chunkPos, @NotNull BlockPos blockPos) {
            //fail conditions
            if (!testPreconditions(worldIn, structureManager, chunkPos)) {
                return;
            }

            //generation
            this.placeBlock(worldIn, VampirismConfig.COMMON.useVanillaCampfire.get() ? Blocks.CAMPFIRE.defaultBlockState() : ModBlocks.FIRE_PLACE.get().defaultBlockState(), 1, 0, 1, structureBoundingBoxIn);
            this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 1, 1, 1, structureBoundingBoxIn);
        }

        @Override
        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag tagCompound) {
            super.addAdditionalSaveData(context, tagCompound);
            tagCompound.putBoolean("advanced", this.advanced);
            tagCompound.putBoolean("specialComponentAdd", this.specialComponentAdd);
        }

        /**
         * @throws IllegalArgumentException if direction size == 0
         */
        private @NotNull StructurePiece getComponent(@NotNull RandomSource rand, @NotNull List<Direction> directions, boolean advanced) {
            @NotNull Direction direction = directions.remove(rand.nextInt(directions.size()));
            //blockpos at center of the 3x3 component
            int xOffset = (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);
            int zOffset = (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);

            //make sure a crafting table is only generated once
            if (!specialComponentAdd && rand.nextInt(2) == 0) {
                specialComponentAdd = true;
                return new SpecialBlock(this.pos.offset(xOffset,0, zOffset), direction, advanced);
            }
            return new Tent(this.pos.offset(xOffset, 0, zOffset), direction, advanced);
        }

        /**
         * @throws IllegalArgumentException if direction size == 0
         */
        private @NotNull StructurePiece getTentComponent(@NotNull RandomSource rand, @NotNull List<Direction> directions, boolean advanced) {
            @NotNull Direction direction = directions.remove(rand.nextInt(directions.size()));
            //blockpos at center of the 3x3 component
            int xOffset = (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);
            int zOffset = (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);
            return new Tent(this.pos.offset(xOffset, 0, zOffset), direction, advanced);
        }
    }

    public static class Tent extends HunterCampPieces {
        private final @NotNull Direction direction;
        private final boolean advanced;
        int xDiff;
        int xCenter;
        private int mirror;

        public Tent(BlockPos pos, @NotNull Direction direction, boolean advanced) {
            super(ModStructures.HUNTER_CAMP_TENT.get(), 1, pos);
            this.setOrientation(direction);
            this.direction = direction;
            this.advanced = advanced;
        }

        public Tent(@NotNull CompoundTag nbt) {
            super(ModStructures.HUNTER_CAMP_TENT.get(), nbt);
            this.direction = Direction.from2DDataValue(nbt.getInt("direction"));
            this.mirror = nbt.getInt("mirror");
            this.advanced = nbt.getBoolean("advanced");
        }

        @Override
        public void postProcess(@NotNull WorldGenLevel worldIn, @NotNull StructureManager structureManager, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, @NotNull BoundingBox structureBoundingBoxIn, @NotNull ChunkPos chunkPos, @NotNull BlockPos blockPos) {
            //set helper variables
            if (mirror == 0 ? (mirror = random.nextInt(2) + 1) == 1 : mirror == 1) {
                this.xDiff = 2;
                this.xCenter = this.pos.getX() + 1;
            } else {
                this.xDiff = 0;
                this.xCenter = this.pos.getX() - 1;
            }

            //fail conditions
            if (!testPreconditions(worldIn, structureManager, chunkPos)) {
                return;
            }

            //helper variable for tent blockstates
            Direction dir = direction == Direction.SOUTH || direction == Direction.WEST ? direction.getOpposite() : direction;
            int nul = this.direction.get2DDataValue() % 4;
            int one = (this.direction.get2DDataValue() + 1) % 4;
            int two = (this.direction.get2DDataValue() + 2) % 4;
            int three = (this.direction.get2DDataValue() + 3) % 4;
            boolean positiveAxisDirection = this.direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE);
            boolean xAxis = this.direction.getAxis().equals(Direction.Axis.X);
            int a = positiveAxisDirection ? one : xAxis ? nul : two;
            int b = !positiveAxisDirection ? three : xAxis ? two : nul;
            int c = positiveAxisDirection ? three : xAxis ? two : nul;
            int d = !positiveAxisDirection ? one : xAxis ? nul : two;

            //----------------------generation---------------------

            //generation of tent blocks
            if (mirror == 1) {
                this.placeBlock(worldIn, ModBlocks.TENT.get().defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()).setValue(TentBlock.POSITION, a), xDiff, 0, 0, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.TENT.get().defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, b), 1, 0, 0, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.TENT_MAIN.get().defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, c), 1, 0, 1, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.TENT.get().defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()).setValue(TentBlock.POSITION, d), xDiff, 0, 1, structureBoundingBoxIn);
            } else {
                this.placeBlock(worldIn, ModBlocks.TENT.get().defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, b), xDiff, 0, 0, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.TENT.get().defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()).setValue(TentBlock.POSITION, a), 1, 0, 0, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.TENT_MAIN.get().defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()).setValue(TentBlock.POSITION, d), 1, 0, 1, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.TENT.get().defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, c), xDiff, 0, 1, structureBoundingBoxIn);
            }

            BlockEntity tile = worldIn.getBlockEntity(this.pos);
            if (tile instanceof TentBlockEntity) {
                ((TentBlockEntity) tile).setSpawn(true);
                if (this.advanced) {
                    ((TentBlockEntity) tile).setAdvanced(true);
                }
            }

            //generate air
            BlockState air = Blocks.AIR.defaultBlockState();
            //generate air towards fireplace
            this.placeBlock(worldIn, air, 1, 0, -1, structureBoundingBoxIn);
            this.placeBlock(worldIn, air, xDiff, 0, -1, structureBoundingBoxIn);
            this.placeBlock(worldIn, air, 1, 1, -1, structureBoundingBoxIn);
            this.placeBlock(worldIn, air, xDiff, 1, -1, structureBoundingBoxIn);
            //generate air above
            this.placeBlock(worldIn, air, xDiff, 1, 0, structureBoundingBoxIn);
            this.placeBlock(worldIn, air, 1, 1, 0, structureBoundingBoxIn);
            this.placeBlock(worldIn, air, 1, 1, 1, structureBoundingBoxIn);
            this.placeBlock(worldIn, air, xDiff, 1, 1, structureBoundingBoxIn);

            //replace top level dirt with grass
            if (worldIn.getBlockState(this.pos.offset(0,-1,-2)).is(BlockTags.DIRT)) {
                this.placeBlock(worldIn, Blocks.GRASS_BLOCK.defaultBlockState(), 1, -1, -1, structureBoundingBoxIn);
            }
            if (worldIn.getBlockState(atX(this.pos.offset(0,-1,-2),xCenter)).is(BlockTags.DIRT)) {
                this.placeBlock(worldIn, Blocks.GRASS_BLOCK.defaultBlockState(), xDiff, -1, -1, structureBoundingBoxIn);
            }
        }

        private BlockPos atX(BlockPos pos, int newX) {
            return new BlockPos(newX, pos.getY(), pos.getZ());
        }

        @Override
        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag tagCompound) {
            tagCompound.putInt("direction", this.direction.get2DDataValue());
            tagCompound.putInt("mirror", this.mirror);
            tagCompound.putBoolean("advanced", this.advanced);
            super.addAdditionalSaveData(context, tagCompound);
        }
    }

    public static class SpecialBlock extends HunterCampPieces {
        private final Direction direction;
        private final boolean advanced;

        public SpecialBlock(BlockPos pos, Direction direction, boolean advanced) {
            super(ModStructures.HUNTER_CAMP_SPECIAL.get(), 2, pos);
            this.setOrientation(direction);
            this.direction = direction;
            this.advanced = advanced;
        }

        public SpecialBlock(@NotNull CompoundTag compoundNBT) {
            super(ModStructures.HUNTER_CAMP_SPECIAL.get(), compoundNBT);
            this.direction = Direction.from2DDataValue(compoundNBT.getInt("dir"));
            this.advanced = compoundNBT.getBoolean("advanced");
        }

        @Override
        public void postProcess(@NotNull WorldGenLevel worldIn, @NotNull StructureManager structureManager, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, @NotNull BoundingBox structureBoundingBoxIn, @NotNull ChunkPos chunkPos, @NotNull BlockPos blockPos) {
            //fail conditions
            if (!testPreconditions(worldIn, structureManager, chunkPos)) {
                return;
            }

            //generation
            if (advanced) {
                this.placeBlock(worldIn, ModBlocks.WEAPON_TABLE.get().defaultBlockState(), 2, 0, 1, structureBoundingBoxIn);
                this.placeBlock(worldIn, Blocks.CRAFTING_TABLE.defaultBlockState(), 0, 0, 1, structureBoundingBoxIn);
            } else {
                this.placeBlock(worldIn, Blocks.CRAFTING_TABLE.defaultBlockState(), 1, 0, 1, structureBoundingBoxIn);
            }
        }

        @Override
        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, @NotNull CompoundTag tagCompound) {
            super.addAdditionalSaveData(context, tagCompound);
            tagCompound.putInt("dir", this.direction.get2DDataValue());
            tagCompound.putBoolean("advanced", this.advanced);
        }
    }
}
