package de.teamlapen.vampirism.world.gen.structure.hunteroutpost;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.world.gen.structure.StructureEx;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HunterOutpostStructure extends StructureEx {

    public static final Codec<HunterOutpostStructure> CODEC = simpleCodec(HunterOutpostStructure::new);

    public HunterOutpostStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext pContext) {
        return onSurface(pContext, (builder, pos) -> {
            HunterOutpostPieces.addPieces(pContext.structureTemplateManager(), builder, pContext.random(), pos);
        });
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModFeatures.HUNTER_OUTPOST_TYPE.get();
    }

}
