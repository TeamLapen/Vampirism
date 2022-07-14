package de.teamlapen.vampirism.world.gen.structures.huntercamp;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class HunterCampStructure extends Structure {
    public static final Codec<HunterCampStructure> CODEC = simpleCodec(HunterCampStructure::new);

    public HunterCampStructure(Structure.StructureSettings settings) {
        super(settings);//, PieceGeneratorSupplier.simple(HunterCampStructure::checkLocation, HunterCampStructure::generatePieces));
    }

    @NotNull
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> generatePieces(builder, context));
    }

    @NotNull
    @Override
    public StructureType<?> type() {
        return ModFeatures.HUNTER_CAMP.get();
    }

//    private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> cContext) {
//        if (!cContext.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
//            return false;
//        } else {
//            return cContext.getLowestY(12, 15) >= cContext.chunkGenerator().getSeaLevel();
//        }
//    }

    private static <C extends FeatureConfiguration> void generatePieces(StructurePiecesBuilder structurePiecesBuilder, GenerationContext cContext) {
        HunterCampPieces.addStartPieces(structurePiecesBuilder, cContext);

    }
}
