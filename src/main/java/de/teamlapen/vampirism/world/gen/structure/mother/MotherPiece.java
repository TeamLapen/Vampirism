package de.teamlapen.vampirism.world.gen.structure.mother;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import org.jetbrains.annotations.NotNull;

public class MotherPiece extends ScatteredFeaturePiece {

    public MotherPiece(RandomSource random, int x, int z) {
        super(VampirismFeatures.MOTHER.get(), x, 64, z, 40, 20, 20, Direction.Plane.HORIZONTAL.getRandomDirection(random));
    }

    public MotherPiece(CompoundTag tag) {
        super(VampirismFeatures.MOTHER.get(), tag);
    }

    @Override
    protected void placeBlock(WorldGenLevel p_73435_, BlockState p_73436_, int p_73437_, int p_73438_, int p_73439_, BoundingBox p_73440_) {
        super.placeBlock(p_73435_, p_73436_, p_73437_ + 3, p_73438_, p_73439_ + 3, p_73440_);
    }

    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager structureManager, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos blockPos) {
        BlockState log = ModBlocks.DARK_SPRUCE_LOG.get().defaultBlockState();
        BlockState mother = ModBlocks.MOTHER.get().defaultBlockState();
        if (updateAverageGroundHeight(level, box, 5)) {
            int y = 0;
            // level 0
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    if ((i == 2 || i == -2) && (j == 2 || j == -2)) {
                        continue;
                    }
                    this.placeBlock(level, log, i, y, j, box);
                }
            }

            //mother
            this.placeBlock(level, mother, 0, y, 0, box);

            // level 1
            y += 1;
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    if ((i == 2 || i == -2) && (j == 2 || j == -2)) {
                        continue;
                    }
                    this.placeBlock(level, log, i, y, j, box);
                }
            }

            // level 2
            y += 1;
            this.placeBlock(level, log, -3,y, -1, box);
            this.placeBlock(level, log, -3,y , 0, box);
            this.placeBlock(level, log, -3,y , 1, box);

            this.placeBlock(level, log, -2,y , -2, box);
            this.placeBlock(level, log, -2,y , -1, box);
            this.placeBlock(level, log, -2,y , 0, box);
            this.placeBlock(level, log, -2,y , 1, box);
            this.placeBlock(level, log, -2,y , 2, box);

            this.placeBlock(level, log, -1,y , -3, box);
            this.placeBlock(level, log, -1,y , -2, box);
            this.placeBlock(level, log, -1,y , -1, box);
            this.placeBlock(level, log, -1,y , 0, box);
            this.placeBlock(level, log, -1,y , 1, box);
            this.placeBlock(level, log, -1,y , 2, box);
            this.placeBlock(level, log, -1,y , 3, box);

            this.placeBlock(level, log, 0,y , -3, box);
            this.placeBlock(level, log, 0,y , -2, box);
            this.placeBlock(level, log, 0,y , 2, box);
            this.placeBlock(level, log, 0,y , 3, box);

            this.placeBlock(level, log, 1,y , -3, box);
            this.placeBlock(level, log, 1,y , -2, box);
            this.placeBlock(level, log, 1,y , -1, box);
            this.placeBlock(level, log, 1,y , 1, box);
            this.placeBlock(level, log, 1,y , 2, box);
            this.placeBlock(level, log, 1,y , 3, box);

            this.placeBlock(level, log, 2,y , -2, box);
            this.placeBlock(level, log, 2,y , -1, box);
            this.placeBlock(level, log, 2,y , 0, box);
            this.placeBlock(level, log, 2,y , 1, box);
            this.placeBlock(level, log, 2,y , 2, box);

            this.placeBlock(level, log, 3,y , -1, box);
            this.placeBlock(level, log, 3,y , 0, box);
            this.placeBlock(level, log, 3,y , 1, box);

            // level 3
            y += 1;
            this.placeBlock(level, log, -3, y, -1, box);
            this.placeBlock(level, log, -3, y, 0, box);
            this.placeBlock(level, log, -3, y, 1, box);

            this.placeBlock(level, log, -2, y, -2, box);
            this.placeBlock(level, log, -2, y, -1, box);
            this.placeBlock(level, log, -2, y, 0, box);
            this.placeBlock(level, log, -2, y, 1, box);
            this.placeBlock(level, log, -2, y, 2, box);

            this.placeBlock(level, log, -1, y, -3, box);
            this.placeBlock(level, log, -1, y, -2, box);
            this.placeBlock(level, log, -1, y, 2, box);
            this.placeBlock(level, log, -1, y, 3, box);

            this.placeBlock(level, log, 0, y, -3, box);
            this.placeBlock(level, log, 0, y, 3, box);

            this.placeBlock(level, log, 1, y, -3, box);
            this.placeBlock(level, log, 1, y, -2, box);
            this.placeBlock(level, log, 1, y, 2, box);
            this.placeBlock(level, log, 1, y, 3, box);

            this.placeBlock(level, log, 2, y, -2, box);
            this.placeBlock(level, log, 2, y, -1, box);
            this.placeBlock(level, log, 2, y, 1, box);
            this.placeBlock(level, log, 2, y, 2, box);

            this.placeBlock(level, log, 3, y, -1, box);
            this.placeBlock(level, log, 3, y, 0, box);
            this.placeBlock(level, log, 3, y, 1, box);

            placeRoots(level, structureManager, chunkGenerator, random, box, chunkPos, blockPos);
        }
    }

    protected void placeRoots(@NotNull WorldGenLevel level, @NotNull StructureManager structureManager, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos blockPos) {
        BlockState roots = ModBlocks.CURSED_ROOTED_DIRT.get().defaultBlockState();
        int y = -1;
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if ((i == 2 || i == -2) && (j == 2 || j == -2)) {
                    continue;
                }
                this.placeBlock(level, roots, i, y, j, box);
            }
        }
    }
}
