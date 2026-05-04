package de.teamlapen.vampirism.common.world.structures.draculacastle;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModStructures;
import de.teamlapen.vampirism.common.world.structures.StructureExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class DraculaCastleStructure extends StructureExtension {

    public static final MapCodec<DraculaCastleStructure> CODEC = simpleCodec(DraculaCastleStructure::new);

    public DraculaCastleStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onSurface(context, (builder, pos) -> generatePieces(builder, context, pos));
    }

    protected void generatePieces(StructurePiecesBuilder builder, GenerationContext context, BlockPos pos) {
        DraculaCastlePieces.addCastlePieces(context.structureTemplateManager(), pos, builder);
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.DRACULA_CASTLE_TYPE.get();
    }
}
