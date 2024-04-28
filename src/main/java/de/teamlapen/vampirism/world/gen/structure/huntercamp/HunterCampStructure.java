package de.teamlapen.vampirism.world.gen.structure.huntercamp;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.core.ModStructures;
import de.teamlapen.vampirism.world.gen.structure.StructureEx;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HunterCampStructure extends StructureEx {
    public static final MapCodec<HunterCampStructure> CODEC = simpleCodec(HunterCampStructure::new);

    public HunterCampStructure(@NotNull Structure.StructureSettings settings) {
        super(settings);
    }

    @NotNull
    @Override
    public Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        return onSurface(context, (builder, pos) -> HunterCampPieces.addStartPieces(builder, context.random(), pos));
    }

    @NotNull
    @Override
    public StructureType<?> type() {
        return ModStructures.HUNTER_CAMP_TYPE.get();
    }
}
