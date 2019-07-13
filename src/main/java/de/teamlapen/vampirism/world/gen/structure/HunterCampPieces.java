package de.teamlapen.vampirism.world.gen.structure;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class HunterCampPieces {

    public static class Start extends HunterCampPieces.Campfire {

        public Start(Random random, int componentType) {
            super(random, componentType);
        }

        public Start(TemplateManager manager, CompoundNBT p_i51343_2_) {
            super(manager, p_i51343_2_);
        }
    }

    public static class Campfire extends Piece {
        private final Direction[] directions = new Direction[]{Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST};
        private int tentCount;

        public Campfire(Random random, int p_i50260_2_) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPFIRE, p_i50260_2_);
            int tents = random.nextInt(8);
            if (tents < 5) tentCount = 1;
            else if (tents < 7) tentCount = 2;
            else if (tents < 8) tentCount = 3;
            else tentCount = 4;
            this.boundingBox = new MutableBoundingBox(0, 0, 0, 3, 1, 3);
        }

        public Campfire(TemplateManager manager, CompoundNBT p_i51343_2_) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPFIRE, p_i51343_2_);
        }

        @Override
        public void buildComponent(StructurePiece componentIn, List<StructurePiece> listIn, Random rand) {
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
            this.setBlockState(worldIn, ModBlocks.fire_place.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
            return super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, p_74875_4_);
        }
    }

    public static class Tent extends Piece {
        public Tent(int p_i50260_2_) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPTENT, p_i50260_2_);
        }

        public Tent(TemplateManager manager, CompoundNBT p_i51343_2_) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPTENT, p_i51343_2_);
        }
    }

    public abstract static class Piece extends StructurePiece {

        protected Piece(IStructurePieceType p_i50260_1_, int p_i50260_2_) {
            super(p_i50260_1_, p_i50260_2_);
        }

        public Piece(IStructurePieceType p_i51343_1_, CompoundNBT p_i51343_2_) {
            super(p_i51343_1_, p_i51343_2_);
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound) {

        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
            return true;
        }
    }
}
