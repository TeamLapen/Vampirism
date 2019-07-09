package de.teamlapen.vampirism.world.gen.village;

import de.teamlapen.vampirism.blocks.BlockChurchAltar;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * Vanilla Church with a {@link de.teamlapen.vampirism.blocks.BlockChurchAltar} inside
 */
public class VillagePieceModChurch extends VillagePieces.Church {

    public VillagePieceModChurch() {
    }

    public VillagePieceModChurch(VillagePieces.Start start, int p_i45564_2_, Random rand, MutableBoundingBox p_i45564_4_, Direction facing) {
        super(start, p_i45564_2_, rand, p_i45564_4_, facing);
    }

    @Override
    public boolean addComponentParts(@Nonnull IWorld worldIn, @Nonnull Random random, @Nonnull MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
        super.addComponentParts(worldIn, random, structureBoundingBoxIn, p_74875_4_);
        this.setBlockState(worldIn, ModBlocks.church_altar.getDefaultState().with(BlockChurchAltar.FACING, Direction.SOUTH), 2, 2, 7, structureBoundingBoxIn);
        return true;
    }

    public static class CreationHandler implements VillagerRegistry.IVillageCreationHandler {


        @Override
        public VillagePieces.Village buildComponent(VillagePieces.PieceWeight villagePiece, VillagePieces.Start startPiece, List<StructurePiece> pieces, Random random, int p1, int p2, int p3, Direction facing, int p5) {
            MutableBoundingBox structureboundingbox = MutableBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 5, 12, 9, facing);
            return canVillageGoDeeper(structureboundingbox) && StructurePiece.findIntersecting(pieces, structureboundingbox) == null ? new VillagePieceModChurch(startPiece, p5, random, structureboundingbox, facing) : null;
        }

        @Override
        public Class<?> getComponentClass() {
            return VillagePieceModChurch.class;
        }

        /**
         * @param random
         * @param terrainType Apparently rather the village size, than the terrain type
         * @return
         */
        @Override
        public VillagePieces.PieceWeight getVillagePieceWeight(Random random, int terrainType) {
            return new VillagePieces.PieceWeight(VillagePieceModChurch.class, 20, MathHelper.nextInt(random, 0, 1 + terrainType));
        }
    }
}
