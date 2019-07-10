package de.teamlapen.vampirism.world.gen.features;

import de.teamlapen.vampirism.world.gen.biome.VampirismBiomeFeatures;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public class HunterCampPieces {

    public static class Piece extends TemplateStructurePiece {
        public Piece(TemplateManager p_i51338_1_, int p_i51338_2_) {
            super(VampirismBiomeFeatures.StructurePieceTypes.HUNTERCAMP, p_i51338_2_);
        }

        public Piece(TemplateManager p_i51339_1_, CompoundNBT p_i51339_2_) {
            super(VampirismBiomeFeatures.StructurePieceTypes.HUNTERCAMP, p_i51339_2_);
        }

        @Override
        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {

        }

        @Override
        public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
            return super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, p_74875_4_);
        }
    }
}
