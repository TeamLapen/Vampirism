package de.teamlapen.vampirism.world.gen.structure.vampirealtar;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.world.gen.structure.StructureEx;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class VampireAltarStructure extends StructureEx {

    public static final Codec<VampireAltarStructure> CODEC = simpleCodec(VampireAltarStructure::new);

    public VampireAltarStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext pContext) {
        return onSurface(pContext, (builder, pos) -> {
            VampireAltarPieces.addPieces(pContext.structureTemplateManager(), builder, pContext.random(), pos);
        });
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModFeatures.VAMPIRE_ALTAR_TYPE.get();
    }

}
