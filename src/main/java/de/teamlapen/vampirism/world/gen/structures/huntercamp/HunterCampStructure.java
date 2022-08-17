package de.teamlapen.vampirism.world.gen.structures.huntercamp;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModFeatures;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HunterCampStructure extends Structure {
    public static final Codec<HunterCampStructure> CODEC = simpleCodec(HunterCampStructure::new);

    public HunterCampStructure(@NotNull Structure.StructureSettings settings) {
        super(settings);
    }

    @NotNull
    @Override
    public Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> generatePieces(builder, context));
    }

    @NotNull
    @Override
    public StructureType<?> type() {
        return ModFeatures.HUNTER_CAMP.get();
    }

    private static <C extends FeatureConfiguration> void generatePieces(@NotNull StructurePiecesBuilder structurePiecesBuilder,@NotNull GenerationContext cContext) {
        HunterCampPieces.addStartPieces(structurePiecesBuilder, cContext);

    }
}
