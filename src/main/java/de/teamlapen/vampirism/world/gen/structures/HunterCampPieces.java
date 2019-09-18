package de.teamlapen.vampirism.world.gen.structures;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.blocks.TentBlock;
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

public class HunterCampPieces extends StructurePiece {

    public HunterCampPieces(IStructurePieceType structurePiece, int id) {
        super(structurePiece, id);
    }

    public HunterCampPieces(IStructurePieceType structurePiece, CompoundNBT nbt) {
        super(structurePiece, nbt);
    }

    @Override
    public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
        return true;
    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound) {
    }

    public static class Fireplace extends HunterCampPieces {

        public Fireplace() {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPFIRE, 0);
        }

        public Fireplace(TemplateManager templateManager, CompoundNBT nbt) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPFIRE, nbt);
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
            this.setBlockState(worldIn, ModBlocks.fire_place.getDefaultState(), 2, 1, 2, structureBoundingBoxIn);
            return true;
        }

        public void buildComponent(StructurePiece component, List<StructurePiece> listIn, Random rand) {
            List<Integer> list = Lists.newArrayList(0, 1, 2, 3);
            int i = rand.nextInt(8);
            if (i < 1) listIn.add(new Tent(1, Direction.byHorizontalIndex(list.remove(rand.nextInt(list.size())))));
            if (i < 2) listIn.add(new Tent(2, Direction.byHorizontalIndex(list.remove(rand.nextInt(list.size())))));
            if (i < 4) listIn.add(new Tent(3, Direction.byHorizontalIndex(list.remove(rand.nextInt(list.size())))));
            listIn.add(new Tent(4, Direction.byHorizontalIndex(list.remove(rand.nextInt(list.size())))));
        }
    }

    public static class Tent extends HunterCampPieces {
        public Tent(int id, Direction direction) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPTENT, id);
            this.setCoordBaseMode(direction);
        }

        public Tent(TemplateManager templateManager, CompoundNBT nbt) {
            super(ModWorld.StructurePieceTypes.HUNTERCAMPTENT, nbt);
        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
            this.setBlockState(worldIn, ModBlocks.tent_main.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, Direction.EAST), 1, 1, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, Direction.WEST), 2, 1, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, ModBlocks.tent.getDefaultState().with(TentBlock.FACING, Direction.SOUTH), 2, 1, 2, structureBoundingBoxIn);
            return true;
        }
    }
}
