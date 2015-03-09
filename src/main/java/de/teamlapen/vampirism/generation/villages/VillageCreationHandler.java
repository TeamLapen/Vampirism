package de.teamlapen.vampirism.generation.villages;

import java.util.List;
import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import de.teamlapen.vampirism.util.Logger;

/**
 * Handler used for the registration of VillageModChurchPiece
 * @author maxanier
 *
 */
public class VillageCreationHandler implements IVillageCreationHandler {

	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i) {
		return new StructureVillagePieces.PieceWeight(VillageModChurchPiece.class, 20, MathHelper.getRandomIntegerInRange(random, 0 + i, 3 + i * 2));
	}

	@Override
	public Class<?> getComponentClass() {
		return VillageModChurchPiece.class;
	}

	@Override
	public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
		return VillageModChurchPiece.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
	}

}
