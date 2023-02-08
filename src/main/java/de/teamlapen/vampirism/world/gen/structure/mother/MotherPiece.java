package de.teamlapen.vampirism.world.gen.structure.mother;

import de.teamlapen.vampirism.blocks.DarkSpruceLogs;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import org.jetbrains.annotations.NotNull;

public class MotherPiece extends ScatteredFeaturePiece {

    public MotherPiece(RandomSource random, int x, int z) {
        super(VampirismFeatures.MOTHER.get(), x, 64, z, 8, 8, 8, Direction.Plane.HORIZONTAL.getRandomDirection(random));
    }

    public MotherPiece(CompoundTag tag) {
        super(VampirismFeatures.MOTHER.get(), tag);
    }

    @Override
    protected void placeBlock(@NotNull WorldGenLevel p_73435_, @NotNull BlockState p_73436_, int p_73437_, int p_73438_, int p_73439_, @NotNull BoundingBox p_73440_) {
        super.placeBlock(p_73435_, p_73436_, p_73437_ + 3, p_73438_, p_73439_ + 3, p_73440_);
    }

    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager structureManager, @NotNull ChunkGenerator chunkGenerator, @NotNull RandomSource random, @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos blockPos) {
        if (updateAverageGroundHeight(level, box, -5)) {
            placeLogs(level, box);
            placeMother(level, box);
            placeRoots(level, box, random);
        }
    }

    protected void placeLogs(WorldGenLevel level, BoundingBox box) {
        for (int i = 0; i < 6; i++) {
            generateLogSlate(level, box, i);
        }
        BlockState log = ModBlocks.DARK_SPRUCE_LOG.get().defaultBlockState().setValue(DarkSpruceLogs.INVULNERABLE, true);
        generateBox(level, box, 2, 6, 0, 4, 6, 0, log, log, false);
        generateBox(level, box, 1, 6, 1, 2, 6, 1, log, log, false);
        placeAir(level, box);
    }

    protected void placeAir(WorldGenLevel level, BoundingBox box) {
        BlockState air = Blocks.AIR.defaultBlockState();
        generateBox(level, box, 2, 5, 2, 5, 5, 2, air, air, false);
        generateBox(level, box, 1, 5, 3, 6, 5, 4, air, air, false);
        generateBox(level, box, 2, 5, 5, 6, 5, 5, air, air, false);
        generateBox(level, box, 3, 5, 6, 5, 5, 6, air, air, false);

        generateBox(level, box, 3, 4, 3, 5, 4, 5, air, air, false);
        generateBox(level, box, 2, 4, 4, 2, 4, 4, air, air, false);

        generateBox(level, box, 3, 3, 4, 4, 3, 4, air, air, false);
    }

    protected void generateLogSlate(WorldGenLevel level, BoundingBox box, int yHeight) {
        BlockState log = ModBlocks.DARK_SPRUCE_LOG.get().defaultBlockState().setValue(DarkSpruceLogs.INVULNERABLE, true);
        generateBox(level, box, 2, yHeight, 0, 5, yHeight, 0, log, log, false);
        generateBox(level, box, 1, yHeight, 1, 6, yHeight, 1, log, log, false);
        generateBox(level, box, 0, yHeight, 2, 7, yHeight, 5, log, log, false);
        generateBox(level, box, 1, yHeight, 6, 6, yHeight, 6, log, log, false);
        generateBox(level, box, 2, yHeight, 7, 5, yHeight, 7, log, log, false);
    }

    protected void placeMother(@NotNull WorldGenLevel level, @NotNull BoundingBox box) {
        BlockState mother = ModBlocks.MOTHER.get().defaultBlockState();
        BlockState remains = ModBlocks.REMAINS.get().defaultBlockState();
        placeBlock(level, mother, 3, 1, 3, box);
        generateBox(level, box, 3, 0, 3, 4, 0, 4, remains, remains, false);
    }

    protected void placeRoots(@NotNull WorldGenLevel level, @NotNull BoundingBox box, @NotNull RandomSource random) {
        BlockState remains = ModBlocks.REMAINS.get().defaultBlockState();
        BlockState active = ModBlocks.ACTIVE_VULNERABLE_REMAINS.get().defaultBlockState();
        generateBox(level, box, 2, -1, 2, 5, -1, 5, remains, remains, false);
        generateBox(level, box, 1, -1, 3, 1, -1, 4, remains, remains, false);

        generateBox(level, box, 2, -2, 1, 2, -2, 1, remains, remains, false);
        generateBox(level, box, 2, -2, 2, 4, -2, 5, remains, remains, false);
        placeBlock(level, remains, 5, -2, 2, box);
        generateBox(level, box, 5, -2, 4, 6, -2, 4, remains, remains, false);
        generateBox(level, box, 1, -2, 3, -1, -2, 3, remains, remains, false);
        placeBlock(level, remains, 1, -2, 5, box);
        generateBox(level, box, 1, -2, 6, 3, -2, 6, remains, remains, false);
        int y = -3 - 2;
        int x = -5;
        int z = -7;
        placeBlock(level, remains, x + 0, y + 0, z + 14, box);
        placeBlock(level, remains, x + 1, y + 0, z + 14, box);
        placeBlock(level, remains, x + 2, y + 0, z + 2, box);
        placeBlock(level, remains, x + 2, y + 0, z + 14, box);
        placeBlock(level, remains, x + 3, y + 0, z + 2, box);
        placeBlock(level, remains, x + 4, y + 0, z + 8, box);
        placeBlock(level, remains, x + 5, y + 0, z + 8, box);
        placeBlock(level, remains, x + 6, y + 0, z + 11, box);
        placeBlock(level, remains, x + 7, y + 0, z + 4, box);
        placeBlock(level, active, x + 8, y + 0, z + 9, box);
        placeBlock(level, remains, x + 12, y + 0, z + 16, box);
        placeBlock(level, remains, x + 12, y + 0, z + 17, box);
        placeBlock(level, remains, x + 13, y + 0, z + 17, box);
        placeBlock(level, remains, x + 14, y + 0, z + 17, box);

        placeBlock(level, remains, x + 1, y + 1, z + 10, box);
        placeBlock(level, remains, x + 2, y + 1, z + 10, box);
        placeBlock(level, remains, x + 2, y + 1, z + 14, box);
        placeBlock(level, remains, x + 3, y + 1, z + 2, box);
        placeBlock(level, remains, x + 4, y + 1, z + 2, box);
        placeBlock(level, remains, x + 4, y + 1, z + 17, box);
        placeBlock(level, remains, x + 4, y + 1, z + 18, box);
        placeBlock(level, remains, x + 5, y + 1, z + 2, box);
        placeBlock(level, remains, x + 5, y + 1, z + 8, box);
        placeBlock(level, remains, x + 5, y + 1, z + 17, box);
        placeBlock(level, remains, x + 6, y + 1, z + 11, box);
        placeBlock(level, remains, x + 7, y + 1, z + 4, box);
        placeBlock(level, remains, x + 7, y + 1, z + 5, box);
        placeBlock(level, remains, x + 7, y + 1, z + 11, box);
        placeBlock(level, remains, x + 8, y + 1, z + 0, box);
        placeBlock(level, remains, x + 8, y + 1, z + 8, box);
        placeBlock(level, remains, x + 8, y + 1, z + 9, box);
        placeBlock(level, remains, x + 8, y + 1, z + 10, box);
        placeBlock(level, remains, x + 9, y + 1, z + 0, box);
        placeBlock(level, remains, x + 9, y + 1, z + 16, box);
        placeBlock(level, remains, x + 9, y + 1, z + 17, box);
        placeBlock(level, remains, x + 11, y + 1, z + 4, box);
        placeBlock(level, remains, x + 11, y + 1, z + 13, box);
        placeBlock(level, remains, x + 12, y + 1, z + 3, box);
        placeBlock(level, remains, x + 12, y + 1, z + 4, box);
        placeBlock(level, remains, x + 12, y + 1, z + 12, box);
        placeBlock(level, remains, x + 12, y + 1, z + 13, box);
        placeBlock(level, remains, x + 12, y + 1, z + 14, box);
        placeBlock(level, active, x + 12, y + 1, z + 15, box);
        placeBlock(level, remains, x + 12, y + 1, z + 16, box);
        placeBlock(level, remains, x + 13, y + 1, z + 3, box);
        placeBlock(level, remains, x + 14, y + 1, z + 12, box);
        placeBlock(level, remains, x + 15, y + 1, z + 12, box);


        placeBlock(level, active, x + 2, y + 2, z + 10, box);
        placeBlock(level, remains, x + 2, y + 2, z + 14, box);
        placeBlock(level, remains, x + 3, y + 2, z + 10, box);
        placeBlock(level, remains, x + 3, y + 2, z + 13, box);
        placeBlock(level, remains, x + 3, y + 2, z + 14, box);
        placeBlock(level, remains, x + 4, y + 2, z + 6, box);
        placeBlock(level, active, x + 4, y + 2, z + 7, box);
        placeBlock(level, remains, x + 4, y + 2, z + 8, box);
        placeBlock(level, remains, x + 4, y + 2, z + 10, box);
        placeBlock(level, remains, x + 4, y + 2, z + 13, box);
        placeBlock(level, remains, x + 5, y + 2, z + 2, box);
        placeBlock(level, remains, x + 5, y + 2, z + 8, box);
        placeBlock(level, remains, x + 5, y + 2, z + 14, box);
        placeBlock(level, remains, x + 5, y + 2, z + 15, box);
        placeBlock(level, remains, x + 5, y + 2, z + 16, box);
        placeBlock(level, remains, x + 5, y + 2, z + 17, box);
        placeBlock(level, remains, x + 6, y + 2, z + 2, box);
        placeBlock(level, remains, x + 6, y + 2, z + 8, box);
        placeBlock(level, remains, x + 7, y + 2, z + 0, box);
        placeBlock(level, active, x + 7, y + 2, z + 1, box);
        placeBlock(level, remains, x + 7, y + 2, z + 2, box);
        placeBlock(level, remains, x + 7, y + 2, z + 3, box);
        placeBlock(level, remains, x + 7, y + 2, z + 5, box);
        placeBlock(level, remains, x + 7, y + 2, z + 8, box);
        placeBlock(level, remains, x + 7, y + 2, z + 10, box);
        placeBlock(level, remains, x + 7, y + 2, z + 11, box);
        placeBlock(level, remains, x + 7, y + 2, z + 13, box);
        placeBlock(level, remains, x + 7, y + 2, z + 14, box);
        placeBlock(level, remains, x + 8, y + 2, z + 0, box);
        placeBlock(level, remains, x + 8, y + 2, z + 9, box);
        placeBlock(level, remains, x + 8, y + 2, z + 10, box);
        placeBlock(level, remains, x + 8, y + 2, z + 13, box);
        placeBlock(level, remains, x + 8, y + 2, z + 14, box);
        placeBlock(level, remains, x + 8, y + 2, z + 15, box);
        placeBlock(level, active, x + 8, y + 2, z + 16, box);
        placeBlock(level, remains, x + 9, y + 2, z + 13, box);
        placeBlock(level, remains, x + 9, y + 2, z + 16, box);
        placeBlock(level, remains, x + 10, y + 2, z + 7, box);
        placeBlock(level, remains, x + 10, y + 2, z + 9, box);
        placeBlock(level, remains, x + 10, y + 2, z + 13, box);
        placeBlock(level, remains, x + 11, y + 2, z + 4, box);
        placeBlock(level, remains, x + 11, y + 2, z + 5, box);
        placeBlock(level, remains, x + 11, y + 2, z + 6, box);
        placeBlock(level, remains, x + 11, y + 2, z + 13, box);
        placeBlock(level, remains, x + 12, y + 2, z + 9, box);
        placeBlock(level, remains, x + 12, y + 2, z + 11, box);
        placeBlock(level, active, x + 13, y + 2, z + 7, box);
        placeBlock(level, remains, x + 13, y + 2, z + 8, box);
        placeBlock(level, remains, x + 13, y + 2, z + 9, box);
        placeBlock(level, remains, x + 13, y + 2, z + 11, box);
        placeBlock(level, remains, x + 14, y + 2, z + 6, box);
        placeBlock(level, remains, x + 14, y + 2, z + 7, box);
        placeBlock(level, remains, x + 14, y + 2, z + 11, box);
        placeBlock(level, remains, x + 14, y + 2, z + 12, box);
        placeBlock(level, remains, x + 15, y + 2, z + 7, box);

        placeBlock(level, remains, x + 4, y + 3, z + 10, box);
        placeBlock(level, remains, x + 4, y + 3, z + 13, box);
        placeBlock(level, remains, x + 5, y + 3, z + 13, box);
        placeBlock(level, active, x + 5, y + 3, z + 14, box);
        placeBlock(level, remains, x + 6, y + 3, z + 8, box);
        placeBlock(level, remains, x + 6, y + 3, z + 13, box);
        placeBlock(level, remains, x + 7, y + 3, z + 3, box);
        placeBlock(level, remains, x + 7, y + 3, z + 4, box);
        placeBlock(level, remains, x + 7, y + 3, z + 5, box);
        placeBlock(level, active, x + 7, y + 3, z + 6, box);
        placeBlock(level, remains, x + 7, y + 3, z + 7, box);
        placeBlock(level, remains, x + 7, y + 3, z + 8, box);
        placeBlock(level, remains, x + 7, y + 3, z + 13, box);
        placeBlock(level, remains, x + 8, y + 3, z + 10, box);
        placeBlock(level, remains, x + 10, y + 3, z + 7, box);
        placeBlock(level, remains, x + 10, y + 3, z + 8, box);
        placeBlock(level, remains, x + 10, y + 3, z + 9, box);
        placeBlock(level, remains, x + 11, y + 3, z + 6, box);
        placeBlock(level, remains, x + 11, y + 3, z + 7, box);
        placeBlock(level, remains, x + 11, y + 3, z + 9, box);
        placeBlock(level, remains, x + 11, y + 3, z + 11, box);
        placeBlock(level, remains, x + 12, y + 3, z + 9, box);
        placeBlock(level, remains, x + 12, y + 3, z + 11, box);

    }

}
