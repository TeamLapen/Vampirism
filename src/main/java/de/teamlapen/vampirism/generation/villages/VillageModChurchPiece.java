package de.teamlapen.vampirism.generation.villages;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import de.teamlapen.vampirism.ModBlocks;

/**
 * Village structure piece. A church with the block to become a normal player again
 * 
 * @author maxanier
 *
 */
public class VillageModChurchPiece extends StructureVillagePieces.Church {

	public static VillageModChurchPiece buildComponent(StructureVillagePieces.Start p_74919_0_, List p_74919_1_, Random p_74919_2_, int p_74919_3_, int p_74919_4_, int p_74919_5_, int p_74919_6_,
			int p_74919_7_) {
		StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_74919_3_, p_74919_4_, p_74919_5_, 0, 0, 0, 5, 12, 9, p_74919_6_);
		return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_74919_1_, structureboundingbox) == null ? new VillageModChurchPiece(p_74919_0_, p_74919_7_,
				p_74919_2_, structureboundingbox, p_74919_6_) : null;
	}

	public VillageModChurchPiece() {
		super();
	}

	public VillageModChurchPiece(Start p_i2102_1_, int p_i2102_2_, Random p_i2102_3_, StructureBoundingBox p_i2102_4_, int p_i2102_5_) {
		super(p_i2102_1_, p_i2102_2_, p_i2102_3_, p_i2102_4_, p_i2102_5_);
	}

	@Override
	public boolean addComponentParts(World p_74875_1_, Random p_74875_2_, StructureBoundingBox p_74875_3_) {
		super.addComponentParts(p_74875_1_, p_74875_2_, p_74875_3_);
		int i = this.getMetadataWithOffset(ModBlocks.churchAltar, 1);
		this.placeBlockAtCurrentPosition(p_74875_1_, ModBlocks.churchAltar, i, 2, 2, 7, p_74875_3_);
		return true;
	}
}
