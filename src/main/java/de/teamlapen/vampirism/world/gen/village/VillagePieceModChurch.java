package de.teamlapen.vampirism.world.gen.village;

import de.teamlapen.vampirism.blocks.BlockChurchAltar;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * Vanilla Church with a {@link de.teamlapen.vampirism.blocks.BlockChurchAltar} inside
 */
public class VillagePieceModChurch extends StructureVillagePieces.Church {

    public VillagePieceModChurch() {

    }

    public VillagePieceModChurch(StructureVillagePieces.Start start, int p_i45564_2_, Random rand, StructureBoundingBox p_i45564_4_, EnumFacing facing) {
        super(start, p_i45564_2_, rand, p_i45564_4_, facing);
    }

    @Override
    public boolean addComponentParts(@Nonnull World worldIn, @Nonnull Random random, @Nonnull StructureBoundingBox structureBoundingBoxIn) {
        super.addComponentParts(worldIn, random, structureBoundingBoxIn);
        this.setBlockState(worldIn, ModBlocks.church_altar.getDefaultState().withProperty(BlockChurchAltar.FACING, EnumFacing.SOUTH), 2, 2, 7, structureBoundingBoxIn);
        return true;
    }

    public static class CreationHandler implements VillagerRegistry.IVillageCreationHandler {

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 5, 12, 9, facing);
            return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new VillagePieceModChurch(startPiece, p5, random, structureboundingbox, facing) : null;
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
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int terrainType) {
            return new StructureVillagePieces.PieceWeight(VillagePieceModChurch.class, 20, MathHelper.getInt(random, 0, 1 + terrainType));
        }
    }
}
