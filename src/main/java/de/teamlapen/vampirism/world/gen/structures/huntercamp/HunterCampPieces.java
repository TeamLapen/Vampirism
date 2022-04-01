package de.teamlapen.vampirism.world.gen.structures.huntercamp;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.blockentity.TentBlockEntity;
import de.teamlapen.vampirism.blocks.TentBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
public abstract class HunterCampPieces extends StructurePiece {
    public static <C extends FeatureConfiguration> void addStartPieces(StructurePiecesBuilder structurePiecesBuilder, PieceGenerator.Context<C> cContext) {
        Random rand = cContext.random();
        Fireplace hunterCamp = new Fireplace(rand, cContext.chunkPos().x * 16 + rand.nextInt(16), 63, cContext.chunkPos().z * 16 + rand.nextInt(16));
        structurePiecesBuilder.addPiece(hunterCamp);
        hunterCamp.addChildren(hunterCamp, structurePiecesBuilder, rand);
    }

    protected final int x, z;
    protected int y;


    public HunterCampPieces(StructurePieceType structurePieceType, int part, int x, int y, int z) {
        super(structurePieceType, part, new BoundingBox(x - 1, y, z - 1, x + 1, y + 2, z + 1));
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public HunterCampPieces(StructurePieceType structurePieceType, CompoundTag nbt) {
        super(structurePieceType, nbt);
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.z = nbt.getInt("z");
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureFeatureManager structureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos blockPos) {
        this.y = worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        this.boundingBox = new BoundingBox(this.x - 1, this.y, this.z - 1, this.x + 1, this.y + 2, this.z + 1);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        tagCompound.putInt("x", x);
        tagCompound.putInt("y", y);
        tagCompound.putInt("z", z);
    }

    protected boolean testPreconditions(WorldGenLevel worldIn, StructureFeatureManager manager, ChunkPos chunkPos) {
        if (!VampirismConfig.COMMON.enableHunterTentGeneration.get()) return false;
        for (StructureStart value : worldIn.getChunk(chunkPos.x, chunkPos.z).getAllStarts().values()) {
            if (value != StructureStart.INVALID_START && value.getFeature() != ModFeatures.hunter_camp) {
                return false;
            }
        }
        return this.y >= 63
                && !worldIn.getBlockState(new BlockPos(x, y - 1, z)).getMaterial().isLiquid()
                && !manager.getStructureAt(new BlockPos(x, y, z), StructureFeature.VILLAGE).isValid();
    }

    public static class Fireplace extends HunterCampPieces {
        boolean specialComponentAdd = false;
        private boolean advanced;

        public Fireplace(Random random, int x, int y, int z) {
            super(ModFeatures.hunter_camp_fireplace, 0, x, y, z);
            this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
        }

        public Fireplace(CompoundTag nbt) {
            super(ModFeatures.hunter_camp_fireplace, nbt);
            advanced = nbt.getBoolean("advanced");
            specialComponentAdd = nbt.getBoolean("specialComponentAdd");
        }

        @Override
        public void addChildren(StructurePiece componentInt, StructurePieceAccessor listIn, Random rand) {
            //adds 1-4 tent or crafting table elements to the structure (max 1 per direction && max 1 crafting table)
            @Nonnull List<Direction> directions = Lists.newArrayList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
            if (rand.nextInt(3) == 0) {
                this.advanced = true;
                //advanced
                listIn.addPiece(getTentComponent(rand, directions, true));
                listIn.addPiece(getTentComponent(rand, directions, false));
                int i = rand.nextInt(4);
                if (i < 2)
                    listIn.addPiece(getComponent(rand, directions, true));
                if (i < 1)
                    listIn.addPiece(getComponent(rand, directions, true));
            } else {
                //normal
                listIn.addPiece(getTentComponent(rand, directions, false));
                if (rand.nextInt(2) == 0)
                    listIn.addPiece(getComponent(rand, directions, false));
            }
        }

        @Override
        public void postProcess(WorldGenLevel worldIn, StructureFeatureManager structureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos blockPos) {
            super.postProcess(worldIn, structureManager, chunkGenerator, random, structureBoundingBoxIn, chunkPos, blockPos);

            //fail conditions
            if (!testPreconditions(worldIn, structureManager, chunkPos)) {
                return;
            }

            //generation
            this.placeBlock(worldIn, VampirismConfig.COMMON.useVanillaCampfire.get() ? Blocks.CAMPFIRE.defaultBlockState() : ModBlocks.fire_place.defaultBlockState(), 1, 0, 1, structureBoundingBoxIn);
            this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), 1, 1, 1, structureBoundingBoxIn);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
            super.addAdditionalSaveData(context, tagCompound);
            tagCompound.putBoolean("advanced", this.advanced);
            tagCompound.putBoolean("specialComponentAdd", this.specialComponentAdd);
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
                return new SpecialBlock(x, y, z, direction, advanced);
            }
            return new Tent(x, y, z, direction, advanced);
        }

        /**
         * @throws IllegalArgumentException if direction size == 0
         */
        private StructurePiece getTentComponent(Random rand, List<Direction> directions, boolean advanced) {
            @Nonnull Direction direction = directions.remove(rand.nextInt(directions.size()));
            //blockpos at center of the 3x3 component
            int x = this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);
            int z = this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? 3 : -3 : 0);
            return new Tent(x, y, z, direction, advanced);
        }
    }

    public static class Tent extends HunterCampPieces {
        private final Direction direction;
        private final boolean advanced;
        int xDiff;
        int xCenter;
        private int mirror;

        public Tent(int x, int y, int z, Direction direction, boolean advanced) {
            super(ModFeatures.hunter_camp_tent, 1, x, y, z);
            this.setOrientation(direction);
            this.direction = direction;
            this.advanced = advanced;
        }

        public Tent(CompoundTag nbt) {
            super(ModFeatures.hunter_camp_tent, nbt);
            direction = Direction.from2DDataValue(nbt.getInt("direction"));
            mirror = nbt.getInt("mirror");
            advanced = nbt.getBoolean("advanced");
        }

        @Override
        public void postProcess(WorldGenLevel worldIn, StructureFeatureManager structureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos blockPos) {
            //set helper variables
            if (mirror == 0 ? (mirror = random.nextInt(2) + 1) == 1 : mirror == 1) {
                xDiff = 2;
                xCenter = this.x + 1;
            } else {
                xDiff = 0;
                xCenter = this.x - 1;
            }

            super.postProcess(worldIn, structureManager, chunkGenerator, random, structureBoundingBoxIn, chunkPos, blockPos);

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
                this.placeBlock(worldIn, ModBlocks.tent.defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()).setValue(TentBlock.POSITION, a), xDiff, 0, 0, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.tent.defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, b), 1, 0, 0, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.tent_main.defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, c), 1, 0, 1, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.tent.defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()).setValue(TentBlock.POSITION, d), xDiff, 0, 1, structureBoundingBoxIn);
            } else {
                this.placeBlock(worldIn, ModBlocks.tent.defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, b), xDiff, 0, 0, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.tent.defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()).setValue(TentBlock.POSITION, a), 1, 0, 0, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.tent_main.defaultBlockState().setValue(TentBlock.FACING, dir.getOpposite()).setValue(TentBlock.POSITION, d), 1, 0, 1, structureBoundingBoxIn);
                this.placeBlock(worldIn, ModBlocks.tent.defaultBlockState().setValue(TentBlock.FACING, dir).setValue(TentBlock.POSITION, c), xDiff, 0, 1, structureBoundingBoxIn);
            }

            BlockEntity tile = worldIn.getBlockEntity(new BlockPos(x, y, z));
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
            if (BlockTags.DIRT.contains(worldIn.getBlockState(new BlockPos(x, y - 1, z - 2)).getBlock())) {
                this.placeBlock(worldIn, Blocks.GRASS_BLOCK.defaultBlockState(), 1, -1, -1, structureBoundingBoxIn);
            }
            if (BlockTags.DIRT.contains(worldIn.getBlockState(new BlockPos(xCenter, y - 1, z - 2)).getBlock())) {
                this.placeBlock(worldIn, Blocks.GRASS_BLOCK.defaultBlockState(), xDiff, -1, -1, structureBoundingBoxIn);
            }
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
            tagCompound.putInt("direction", this.direction.get2DDataValue());
            tagCompound.putInt("mirror", this.mirror);
            tagCompound.putBoolean("advanced", this.advanced);
            super.addAdditionalSaveData(context, tagCompound);
        }

        @Override
        protected boolean testPreconditions(WorldGenLevel worldIn, StructureFeatureManager manager, ChunkPos chunkPos) {
            return super.testPreconditions(worldIn, manager, chunkPos)
                    && !worldIn.getBlockState(new BlockPos(xCenter, y - 1, z - 1)).getMaterial().isLiquid()
                    && !worldIn.getBlockState(new BlockPos(x, y - 1, z - 1)).getMaterial().isLiquid()
                    && !worldIn.getBlockState(new BlockPos(xCenter, y - 1, z)).getMaterial().isLiquid()
                    //distance to campfire block
                    && (Math.abs(this.y - worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0), this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0))) < 2);
        }
    }

    public static class SpecialBlock extends HunterCampPieces {
        private final Direction direction;
        private final boolean advanced;

        public SpecialBlock(int x, int y, int z, Direction direction, boolean advanced) {
            super(ModFeatures.hunter_camp_special, 2, x, y, z);
            this.setOrientation(direction);
            this.direction = direction;
            this.advanced = advanced;
        }

        public SpecialBlock(CompoundTag compoundNBT) {
            super(ModFeatures.hunter_camp_special, compoundNBT);
            this.direction = Direction.from2DDataValue(compoundNBT.getInt("dir"));
            this.advanced = compoundNBT.getBoolean("advanced");
        }

        @Override
        public void postProcess(WorldGenLevel worldIn, StructureFeatureManager structureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos blockPos) {
            super.postProcess(worldIn, structureManager, chunkGenerator, random, structureBoundingBoxIn, chunkPos, blockPos);

            //fail conditions
            if (!testPreconditions(worldIn, structureManager, chunkPos)) {
                return;
            }

            //generation
            if (advanced) {
                if (!worldIn.getBlockState(new BlockPos(this.x + 1, worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.x + 1, this.z) - 1, z)).getMaterial().isReplaceable())
                    this.placeBlock(worldIn, ModBlocks.weapon_table.defaultBlockState(), 2, worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.x + 1, this.z) - y, 1, structureBoundingBoxIn);
                if (!worldIn.getBlockState(new BlockPos(this.x - 1, worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.x - 1, this.z) - 1, z)).getMaterial().isReplaceable())
                    this.placeBlock(worldIn, Blocks.CRAFTING_TABLE.defaultBlockState(), 0, worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.x - 1, this.z) - y, 1, structureBoundingBoxIn);
            } else {
                this.placeBlock(worldIn, Blocks.CRAFTING_TABLE.defaultBlockState(), 1, 0, 1, structureBoundingBoxIn);
            }
            return;
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
            super.addAdditionalSaveData(context, tagCompound);
            tagCompound.putInt("dir", this.direction.get2DDataValue());
            tagCompound.putBoolean("advanced", this.advanced);
        }


        @Override
        protected boolean testPreconditions(WorldGenLevel worldIn, StructureFeatureManager manager, ChunkPos chunkPos) {
            return super.testPreconditions(worldIn, manager, chunkPos)
                    && (Math.abs(this.y - worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.x + (direction.getAxis().equals(Direction.Axis.X) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0), this.z + (direction.getAxis().equals(Direction.Axis.Z) ? direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? -3 : 3 : 0))) < 3);
        }
    }
}
