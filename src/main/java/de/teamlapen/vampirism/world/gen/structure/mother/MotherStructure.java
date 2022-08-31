package de.teamlapen.vampirism.world.gen.structure.mother;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

public class MotherStructure extends SinglePieceStructure {
    public static final Codec<MotherStructure> CODEC = simpleCodec(MotherStructure::new);


    public MotherStructure(StructureSettings settings) {
        super(MotherPiece::new,40,20,settings);
    }

    private void generatePieces(@NotNull StructurePiecesBuilder builder, @NotNull GenerationContext context) {

    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModFeatures.MOTHER.get();
    }
}
